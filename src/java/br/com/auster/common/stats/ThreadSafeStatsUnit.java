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

public class ThreadSafeStatsUnit extends StatsUnit {

	@Override
	protected synchronized void starting() {
		super.starting();
	}

	@Override
	protected synchronized boolean finished() {
		return super.finished();
	}

	@Override
	protected synchronized StatsUnit startNestedStats(String description) {
		return super.startNestedStats(description);
	}

	@Override
	protected synchronized void copyAndReset(StatsUnit copy) {
		super.copyAndReset(copy);
	}

	@Override
	protected synchronized boolean isReset() {
		return super.isReset();
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}
}
