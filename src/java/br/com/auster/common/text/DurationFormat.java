/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on Aug 5, 2005
 */
package br.com.auster.common.text;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * <b>Title:</b> DurationFormat
 * </p>
 * <p>
 * <b>Description:</b> TODO class description
 * </p>
 * <p>
 * <b>Copyright:</b> Copyright (c) 2004-2005
 * </p>
 * <p>
 * <b>Company:</b> Auster Solutions
 * </p>
 * 
 * @author rbarone
 * @version $Id: DurationFormat.java 434 2008-06-30 16:06:03Z aleite $
 */
public class DurationFormat {

	/**
	 * Formats the specified duration in seconds to the respective numbers of
	 * hours, minutes and seconds.
	 * 
	 * <p>
	 * The resulting String will be in the form:
	 * <code>hour + suffix + minute + suffix + second + suffix</code>
	 * 
	 * @param durationInSeconds
	 *            The duration that will be formatted - must represent a number
	 *            of seconds.
	 * @param hourSuffix
	 *            The String that will be appended to the hour part.
	 * @param minuteSuffix
	 *            The String that will be appended to the minute part.
	 * @param secondSuffix
	 *            The String that will be appended to the second part.
	 * @return the formatted time in hours, minutes and seconds.
	 */
	public static String formatFromSeconds(double durationInSeconds,
			String hourSuffix, String minuteSuffix, String secondSuffix) {

		if (!isValid(durationInSeconds)) {
			return null;
		}

		int[] time = getHMS(durationInSeconds);

		StringBuilder result = new StringBuilder();
		format00(time[0], result).append(hourSuffix);
		format00(time[1], result).append(minuteSuffix);
		format00(time[2], result).append(secondSuffix);
		return result.toString();
	}

	public static String formatFromMinute(String durationInMinute) {
		if (!isValid(durationInMinute))
			return durationInMinute;

		Double duration = Double
				.parseDouble(durationInMinute.replace(",", ".")) * 60;

		return formatFromSeconds(duration, "h", "m", "s");
	}

	public static String formatFromBytes(String durationInBytes) {

		if (!isValid(durationInBytes) || !durationInBytes.contains("bytes")) {
			return durationInBytes;
		}

		Double tmp = Double.parseDouble(durationInBytes.substring(0,
				durationInBytes.length() - 6));

		tmp = (tmp / 1000);

		return formatFromSeconds(tmp.intValue() * 60);
	}

	/**
	 * Formats the specified duration in seconds to the respective numbers of
	 * minutes and seconds.
	 * <p>
	 * The <code>rollOver</code> attribute tells if the number of hours from
	 * <code>durationInSeconds</code> should be added (set as
	 * <code>true</code>) to the number of minutes, or not. In other words, a
	 * value of 3601 (which is 01:00:01) would be formatted as
	 * <code>60:01</code> with <code>rollOver</code> set to
	 * <code>true</code>, and <code>00:01</code> otherwise.
	 * 
	 * <p>
	 * The resulting String will be in the form:
	 * <code>minute + suffix + second + suffix</code>
	 * 
	 * @param durationInSeconds
	 *            The duration that will be formatted - must represent a number
	 *            of seconds.
	 * @param minuteSuffix
	 *            The String that will be appended to the minute part.
	 * @param secondSuffix
	 *            The String that will be appended to the second part.
	 * @param rollOver
	 * @return the formatted time in hours, minutes and seconds.
	 */
	public static String formatFromSeconds(double durationInSeconds,
			String minuteSuffix, String secondSuffix, boolean rollOver) {

		if (!isValid(durationInSeconds)) {
			return null;
		}

		int[] time = getHMS(durationInSeconds);

		StringBuilder result = new StringBuilder();
		// if rollOver is true, then increment 60x the number of hours.
		if (rollOver) {
			time[1] += (time[0] * 60);
		}
		format00(time[1], result).append(minuteSuffix);
		format00(time[2], result).append(secondSuffix);
		return result.toString();
	}

	/**
	 * This is a short cut to
	 * {@link #formatFromSeconds(double, String, String, boolean)} with the
	 * <code>rollOver</code> attribute set as true.
	 */
	public static String formatFromSeconds(double durationInSeconds,
			String minuteSuffix, String secondSuffix) {

		if (!isValid(durationInSeconds)) {
			return null;
		} else {
			return formatFromSeconds(durationInSeconds, minuteSuffix,
					secondSuffix, true);
		}
	}

	/**
	 * Formats the specified duration in seconds to the respective numbers of
	 * hours, minutes and seconds.
	 * 
	 * <p>
	 * The resulting String will be in the form:
	 * <code>hour + 'h' + minute + 'm' + second + 's'</code>
	 * 
	 * @param durationInSeconds
	 *            The duration that will be formatted - must represent a number
	 *            of seconds.
	 * @return the formatted time in hours, minutes and seconds.
	 */
	public static String formatFromSeconds(double durationInSeconds) {
		if (!isValid(durationInSeconds)) {
			return null;
		} else {
			return formatFromSeconds(durationInSeconds, "h", "m", "s");
		}
	}

	/**
	 * Formats the specified Date to the respective numbers of hours, minutes
	 * and seconds. Only the Time of the class Date is used (hours, minutes and
	 * seconds). Year and day are not used.
	 * 
	 * <p>
	 * The resulting String will be in the form:
	 * <code>hour + 'h' + minute + 'm' + second + 's'</code>
	 * 
	 * @param Date
	 *            The Date that will be formatted.
	 * @return the formatted time in hours, minutes and seconds.
	 */
	public static String formatFromDate(Date date) {
		if (!isValid(date)) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return formatFromSeconds(cal.get(Calendar.HOUR_OF_DAY) * 3600
				+ cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND),
				"h", "m", "s");
	}

	/**
	 * Converts a duration in seconds to an array contating the respective
	 * number of hours, minutes and seconds.
	 * 
	 * @param durationInSeconds
	 *            The duration that will be formatted - must represent a number
	 *            of seconds.
	 * @return a three-element array containg the number of hours, minutes and
	 *         seconds converted from the specified duration.
	 */
	public static int[] getHMS(double durationInSeconds) {
		if (!isValid(durationInSeconds)) {
			return null;
		}

		double temp = durationInSeconds % 3600;
		int[] result = new int[3];
		result[0] = (int) (durationInSeconds / 3600);
		result[1] = (int) (temp / 60);
		result[2] = (int) (temp - (result[1] * 60));
		return result;
	}

	protected static StringBuilder format00(int value, StringBuilder buffer) {
		if (!isValid(value)) {
			return null;
		}

		if (value < 10) {
			buffer.append('0');
		}
		return buffer.append(value);
	}

	private static final boolean isValid(String value) {
		if (value != null && !value.equals(null) && !value.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private static final boolean isValid(Number value) {
		if (value != null && !value.equals(null) && !value.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private static final boolean isValid(Date value) {
		if (value != null && !value.equals(null) && !value.equals("")) {
			return true;
		} else {
			return false;
		}
	}

}
