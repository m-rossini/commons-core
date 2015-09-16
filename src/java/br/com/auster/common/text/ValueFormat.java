package br.com.auster.common.text;

import java.text.DecimalFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

public class ValueFormat {
	private static Logger log = Logger.getLogger(ValueFormat.class);
	
	private static DecimalFormat df = new DecimalFormat("0.000000");
	private static final Locale l = new Locale("pt", "BR", "");

	public static final String formatValue(String value) {
		if (isValid(value)) {
			return String.format(l, "%,.2f", Double.parseDouble(value));
		} else {
			return value;
		}
	}

	public static final String formatValue(Number value) {
		if (isValid(value)) {
			return formatValue(String.valueOf(value.doubleValue()));
		} else {
			return null;
		}
	}

	private static final String replacePoint(String value) {
		return value.replace(".", ",");
	}

	public static final String formatPoint(Number value) {
		if (isValid(value)) {
			return formatPoint(String.valueOf(value.doubleValue()));
		} else {
			return null;
		}
	}

	public static final String formatPoint(String value) {
		if (isValid(value)) {
			return replaceComma(String.format(l, "%,.2f", Double
					.parseDouble(value)));
		} else {
			return value;
		}
	}

	private static final String replaceComma(String value) {
		return value.replace(",", ".");
	}

	public static final String formatQuantity(Number value) {
		return String.valueOf(value.intValue());
	}

	public static final String formatQuantity(String value) {
		if (isValid(value)) {
			Double d = Double.parseDouble(replacePoint(value));
			return String.valueOf(d.intValue());
		} else {
			return null;
		}
	}

	public static final String formatAliquota(Number value) {
		if(isValid(value)){
			return replaceComma(df.format(value));
		} else {
			return null;
		}
	}

	public static final String formatAliquota(String value) {
		if(isValid(value)){
			return formatAliquota(Double.parseDouble(replaceComma(value)));
		} else {
			return value;
		}
	}

	public static final String formatPhone(int phone) {
		return formatPhone(String.valueOf(phone));
	}

	public static final String formatPhone(String phone) {
		if (isValid(phone) && phone.length() >= 10) {
			StringBuffer s = new StringBuffer("");
			s.append(phone.subSequence(0, 2) + "-");
			s.append(phone.subSequence(2, 6) + "-");
			s.append(phone.subSequence(6, 10));
			return s.toString();
		} else {
			return phone;
		}
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
}
