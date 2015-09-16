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
 * Created on 26/10/2004
 */
package br.com.auster.common.util;

import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;


/**
 * @author Ricardo Barone
 * @version $Id: ConfigUtils.java 91 2005-04-07 21:13:55Z framos $
 */
public class ConfigUtils
{ // The XML config constants

   public static final String LOG4J_NAMESPACE_URI = "http://jakarta.apache.org/log4j/";
   public static final String CONFIGURATION_ELEMENT = "configuration";

   /**
    * Configures the Log4J.
    * 
    * @param root
    *            the DOM tree root that contains all the configuration,
    *            including the Log4J configuration. The element
    *            "log4j:configuration" will be searched as the root of the
    *            Log4J configuration (must be the root itself or one of it's
    *            childs - this method is NOT recursive).
    * @throws IllegalArgumentException
    *             if could not find a configuration for the Log4J.
    */
   public static void configureLog4J(Element root) throws IllegalArgumentException
   {

      Element log4jConfig;
      if(DOMUtils.verifyElement(root, LOG4J_NAMESPACE_URI, CONFIGURATION_ELEMENT))
      {
         log4jConfig = root;
      }
      else
      {
         log4jConfig = DOMUtils.getElement(root,
                                           LOG4J_NAMESPACE_URI,
                                           CONFIGURATION_ELEMENT,
                                           true);
      }

      // configures the log4J library
      org.apache.log4j.xml.DOMConfigurator.configure(log4jConfig);
   }

}