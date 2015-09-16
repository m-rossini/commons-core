package br.com.auster.common.stats;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class StatsMapping {

	private static final String PREFIX = "  ";

	private static Logger log = Logger.getLogger(StatsMapping.class);

	private Map<String, StatsUnit> statsMap = new TreeMap<String, StatsUnit>();

	private StatsUnit currentStats = null;

	protected static final VoidStats VOID_STATS = new VoidStats();

	protected StatsMapping() {
		// empty constructor
	}

	protected StatsUnit started(String description) {
		if (this.currentStats == null) {
			StatsUnit stats = this.statsMap.get(description);
			if (stats == null) {
				stats = StatsFactory.buildStatsUnit();
				this.statsMap.put(description, stats);
			}
			this.currentStats = stats;
			stats.starting();
			return stats;
		} else {
			StatsUnit parentStats = this.currentStats;
			return parentStats.startNestedStats(description);
		}
	}

	public boolean finished() {
		if (this.currentStats == null) {
			log.warn("finished() foi chamado sem ter chamado starting()");
		} else {
			if (this.currentStats.finished()) {
				this.currentStats = null;
			}
		}
		return this.currentStats == null;
	}

	protected boolean isPending() {
		return this.currentStats != null;
	}

	protected String dumpThreadStats(String threadName) {
		return dumpThreadStats(threadName, 1);
	}

	protected String dumpThreadStats(String threadName, int nestingLevel) {
		StringBuilder buffer = new StringBuilder();
		StatsUnit statsCopy = new StatsUnit(); // nao precisa usar StatsUnitFactory.buildStatsUnit();
		for (Entry<String, StatsUnit> threadStatsEntry : this.statsMap.entrySet()) {
			StatsUnit stats = threadStatsEntry.getValue();
			stats.copyAndReset(statsCopy);
			if (!statsCopy.isReset()) {
				buffer.append(threadName);
				buffer.append('\t');
				buffer.append(nestingLevel);
				buffer.append('\t');
				for (int i = 1; i < nestingLevel; i++) {
					buffer.append(PREFIX);
				}
				buffer.append(threadStatsEntry.getKey());
				buffer.append('\t');
				buffer.append(statsCopy.toString());
				buffer.append('\n');
				StatsMapping nestedStats = statsCopy.getNestedStats();
				if (nestedStats != null) {
					buffer.append(nestedStats.dumpThreadStats(threadName, nestingLevel + 1));
				}
			}
		}
		return buffer.toString();
	}

	public static final class VoidStats extends StatsMapping {

		@Override
		protected String dumpThreadStats(String threadName, int nestingLevel) {
			return null;
		}

		@Override
		protected String dumpThreadStats(String threadName) {
			return null;
		}

		@Override
		public boolean finished() {
			return true;
		}

		@Override
		protected boolean isPending() {
			return false;
		}

		@Override
		protected StatsUnit started(String description) {
			return null;
		}
	}
}
