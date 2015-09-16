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
 * Created on Jan 24, 2005
 */
package br.com.auster.common.log;

import java.io.File;
import java.util.Properties;
import java.util.Enumeration;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Element;

import br.com.auster.common.util.ConfigUtils;

/**
 * <p><b>Title:</b> LogFactory</p>
 * <p><b>Description:</b> Helper class to configure log subsystem. Still needs to add some utility 
 * methods to this class to attend different scenarios </p>
 * <p><b>Copyright:</b> Copyright (c) 2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: LogFactory.java 103 2005-05-17 21:38:10Z etirelli $
 */
public class LogFactory {
	private static boolean configured = false;
	
	protected LogFactory() {
	}
	
	public static Logger getLogger(Class c) {
		if(!configured) {
                        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
                        if(!appenders.hasMoreElements()) {
				LogFactory.configureLogSystem();
			} else {
				setConfigured();
			}
		}
		return Logger.getLogger(c);
	}

	protected static synchronized void setConfigured() {
		configured = true;
	}
	
	public static synchronized void configureLogSystem(String configFile) {
    if(!configured) {
      if((configFile != null) && ((new File(configFile)).exists())) {
        PropertyConfigurator.configure(configFile);   
      } else {
        BasicConfigurator.configure();
      }
				setConfigured();
    }
	}
	
	public static synchronized void configureLogSystem(Properties properties) {
    if(!configured) {
	  	PropertyConfigurator.configure(properties);		
				setConfigured();
    }
	}
	
	/***
	 * Configures the Log Subsystem thru a XML configuration file supported by Log4j
	 * @param config A DOM Element holding the configuration
	 */
	public static synchronized void configureLogSystem(Element config) {
    if(!configured) {
	    ConfigUtils.configureLog4J(config);
				setConfigured();
    }
	}
	
	protected static synchronized void configureLogSystem() {
    if(!configured) {
      BasicConfigurator.configure();
				setConfigured();
    }
	}

}
