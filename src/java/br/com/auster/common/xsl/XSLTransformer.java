/*
 * Copyright (c) 2004 Auster Solutions do Brasil LTDA. All Rights Reserved.
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
 * Created on 17/11/2004
 */
package br.com.auster.common.xsl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.CollectionUtils;

import br.com.auster.common.io.IOUtils;


/**
 * @author Ricardo Barone
 * @version $Id: XSLTransformer.java 107 2005-05-18 19:08:46Z rbarone $
 */
public class XSLTransformer {
   
   public final static int XSL_SYSTEM_ID = 0;
   public final static int SOURCE_SYSTEM_ID = 1;
   public final static int RESULT_SYSTEM_ID = 2;

   private XSLTransformer()
   {
      // empty constructor
   }

   public static void transform(String xslPath,
                                int xslSystemId,
                                String sourcePath, 
                                String resultPath)
   throws IOException
   {
      InputStream xslInputStream = null;
      InputStream sourceStream = null;
      OutputStream resultStream = null;

      try {
         xslInputStream = IOUtils.openFileForRead(xslPath);
         sourceStream = IOUtils.openFileForRead(new File(sourcePath));
         resultStream = IOUtils.openFileForWrite(new File(resultPath), false);
         
         String systemIdPath;
         switch(xslSystemId) {
            case SOURCE_SYSTEM_ID:
               systemIdPath = sourcePath;
               break;
            case RESULT_SYSTEM_ID:
               systemIdPath = resultPath;
               break;
            default:
               systemIdPath = xslPath;
         }

         // 1. Instantiate a TransformerFactory.
         TransformerFactory tFactory = TransformerFactory.newInstance();

         // 2. Use the TransformerFactory to process the stylesheet Source and
         //    generate a Transformer.
         Transformer transformer = tFactory.newTransformer(new StreamSource(
               xslInputStream, systemIdPath));

         // 3. Use the Transformer to transform an XML Source and send the
         //    output to a Result object.
         transformer.transform(new StreamSource(sourceStream), new StreamResult(
               resultStream));

      } catch(Exception ie) {
         throw new RuntimeException(ie);
      } finally {
         if(xslInputStream != null) {
            xslInputStream.close();
         }
         if(sourceStream != null) {
            sourceStream.close();
         }
         if(resultStream != null) {
            resultStream.close();
         }
      }
   }

   /**
    * Transforms a source to a result using a XSL file.
    * 
    * Execute <code>java com.tti.common.xsl.XSLTransformer</code> without args
    * to see usage message.
    * 
    * @param args
    */
   public static void main(String[] args)
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("\nUsage: java com.tti.common.xsl.XSLTransformer");
      buffer.append(" [-x|-s|-r] xslPath sourcePath resultPath\n\n");
      buffer.append("  xslPath       path to the XSL file to be used\n");
      buffer.append("  sourcePath    path to the source file to be processed\n");
      buffer.append("  sourcePath    path to the result file to be created\n\n");
      buffer.append("options:\n");
      buffer.append("  -x            use the XSL file path as the XSL System ID\n");
      buffer.append("  -s            use the source file path as the XSL System ID\n");
      buffer.append("  -r            use the result file path as the XSL System ID\n\n");
      buffer.append("Transforms sourcePath to resultPath using xslPath.\n");
      buffer.append("Optionally, you can provide -x or -s or -r to set the ");
      buffer.append("System ID that will be used to resolve relative URLs ");
      buffer.append("during the transformation processing. The default is -x.\n");
      String usage = buffer.toString();
      
      LinkedList argList = new LinkedList();
      CollectionUtils.addAll(argList, args);
      
      String sysIdOption = null;
      int xslSystemId = XSL_SYSTEM_ID;
      if(args.length < 3 || args.length > 4) {
         System.out.println("Invalid number of arguments.\n" + usage);
         System.exit(1);
      } else if(args.length == 4) {
         sysIdOption = (String) argList.removeFirst();
         if(sysIdOption.equals("-x")) {
            // already set - default option XSL_SYSTEM_ID
         } else if(sysIdOption.equals("-s")) {
            xslSystemId = SOURCE_SYSTEM_ID;
         } else if(sysIdOption.equals("-r")) {
            xslSystemId = RESULT_SYSTEM_ID;
         } else {
            System.out.println("Invalid Option: '" + sysIdOption + "'.\n" + usage);
            System.exit(1);
         }
      }
      
      String xslPath = (String) argList.removeFirst();
      String sourcePath = (String) argList.removeFirst();
      String resultPath = (String) argList.removeFirst();
      
      try {
         XSLTransformer.transform(xslPath, xslSystemId, 
                                  sourcePath, resultPath);         
      } catch(IOException e) {
         System.out.println("Unexpected Error Occured! " +
                            "Please verify if XSL and Source file exist, " + 
                            "and check the write permissions on the Result path.");
         e.printStackTrace();
         System.exit(1);
      }
   }

}