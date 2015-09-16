/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Created on 09/02/2006
 */
package br.com.auster.common.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.auster.common.util.I18n;

/**
 * Allows the creation of multiple files, using the methods specified in the <code>java.io.OutputStream</code> class.
 * <p>
 * The files created will be named according to the string parameter in the class´ constructors methods. The value for 
 * 	this parameter must be the path where the files will be created and a pattern for the name. This pattern should have, 
 * 	at some point, something like <code>{</code>&lt;number-format-pattern&gt;<code>}</code>. As an example:
 * <li>
 *   <ul>/tmp/filename{000).txt</ul>
 * </li>
 * <p>
 * If this pattern is not matched, then an <code>IllegalArgumentException</code> will be thrown when creating instances
 * 	of <code>MultiFileOutputStream</code>. Also, if {@link #MultiFileOutputStream(String, int, int)} is called with a 
 *  count start value lower than <code>1</code>, its overwritten to the default value (<code>1</code>).
 * <p>
 * To write to the current opened file, just use one of the <code>write()</code> methods signature. To switch to the 
 * 	next file, call {@link #close()} and keep writing; the first write operation after the close will automatically 
 *  open a new file.
 * <p>
 * As with the write operations, <code>flush()</code> also is forwarded to the currently opened file. 
 * <p>
 * Between the close and the next write operation, the current file output stream is set to <code>null</code>. So, if 
 *  a flush or (another) close operation is called, an <code>IOException</code> is thrown.
 * 
 *   
 * @author framos
 * @version $Id$
 */
public class MultiFileOutputStream extends OutputStream {
	
	
	
	// #######################
	// Static constants
	// #######################	
	
	public static final String PATTERN_STARTING_CHAR = "{";
	public static final String PATTERN_ENDING_CHAR = "}";
	
	public static final I18n i18n = I18n.getInstance(MultiFileOutputStream.class);
	public static final Logger log = Logger.getLogger(MultiFileOutputStream.class);
	
	

	// #######################
	// Instance variables
	// #######################	
	
	protected int count;
	protected int limit;
	protected String pattern;
	protected OutputStream currentFile;
	protected String currentFilename;
	protected String countPattern;
	
	protected List createdFiles;
	
	protected ThreadLocal numberFormatter;
	
	
	
	// #######################
	// Constructors
	// #######################	
	
	/**
	 * Creates a new <code>MultiFileOutputStream</code> with the specified filename pattern, and using the default
	 * 	values for start count (<code>1</code>) and limit (<code>Integer.MAX_VALUE</code>). If the pattern is not 
	 * 	correctly set, then an <code>IllegalArgumentException</code> will be thrown.
	 * 
	 * @param _filenamePattern the pattern for file names
	 * 
	 * @throws IOException exception while opening the first file
	 */
	public MultiFileOutputStream(String _filenamePattern) throws IOException {
		this(_filenamePattern, 1, Integer.MAX_VALUE);
	}
	
	/**
	 * As the previous definition, but the caller can specify the start count and limit.
	 *  
	 * @param _filenamePattern the pattern for file names
	 * @param _startCount the starting counter value
	 * @param _countLimit the limit of created files
	 * 
	 * @throws IOException exception while opening the first file
	 */
	public MultiFileOutputStream(String _filenamePattern, int _startCount, int _countLimit) throws IOException {
		if (_startCount < 1) {
			_startCount = 1;
		}
		this.pattern = _filenamePattern;
		this.countPattern = getCounterPattern(_filenamePattern);
		log.debug(i18n.getString("multifileoutputstream.countPattern", this.countPattern));
		// setting this thread´s local number formatter
		this.numberFormatter = new ThreadLocal();
		this.numberFormatter.set(new DecimalFormat(this.countPattern));
		this.count = _startCount;
		log.debug(i18n.getString("multifileoutputstream.startCount", String.valueOf(this.count)));
		this.limit = _countLimit;
		log.debug(i18n.getString("multifileoutputstream.limitCount", String.valueOf(this.limit)));
		this.createdFiles = new ArrayList();
		this.currentFile = openNextFile();
	}
	
	
	
	// #######################
	// Interface methods
	// #######################	
	
	/**
	 * Forwards the write operation to the currently opened file output stream. If a close operation was 
	 * 	executed previously, then the next file is opened before writing.
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int _char) throws IOException {
		if (this.currentFile == null) {
			this.currentFile = openNextFile();
		}
		this.currentFile.write(_char);
	}
	
	/**
	 * Forwards the write operation to the currently opened file output stream. If a close operation was 
	 * 	executed previously, then the next file is opened before writing.
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] _chars) throws IOException {
		if (this.currentFile == null) {
			this.currentFile = openNextFile();
		}
		this.currentFile.write(_chars);
	}
	
	/**
	 * Forwards the write operation to the currently opened file output stream. If a close operation was 
	 * 	executed previously, then the next file is opened before writing.
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] _chars, int _offset, int _length) throws IOException {
		if (this.currentFile == null) {
			this.currentFile = openNextFile();
		}
		this.currentFile.write(_chars, _offset, _length);
	}

	/**
	 * Closes the currently opened file.
	 * 
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		if (this.currentFile == null) {
			throw new IOException(i18n.getString("multifileoutputstream.closingNull"));
		}
		log.debug(i18n.getString("multifileoutputstream.fileClosed", this.currentFilename));
		this.currentFile.close();
		this.currentFile = null;
	}
	
	/**
	 * Forwards the flush operation to the currently opened file output stream. 
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		if (this.currentFile == null) {
			throw new IOException(i18n.getString("multifileoutputstream.flushingNull"));
		} 
		this.currentFile.flush();
	}
	
	/**
	 * @see java.lang.String#toString()
	 */
	public String toString() {
		return "[MultiFileOutputStream] {" +
			"pattern='" + this.pattern+"'" +
			"count=" + (this.count-this.createdFiles.size()) + 
			"limit=" + this.limit +
			"files=" + this.createdFiles;
	}
		

	
	// #######################
	// Public methods
	// #######################	
	
	/**
	 * Returns an immutable list with the names of the generated files. 
	 */
	public List getGeneratedFilesList() {
		return Collections.unmodifiableList(this.createdFiles);
	}

	/**
	 * Returns the current count value. It always points to the next value used when opening files. 
	 */
	public int getCurrentCount() {
		return this.count;
	}
	
	/**
	 * Returns the max. number of files this instance will create
	 */
	public int getCountLimit() {
		return this.limit;
	}

	/**
	 * Returns the name of the currently opened file. Will continue returning the current name if the output 
	 *  stream was closed and while the next file is not created. 
	 */
	public String getCurrentFilename() {
		return this.currentFilename;
	}
	
	
	// #######################
	// Private methods
	// #######################	
	
	protected String getNextFilename() {
		NumberFormat nf = (NumberFormat)this.numberFormatter.get();
		return this.pattern.replace(PATTERN_STARTING_CHAR+this.countPattern+PATTERN_ENDING_CHAR, nf.format(this.count));
	}
	
	protected OutputStream openNextFile() throws IOException {
		if (this.limit < this.count) {
			throw new IOException(i18n.getString("multifileoutputstream.limitReached")); 
		}
		this.currentFilename = getNextFilename();
		OutputStream file = IOUtils.openFileForWrite(new File(this.currentFilename), false);
		this.createdFiles.add(this.currentFilename);
		log.debug(i18n.getString("multifileoutputstream.createdFile", this.currentFilename));
		this.count++;
		return file;
	}
	
	protected String getCounterPattern(String _pattern) {
		int start = _pattern.indexOf(PATTERN_STARTING_CHAR);
		int end = _pattern.indexOf(PATTERN_ENDING_CHAR);
		if ((start < 0) || (end < start)) {
			throw new IllegalArgumentException(i18n.getString("multifileoutputstream.patternIncorrect"));
		}
		return _pattern.substring(start+1, end);
	}
	
	
	 
}
