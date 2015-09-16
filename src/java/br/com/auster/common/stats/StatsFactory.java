package br.com.auster.common.stats;

public class StatsFactory {

	protected static StatsUnit buildStatsUnit() {
		if (StatsManager.isDumperRunning()) {
			return new ThreadSafeStatsUnit();
		} else {
			return new StatsUnit();
		}
	}

	protected static StatsMapping buildStatsMapping() {
		if (StatsManager.isDumperRunning()) {
			return new ThreadSafeStatsMapping();
		} else {
			return new StatsMapping();
		}
	}
}
