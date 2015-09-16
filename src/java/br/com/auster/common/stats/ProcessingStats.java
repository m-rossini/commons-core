package br.com.auster.common.stats;

import org.apache.log4j.Logger;

public class ProcessingStats extends StatsMapping {

	private final static Logger log = Logger.getLogger(ProcessingStats.class);

	private final static StatsManager processingStats = new StatsManager();

	public static StatsMapping starting(Class<?> clazz, String... subDescription) {
		if (StatsManager.isStatsEnabled()) {
			return processingStats.starting(clazz, subDescription);
		} else {
			return VOID_STATS;
		}
	}

	public static StatsMapping starting(String description, String... subDescription) {
		if (StatsManager.isStatsEnabled()) {
			return processingStats.starting(description, subDescription);
		} else {
			return VOID_STATS;
		}
	}

	public static void dumpMyStats(String title) {
		if (StatsManager.isStatsEnabled()) {
			log.info(processingStats.dumpMyStats(title));
		}
	}

	public static void dumpMyStats(String title, boolean enableDump) {
		if (StatsManager.isStatsEnabled()) {
			log.info(processingStats.dumpMyStats(title, enableDump));
		}
	}

	public static void dontDumpMyStats() {
		if (StatsManager.isStatsEnabled()) {
			processingStats.dontDumpMyStats();
		}
	}

	public static void dumpAllStats() {
		if (StatsManager.isStatsEnabled()) {
			log.info(processingStats.dumpAllStats());
		}
	}
}
