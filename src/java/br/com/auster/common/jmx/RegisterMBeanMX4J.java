/*
 * Copyright (c) 2004-2005 Auster Solutions. All Rights Reserved.
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
 * Created on 16/06/2005
 */
package br.com.auster.common.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
/**
 * <p><b>Title:</b> RegisterMBeanMX4J</p>
 * <p><b>Description:
 * This class implements a Register Service for DWare. 
 * Specifically it implements the Current Platform MBean Server
 * </b> </p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author mtengelm
 * @version $Id: RegisterMBeanMX4J.java 296 2006-08-28 19:24:07Z framos $
 */
public class RegisterMBeanMX4J implements RegisterMBeanHandler {
	/**
	 * Configuration Element Name for register services
	 */
	private static final String      REGISTER_ELEMENT                 = "register";
	
	/**
	 * Class name attribute name for register services class
	 */
	private static final String      CLASS_NAME_ATTR                  = "class-name";
	
	/**
	 * RMI Registry IP address
	 */
	private static final String      IP_ADDRESS                       = "ip-address";
	private static final String      TCP_PORT                         = "tcp-port";
	private static final String      JNDI_NAME                        = "jndi-name";
	
	private static final Logger log = Logger.getLogger(RegisterMBeanMX4J.class);
	private MBeanServer server;
	
	/**
	 * Empty Constructor
	 */
	public RegisterMBeanMX4J() {
		super();
	}
	
	/**
	 * @inheritDoc
	 */
	public void configure(Element config) throws MBeanHandlerException {
		Element regElement = (config.getLocalName().equals(REGISTER_ELEMENT)) ?
				config :
				DOMUtils.getElement(config, REGISTER_ELEMENT, true);
		String ipAddress = DOMUtils.getAttribute(regElement, IP_ADDRESS, true);
		String tcpPort   = DOMUtils.getAttribute(regElement, TCP_PORT, true);
		String jndiName  = DOMUtils.getAttribute(regElement, JNDI_NAME, true);
		
		server = MBeanServerFactory.createMBeanServer();
		
		// Register and start the rmiregistry MBean, needed by JSR 160 RMIConnectorServer
		ObjectName namingName;
		try {
			namingName = ObjectName.getInstance("naming:type=rmiregistry");
			log.debug("Naming object=" + namingName + " Class=" + namingName.getClass());
			server.createMBean("mx4j.tools.naming.NamingService", namingName, null, 
					new Object[] {new Integer(tcpPort)}, new String[] {"int"});
			server.invoke(namingName, "start", null, null);
			
			int namingPort = ((Integer)server.getAttribute(namingName, "Port")).intValue();
			
			String jndiPath = "/"+jndiName;
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ipAddress+":"+namingPort+jndiPath);
			log.debug("MBean Server RMI Desired URL=" + url);
			
			// Create and start the RMIConnectorServer
			JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
			connectorServer.start();   
			System.out.println("MBan server RMI Current URL=" + connectorServer.getAddress());
			for (Iterator itr = connectorServer.getAttributes().entrySet().iterator();itr.hasNext();){            
				Map.Entry map = (Map.Entry) itr.next();
				log.debug("Key=[" + map.getKey() + "] Value=[" + map.getValue() + "]");
			}
			
		} catch (MalformedObjectNameException e) {
			log.error("Error creating MBean", e);
			throw new MBeanHandlerException("Error creating MBean", e);
		} catch (InstanceAlreadyExistsException e) {
			log.error("Error creating MBean", e);
			throw new MBeanHandlerException("Error creating MBean", e);
		} catch (MBeanRegistrationException e) {
			log.error("Error registering MBean", e);
			throw new MBeanHandlerException("Error registering MBean", e);
		} catch (NotCompliantMBeanException e) {
			log.error("Error creating MBean", e);
			throw new MBeanHandlerException("Error creating MBean", e);
		} catch (InstanceNotFoundException e) {
			log.error("Error creating MBean Server", e);
			throw new MBeanHandlerException("Error creating MBean", e);
		} catch (ReflectionException e) {
			log.error("Error creating MBean Server", e);
			throw new MBeanHandlerException("Error creating MBean", e);
		} catch (MBeanException e) {
			log.error("Error creating MBean Server", e);
			throw new MBeanHandlerException("Error creating MBean", e);
		} catch (AttributeNotFoundException e) {
			log.error("Error getting MBean Server Connection Port", e);
			throw new MBeanHandlerException("Error getting MBean Server Connection Port", e);
		} catch (MalformedURLException e) {
			log.error("Error creating URL", e);
			throw new MBeanHandlerException("Error creating URL", e);
		} catch (IllegalStateException e) {
			log.error("Error Starting MBean Server", e);
			throw new MBeanHandlerException("Error starting MBean Server", e);
		} catch (IOException e) {
			log.error("Error Creating JMXConnector to MBean Server", e);
			throw new MBeanHandlerException("Error creating JMXConnector to MBean Server", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see br.com.auster.dware.management.RegisterMBeanHandler#registerMBean()
	 */
	public void registerMBean(AusterMBean mbean) {
		try {
			String obName = AusterManagementServices.getDomain()+mbean.getMBeanName();
			ObjectName name = new ObjectName(obName);
			log.info("registering mbean named " + obName);
			server.registerMBean(mbean, name);
		} catch (MalformedObjectNameException e) {
			log.error("Error creating mbean name", e);
		} catch (NullPointerException e) {
			log.error("Error creating mbean and registering mbean", e);
		} catch (InstanceAlreadyExistsException e) {
			log.error("Error creating mbean and registering mbean", e);
		} catch (MBeanRegistrationException e) {
			log.error("Error creating mbean and registering mbean", e);
		} catch (NotCompliantMBeanException e) {
			log.error("Error creating mbean and registering mbean", e);
		}
	}
	
}
