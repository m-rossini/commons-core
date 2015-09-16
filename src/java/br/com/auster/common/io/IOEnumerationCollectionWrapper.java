/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 25/09/2007
 */
package br.com.auster.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * This is a simple Enumerator for processing many input streams as one.
 * 
 * This class implements Enumeration Interface.
 * 
 * It also supports conversion from types, thru expectedType methods.
 * The convertion is done on nextElement() method.
 * 
 * We set the expected and desired run-time type before using the enumeration.
 * And the nextElement() method will convert the received list on constructor to
 * the expected type, if compatible.
 * 
 * The support as of this version is only for Input data, not output.
 * 
 * 
 * 
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class IOEnumerationCollectionWrapper implements Enumeration {
	public static final int			TYPE_FILE									= 1;
	public static final int			TYPE_INPUTSTREAM					= 2;
	public static final int			TYPE_READABLEBYTECHANNEL	= 3;

	private static final Logger	log												= Logger
																														.getLogger(IOEnumerationCollectionWrapper.class);

	private Iterator						collectionIterator;
	// private Collection list;
	private int									type;
	
	private int size;

	public IOEnumerationCollectionWrapper(Collection list) {
		// this.list = list;
		this.collectionIterator = list.iterator();
		this.size = list.size();
	}

	public int getSize() {
		return size;
	}
	
	public void setExpectedType(int type) {
		this.type = type;
	}

	public int getExpectedType() {
		return type;
	}

	/**
	 * 
	 * @return
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return collectionIterator.hasNext();
	}

	/**
	 * 
	 * @return
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		Object next = collectionIterator.next();

		if (next instanceof File) {
			File file = (File) next;
			switch (type) {
			case TYPE_FILE:
				return file;
			case TYPE_INPUTSTREAM:
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					log.fatal("Error creating a FileInputStream", e);
					return null;
				}
			case TYPE_READABLEBYTECHANNEL:
				try {
					return new FileInputStream(file).getChannel();
				} catch (FileNotFoundException e) {
					log.fatal("Error creating a FileChannel", e);
					return null;
				}
			default:
				return file;
			}
		}

		if (next instanceof InputStream) {
			InputStream file = (InputStream) next;
			switch (type) {
			case TYPE_FILE:
				throw new RuntimeException("Cannot Convert from InputStream to File.InputStream:"
						+ file);
			case TYPE_INPUTSTREAM:
				return file;
			case TYPE_READABLEBYTECHANNEL: {
				if (file instanceof FileInputStream) {
					return ((FileInputStream) file).getChannel();
				} else {
					return Channels.newChannel(file);
				}
			}
			default:
				return file;
			}
		}

		if (next instanceof ReadableByteChannel) {
			ReadableByteChannel file = (ReadableByteChannel) next;
			switch (type) {
			case TYPE_FILE:
				throw new RuntimeException("Cannot Convert from Channel to File.Channel:" + file);
			case TYPE_INPUTSTREAM:
				return Channels.newInputStream(file);
			case TYPE_READABLEBYTECHANNEL: {
				return file;
			}
			default:
				return file;
			}
		}

		return next;
	}

}
