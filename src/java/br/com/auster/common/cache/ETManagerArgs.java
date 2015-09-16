package br.com.auster.common.cache;

import java.util.Map;
import java.util.Set;


public interface ETManagerArgs {

	public String getName();
	public void setParms(String name, Map args);
	public void setParms(Map args);
	public void process();
	public Set getTableNames();
	public Map getTables();
	public Object getTable(String name);
	
}
