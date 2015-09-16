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
 * Created on 07/11/2004
 */
package br.com.auster.common.cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.xml.DOMUtils;


/**
 * This class implements a Cache of a Multi Key Hash Maps.
 * An arbitrary number of caches can be hold by this class.
 * 
 * A Multi Key HashMap is a map that can be accessed by up to 5 Keys in order to get a Value.
 * 
 * This Class requires some XML configuration as follow (Note that table element can appear many times):
 * 
 * <table> 			
 * 		table-name="table-name"    						===>This is the name of the table
 *		data-path="conf/tables/CNL.txt">				===>This is the Flat File with data to be cached. 
 *		<items											===>Defines the keys and the value
 *			type="index" 		===>Identifies if the file is separated or not. Can be "index" | "slice"
 *			separator=";">		===>If type="index", then this is the separator, otherwise it will be ignored
 *			<value name="locationCode" index="1" />  What is the value. If type="slice" you need to specify start=n" size="m"
 *			<key   name="cityName"     index="3" />  First key, with same syntax of value element.
 *			<key   name="stateCode"    index="4" />  key number N.....
 *		</items>
 * </table>	
 * @author Marcos Tengelmann
 * 07/11/2004
 * @version $Id: MultiKeyCache.java 91 2005-04-07 21:13:55Z framos $
 */
public class MultiKeyCache implements ExternalTableManager {
	
	private class Item {
		private Integer index;
		private Integer start;
		private Integer size;
		
		/**
		 * @return Returns the index.
		 */
		protected Integer getIndex() {
			return index;
		}
		/**
		 * @param index The index to set.
		 */
		protected void setIndex(Integer index) {
			this.index = index;
		}
		/**
		 * @return Returns the size.
		 */
		protected Integer getSize() {
			return size;
		}
		/**
		 * @param size The size to set.
		 */
		protected void setSize(Integer size) {
			this.size = size;
		}
		/**
		 * @return Returns the start.
		 */
		protected Integer getStart() {
			return start;
		}
		/**
		 * @param start The start to set.
		 */
		protected void setStart(Integer start) {
			this.start = start;
		}
	}
	private static final String TABLE_ELEMENT = "table";
	private static final String DATA_FILE_ATTR = "data-path";
	private static final String TABLE_NAME_ATTR = "table-name";
	private static final String ITEMS_ELEMENT = "items";
	private static final String KEY_ELEMENT = "key";
	private static final String NAME_ATTR = "name";
	private static final String TYPE_ATTR = "type";
	private static final String TYPE_INDEX = "index";
	private static final String TYPE_SLICE = "slice";
	private static final String SEPARATOR_ATTR = "separator";
	private static final String INDEX_ATTR = "index";
	private static final String SIZE_ATTR = "size";
	private static final String START_ATTR = "start";
	private static final String VALUE_ELEMENT = "value";	

	private static Element config;
	private static Map tableKeyMap = new HashMap();
	private static Map tablesMap = new HashMap();
	private static Map tableValueMap = new HashMap();
	
	private String separator;
	private String type;

	/**
	 * 
	 */
	public MultiKeyCache() {
		super();
	}
	public MultiKeyCache(Element config) {
		super();
		try {
			configure(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see com.tti.common.xsl.ExternalTableManager#configure(org.w3c.dom.Element)
	 */
	public void configure(Element manager) throws Exception {
		config=manager;
		
		NodeList children = DOMUtils.getElements(manager,TABLE_ELEMENT);
		int qtde = children.getLength();
		for (int i=0; i < qtde; i++) {
			Map lkMap = new LinkedHashMap();
			Element table = (Element) children.item(i);
			String tableName = DOMUtils.getAttribute(table,TABLE_NAME_ATTR,true);
			String dataFile  = DOMUtils.getAttribute(table,DATA_FILE_ATTR,true);			
			
			//Handle items Element
			Element items = DOMUtils.getElement(table,ITEMS_ELEMENT,true);
			type = DOMUtils.getAttribute(items,TYPE_ATTR,true);
			if (type.equals(TYPE_INDEX)) {
				separator = DOMUtils.getAttribute(items,SEPARATOR_ATTR,true);
			} else if (!type.equals(TYPE_SLICE)) {
				throw new Exception("type attribute in items Element can be = {index | slice}");
			}
			//Handle key elements inside items Element.
			NodeList itemsChildren = DOMUtils.getElements(items,KEY_ELEMENT);
			int itemCount = itemsChildren.getLength();
			for (int j=0; j < itemCount; j++) {
				Element keyElement = (Element) itemsChildren.item(j);
				String name = DOMUtils.getAttribute(keyElement,NAME_ATTR,true);
				Integer index = null;
				Integer start = null;
				Integer size = null;
				if (this.type.equals(TYPE_INDEX)) {
					 index = Integer.valueOf(DOMUtils.getAttribute(keyElement,INDEX_ATTR,true));
				} else {
					start = Integer.valueOf(DOMUtils.getAttribute(keyElement,START_ATTR,true));
					size = Integer.valueOf(DOMUtils.getAttribute(keyElement,SIZE_ATTR,true));
				}
				Item keyObj = new Item();
				keyObj.setIndex(index);
				keyObj.setSize(size);
				keyObj.setStart(start);
				lkMap.put(name,keyObj);				
			}
			//Handle Value Element in items Elemnt
			Element value = DOMUtils.getElement(items,VALUE_ELEMENT,true);
			String valueName = DOMUtils.getAttribute(value,NAME_ATTR,true);
			Integer valueIndex=null;
			Integer valueStart=null;
			Integer valueSize=null;
			if (this.type.equals(TYPE_INDEX)) {
				valueIndex = Integer.valueOf(DOMUtils.getAttribute(value,INDEX_ATTR,true));
			} else {
				valueStart = Integer.valueOf(DOMUtils.getAttribute(value,START_ATTR,true));
				valueSize = Integer.valueOf(DOMUtils.getAttribute(value,SIZE_ATTR,true));
			}
			Item valueObj = new Item();
			valueObj.setIndex(valueIndex);
			valueObj.setSize(valueSize);
			valueObj.setStart(valueStart);			
			tableValueMap.put(tableName,valueObj);
			
			tableKeyMap.put(tableName,lkMap);
			
			tablesMap.put(tableName,transform(tableName,dataFile));
		}
	}
	
	private MultiKeyMap transform(String tableName,String dataFile) throws Exception {
		MultiKeyMap mkp = MultiKeyMap.decorate(new HashedMap(50000, (float) 0.9));
		Map lkMap = (LinkedHashMap) tableKeyMap.get(tableName);
		int keyCount = lkMap.size();
		if (keyCount > 5) {
			throw new Exception("Cannot have more than 5 keys");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		
		String record;			
		
		while ( (record = br.readLine()) != null ) {
			//TODO Implementar separação por start/size
			if (this.type.equals(TYPE_INDEX)) {
				String[] result = record.split(separator);
				for (int i = 0; i < result.length;i++) {
					Item valueObj = (Item) tableValueMap.get(tableName);
					String value = result[valueObj.getIndex().intValue()-1];
					
					Iterator itr = lkMap.values().iterator();
					String keys[] = new String[2];
					int index = 0;
					while (itr.hasNext()) {
						Item keyObj = (Item)itr.next();
						keys[index++] = result[keyObj.getIndex().intValue()-1];
					}
					switch (keyCount) {
						case 1: {put1(mkp, keys,value);break;}
						case 2: {put2(mkp, keys,value);break;}
						case 3: {put3(mkp, keys,value);break;}
						case 4: {put4(mkp, keys,value);break;}
						case 5: {put5(mkp, keys,value);break;}
						}
					;
				}
			}
		}
		return mkp;
	}
	
	private void put1(MultiKeyMap mkp, String[] keys, String value) {
		MultiKey mk = new MultiKey(new Object[] {keys[0]} );
		mkp.put(mk,value);
	}
	private void put2(MultiKeyMap mkp, String[] keys, String value) {
		mkp.put(keys[0],keys[1],value);
	}	
	private void put3(MultiKeyMap mkp,String[] keys, String value) {
		mkp.put(keys[0],keys[1],keys[2],value);
	}	
	private void put4(MultiKeyMap mkp,String[] keys, String value) {
		mkp.put(keys[0],keys[1],keys[2],keys[3],value);
	}	
	private void put5(MultiKeyMap mkp,String[] keys, String value) {
		mkp.put(keys[0],keys[1],keys[2],keys[3],keys[4],value);
	}
	
	public String get(String tableName, String key) {
		key=key.trim();
		String resultCache=null;
		MultiKeyMap mkp = (MultiKeyMap) tablesMap.get(tableName);
		MultiKey mk = new MultiKey( new Object[] {key});
		resultCache = (String) mkp.get(mk);
		return (resultCache==null ? "":resultCache);
	}
	public String get(String tableName, String key1, String key2) {
		key1=key1.trim();
		key2=key2.trim();		
		String resultCache=null;
		MultiKeyMap mkp = (MultiKeyMap) tablesMap.get(tableName);
		resultCache = (String) mkp.get(key1,key2);
		return (resultCache==null ? "":resultCache);
	}
	public String get(String tableName, String key1, String key2, String key3) {		
		key1=key1.trim();
		key2=key2.trim();
		key3=key3.trim();
		String resultCache=null;
		MultiKeyMap mkp = (MultiKeyMap) tablesMap.get(tableName);
		resultCache = (String) mkp.get(key1,key2,key3);
		return (resultCache==null ? "":resultCache);
	}
	public String get(String tableName, String key1, String key2, String key3, String key4) {
		key1=key1.trim();
		key2=key2.trim();
		key3=key3.trim();
		key4=key4.trim();
		String resultCache=null;
		MultiKeyMap mkp = (MultiKeyMap) tablesMap.get(tableName);
		resultCache = (String) mkp.get(key1,key2,key3,key4);
		return (resultCache==null ? "":resultCache);
	}
	public String get(String tableName, String key1, String key2, String key3, String key4,String key5) {
		key1=key1.trim();
		key2=key2.trim();
		key3=key3.trim();
		key4=key4.trim();
		key5=key5.trim();
		String resultCache=null;
		MultiKeyMap mkp = (MultiKeyMap) tablesMap.get(tableName);
		resultCache = (String) mkp.get(key1,key2,key3,key4,key5);
		return (resultCache==null ? "":resultCache);
	}	
}
