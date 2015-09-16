/*
* Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
* Created on 21/07/2006
*/


package br.com.auster.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.auster.common.text.DateFormat;

/**
 * @author gmatias
 * @version $Id$
 */
public class DateUtils extends DateFormat {

	

	
	public static final long difference(Date _start, Date _end, char _outPattern) 
	          throws IllegalArgumentException, ParseException {
		
		long difference = _end.getTime() - _start.getTime();
		return DateFormat.convertFromMilliseconds(difference, _outPattern);
	}
		
	public static final int monthDays(Date date) {
		
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
	  
		switch(calendar.get(Calendar.MONTH) + 1) {
	    	case 4:
	    	case 6:
	    	case 9:
	    	case 11:
	    		return 30;
	    	case 2:
	    		if(calendar.isLeapYear(calendar.get(Calendar.YEAR)))
	    			return 29;
	    		else
	    			return 28;
	    	default:
	    		return 31;
		}
	}
	
}
