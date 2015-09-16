package br.com.auster.common.stats;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StatsManager {

	private static final int dumpInterval = getDumpInterval();

	private static final boolean ignoreBlackList = Boolean.getBoolean("auster.stats.dump.unconditionally");

	private static final boolean statsEnabled = Boolean.getBoolean("auster.stats.enabled");

	static {
		if (isDumperRunning()) {
			new StatsDumper(dumpInterval).start();
		}
	}

	private Set<String> blackListedThreads = new HashSet<String>();

	private final Lock blackListLock = new ReentrantLock();

	private final Map<String, StatsMapping> allStats = new HashMap<String, StatsMapping>();

	private final Lock allStatsLock = new ReentrantLock();

	protected static boolean isDumperRunning() {
		return isStatsEnabled() && dumpInterval > 0;
	}

	protected static boolean isStatsEnabled() {
		return statsEnabled;
	}
	
	private static int getDumpInterval() {
		try {
			String property = System.getProperty("auster.stats.dump.interval");
			if (property == null) {
				return 0;
			} else {
				return Integer.parseInt(property);
			}
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected StatsMapping starting(Class<?> clazz, String... subDescription) {
		return starting(clazz.getSimpleName(), subDescription);
	}

	protected StatsMapping starting(String description, String... subDescription) {
		String threadName = Thread.currentThread().getName();
		StatsMapping instance = getThreadStats(threadName);
		instance.started(join(description, subDescription));
		return instance;
	}

	protected String dumpMyStats(String title) {
		return dumpMyStats(title, false);
	}

	protected String dumpMyStats(String title, boolean enableDump) {
		String threadName = Thread.currentThread().getName();
		StatsMapping threadStats = getThreadStats(threadName);
		String statsDump = threadStats.dumpThreadStats(threadName);
		if (enableDump) {
			doDumpMyStats(threadName);
		}
		if (title == null) {
			return statsDump;
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append(title);
//			buffer.append(" (Thread ");
//			buffer.append(threadName);
//			buffer.append(")");
			buffer.append('\n');
			buffer.append(statsDump);
			return buffer.toString();
		}
	}

	protected void dontDumpMyStats() {
		dontDumpMyStats(Thread.currentThread().getName());
	}

	private void dontDumpMyStats(String threadName) {
		if (!ignoreBlackList) {
			this.blackListLock.lock();
			try {
				this.blackListedThreads.add(threadName);
			} finally {
				this.blackListLock.unlock();
			}
		}
	}

	private void doDumpMyStats(String threadName) {
		if (!ignoreBlackList) {
			this.blackListLock.lock();
			try {
				this.blackListedThreads.remove(threadName);
			} finally {
				this.blackListLock.unlock();
			}
		}
	}

	private Set<String> getBlackListedThreads() {
		if (!ignoreBlackList) {
			this.blackListLock.lock();
			try {
				return this.blackListedThreads;
			} finally {
				this.blackListLock.unlock();
			}
		} else {
			return Collections.emptySet();
		}
	}

	protected String dumpAllStats() {
		Map<String, StatsMapping> allStatsCopy = copyAllStats();
		allStatsCopy.keySet().removeAll(getBlackListedThreads());
		if (allStatsCopy.isEmpty()) {
			return "No stats to dump.";
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("Dumping stats:\n");
		for (Entry<String, StatsMapping> statsEntry : allStatsCopy.entrySet()) {
			String threadName = statsEntry.getKey();
			StatsMapping threadStats = statsEntry.getValue();
//			buffer.append("Thread ");
//			buffer.append(threadName);
//			buffer.append(":\n");
			buffer.append(threadStats.dumpThreadStats(threadName));
		}
		return buffer.toString();
	}

	private String join(String description, String[] subDescription) {
		if (subDescription.length == 0) {
			return description;
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append(description);
			for (String item : subDescription) {
				buffer.append('.');
				buffer.append(item);
			}
			return buffer.toString();
		}
	}

	private StatsMapping getThreadStats(String threadName) {
		this.allStatsLock.lock();
		try {
			StatsMapping threadStats = this.allStats.get(threadName);
			if (threadStats == null) {
				threadStats = StatsFactory.buildStatsMapping();
				this.allStats.put(threadName, threadStats);
			}
			return threadStats;
		} finally {
			this.allStatsLock.unlock();
		}
	}

	private Map<String,StatsMapping> copyAllStats() {
		this.allStatsLock.lock();
		try {
			return new TreeMap<String, StatsMapping>(this.allStats);
		} finally {
			this.allStatsLock.unlock();
		}
	}
}
