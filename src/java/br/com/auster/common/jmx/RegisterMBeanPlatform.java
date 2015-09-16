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

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;


/**
 * <p>
 * <b>Title:</b> RegisterMBeanPlatform
 * </p>
 * <p>
 * <b>Description: This class implements a Register Service for DWare.
 * Specifically it implements the Current Platform MBean Server </b>
 * </p>
 * <p>
 * <b>Copyright:</b> Copyright (c) 2004-2005
 * </p>
 * <p>
 * <b>Company:</b> Auster Solutions
 * </p>
 * 
 * @author mtengelm
 * @version $Id: RegisterMBeanPlatform.java 296 2006-08-28 19:24:07Z framos $
 */
public class RegisterMBeanPlatform implements RegisterMBeanHandler {

	private static final Logger	log	= Logger.getLogger(RegisterMBeanPlatform.class);

	/**
	 * Empty Constructor
	 */
	public RegisterMBeanPlatform() {
		super();
	}

	/**
	 * @inheritDoc
	 */
	public void configure(Element config) throws MBeanHandlerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.dware.management.RegisterMBeanHandler#registerMBean()
	 */
	public void registerMBean(AusterMBean mbean) {
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			String obName = AusterManagementServices.getDomain() + mbean.getMBeanName();
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
