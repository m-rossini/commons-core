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
package br.com.auster.common.tests.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import br.com.auster.common.io.MultiFileOutputStream;

/**
 * @author framos
 * @version $Id$
 */
public class MultiFileTest extends TestCase {

	
	public void testInvalidPattern() {
		String tmpDir = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "testfile.txt";
		try {
			MultiFileOutputStream os = new MultiFileOutputStream(tmpDir);
			assertNull(os);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail();
		} catch (IllegalArgumentException iae) {
			assertTrue(true);
			return;
		}		
		fail();
	}
	
	public void testInvalidLimits() {
		String tmpDir = getFilenamePattern();
		try {
			MultiFileOutputStream os = new MultiFileOutputStream(tmpDir, 0, 10);
			assertEquals(2, os.getCurrentCount());
			os.close();
			removeFiles(os.getGeneratedFilesList());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
		assertTrue(true);
	}
	
	public void testSingleFile() {
		String tmpDir = getFilenamePattern();
		try {
			MultiFileOutputStream os = new MultiFileOutputStream(tmpDir, 1, 1);
			os.write("someone didit".getBytes());
			os.close();
			assertEquals(1, os.getGeneratedFilesList().size());
			removeFiles(os.getGeneratedFilesList());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
	}
	
	public void testMultiFile() {
		String tmpDir = getFilenamePattern();
		try {
			MultiFileOutputStream os = new MultiFileOutputStream(tmpDir, 1, 3);
			os.write("someone didit".getBytes());
			os.close();
			os.write("someone didit".getBytes());
			os.close();
			os.write("someone didit".getBytes());
			os.close();
			assertEquals(3, os.getGeneratedFilesList().size());
			removeFiles(os.getGeneratedFilesList());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
	}

	public void testExceedingLimits() {
		String tmpDir = getFilenamePattern();
		MultiFileOutputStream os = null;
		try {
			try {
				os = new MultiFileOutputStream(tmpDir, 1, 3);
				os.write("someone didit".getBytes());
				os.close();
				os.write("someone didit".getBytes());
				os.close();
				os.write("someone didit".getBytes());
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				fail();
				return;
			} 
			try {
				os.write("someone didit".getBytes());
			} catch (IOException e) {
				assertTrue(true);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
				return;
			}
		} finally {
			try { 
				assertEquals(3, os.getGeneratedFilesList().size());
				removeFiles(os.getGeneratedFilesList());
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	public void testClosingNull() {
		String tmpDir = getFilenamePattern();
		MultiFileOutputStream os = null;
		try {
			os = new MultiFileOutputStream(tmpDir, 1, 3);
			os.write("someone didit".getBytes());
			os.close();
			assertEquals(1, os.getGeneratedFilesList().size());
			os.close();
		} catch (IOException ioe) {
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				assertEquals(1, os.getGeneratedFilesList().size());
				removeFiles(os.getGeneratedFilesList());
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	public void testFlushingNull() {
		String tmpDir = getFilenamePattern();
		MultiFileOutputStream os = null;
		try {
			os = new MultiFileOutputStream(tmpDir, 1, 3);
			os.write("someone didit".getBytes());
			os.flush();
			assertEquals(1, os.getGeneratedFilesList().size());
			os.flush();
		} catch (IOException ioe) {
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				assertEquals(1, os.getGeneratedFilesList().size());
				removeFiles(os.getGeneratedFilesList());
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	
	private String getFilenamePattern() {
		return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "testfile{000}.txt";
	}
	
	private void removeFiles(List _list)  throws IOException {
		for (Iterator it=_list.iterator(); it.hasNext();) {
			String filename = (String)it.next();
			File f = new File(filename);
			assertTrue(f.exists());
			assertTrue(f.isFile());
			System.out.println("removing file " + filename);
			f.delete();
		}
	}
}
