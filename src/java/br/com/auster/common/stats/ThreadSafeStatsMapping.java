package br.com.auster.common.stats;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeStatsMapping extends StatsMapping {

	private Lock statsMapLock = new ReentrantLock();

	@Override
	protected synchronized StatsUnit started(String description) {
		this.statsMapLock.lock();
		try {
			return super.started(description);
		} finally {
			this.statsMapLock.unlock();
		}
	}

	@Override
	protected String dumpThreadStats(String threadName, int nestingLevel) {
		this.statsMapLock.lock();
		try {
			return super.dumpThreadStats(threadName, nestingLevel);
		} finally {
			this.statsMapLock.unlock();
		}
	}
}
