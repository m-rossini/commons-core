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
 * Created on 15/06/2004
 */
package br.com.auster.common.xsl.extensions;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.XMLReader;

import br.com.auster.common.io.NIOUtils;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.common.xml.sax.NIOInputSource;


/**
 * Esta Classe é Utilizada para armazenar de forma Estatica um Estrutura XML em
 * formato DOM O Metodo init() deverá ser chamado para Inicializar a DOM. O
 * Metodo getResults() simplesmente devolde a DOM inteira a partir do ROOT Node
 * (Que tem o nome de ROOT).
 * 
 * @author Marcos Tengelmann
 * @version $Id: DOMHolder.java 107 2005-05-18 19:08:46Z rbarone $
 */
public class DOMHolder {

   protected static Node root;
   protected static final String XML_READER_ELEMENT = "xml-reader";
   protected static final String CONF_PARAM = "xml-conf";
   protected static final String FILE_PARAM = "input-file";
   protected static XMLReader xmlReader = null;
   protected static final String CLASS_NAME_ATTR = "class-name";
   protected static Element config = null;

   private static Logger log = Logger.getLogger(DOMHolder.class);

   /**
    * Metodo Chamado pelo Metodo init(). Esse metodo Instancia uma classe que
    * faz o parse do sequencial para XML (Ver UDD)
    * 
    * @param config
    * @return XML Reader do atributo class-name do elemento de configuração
    * @throws ClassNotFoundException
    * @throws NoSuchMethodException
    * @throws InstantiationException
    * @throws IllegalAccessException
    * @throws java.lang.reflect.InvocationTargetException
    */
   protected static final XMLReader getXMLReaderInstance(Element config)
         throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
         IllegalAccessException, java.lang.reflect.InvocationTargetException
   {
      String className = DOMUtils.getAttribute(config, CLASS_NAME_ATTR, true);
      Class[] c = { Element.class };
      Object[] o = { config };
      return (XMLReader) Class.forName(className).getConstructor(c).newInstance(o);
   }

   /**
    * Cria a arvore DOM a partir de um arquivo sequencia utilizando o UDD
    * 
    * @param config
    * @param fileName
    */
   public static void init(Element config, String fileName)
   {
      log.debug("Starting init()");

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      NIOInputSource input = null;

      File ff = new File(fileName);

      try {
         xmlReader = getXMLReaderInstance(DOMUtils.getElement(config,
                                                              XML_READER_ELEMENT,
                                                              true));

         input = new NIOInputSource(NIOUtils.openFileForRead(fileName));

         Source source = new SAXSource(xmlReader, input);
         DOMResult result = new DOMResult();

         SAXTransformerFactory.newInstance().newTransformer().transform(source, result);

         root = result.getNode();
      } catch(Exception e) {
         e.printStackTrace();
      }
      log.debug("Finishing init()");
   }

   /**
    * Devolve o root node da arqvore DOM armazenada
    * 
    * @return root node da arvore DOM Armazenada
    */
   public static Node getResults()
   {
      log.debug("getResults() called.");
      return root;
   }

}