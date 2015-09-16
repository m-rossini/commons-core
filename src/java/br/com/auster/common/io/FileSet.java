/*
 * Copyright (c) 2004 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 16/11/2004
 */
package br.com.auster.common.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.xml.DOMUtils;


/**
 * This class is used to get a file or directory set from a XML configuration or
 * String Pattern.
 * 
 * It's underlying implementation class is
 * <code>org.apache.tools.ant.DirectoryScanner</code>, so be sure to have ant
 * in the classpath.
 * 
 * @see "Apache Ant Javadoc (org.apache.tools.ant.DirectoryScanner) for more
 *      information on the configuration possibilities"
 * @author Ricardo Barone
 * @version $Id: FileSet.java 91 2005-04-07 21:13:55Z framos $
 */
public class FileSet {

   public static final String FS_LIST_ELEMENT = "fileset-list";
   public static final String FS_ELEMENT = "fileset";
   public static final String FS_DIR_ATTR = "dir";
   public static final String CASE_ATTR = "casesensitive";
   public static final String INCLUDE_ELEMENT = "include";
   public static final String EXCLUDE_ELEMENT = "exclude";
   public static final String FILTER_ATTR = "name";

   private static Logger log = Logger.getLogger(FileSet.class);

   /**
    * Given a file set filter, return a <code>DirectoryScanner</code> that
    * corresponds to it.
    * 
    * <p>
    * The <code>fileset</code> tag uses <code>dir</code> and
    * <code>casesensitive</code> attribute. Only two nested elements are
    * suported: <code>include</code> and <code>exclude</code>. They accept
    * attribute <code>name</code> to define the pattern of filtering. An
    * example is shown bellow. <p/>
    * 
    * <blockquote>
    * 
    * <pre>
    *  
    *  &lt;fileset dir=&quot;./../&quot; casesensitive=&quot;true&quot; &gt;
    *    &lt;include name=&quot;** /*.java&quot;/&gt;
    *    &lt;exclude name=&quot;** /*Test*&quot;/&gt;
    *  &lt;/fileset&gt;
    *  
    * </pre>
    * 
    * </blockquote>
    * 
    * @param fs
    *           file set filter in a DOM tree.
    */
   public final static DirectoryScanner getDirectoryScannerFromFileSet(Element fs)
   {
      final DirectoryScanner dirScan = new DirectoryScanner();
      dirScan.setBasedir(DOMUtils.getAttribute(fs, FS_DIR_ATTR, true));
      dirScan.setCaseSensitive(DOMUtils.getBooleanAttribute(fs, CASE_ATTR));

      // Get include filters from fileset elements
      NodeList nodeList = DOMUtils.getElements(fs, INCLUDE_ELEMENT);
      if(nodeList.getLength() > 0) {
         String[] includes = new String[nodeList.getLength()];
         for(int i = 0; i < includes.length; i++) {
            includes[i] = DOMUtils.getAttribute((Element) nodeList.item(i),
                                                FILTER_ATTR,
                                                true);
         }
         dirScan.setIncludes(includes);
      }

      // Get exclude filters from fileset elements
      nodeList = DOMUtils.getElements(fs, EXCLUDE_ELEMENT);
      if(nodeList.getLength() > 0) {
         String[] excludes = new String[nodeList.getLength()];
         for(int i = 0; i < excludes.length; i++) {
            excludes[i] = DOMUtils.getAttribute((Element) nodeList.item(i),
                                                FILTER_ATTR,
                                                true);
         }
         dirScan.setExcludes(excludes);
      }

      dirScan.scan();
      return dirScan;
   }

   /**
    * Given a list of file set filters, return an array of
    * <code>DirectoryScanner</code> that complains with them.
    * 
    * @param fslist
    *           a list of file set filters in a DOM tree.
    * @see #getDirectoryScannerFromFileSet(Element)
    */
   public final static DirectoryScanner[] getDirectoryScannerFromFileSetList(Element fsList)
   {
      final NodeList nodeList = DOMUtils.getElements(fsList, FS_ELEMENT);
      final DirectoryScanner[] dirScanList = new DirectoryScanner[nodeList.getLength()];
      for(int i = 0; i < dirScanList.length; i++) {
         dirScanList[i] = getDirectoryScannerFromFileSet((Element) nodeList.item(i));
      }
      return dirScanList;
   }

   /**
    * Given a list of file set filters, return an array of <code>File</code>
    * objects that complains with those rules.
    * 
    * @param fslist
    *           a list of file set filters in a DOM tree.
    * @return an array of files that complains with the filter.
    * @see #getDirectoryScannerFromFileSetList(Element)
    */
   public final static File[] getIncludedFiles(Element fsList)
   {
      final List includedFiles = new ArrayList();
      final DirectoryScanner[] dirScanList = getDirectoryScannerFromFileSetList(fsList);
      for(int i = 0; i < dirScanList.length; i++) {
         String[] files = dirScanList[i].getIncludedFiles();
         String fpath = (dirScanList[i].getBasedir()).getAbsolutePath()
               + System.getProperty("file.separator");
         for(int j = 0; j < files.length; j++) {
            includedFiles.add(new File(fpath + files[j]));
         }
      }

      return (File[]) includedFiles.toArray(new File[0]);
   }

   /**
    * Given a list of file set filters, return an array of <code>File</code>
    * objects that complains with those rules.
    * 
    * @param fslist
    *           a list of file set filters in a DOM tree.
    * @return an array of directories that complains with the filter.
    * @see #getDirectoryScannerFromFileSetList(Element)
    */
   public final static File[] getIncludedDirectories(Element fsList)
   {
      final List includedFiles = new ArrayList();
      final DirectoryScanner[] dirScanList = getDirectoryScannerFromFileSetList(fsList);
      for(int i = 0; i < dirScanList.length; i++) {
         String[] files = dirScanList[i].getIncludedDirectories();
         String fpath = (dirScanList[i].getBasedir()).getAbsolutePath()
               + System.getProperty("file.separator");
         for(int j = 0; j < files.length; j++) {
            includedFiles.add(new File(fpath + files[j]));
         }
      }

      return (File[]) includedFiles.toArray(new File[0]);
   }

   /**
    * Given a basedir and an include pattern filter, return an array of
    * <code>File</code> objects that complains with those rules.
    * 
    * @param baseDir
    *           the base directory from where the files will be searched.
    * @param filter
    *           the include pattern filter according to
    *           <code>com.tti.common.util.FileSet</code>.
    * @param return
    *           the matching files found in the base directory.
    */
   public final static File[] getFiles(String baseDir, String filter)
   {
      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         Document document = dbf.newDocumentBuilder().newDocument();
         Element include = document.createElement(INCLUDE_ELEMENT);
         include.setAttribute(FILTER_ATTR, filter);
         Element fileset = document.createElement(FS_ELEMENT);
         fileset.setAttribute(FS_DIR_ATTR, baseDir);
         fileset.appendChild(include);
         Element fsList = document.createElement(FS_LIST_ELEMENT);
         fsList.appendChild(fileset);
         return FileSet.getIncludedFiles(fsList);
      } catch(javax.xml.parsers.ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }
   
   /**
    * Creates the file list based on the specified mask
    * 
    * @param mask mask to select files to process
    * @return the matching files found.
    */
   public static File[] getFiles(String mask) {
     String basedir = ".";
     if (mask.startsWith(File.separator)) {
       basedir = File.separator;
       mask = mask.substring(1);
     } else if (mask.charAt(1) == ':') {
       // handle windows drive letter (like "C:")
       basedir = mask.substring(0,3);
       mask = mask.substring(3);
     }
     return getFiles(basedir, mask);
   }
}