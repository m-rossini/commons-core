/*
 * Copyright (c) 2004-2008 Auster Solutions. All Rights Reserved.
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
 * Created on 10/07/2008
 */
package br.com.auster.common.stats;

import org.apache.log4j.Logger;

public class StatsUnit {

	private static Logger log = Logger.getLogger(StatsUnit.class);

	private int counter = 0;

	private double totalMilliSeconds = 0.0;

	private long lastNanoSeconds = 0L;

	private StatsMapping nestedStats = null;

	protected void starting() {
		if (isTherePendingStats()) {
			log.warn("starting() foi chamado sem ter chamado finished()");
		} else {
			this.lastNanoSeconds = System.nanoTime();
		}
	}

	protected StatsUnit startNestedStats(String description) {
		if (this.nestedStats == null) {
			this.nestedStats = StatsFactory.buildStatsMapping();
		}
		return this.nestedStats.started(description);
	}

	protected boolean finished() {
		if (this.nestedStats != null && this.nestedStats.isPending()) {
			this.nestedStats.finished();
			return false;
		} else {
			if (isTherePendingStats()) {
				this.totalMilliSeconds += (System.nanoTime() - this.lastNanoSeconds) / 1000000.0;
				this.counter++;
				this.lastNanoSeconds = 0L;
			} else {
				log.warn("finished() foi chamado sem ter chamado starting()");
			}
			return true;
		}
	}

	protected void copyAndReset(StatsUnit copy) {
		if (isReset()) {
			copy.reset();
		} else {
			copy.counter = this.counter;
			copy.totalMilliSeconds = this.totalMilliSeconds;
			copy.lastNanoSeconds = this.lastNanoSeconds;
			copy.nestedStats = this.nestedStats;
			reset();
		}
	}

	protected boolean isReset() {
		return getCount() == 0 && !isTherePendingStats()
				&& this.nestedStats != null && !this.nestedStats.isPending();
	}

	private void reset() {
		this.counter = 0;
		this.totalMilliSeconds = 0.0;
		// this.lastNanoSeconds nao pode ser resetado
	}

	protected StatsMapping getNestedStats() {
		return this.nestedStats;
	}

	private boolean isTherePendingStats() {
		return this.lastNanoSeconds > 0L;
	}

	private double getTotalMillis() {
		return this.totalMilliSeconds;
	}

	private int getCount() {
		return this.counter;
	}

	private double getAverageMillis() {
		if (this.counter == 0) {
			return 0.0;
		} else {
			return this.totalMilliSeconds / this.counter;
		}
	}

	@Override
	public String toString() {
		// TODO alterar para %.1f
		return String.format("%d x %.1f = %.1f ms%s", getCount(),
				getAverageMillis(), getTotalMillis(),
				(isTherePendingStats() ? " *" : ""));
	}
}
