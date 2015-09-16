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
 * Created on 03/11/2004
 */
package br.com.auster.common.xsl.extensions;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used as a XSL extension to help to group values by
 * given composed keys.
 * 
 * @author Ricardo Barone
 * @version $Id: GroupBy.java 91 2005-04-07 21:13:55Z framos $
 */
public final class GroupBy {

   /**
    * This class represents a group structure. It constains the
    * necessary attributes to help and make easier the grouping task.
    */
   protected final class Group {

      protected final Map childrenKeys = new LinkedHashMap();
      protected final Map attributes = new LinkedHashMap();
      protected String groupName = "group";
      protected String keyName = "key";
      protected final String keyValue;

      public Group(String keyValue)
      {
         this.keyValue = keyValue;
      }

      /**
       * Clears the entire grouping structure.
       */
      public final void clear()
      {
         this.childrenKeys.clear();
         this.attributes.clear();
      }

      /**
       * Adds a value to a group attribute. The
       * <code>keyStart</code> will be used to decide if this
       * attribute will be put in this group or in a child of it.
       * @param keys the composed key.
       * @param keyStart the step of the recursion. It will point to
       * the array position of <code>keys</code> that represents the
       * child group key's name.
       * @param attrName the attribute name.
       * @param attrValue the value of the attribute.
       */
      public final void setAttribute(String[] keys,
                                     int keyStart,
                                     String attrName,
                                     Object attrValue)
      {
         if(keys.length > keyStart) {
            final String key = keys[keyStart];
            // The attribute will not be put here. A child of this
            // group will own it.
            final Group group;
            if(this.childrenKeys.containsKey(key)) {
               group = (Group) this.childrenKeys.get(key);
            } else {
               group = new Group(key);
               this.childrenKeys.put(key, group);
            }
            group.setAttribute(keys, keyStart + 1, attrName, attrValue);
         } else {
            // If we found the last key of the array
            this.attributes.put(attrName, attrValue);
         }
      }

      /**
       * Gets an attribute named <code>attrName</code> from the
       * group represented by the <code>keys</code>.
       * @param keys the keys used to find the wanted group.
       * @param keyStart the nested level of this group.
       * @param attrName the attribute name.
       * @return the attribute named <code>attrName</code> from the
       * given group, specified by <code>keys</code>. If not found,
       * an empty String is returned.
       */
      public final Object getAttribute(String[] keys, int keyStart, String attrName)
      {
         if(keys.length > keyStart) {
            final String key = keys[keyStart];
            // The attribute wanted is not here. A child of this
            // group may own it.
            if(!this.childrenKeys.containsKey(key)) return "";

            final Group group = (Group) this.childrenKeys.get(key);
            return group.getAttribute(keys, keyStart + 1, attrName);
         } else {
            // If we found the last key of the array
            if(this.attributes.containsKey(attrName))
               return this.attributes.get(attrName);
            else
               return "";
         }
      }

      /**
       * Gets the group structure as a node set.
       *
       * @param document the document used to create the internal nodes.
       * @return a node set as the following generic example:
       *
       * <group key="value" attrName="attrValue" attrName2="attrValue2" ...>
       *     <group key="value" attrName="attrValue" attrName2="attrValue2" ...>
       *         <group key="value" attrName="attrValue" attrName2="attrValue2" ...>
       *             ...
       *         </group>
       *         ...
       *     </group>
       *     ...
       * </group>
       */
      public final Node getNodeStructure(Document document)
            throws ParserConfigurationException
      {
         final Element root = document.createElement(this.groupName);
         // Gets the children nodes
         Iterator it = this.childrenKeys.values().iterator();
         while(it.hasNext()) {
            Group group = (Group) it.next();
            root.appendChild(group.getNodeStructure(document));
         }
         // Sets the attributes
         root.setAttribute(this.keyName, this.keyValue);
         it = this.attributes.keySet().iterator();
         while(it.hasNext()) {
            String key = (String) it.next();
            String value = (String) this.attributes.get(key);
            root.setAttribute(key, value);
         }

         return root;
      }
   }

   /***********************************/
   /* START OF GROUPBY IMPLEMENTATION */
   /***********************************/

   protected final Group root = new Group("root");

   /**
    * Clears the grouping structure.
    */
   public final void clear()
   {
      this.root.clear();
   }

   /**
    * Clears the grouping structure.
    */
   public final void clear(XSLProcessorContext context, Element elem)
   {
      this.clear();
   }

   /**
    * This method is used to add or replace an attribute to a
    * group. If the group with the given composed key does not exist,
    * it will be created. If it is a subgroup of another group, all
    * the grouping structure will be created, if it does not
    * exist. If it already exists, then the given attribute value
    * will be stored into that group.
    *
    * @param keySep the key separator. It's the character that will
    * be used to identify each key of the composed key.
    * @param composedKey the composed key.
    * @param attrName the attribute name.
    * @param attrValue the attribute value.
    * @param attrType the attribute type. It's used to defined the
    * object type to convert <code>attrValue</code> to.
    */
   public final void group(String keySep,
                           String composedKey,
                           String attrName,
                           String attrValue,
                           String attrType)
   {
      final Object value;

      try {
         if(attrType.equals("Integer") || attrType.equals("Long"))
            value = new Long(attrValue);
         else if(attrType.equals("Float") || attrType.equals("Double"))
            value = new Double(attrValue);
         else
            value = attrValue;
      } catch(NumberFormatException e) {
         e.printStackTrace();
      }

      this.group(keySep, composedKey, attrName, attrValue);
   }

   /**
    * This method is used to add or replace an attribute to a
    * group. If the group with the given composed key does not exist,
    * it will be created. If it is a subgroup of another group, all
    * the grouping structure will be created, if it does not
    * exist. If it already exists, then the given attribute value
    * will be stored into that group.
    *
    * @param keySep the key separator. It's the character that will
    * be used to identify each key of the composed key.
    * @param composedKey the composed key.
    * @param attrName the attribute name.
    * @param attrValue the attribute value.
    */
   public final void group(String keySep,
                           String composedKey,
                           String attrName,
                           Object attrValue)
   {
      final String[] keys = composedKey.split(keySep, -1);

      this.root.setAttribute(keys, 0, attrName, attrValue);
   }

   /**
    * Gets the attribute named <code>attrName</code> from the
    * grouping structure as a double value.
    * @param keySep the character used as key separator.
    * @param composedKey the composed key.
    * @param attrName the attribute name to be searched.
    * @return the double value for the given attribute name and
    * composed key.
    */
   public final double getDouble(String keySep, String composedKey, String attrName)
   {
      final Object obj = this.getAttr(keySep, composedKey, attrName);
      if(obj instanceof Number) return ((Number) obj).doubleValue();
      if(obj instanceof String) if(((String) obj).length() == 0)
         return 0;
      else
         try {
            return Double.parseDouble((String) obj);
         } catch(NumberFormatException e) {
            e.printStackTrace();
         }
      return 0;
   }

   /**
    * Gets the attribute named <code>attrName</code> from the
    * grouping structure as a long value.
    * @param keySep the character used as key separator.
    * @param composedKey the composed key.
    * @param attrName the attribute name to be searched.
    * @return the long value for the given attribute name and
    * composed key.
    */
   public final long getLong(String keySep, String composedKey, String attrName)
   {
      final Object obj = this.getAttr(keySep, composedKey, attrName);
      if(obj instanceof Number) return ((Number) obj).longValue();
      if(obj instanceof String) if(((String) obj).length() == 0)
         return 0;
      else
         try {
            return Long.parseLong((String) obj);
         } catch(NumberFormatException e) {
            e.printStackTrace();
         }
      return 0;
   }

   /**
    * Gets the attribute named <code>attrName</code> from the
    * grouping structure as an object.
    * @param keySep the character used as key separator.
    * @param composedKey the composed key.
    * @param attrName the attribute name to be searched.
    * @return the value (as an object) for the given attribute name
    * and composed key.
    */
   public final Object getAttr(String keySep, String composedKey, String attrName)
   {
      final String[] keys = composedKey.split(keySep, -1);
      return this.root.getAttribute(keys, 0, attrName);
   }

   /**
    * Gets the group structure as a node set.
    *
    * @return a node set as the following generic example:
    *
    * <group key="value" attrName="attrValue" attrName2="attrValue2" ...>
    *     <group key="value" attrName="attrValue" attrName2="attrValue2" ...>
    *         <group key="value" attrName="attrValue" attrName2="attrValue2" ...>
    *             ...
    *         </group>
    *         ...
    *     </group>
    *     ...
    * </group>
    */
   public final Node getNodeStructure() throws ParserConfigurationException
   {
      final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      /*
       debugNode(this.root.getNodeStructure(document));
       */
      return this.root.getNodeStructure(document);
   }

   private void debugNode(Node myNode)
   {
      System.out.println("getLocalName " + myNode.getLocalName());
      System.out.println("getNodeType " + myNode.getNodeType());
      System.out.println("getNodeValue " + myNode.getNodeValue());
      System.out.println("getNodeName " + myNode.getNodeName());
      NodeList child = myNode.getChildNodes();
      System.out.println("Children " + child.getLength());
      if(myNode.hasAttributes()) {
         NamedNodeMap attrs = myNode.getAttributes();
         System.out.println("Attributes is " + attrs.getLength());
         for(int i = 0; i < attrs.getLength(); i++) {
            System.out.println("Attribute #" + i);
            Node attr = attrs.item(i);
            System.out.println("Attr Name " + attr.getNodeName());
            System.out.println("Attr Value " + attr.getNodeValue());
         }
      } else {
         System.out.println("No Attibutes");
      }
      for(int i = 0; i < child.getLength(); i++) {
         System.out.println("Child #" + i);
         debugNode(child.item(i));
      }
   }

}