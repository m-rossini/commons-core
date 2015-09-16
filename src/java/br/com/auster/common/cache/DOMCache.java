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
 * Created on 04/11/2004
 */
package br.com.auster.common.cache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;

import br.com.auster.common.io.NIOUtils;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.common.xml.sax.NIOInputSource;




/**
 * @author Marcos Tengelmann
 * 04/11/2004
 * @version $Id: DOMCache.java 107 2005-05-18 19:08:46Z rbarone $
 */
public class DOMCache implements ExternalTableManager {
	
	private static Map tablesMap = new HashMap();	
	private static Element config;	
	private static final String XML_READER_ELEMENT = "xml-reader";
	private static final String DATA_FILE_ATTR = "data-path";
	private static final String TABLE_NAME_ATTR = "table-name";
	private static final String TABLE_ELEMENT = "table";
	private static final String CLASS_NAME_ATTR = "class-name";
	
	private static Logger log = Logger.getLogger(DOMCache.class);
	
	/**
	 * Empty Constructor
	 */
	public DOMCache() {
		super();
	}

	public DOMCache(Element config) {
		super();
		configure(config);
	}
	
	/***
	 * 
	 * @param manager
	 */
	public void configure(Element manager) {
		DOMCache.config=manager;
		
		NodeList children = DOMUtils.getElements(manager,TABLE_ELEMENT);
		int qtde = children.getLength();
		for (int i=0; i < qtde; i++) {
			Element table = (Element) children.item(i);
			String tableName = DOMUtils.getAttribute(table,TABLE_NAME_ATTR,true);
			String dataFile  = DOMUtils.getAttribute(table,DATA_FILE_ATTR,true);
			Element xmlReader = DOMUtils.getElement(table,XML_READER_ELEMENT,true);
			put(tableName, transform(dataFile,xmlReader));
			log.debug("DOMCache Transformation for table " + tableName + " is now complete.");
		}
	}
	
	/**
	 * @param dataFile
	 * @param uddFile
	 * @param xmlReader
	 * @return
	 */
	private static Node transform(String dataFile, Element xmlReaderConfig) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);				
		NIOInputSource input = null;
		String xmlReaderName = DOMUtils.getAttribute(xmlReaderConfig,CLASS_NAME_ATTR,true);
		log.debug("Starting parser for DOMCache. UDD Class name is " + xmlReaderName);
		File ff = new File(dataFile);
		DOMResult result = null;
		try {	
			Class[] c = {Element.class};
			Object[] o = {xmlReaderConfig};
			XMLReader xmlReader = (XMLReader) Class.forName(xmlReaderName).getConstructor(c).newInstance(o);			
			input = new NIOInputSource(NIOUtils.openFileForRead(dataFile));					
			Source source = new SAXSource(xmlReader, input);
			result = new DOMResult();
			SAXTransformerFactory.newInstance().newTransformer().transform(source, result);			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return result.getNode();
	}

	/***
	 * 
	 * @return
	 */
	public static String[] getTablesName() {
		return (String[]) DOMCache.tablesMap.keySet().toArray();		
	}
	
	/***
	 * 
	 * @param name
	 * @return
	 */
	public static Node getTable(String name) {
		return (Node) DOMCache.tablesMap.get(name);
	}
	
	/**
	 * 
	 */
	public static void clear() {
		tablesMap.clear();
	}
	/**
	 * @param tableName
	 * @param tableContent
	 * @return
	 */
	public static Object put(String tableName, Node tableContent) {
		return tablesMap.put(tableName, tableContent);
	}
}
