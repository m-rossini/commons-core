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
 * Created on 13/10/2004
 */
package br.com.auster.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xpath.NodeSet;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.util.I18n;

/**
 * This class has a lot of utilities to manipulate and debug XML files and
 * programs.
 * 
 * @author Ricardo Barone
 * @version $Id: DOMUtils.java 265 2006-06-20 20:53:36Z mtengelm $
 */
public final class DOMUtils {
	
	 public static final String CLASS_NAME_ATTR = "class-name";
	 
	 public static final String RECORD_TAG = "record";
   public static final String RESULT_TAG = "result";

   private static final I18n i18n = I18n.getInstance(DOMUtils.class);

   /**
    * This method is used to translate a node list to its formatted string
    * representation.
    * 
    * @param nodeList
    *           the node list.
    * @param indent
    *           true to output the formatted string using indentation.
    */
   public static StringBuffer node2String(NodeList nodeList, boolean indent)
   {
      final StringBuffer result = new StringBuffer();
      for(int i = 0, size = nodeList.getLength(); i < size; i++) {
         result.append(node2String(nodeList.item(i), indent));
      }
      return result;
   }

   public static StringBuffer node2String(Node root, boolean indent, boolean omitXML) {
     if(root == null) return new StringBuffer();

     try {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if(indent) {
           transformer.setOutputProperty(OutputKeys.INDENT, "true");
           transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT,
                                         "4");
        }
        //OutputPropertiesFactory.
        if (omitXML) {
        	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        // Transforms the DOM tree into String
        StringWriter result = new StringWriter();
        try {
           transformer.transform(new DOMSource(root), new StreamResult(result));
        } catch(TransformerException e) {
           e.printStackTrace();
        }
        return result.getBuffer();

     } catch(TransformerConfigurationException e) {
        e.printStackTrace();
        return new StringBuffer();
     }
  	 
   }
   /**
    * This method is used to translate a node to its formatted string
    * representation.
    * 
    * @param root
    *           the root node.
    * @param indent
    *           true to output the formatted string using indentation.
    */
   public static StringBuffer node2String(Node root, boolean indent)
   {
  	 return node2String(root, indent, false);
   }

   public static Node string2Node(String root)
   {
  	 	DOMResult result = new DOMResult();
      if(root == null) return result.getNode();

      try {
         Transformer transformer = TransformerFactory.newInstance().newTransformer();

         // Transforms the DOM tree into String         
         try {
            transformer.transform(new StreamSource(new StringReader(root)), result);
         } catch(TransformerException e) {
            e.printStackTrace();
         }
         return ((Document)result.getNode()).getDocumentElement();
         //Document doc = (Document)domResult.getNode();
         //Element elem = doc.getDocumentElement();

      } catch(TransformerConfigurationException e) {
         e.printStackTrace();
         return result.getNode();
      }
   }
   /**
    * This method is used to translate a attribute list to its formatted string
    * representation.
    * 
    * @param attrList
    *           the attribute list.
    */
   public static final StringBuffer attrList2String(NamedNodeMap attrList)
   {
      final StringBuffer result = new StringBuffer();
      for(int i = 0, size = attrList.getLength(); i < size; i++) {
         Attr attr = (Attr) attrList.item(i);
         result.append(' ').append(attr.getName()).append("=\"").append(attr.getValue()).append('\"');
      }
      return result;
   }
   
   /**
    * Given a configuration, creates an instance of the class
    * specified in the "class-name" attribute of it. Its constructor
    * must accept only one parameter: the <code>config</code> object.
    * @param config the DOM tree corresponding to the configuration
    * of the class to be instantiated.
    * @return a instance based on the information of the given config element.
    * @throws Exception if some error occurs while instantiating the class.
    */
   public final static Object getInstance(Element config) 
       throws Exception
   {
       final String className = getAttribute(config, CLASS_NAME_ATTR, true);

       Class[] c = {Element.class};
       Object[] o = {config};
       return Class.forName(className).getConstructor(c).newInstance(o);
   }

   /**
    * Given a node, return the text inside it. For example: <abc>This is a text
    * </abc> If the node abc is given, the text "This is a text" will be
    * returned.
    * 
    * @param node
    *           the node to get the text from.
    * @return the child text inside the node.
    */
   public static final StringBuffer getText(Node node)
   {
      final StringBuffer value = new StringBuffer();
      final NodeList children = node.getChildNodes();
      for(int i = 0, size = children.getLength(); i < size; i++) {
         Node ci = children.item(i);
         if(ci.getNodeType() == Node.TEXT_NODE) {
            value.append(ci.getNodeValue());
         }
      }
      return value;
   }

   /**
    * Gets the boolean value of the attribute from the given element.
    * 
    * @param node
    *           the element to search for the attribute.
    * @param attribute
    *           the attribute name.
    * @return true if the attribute is defined and its value is "true". False
    *         otherwise.
    */
   public static final boolean getBooleanAttribute(Element node, String attribute)
   {
      return getBooleanAttribute(node, attribute, false);
   }
   
   /**
     * Gets the boolean value of the attribute from the given element.
     * 
     * @param node
     *          the element to search for the attribute.
     * @param attribute
     *          the attribute name.
     * @param defaultValue
     *          the default value that will be returned if the attribute is not
     *          defined.
     * @return true if the attribute is defined and its value is "true". The
     *         specified <code>defaultValue</code> otherwise.
     */
   public static final boolean getBooleanAttribute(Element node, 
                                                   String attribute, 
                                                   boolean defaultValue)
   {
     final String encrypt = node.getAttribute(attribute).trim();
     if (encrypt.length() == 0) {
       return defaultValue;
     }
     return Boolean.valueOf(encrypt).booleanValue();
   }

   /**
    * Gets the integer value of the attribute from the given element, throwing
    * an exception if it is mandatory and does not exist or is invalid.
    * 
    * @param node
    *           the element to search for the attribute.
    * @param attribute
    *           the attribute name.
    * @param isMandatory
    *           if the attribute must exist
    * 
    * @return the value of the given attribute in the given element.
    * @throws IllegalArgumentException
    *            if the attribute is mandatory and does not exist or is not a
    *            number.
    */
   public static final int getIntAttribute(Element node,
                                           String attribute,
                                           boolean isMandatory)
         throws IllegalArgumentException
   {
      try {
         return Integer.parseInt(getAttribute(node, attribute, isMandatory));
      } catch(NumberFormatException e) {
    	 if (!isMandatory) { return 0; }
         throw new IllegalArgumentException(i18n.getString("attrNotInt",
                                                           attribute,
                                                           node.getNodeName()));
      }
   }

   /**
    * Gets the attribute from the element, throwing an exception if it is
    * mandatory and does not exist.
    * 
    * @param node
    *           the element to search for the attribute.
    * @param attribute
    *           the attribute name.
    * @param isMandatory
    *           if the attribute must exist
    * 
    * @return the value of the given attribute in the given element.
    * @throws IllegalArgumentException
    *            if the attribute is mandatory and does not exist.
    */
   public static final String getAttribute(Element node,
                                           String attribute,
                                           boolean isMandatory)
         throws IllegalArgumentException
   {
      if(isMandatory && !node.hasAttribute(attribute))
            throw new IllegalArgumentException(i18n.getString("attrNotDefined",
                                                              attribute,
                                                              node.getNodeName()));

      return node.getAttribute(attribute);
   }

   /**
    * Gets the element with the given local name from the node, throwing an
    * exception if it is mandatory and does not exist.
    * 
    * @param node
    *           the element that will be used as the root node for searching
    * @param localName
    *           the local name of the elements searched. Use null to match all.
    * @param isMandatory
    *           if the element must exist
    * 
    * @return an element that is child of given node and that matches the given
    *         local name.
    * @throws IllegalArgumentException
    *            if the element is mandatory and does not exist.
    */
   public static final Element getElement(Element node,
                                          String localName,
                                          boolean isMandatory)
         throws IllegalArgumentException
   {
      return getElement(node, null, localName, isMandatory);
   }

   /**
    * Gets the element with the given local name and name space from the node,
    * throwing an exception if it is mandatory and does not exist.
    * 
    * @param node
    *           the element that will be used as the root node for searching
    * @param namespaceURI
    *           the name space URI of the elements searched. Use null to match
    *           all.
    * @param localName
    *           the local name of the elements searched. Use null to match all.
    * @param isMandatory
    *           if the element must exist
    * 
    * @return an element that is child of given node and that matches the given
    *         local name and name space.
    * @throws IllegalArgumentException
    *            if the element is mandatory and does not exist.
    */
   public static final Element getElement(Element node,
                                          String namespaceURI,
                                          String localName,
                                          boolean isMandatory)
         throws IllegalArgumentException
   {
      final NodeList nodeList = getElements(node, namespaceURI, localName);
      if(isMandatory && nodeList.getLength() == 0) {
         throw new IllegalArgumentException(i18n.getString("NSelementNotFound",
                                                           (localName == null) ? "*"
                                                                 : localName,
                                                           (namespaceURI == null) ? "*"
                                                                 : namespaceURI,
                                                           node.getNodeName()));
      }
      return (nodeList.getLength() > 0 ? (Element) nodeList.item(0) : null);
   }

   /**
    * Gets the elements with the given local name from the node.
    * 
    * @param node
    *           the element that will be used as the root node for searching.
    * @param localName
    *           the local name of the elements searched. Use null to match all.
    * @return a node set containing elements that are children of the given node
    *         and that matches the given local name.
    */
   public static final NodeList getElements(Element node, String localName)
   {
      return getElements(node, null, localName);
   }

   /**
    * Gets the elements with the given local name and name space from the node.
    * 
    * @param node
    *           the element that will be used as the root node for searching
    * @param namespaceURI
    *           the name space URI of the elements searched. Use null to match
    *           all.
    * @param localName
    *           the local name of the elements searched. Use null to match all.
    * @return a node set containing elements that are children of the given node
    *         and that matches the given local name and name space.
    */
   public static final NodeList getElements(Element node,
                                            String namespaceURI,
                                            String localName)
   {
      final NodeSet nodeSet = new NodeSet();
      final NodeList nodeList = node.getChildNodes();
      for(int i = 0, size = nodeList.getLength(); i < size; i++) {
         Node child = nodeList.item(i);
         // Checks if the child found is an element that matches the
         // parameters
         if(child.getNodeType() == Node.ELEMENT_NODE) {
            if(verifyElement(child, namespaceURI, localName)) {
               nodeSet.addElement(child);
            }
         }
      }
      return nodeSet;
   }

   /**
    * Verifies if the Element's Name and NamespaceURI are the same as the given
    * parameters.
    * 
    * @param node
    *           the Element to be verified.
    * @param namespaceURI
    *           the namespace URI that it wants to match.
    * @param localName
    *           the Element's name that it wants to match.
    * @return true if it matches both the NamespaceURI and localName, false
    *         otherwise.
    */
   public static final boolean verifyElement(Node node, String namespaceURI, String localName) {
    if ((namespaceURI == null || (node.getNamespaceURI() != null && node.getNamespaceURI()
        .equals(namespaceURI)))
        && (localName == null || ((node.getLocalName() != null && node.getLocalName()
            .equals(localName)) || (node.getLocalName() == null && node.getNodeName()
            .equals(localName))))) {
      return true;
    }
    return false;
  }

   public final static Element openDocument(String filename, boolean isEncrypted)
         throws ParserConfigurationException, SAXException, IOException,
         GeneralSecurityException
   {
      return openDocument(IOUtils.openFileForRead(filename, isEncrypted));
   }

   
   public final static Element openDocument(InputStream inStream) 
   		throws ParserConfigurationException, SAXException, IOException, 
		GeneralSecurityException
   {
	    // Parses the XML config file
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
        /*
        try {
          dbf.setXIncludeAware(true);  // requires at least JAXP 1.3
        } catch (UnsupportedOperationException e) {
          System.out.println("XInclude not supported in current JAXP implementation.");
        }
        */
	    Document config = dbf.newDocumentBuilder().parse(inStream);
	    Element root = config.getDocumentElement();
	
	    // Closes the config file stream
	    inStream.close();
	
	    return root;

   }
   
   /**
     * Returns the first child of the given Element or null if there is no child
     * (Node.ELEMENT_NODE).
     * 
     * @param node
     *          the Element whose child it wants to find.
     * @return the first child Element or null if none exists.
     */
   public final static Element getFirstChild(Element node) {
     Node elt = node.getFirstChild();
     while (elt != null && elt.getNodeType() != Node.ELEMENT_NODE) {
       elt = elt.getNextSibling();
     }
     return (Element) elt;
   }
   
   /**
    * Returns the last child of the given Element or null if there is no child
    * (Node.ELEMENT_NODE).
    * 
    * @param node
    *          the Element whose child it wants to find.
    * @return the last child Element or null if none exists.
    */
   public final static Element getLastChild(Element node) {
     Node elt = node.getLastChild();
     while (elt != null && elt.getNodeType() != Node.ELEMENT_NODE) {
       elt = elt.getPreviousSibling();
     }
     return (Element) elt;
   }
   
   /**
     * Returns the next sibling of the given Element or null if there are no
     * Node.ELEMENT_NODE siblings after the node.
     * 
     * @param node
     *          the Element whose next sibling it wants to find.
     * @return the next sibling Element or null if none exists.
     */
   public final static Element getNextSibling(Element node) {
     Node elt = node.getNextSibling();
     while (elt != null && elt.getNodeType() != Node.ELEMENT_NODE) {
       elt = elt.getNextSibling();
     }
     return (Element) elt;
   }
   
   /**
    * Returns the previous sibling of the given Element or null if there are no
    * Node.ELEMENT_NODE siblings before the node.
    * 
    * @param node
    *          the Element whose previous sibling it wants to find.
    * @return the previous sibling Element or null if none exists.
    */
   public final static Element getPreviousSibling(Element node) {
     Node elt = node.getPreviousSibling();
     while (elt != null && elt.getNodeType() != Node.ELEMENT_NODE) {
       elt = elt.getPreviousSibling();
     }
     return (Element) elt;
   }
   
   /**
    * Converts the result set to a node set.
    * @param rs the result set.
    * @param root the root of the node set. If <code>null</code>, a new one will be created.
    * @return the same <code>root</code> if it is not <code>null</code>. Otherwise a new one will be created.
    *         It will contain the results of the result set in node set format.
    *
    *         Bellow is a sample of the tree set created as the result.
    *         <result>
    *             <record fieldName1="value1" fieldName2="value2" ... />
    *             ...
    *         </result>
    */
   public static final Node resultSet2NodeSet(ResultSet rs, Node root)
       throws SQLException, ParserConfigurationException
   {
       final Document document;
       if (root == null) {
           document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
           root = document.createElement(RESULT_TAG);
       } else {
           document = root.getOwnerDocument();
       }

       // Tries to discover the name of each column
       final ResultSetMetaData metaData = rs.getMetaData();
       final int columnCount = metaData.getColumnCount();
       final String[] columnName = new String[columnCount];
       
       // Initialize columnName array with all the column names,
       // trimming it if its value is not null
       for (int i = 0; i < columnCount; i++) {
           columnName[i] = metaData.getColumnName(i+1);
           if (columnName[i] != null)
               columnName[i] = columnName[i].trim();
       }

       // Puts each line in the result
       while (rs.next()) {
           Element element = document.createElement(RECORD_TAG);
           root.appendChild(element);
           
           // For each field, creates a entry with the column name and its value
           for (int i = 0; i < columnCount; i++) {
               Object rsObject = rs.getObject(i+1);
               if (columnName[i] != null && rsObject != null)
                   element.setAttribute(columnName[i], rsObject.toString().trim());
           }
       }
       
       return root;
   }

   /**
    * Creates SAX events using the result set. It is assumed that the document of the content handler
    * is already started (method <code>ContentHandler:startDocument()</code>). This method will not end that document.
    * @param rs the result set.
    * @param handler the content handler that will handle all SAX events for this result set transformation.
    *         Bellow is a sample of the events created as the result.
    *         <result>
    *             <record fieldName1="value1" fieldName2="value2" ... />
    *             ...
    *         </result>
    */
   public static final void resultSet2ContentHandler(ResultSet rs, ContentHandler handler) 
       throws SQLException, SAXException
   {
       resultSet2ContentHandler(rs, handler, null);
   }

   /**
    * Creates SAX events using the result set. It is assumed that the document of the content handler
    * is already started (method <code>ContentHandler:startDocument()</code>). This method will not end that document.
    *         Bellow is a sample of the events created as the result.
    *         <result rootAttr1="rootAttr1 value" rootAttr2="rootAttr2 value" ... >
    *             <record fieldName1="value1" fieldName2="value2" ... />
    *             ...
    *         </result>
    *
    * @param rs the result set.
    * @param handler the content handler that will handle all SAX events for this result set transformation.
    * @param rootAtts The root element attributes. If null, no attributes will be set.
    */
   public static final void resultSet2ContentHandler(ResultSet rs, ContentHandler handler, Attributes rootAtts) 
       throws SQLException, SAXException
   {
       // Tries to discover the name of each column
       final ResultSetMetaData metaData = rs.getMetaData();
       final int columnCount = metaData.getColumnCount();
       final String[] columnName = new String[columnCount];
       final AttributesImpl atts = new AttributesImpl();

       // Initialize columnName array with all the column names,
       // trimming it if its value is not null
       for (int i = 0; i < columnCount; i++) {
           columnName[i] = metaData.getColumnName(i+1);
           if (columnName[i] != null)
               columnName[i] = columnName[i].trim();
       }

       handler.startElement("", RESULT_TAG, RESULT_TAG, (rootAtts == null) ? atts : rootAtts);

       // Puts each line in the result
       while (rs.next()) {
           // For each field, creates a entry with the column name and its value
           for (int i = 0; i < columnCount; i++) {
               Object rsObject = rs.getObject(i+1);
               if (columnName[i] != null && rsObject != null)
                   atts.addAttribute("", columnName[i], columnName[i], "CDATA", rsObject.toString().trim());
           }
           handler.startElement("", RECORD_TAG, RECORD_TAG, atts);
           handler.endElement("", RECORD_TAG, RECORD_TAG);
           atts.clear();
       }

       handler.endElement("", RESULT_TAG, RESULT_TAG);
   }
   
}