/*
 * Copyright (c) 2004 TTI Tecnologia. All Rights Reserved.
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
 * Created on Jan 21, 2005
 */
package br.com.auster.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import br.com.auster.common.util.I18n;


/**
 * <P>
 * 	This is a utility class that enables the bundling of a list of files, into a single one using the 
 * 		TAR Java API. It also allows that a file be compressed using ZIP or GZIP algorithms.  
 * </P>
 * @author Frederico A Ramos
 * @version $Id$
 */
public class CompressUtils {

	public static final I18n i18n = I18n.getInstance(CompressUtils.class);
	public static final Logger log = Logger.getLogger(CompressUtils.class);
	
	// #######################
	// Static public methods
	// #######################	
	
	/**
	 * <P>
	 * 	Creates a TAR ball using the list of files passed as argument. This method treats files and directory, including
	 * 		recursively. 
	 * </P>
	 * 
	 * @param _listOfFiles list of files to add to TAR 
	 * @param _bundleFilename the output TAR filename
	 * 
	 * @return a <code>File</code> to the output TAR file
	 * 
	 * @throws IOException if any I/O exceptions occurr
	 */
	public static File createTARBundle(Collection _listOfFiles, String _bundleFilename) throws IOException {
		TarOutputStream outputStream = new TarOutputStream(new FileOutputStream(_bundleFilename));
		outputStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);
		Iterator iterator =  buildListOfFiles(_listOfFiles).iterator();				
		while (iterator.hasNext()) {		
			File file = (File) iterator.next();
			TarEntry entry = new TarEntry(file.getName());
			outputStream.putNextEntry(entry);
			writeFileToBundle(outputStream, new FileInputStream(file));
			outputStream.closeEntry();
		}
		outputStream.close();
		return (new File(_bundleFilename));
	}
	
	/**
	 * <P>
	 * 	Creates a ZIP ball using the list of files passed as argument. This method treats files and directory, including
	 * 		recursively. 
	 * </P>
	 * 
	 * @param _listOfFiles list of files to add to ZIP 
	 * @param _bundleFilename the output ZIP filename
	 * 
	 * @return a <code>File</code> to the output ZIP file
	 * 
	 * @throws IOException if any I/O exceptions occurr
	 */
	public static File createZIPBundle(Collection _listOfFiles, String _bundleFilename) throws IOException {
		ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(_bundleFilename));
		Iterator iterator =  buildListOfFiles(_listOfFiles).iterator();				
		while (iterator.hasNext()) {		
			File file = (File) iterator.next();
			ZipEntry entry = new ZipEntry(file.getName());
			outputStream.putNextEntry(entry);
			writeFileToBundle(outputStream, new FileInputStream(file));
			outputStream.closeEntry();
		}
		outputStream.close();
		return (new File(_bundleFilename));
	}	

	/**
	 * <P>
	 * 	Easy access to create a ZIP file with just one file. Directories are not allowed in this implementation.
	 * </P>
	 * 	
	 * @param _incomingFile the file to be compressed
	 * @param _outputFilename the resulting name of the ZIP file
	 * 
	 * @return a <code>File</code> to the output ZIP file
	 * 
	 * @throws IOException if any I/O exceptions occurr
	 */
	public static File compressAsZIP(File _incomingFile, String _outputFilename) throws IOException {
		if ((_incomingFile == null) || (!_incomingFile.isFile()) ) {
			throw new IllegalArgumentException("invalid incoming file : '" + _incomingFile + "'");
		}		
		if (_outputFilename == null) {
			throw new IllegalArgumentException("invalid output file : '" + _outputFilename + "'");
		}
		Collection temp = new ArrayList();
		temp.add(_incomingFile);
		return createZIPBundle(temp, _outputFilename);
	}
	
	
	/**
	 * <P>
	 * 	Creates a GZip file using as input the <code>_incomingFile</code> parameter. Directories 
	 * 		are not allowed in this implementation.
	 * </P>	
	 * @param _incomingFile the file to be compressed
	 * @param _outputFilename the resulting name of the GZip file
	 * @return a <code>File</code> to the output GZip file
	 * @throws IOException if any I/O exceptions occurr
	 */
	public static File compressAsGZip(File _incomingFile, String _outputFilename) throws IOException {
		if ((_incomingFile == null) || (!_incomingFile.isFile()) ) {
            throw new IllegalArgumentException("invalid incoming file : '" + _incomingFile + "'");
		}		
		if (_outputFilename == null) {
            throw new IllegalArgumentException("invalid output file : '" + _outputFilename + "'");
		}
		GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(_outputFilename));
		FileInputStream inputStream = new FileInputStream(_incomingFile);
		writeFileToBundle(outputStream, inputStream);
		outputStream.close();
		return (new File(_outputFilename));
	}

	
	// #######################
	// Static private methods
	// #######################
	
	/**
	 *	<P>
	 *		Reads from the input stream, in blocks of 32Kb, and writes them to the bundle file output stream.
	 *	</P> 
	 */
	private static void writeFileToBundle(OutputStream _outputStream, InputStream _inputStream) throws IOException {		

		byte[] buffer = new byte[32000];				
		// writes entry to tar file
		int bytesRead = 0;
		while ((bytesRead = _inputStream.read(buffer)) >= 0) {				
			_outputStream.write(buffer, 0, bytesRead);
		}
		_outputStream.flush();		
        _inputStream.close();
	}
	
	/**
	 * <P>
	 *	Creates a list of all the files that will be included in the bundle. This method will add all files 
	 *		and subdirectories if any incoming entry is itself a directory. 
	 * </P> 
	 */
	private static Collection buildListOfFiles(Collection _files) {
		if (_files == null) {
			throw new IllegalArgumentException("list of files to compress is NULL");
		}
		Set listOfFiles = new HashSet();
		Iterator iterator = _files.iterator();
		File currentFile = null;
		while (iterator.hasNext()) {
			// add each entry in incoming collection to the list of files
			Object obj = iterator.next();
			if (obj instanceof String) {
				currentFile = new File((String)obj);
			} else if (obj instanceof File) {
				currentFile = (File) obj;
			} else {
				throw new IllegalArgumentException("invalid element type : " + obj.getClass());
			}
			if (!currentFile.exists()) {
				continue;
			}
			// recursively add each file in this directory, if iterator.next() is a directory.
			if (currentFile.isDirectory()) {				
				listOfFiles.addAll( buildListOfFiles(Arrays.asList(currentFile.listFiles())) );
			} else if (currentFile.isFile()) {
				listOfFiles.add(currentFile);
			} else {
				log.warn(i18n.getString("CompressUtils.unknownfile"));
			}
		}
		return listOfFiles;
	}
}
