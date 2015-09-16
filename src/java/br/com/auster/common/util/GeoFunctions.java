/*
 * Copyright (c) 2004 Auster Solutions do Brasil LTDA. All Rights Reserved.
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
 * Created on 04/11/2004
 */
package br.com.auster.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class holds a series of methods to be used by XSLT during invoice processing
 * It aplies only to invoice, that is why it is here and not in commons package.
 * 
 * @author Marcos Tengelmann
 * 04/11/2004
 * @version $Id: GeoFunctions.java 91 2005-04-07 21:13:55Z framos $
 */
public class GeoFunctions {
	/***
	 * This class represents a Geographical Information aboput a Country..
	 * It acts as a placeholder for state information behaving like a Java Bean
	 * 
	 * @author Marcos Tengelmann
	 * 04/11/2004
	 * @version $Id: GeoFunctions.java 91 2005-04-07 21:13:55Z framos $
	 */
	protected class StateInfo {
		private String stateName;
		private String stateCode;
		/***
		 * Empty Constructor
		 *
		 */
		protected StateInfo() {			
		}
		
		/***
		 * Convenience Constructor.
		 * @param code The State Code
		 * @param name The State Name
		 */
		protected StateInfo(String code, String name) {
			this.setStateCode(code);
			this.setStateName(name);
		}		
		/**
		 * @return Returns the stateCode.
		 */
		protected String getStateCode() {
			return stateCode;
		}
		/**
		 * @param stateCode The stateCode to set.
		 */
		protected void setStateCode(String stateCode) {
			this.stateCode = stateCode;
		}
		/**
		 * @return Returns the stateName.
		 */
		protected String getStateName() {
			return stateName;
		}
		/**
		 * @param stateName The stateName to set.
		 */
		protected void setStateName(String stateName) {
			this.stateName = stateName;
		}
	}
	
	//#############################################
	//Starts GeoFunctions Implementation
	//#############################################	
	private Logger log = Logger.getLogger(this.getClass());
   
	protected static Map references = Collections.synchronizedMap(new HashMap());
	
	protected static final List brList = Collections.synchronizedList(new ArrayList());


	/**
	 * Empty Constructor
	 */
	public GeoFunctions() {
		super();
		createBrasilianStates();		
	}

	private void createBrasilianStates() {	
      log.debug("***ENTRY.GeoFunctions.createBrasilianStates");
		brList.add( new StateInfo("BA","Bahia"));
		brList.add( new StateInfo("AL","Alagoas"));
		brList.add( new StateInfo("SE","Sergipe"));
		brList.add( new StateInfo("PE","Pernambuco"));
		brList.add( new StateInfo("PB","Paraíba"));
		brList.add( new StateInfo("RN","Rio Grande do Norte"));
		brList.add( new StateInfo("CE","Ceará"));		
		brList.add( new StateInfo("PI","Piauí"));
		brList.add( new StateInfo("MA","Maranhão"));
		brList.add( new StateInfo("PR","Paraná"));
		brList.add( new StateInfo("SC","Santa Catarina"));
		brList.add( new StateInfo("RS","Rio Grande do Sul"));
		brList.add( new StateInfo("SP","São Paulo"));
		brList.add( new StateInfo("RJ","Rio de Janeiro"));
		brList.add( new StateInfo("ES","Espirito Santo"));
		brList.add( new StateInfo("MG","Minas Gerais"));
		brList.add( new StateInfo("MS","Mato Grosso do Sul"));
		brList.add( new StateInfo("TO","Tocantins"));
		brList.add( new StateInfo("GO","Goiás"));
		brList.add( new StateInfo("DF","Distrito Federal"));
		brList.add( new StateInfo("MT","Mato Grosso"));
		brList.add( new StateInfo("PA","Pará"));
		brList.add( new StateInfo("AM","Amazônia"));
		brList.add( new StateInfo("AC","Acre"));
		brList.add( new StateInfo("RO","Rondônia"));
		brList.add( new StateInfo("RR","Roraima"));
		brList.add( new StateInfo("AP","Amapá"));
		GeoFunctions.references.put("BR",brList);
      log.debug("***Exit.GeoFunctions.createBrasilianStates");
	}
	
	/***
	 * Returns if it is a supported Country.
	 * @param countryCode. String with 2-Digit(UpperCase) countryCode
	 * @return true if it supported.
	 */
	public boolean isCountrySupported (String countryCode) {
      log.debug("***ENTRY/EXIT.GeoFunctions.isCountrySupported");
		return GeoFunctions.references.containsKey(countryCode);
	}
	
	/***
	 * Returns if the state is a valid state for a given country.
	 * If the country is not supported this method returns false.
	 * 
	 * @param countryCode 2-Digit upercase country code
	 * @param state The state code of country code
	 * @return true if state is a valid state of the country.
	 */
	public boolean isValidState(String countryCode,String state) {
      log.debug("***Entry.GeoFunctions.isValidState");      
		if (!this.isCountrySupported(countryCode)) {
         log.debug("***Exit.GeoFunctions.isValidState");
         return false;
		}
      boolean returnValue = false;
		List list =  (List) GeoFunctions.references.get(countryCode);
      synchronized (list) {
         Iterator itr = list.iterator();
         while(itr.hasNext()) {
            StateInfo si = (StateInfo) itr.next();
            if (si.getStateCode().equals(state)) {
               log.debug("***Exit.GeoFunctions.isValidState");
               returnValue =  true;
               break;
            }
         }
      }

      log.debug("***Exit.GeoFunctions.isValidState");
		return returnValue;
	}	

}
