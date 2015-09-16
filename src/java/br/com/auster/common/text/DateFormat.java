/*
 * Copyright (c) 2004 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 13/10/2004
 */
package br.com.auster.common.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import br.com.auster.common.util.I18n;

/**
 * @author Ricardo Barone
 * @version $Id: DateFormat.java 434 2008-06-30 16:06:03Z aleite $
 */
public class DateFormat {
	private static Logger log = Logger.getLogger(DateFormat.class);

	/**
	 * <code>DateFormat</code> instances should NOT be constructed in standard
	 * programming. Instead, the class should be used as
	 * <code>DateFormat.today("dd/mm/yyyy");</code>.
	 */
	protected DateFormat() {
		super();
	}

	/**
	 * This method computes the difference between two dates.
	 * 
	 * @param st_date
	 *            Start date, following st_pattern.
	 * @param en_date
	 *            End date, following en_pattern.
	 * @param st_pattern
	 *            Pattern for the start date, following SimpleDateFormat.
	 * @param en_pattern
	 *            Pattern for the end date, following SimpleDateFormat.
	 * @param out_pattern
	 *            Pattern for start date, following SimpleDateFormat.
	 * 
	 * @return Returns the difference between the two dates, following
	 *         <code>out_pattern</code>, which can be 'y' (year), 'M'
	 *         (month), 'd' (day), 'h'/'H' (hour), 'm' (minute), 's' (second) or
	 *         'S' (milliseconds).
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>out_pattern</code> is not valid.
	 * @throws ParseException
	 *             if could not parse one of the dates.
	 */
	public static final long difference(String st_date, String st_pattern,
			String en_date, String en_pattern, char out_pattern)
			throws IllegalArgumentException, ParseException {

		if (isValid(st_date)) {
			final long difference = new SimpleDateFormat(en_pattern).parse(
					en_date).getTime()
					- new SimpleDateFormat(st_pattern).parse(st_date).getTime();
			return convertFromMilliseconds(difference, out_pattern);
		} else {
			return 0L;
		}
	}

	/**
	 * This method adds an specified (signed) amount of time to a given
	 * date/time. The input date time, the amount of time and the output must
	 * have their patterns passed as parameter. For example, to add 3 days to
	 * the following date 06/24/2003, we would call this method this way:
	 * 
	 * add("06/24/2003", "MMddyyyy", 3, 'd', "dd/MM/yyyy")
	 * 
	 * which will output a date in dd/MM/yyyy format.
	 * 
	 * @param input_date
	 *            Input date, to which we will add the amount of time.
	 * @param pattern
	 *            Input date format, following SimpleDateFormat formats.
	 * @param amount
	 *            Amount of time to be added to the input_date.
	 * @param amount_pattern
	 *            Following SimpleDateFormat formats, specifies the pattern of
	 *            the amount of time.
	 * @param out_pattern
	 *            Output date format.
	 * 
	 * @return Returns the input date added by an specified amount of time.
	 * 
	 * @throws IllegalArgumentException
	 *             if the <code>amount_pattern</code> is not valid.
	 * @throws ParseException
	 *             if the <code>pattern</code> or <code>out_pattern</code>
	 *             are not valid.
	 */
	public static final String add(String input_date, String pattern,
			int amount, char amount_pattern, String out_pattern)
			throws IllegalArgumentException, ParseException {

		if (!isValid(input_date)) {
			return "";
		}

		final Date date = new SimpleDateFormat(pattern).parse(input_date);
		final GregorianCalendar calendar = new GregorianCalendar();
		final int field;

		calendar.setTime(date);

		switch (amount_pattern) {
		case 'y':
			field = Calendar.YEAR;
			break;
		case 'M':
			field = Calendar.MONTH;
			break;
		case 'd':
			field = Calendar.DATE;
			break;
		case 'H':
			// hour in 0-23 format
			field = Calendar.HOUR_OF_DAY;
			break;
		case 'h':
			// hour in 1-12 AM/PM format
			field = Calendar.HOUR;
			break;
		case 'm':
			field = Calendar.MINUTE;
			break;
		case 's':
			field = Calendar.SECOND;
			break;
		case 'S':
			field = Calendar.MILLISECOND;
			break;
		default:
			throw new IllegalArgumentException(I18n.getInstance(
					DateFormat.class).getString("invalidPattern",
					String.valueOf(amount_pattern)));
		}

		calendar.add(field, amount);
		return new SimpleDateFormat(out_pattern).format(calendar.getTime());
	}

	/**
	 * This method applies a specified format to a date string.
	 * 
	 * @param input_date
	 *            Input date that we will reformat.
	 * @param pattern
	 *            Input date format, following SimpleDateFormat formats.
	 * @param out_pattern
	 *            Output date format.
	 * 
	 * @return Returns the reformatted date string.
	 * 
	 * @throws ParseException
	 *             if some pattern is not valid.
	 */
	public static final String format(String input_date, String pattern,
			String out_pattern) throws ParseException {

		if (isValid(input_date)) {
			return new SimpleDateFormat(out_pattern)
					.format(new SimpleDateFormat(pattern).parse(input_date));
		} else {
			return input_date;
		}
	}

	/**
	 * This method applies a specified format to a date string.
	 * 
	 * @param input_date
	 *            Input date that we will reformat.
	 * @param pattern
	 *            Input date format, following SimpleDateFormat formats.
	 * @param out_pattern
	 *            Output date format.
	 * @param tExp
	 *            Indicates if true, to throw an exception in the case
	 * 
	 * @return Returns the reformatted date string.
	 * @throws ParseException
	 * 
	 */
	public static final String format(String input_date, String pattern,
			String out_pattern, boolean tExp) throws ParseException {

		if (!isValid(input_date)) {
			return input_date;
		}

		try {
			String retValue = input_date;
			retValue = new SimpleDateFormat(out_pattern)
					.format(new SimpleDateFormat(pattern).parse(input_date));
		} catch (ParseException e) {
			if (tExp) {
				throw e;
			}
		}
		return input_date;
	}

	public static final String format(String input_date, String pattern,
			String out_pattern, String tExp) throws ParseException {

		if (isValid(input_date))
			return format(input_date, pattern, out_pattern, new Boolean(tExp)
					.booleanValue());
		else
			return input_date;
	}

	/**
	 * This method returns the current date, in the specified format.
	 * 
	 * @param pattern
	 *            Output date format.
	 * @return Returns the current date, in the specified format.
	 * @throws ParseException
	 *             if the pattern is not valid.
	 */
	public static final String today(String pattern) {
		if (isValid(pattern)) {
			return new SimpleDateFormat(pattern).format(new Date());
		} else {
			return "";
		}
	}

	/**
	 * This method converts a date to an amount of time specified by a pattern.
	 * For example, we can convert "00:01:30", that is in "hh:MM:ss" format, to
	 * seconds, or to milliseconds.
	 * 
	 * @param date
	 *            Date that we convert to an amount of time.
	 * @param pattern
	 *            Date format, following SimpleDateFormat formats.
	 * @param out_pattern
	 *            Format of the amount of time to be output.
	 * 
	 * @return Returns the computed amount of time.
	 * 
	 * @throws ParseException
	 *             if the <code>pattern</code> is not valid.
	 * @throws IllegalArgumentException
	 *             if the <code>out_pattern</code> is not valid.
	 */
	public static final long fromDate(String date, String pattern,
			char out_pattern) throws IllegalArgumentException, ParseException {

		if (isValid(date)) {
			final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
			return convertFromMilliseconds(sdf.parse(date).getTime(),
					out_pattern);
		} else {
			return 0L;
		}
	}

	/**
	 * This method converts an amount of time specified by a pattern to a date
	 * specified by a pattern. For example, we can convert 90 seconds to
	 * "00:01:30", which is specified by the "hh:MM:ss" pattern.
	 * 
	 * @param amount
	 *            Amount of time that will be converted.
	 * @param pattern
	 *            Pattern specified what the unit of the amount of time.
	 * @param out_pattern
	 *            Format of the date to be output.
	 * 
	 * @return Returns the string with the computed date.
	 * 
	 * @throws ParseException
	 *             if the <code>pattern</code> is not valid.
	 * @throws IllegalArgumentException
	 *             if the <code>out_pattern</code> is not valid.
	 */
	public static final String toDate(long amount, char pattern,
			String out_pattern) throws IllegalArgumentException, ParseException {

		if (isValid(amount)) {
			final SimpleDateFormat sdf = new SimpleDateFormat(out_pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
			return sdf.format(new Date(convertToMilliseconds(amount, pattern)));
		} else {
			return "";
		}
	}

	/**
	 * monthDays - return the number of days (integer) of a month. The month
	 * doesn´t have to be explicity passed as a parameter, but rather can be
	 * embedded in a date, given that you pass its pattern.
	 * 
	 * @param date
	 *            Date string.
	 * @param pattern
	 *            The date string pattern.
	 * 
	 * @return Returns the number of days of a month.
	 * 
	 * @throws ParseException
	 *             if the <code>pattern</code> is not valid.
	 */
	public static final int monthDays(String date, String pattern)
			throws ParseException {

		if (!isValid(date)) {
			return 0;
		}

		final GregorianCalendar calendar = new GregorianCalendar();

		calendar.setTime(new SimpleDateFormat(pattern).parse(date));

		switch (calendar.get(Calendar.MONTH) + 1) {
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		case 2:
			if (calendar.isLeapYear(calendar.get(Calendar.YEAR)))
				return 29;
			else
				return 28;
		default:
			return 31;
		}
	}

	/**
	 * Auxiliary method to convert an amount of milliseconds to another unit of
	 * time (for example, seconds, hours, etc).
	 * 
	 * @param amount
	 *            Amount of time (in milliseconds) that will be computed to
	 *            another unit of time.
	 * @param pattern
	 *            Unit of the amount of time to compute.
	 * 
	 * @return Returns the amount in the new unit of time.
	 * 
	 * @throws IllegalArgumentException
	 *             if the <code>pattern</code> is not valid.
	 */
	protected static final long convertFromMilliseconds(long amount,
			char pattern) throws IllegalArgumentException {

		if (!isValid(amount)) {
			return 0L;
		}

		double doubleAmount = amount;

		switch (pattern) {
		case 'y':
			doubleAmount = doubleAmount /= 12;
		case 'M':
			doubleAmount /= 30;
		case 'd':
			doubleAmount /= 24;
		case 'h':
		case 'H':
			doubleAmount /= 60;
		case 'm':
			doubleAmount /= 60;
		case 's':
			doubleAmount /= 1000;
		case 'S':
			return Math.round(doubleAmount);
		default:
			throw new IllegalArgumentException(I18n.getInstance(
					DateFormat.class).getString("invalidPattern",
					String.valueOf(pattern)));
		}
	}

	/**
	 * Auxiliary method to convert an amount of time in a particular unit of
	 * time to milliseconds.
	 * 
	 * @param amount
	 *            Amount of time that will be computed to milliseconds.
	 * @param pattern
	 *            Unit of the amount of time that will be converted.
	 * 
	 * @return Returns the amount in milliseconds.
	 * 
	 * @throws IllegalArgumentException
	 *             if the <code>pattern</code> is not valid.
	 */
	protected static final long convertToMilliseconds(long amount, char pattern)
			throws IllegalArgumentException {

		if (!isValid(amount)) {
			return 0L;
		}

		switch (pattern) {
		case 'y':
			amount *= 12;
		case 'M':
			amount *= 30;
		case 'd':
			amount *= 24;
		case 'h':
		case 'H':
			amount *= 60;
		case 'm':
			amount *= 60;
		case 's':
			amount *= 1000;
		case 'S':
			return amount;
		default:
			throw new IllegalArgumentException(I18n.getInstance(
					DateFormat.class).getString("invalidPattern",
					String.valueOf(pattern)));
		}
	}

	/**
	 * Apply pattern format in Date. The result is the String formated.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * Send
	 * <code>
	 * java.util.Date
	 * </code>
	 *  value with &quot;dd/MM/yyyy&quot; pattern format.
	 * The result is &quot;30/04/1978&quot;.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param date
	 *            Date to convert
	 * @param pattern
	 *            Format pattern to output.
	 * @see <code>java.text.SimpleDateFormat</code>
	 * @return Date formated
	 * @author Danilo Oliveira
	 */
	public static String formatDate(Date date, String pattern) {

		if (!isValid(date)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat();
		try {
			sdf.applyPattern(pattern);
			return sdf.format(date);
		} catch (Exception e) {
			return date.toString();
		}
	}

	private static final boolean isValid(String value) {
		if (value != null && !value.equals("") && !value.equals(null)) {
			return true;
		} else {
			return false;
		}
	}

	private static final boolean isValid(Long value) {
		if (value != null && !value.equals(null)) {
			return true;
		} else {
			return false;
		}
	}

	private static final boolean isValid(Date value) {
		if (value != null && !value.equals(null)) {
			return true;
		} else {
			return false;
		}
	}

	public static final String getDay(String value) {
		if (isValid(value) && value.length() >= 8) {
			return value.substring(6, 8);
		} else {
			return value;
		}
	}

	public static final String getMonth(String value) {
		if (isValid(value) && value.length() >= 6) {
			return value.substring(4, 6);
		} else {
			return value;
		}
	}

	public static final String getYear(String value) {
		if (isValid(value) && value.length() >= 4) {
			return value.substring(2, 4);
		} else {
			return value;

		}
	}
}