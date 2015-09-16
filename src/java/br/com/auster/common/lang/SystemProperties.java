package br.com.auster.common.lang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class SystemProperties {
	private static Logger log = Logger.getLogger(SystemProperties.class);
	public static final int DEBUG=0;
	public static final int INFO=1;
	public static final int WARN=2;
	public static final int ERROR=3;
	public static final int FATAL=4;

	public static Set getProperties() {
		Runtime runner = Runtime.getRuntime();

		HashMap map = new HashMap();
		map.put("available.processors", Integer.toString(runner.availableProcessors()));
		map.put("memory.free", Long.toString(runner.freeMemory()));
		map.put("memory.max", Long.toString(runner.maxMemory()));
		map.put("memory.total", Long.toString(runner.totalMemory()));
		map.putAll(System.getProperties());

		return map.entrySet();
	}

	public static void logProperties(int level) {
		Set props = getProperties();
		for (Iterator itr=props.iterator();itr.hasNext();) {
			Map.Entry entry = (Entry) itr.next();
			String msg = "Property name=[" + entry.getKey() + "].Property value=[" + entry.getValue() + "]";
			switch (level) {
			case 1:{log.info(msg);break;}
			case 2:{log.warn(msg);break;}
			case 3:{log.error(msg);break;}
			case 4:{log.fatal(msg);break;}
			default:{log.debug(msg);break;}
			}
		}
	}
	public static void consoleProperties() {
		Set props = getProperties();
		for (Iterator itr=props.iterator();itr.hasNext();) {
			Map.Entry entry = (Entry) itr.next();
			System.out.println("Property name=[" + entry.getKey() + "].Property value=[" + entry.getValue() + "]");
		}
	}

}
