/*
 *
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
 * Created on Apr 11, 2008
 *
 * @(#)LevelAudit.java Apr 11, 2008
 */
package br.com.auster.common.log;

import org.apache.log4j.Level;

/**
 * The class <code>LevelAudit</code> it is responsible in create the new Level for Audit
 * @author Danilo Oliveira
 * @version $Id$
 * @since JDK 5.0
 */
public class LevelAudit extends Level {

	static public final int AUDIT_INT = Level.ERROR_INT + 5000;

	private static String AUDIT_STR = "AUDIT";

	/**
	 * The <code>AUDIT</code> Level designates finer-grained
	 * informational events than the <code>AUDIT</code level.
	 */
	public static final LevelAudit AUDIT = new LevelAudit(AUDIT_INT, AUDIT_STR, 0);

	protected LevelAudit(int level, String strLevel, int syslogEquiv) {
		super(level, strLevel, syslogEquiv);
	}

	/**
	 * Convert the string passed as argument to a level. If the
	 * conversion fails, then this method returns {@link #TRACE}. 
	 */
	public static Level toLevel(String sArg) {
		return (Level) toLevel(sArg, LevelAudit.AUDIT);
	}

	/**
	 * Convert the string passed as argument to a level. If the
	 * conversion fails, then this method returns {@link #TRACE}. 
	 */
	public static Level toLevel(String sArg, Level defaultValue) {
		if(sArg == null) {
			return defaultValue;
		}
		String stringVal = sArg.toUpperCase();
		if(stringVal.equals(AUDIT_STR)) {
			return LevelAudit.AUDIT;
		}
		return Level.toLevel(sArg, (Level) defaultValue);
	}

	public static Level toLevel(int i) throws  IllegalArgumentException {
		if (i == AUDIT_INT) {
			return LevelAudit.AUDIT;
		}
		return Level.toLevel(i);
	}
}