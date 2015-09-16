/*
/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on Oct 14, 2005
 */
package br.com.auster.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles commons services bar code, in numeric representation. It parses the code, and 
 *  checks for invalid verification digits in all fields, as well as invalid values for product id,  
 *  segment id and value indicator. Helper methods to retrieve each of those general fields are also 
 *  available.
 *   
 * 
 * @author framos
 * @version $Id$
 */
public abstract class CommonsServicesBarCode {

    // total length of the numeric representation of a bar code
    protected static final int COMMONSERVICES_BARCODE_LEN = 48;
    
    // position of each general field
    protected static final int PRODUCT_ID_POSITION = 0;
    protected static final int SEGMENT_ID_POSITION = 1;
    protected static final int VALUE_MODE_POSITION = 2;
    protected static final int DAC_MOD10_POSITION  = 3;
    
    protected static final int VALUE_START_POSITION  = 4;
    protected static final int VALUE_END_POSITION  = 15;
    
    // position of each field DV 
    protected static final int FIELD_1_DV = 11;
    protected static final int FIELD_2_DV = 23;
    protected static final int FIELD_3_DV = 35;
    protected static final int FIELD_4_DV = 47;
    
    // each field´s DV, and the total DV are discarded
    protected static final int[] MOD10_MULTIPLIER = {
        2, 1, 2, 0, 1, 2, 1, 2, 1, 2, 1, 0, 
        2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0, 
        1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 0, 
        2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0
    };
    
    // list of possible segment ids
    public static final String CITYHALL_SEGMENT_CODE               = "1";
    public static final String SANITATION_SEGMENT_CODE             = "2";
    public static final String ENERGY_SEGMENT_CODE                 = "3";
    public static final String TELECOM_SEGMENT_CODE                = "4";
    public static final String GOVERNMENT_INSTITUTION_SEGMENT_CODE = "5";
    public static final String PRIVATE_COMPANY_SEGMENT_CODE        = "6";
    public static final String TRAFFIC_TICKET_SEGMENT_CODE         = "7";
    public static final String BANK_INTERNAL_USAGE_SEGMENT_CODE    = "8";

    public static final String CURRENCY_VALUE_INDICATOR            = "6";
    public static final String ANOTHER_MONETARY_INDEX_INDICATOR    = "7";
	
	
    // Encoding representation of each digit
    protected static final String[] NUMERIC_ENCODING = { 
        /* 0 */ "NNWWN",
        /* 1 */ "WNNNW",
        /* 2 */ "NWNNW",
        /* 3 */ "WWNNN",
        /* 4 */ "NNWNW",
        /* 5 */ "WNWNN",
        /* 6 */ "NWWNN",
        /* 7 */ "NNNWW",
        /* 8 */ "WNNWN",
        /* 9 */ "NWNWN" };
    
    // Encoding for interleaving digits 
    protected static final Map INTERLEAVED_ENCODING = new HashMap();    
    static {
        INTERLEAVED_ENCODING.put("NN", "n");
        INTERLEAVED_ENCODING.put("WN", "w");
        INTERLEAVED_ENCODING.put("NW", "N");
        INTERLEAVED_ENCODING.put("WW", "W");
    }    

    

	public static boolean validateBarCodeLength(String _code) {
        return ((_code != null) && (_code.length() == COMMONSERVICES_BARCODE_LEN));
	}
    
    /**
     * Returns the product id, in integer format
     */
    public static String getProductId(String _code) {
		if (! validateBarCodeLength(_code)) { return null; }
        return Character.toString(_code.charAt(PRODUCT_ID_POSITION));
    }
    
    /**
     * Returns the segment id, in integer format
     */
    public static String getSegmentId(String _code) {
		if (! validateBarCodeLength(_code)) { return null; }
        return Character.toString(_code.charAt(SEGMENT_ID_POSITION));
    }
    
    /**
     * Returns the value indicator, in integer format
     */
   public static String getValueIndicatorCode(String _code) {
	   if (! validateBarCodeLength(_code)) { return null; }
       return Character.toString(_code.charAt(VALUE_MODE_POSITION));
    }
    
   /**
    * Returns the verification digit, in integer format
    */
    public static String getVerificationDigit(String _code) {
		if (! validateBarCodeLength(_code)) { return null; }
        return Character.toString(_code.charAt(DAC_MOD10_POSITION));
    }
    
    /**
     * Returns the value, in String format
     */
    public static String getValue(String _code) {
		if (! validateBarCodeLength(_code)) { return null; }
        return _code.substring(VALUE_START_POSITION, FIELD_1_DV) + 
               _code.substring(FIELD_1_DV+1, VALUE_END_POSITION+1);
    }

    /**
     * Returns the value, in double format
     * @return
     */
    public static double getValueAsDouble(String _code) {
		if (! validateBarCodeLength(_code)) { return 0D; }
        String vlr = getValue(_code);
        double dbvalue = Double.parseDouble(vlr);
        return (dbvalue/100);
    }
    
    /**
     * Returns the rest of the bar code, also known as custom field, in String format
     */
    public static String getCustomField(String _code) {
		if (! validateBarCodeLength(_code)) { return null; }
        return _code.substring(VALUE_END_POSITION+1, FIELD_2_DV) +
			   _code.substring(FIELD_2_DV+1, FIELD_3_DV) +
			   _code.substring(FIELD_3_DV+1, FIELD_4_DV);
    }

    
    /**
     * Returns the entire bar code if parameter is <code>true</code>. Otherwise, the verification digits of each
     *  one of the four fields are stripped from the returning bar code numeric representation.
     *  
     * @param _withDVs if verification digits should be left in the returning value 
     */
    public static String getBarCodeWithoutDVs(String _code) {
		if (! validateBarCodeLength(_code)) { return null; }
        return _code.substring(0, FIELD_1_DV) +
			   _code.substring(FIELD_1_DV+1, FIELD_2_DV) +
			   _code.substring(FIELD_2_DV+1, FIELD_3_DV) +
			   _code.substring(FIELD_3_DV+1, FIELD_4_DV);
    }

    /**
     * Creates an enconded representation of the bar code, according to the <code>2of5 interleaved</code> specification, where
     *  each pair of digits are encoded in a set of 5 bars and spaces, wide or narrow. Plus, it shrinks the resulting String 
     *  using the first digit representation to define which letter will be used (N or W), and the second digit representation to
     *  define if those letters should appear in lower or upper case. Because of this mapping mechanism, the final String is
     *  two times shorter than a unchanged <code>2of5 interleaved</code> representation of the same bar code.  
     * </p>  
     *  
     * @return the shrinked <code>2of5 interleaved</code> representation of this bar code
     */
    public static String encode(String _code)   {
        String codeWithoutDVs = getBarCodeWithoutDVs(_code);
		if (codeWithoutDVs == null) { return null; }
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < codeWithoutDVs.length(); i += 2) {
            String char1 = NUMERIC_ENCODING[Character.getNumericValue(codeWithoutDVs.charAt(i))];
            String char2 = NUMERIC_ENCODING[Character.getNumericValue(codeWithoutDVs.charAt(i+1))];
            for (int j=0; j < 5; j++) {
                String key = char1.substring(j,j+1) + char2.substring(j,j+1);
                String encodedPair = (String) INTERLEAVED_ENCODING.get(key);
                stringbuffer.append(encodedPair);
            }
        }
        return stringbuffer.toString();
    }
    
    /**
     * Checks if the specified encoded representation of a bar code matches the one 
     *  calculated for the current bar code holded by this instance.
     *   
     * @param _encodedBarCode the bar code to match
     * @param _barCode the bar code in numeric format
     * 
     * @return if the current bar code matches the one, encoded in the <code>_encodedBarCode</code> parameter
     */
    public static boolean validateEncoding(String _encodedBarCode, String _barcode) {
        return ((_encodedBarCode != null) && _encodedBarCode.equals(encode(_barcode)));
    }

    /**
     * Basic validation rules for a new bar code. If any of the conditions does not match, than an exception
     *  will be raised. 
     */
    public static boolean validateBarCode(String _code) {
		// checking length
		if (! validateBarCodeLength(_code)) { 
			return false; 
		}
        // checking product id 
        if (Character.getNumericValue(_code.charAt(PRODUCT_ID_POSITION)) != 8) {
            return false;
        }
        // checking possible values for segment id
        if ((Character.getNumericValue(_code.charAt(SEGMENT_ID_POSITION)) < Character.getNumericValue(CITYHALL_SEGMENT_CODE.charAt(0))) ||
            (Character.getNumericValue(_code.charAt(SEGMENT_ID_POSITION)) > Character.getNumericValue(BANK_INTERNAL_USAGE_SEGMENT_CODE.charAt(0)))) {
			return false;
        }
        // checking possible values for value indicator
        if ((!CURRENCY_VALUE_INDICATOR.equals((Character.toString(_code.charAt(VALUE_MODE_POSITION))))) &&
            (!ANOTHER_MONETARY_INDEX_INDICATOR.equals(Character.toString(_code.charAt(VALUE_MODE_POSITION))))) {
			return false;
        }
        // checking DV for entire bar code
        if (calculateMOD10(_code) != Character.getNumericValue(_code.charAt(DAC_MOD10_POSITION))) {
			return false;
        }
        // checking DV for each field
        if (calculateMOD10(_code.substring(FIELD_3_DV+1)) != Character.getNumericValue(_code.charAt(FIELD_4_DV))) {
			return false;
        }
        if (calculateMOD10(_code.substring(FIELD_2_DV+1, FIELD_3_DV+1)) != Character.getNumericValue(_code.charAt(FIELD_3_DV))) {
			return false;
        }
        if (calculateMOD10(_code.substring(FIELD_1_DV+1, FIELD_2_DV+1)) != Character.getNumericValue(_code.charAt(FIELD_2_DV))) {
			return false;
        }
        if (calculateMOD10(_code.substring(0, FIELD_1_DV+1)) != Character.getNumericValue(_code.charAt(FIELD_1_DV))) {
			return false;
        }
		return true;
    }    
    
    private static int calculateMOD10(String _code) {
        int dv = 0;
        for (int i=_code.length()-1, pos=COMMONSERVICES_BARCODE_LEN-1; i >= 0; i--, pos--) {
            char digit = _code.charAt(i);
            if (!Character.isDigit(digit)) {
                throw new IllegalArgumentException("Bar code must contain only digits");
            }
            int mod10 = Integer.parseInt(digit+"") * MOD10_MULTIPLIER[pos];
            dv += (mod10%10) + (mod10/10);
        }
        int dac = (10 - (dv%10));
        // returning the remaining of div.10, so that when dac == 10, it will return 0, as 
        // expected
        return dac%10;
    }
    
}
