package br.com.auster.common.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A HashMap that has a <code>name</code> attribute. 
 *
 * <p><b>Title:</b> NamedMap</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2006</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author rbarone
 * @version $Id: NamedHashMap.java 402 2007-10-23 13:48:58Z gportuga $
 */
public class NamedHashMap {
	

	private String name; 
	private HashMap map;

	public NamedHashMap() {
		
	}
	
	public NamedHashMap(String name) {
		this.map = new HashMap();
		this.name = name;
	}

	public NamedHashMap(String name, int initialCapacity) {
		this.map = new HashMap(initialCapacity);
		this.name = name;
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set entrySet() {
		return map.entrySet();
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set keySet() {
		return map.keySet();
	}

	public Object put(Object key, Object value) {
		return map.put(key, value);
	}

	public void putAll(Map m) {
		map.putAll(m);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection values() {
		return map.values();
	}

	public NamedHashMap(String name, Map m) {
		this.map = new HashMap(m);
		this.name = name;
	}

	public NamedHashMap(String name, int initialCapacity, float loadFactor) {
		this.map = new HashMap(initialCapacity, loadFactor);
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

}
