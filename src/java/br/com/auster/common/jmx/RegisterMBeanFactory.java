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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * <p><b>Title:</b> RegisterMBeanFactory</p>
 * <p><b>Description:
 * This class provides a flexible way to instantiate different classes (One per Execution)
 * to provide MBean registration services
 * </b> </p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author mtengelm
 * @version $Id: RegisterMBeanFactory.java 296 2006-08-28 19:24:07Z framos $
 */
public class RegisterMBeanFactory {
	private static Logger log = LogFactory.getLogger(RegisterMBeanFactory.class);
	
	/**
	 * Default Monitoring Class Name. It is the Platform
	 */
	private static final String      DEFAULT_REGISTER                 = "br.com.auster.common.jmx.RegisterMBeanPlatform";
	
	/**
	 * Configuration Element Name for register services
	 */
	private static final String      REGISTER_ELEMENT                 = "register";
	
	/**
	 * Class name attribute name for register services class
	 */
	private static final String      CLASS_NAME_ATTR                  = "class-name";
	
	/**
	 * Holds the use-default attribute of register Element. This attribute
	 * indicades, if true: 1. If the attribute class-name is omitted, use default
	 * Class Name as Defined by
	 * 
	 * @linkplain DEFAULT_REGISTER Attribute of this class. or 2. If the class
	 *            defined on class-name attribute could not be instantiated, try
	 *            default class
	 */
	private static final String      USE_DEFAULT_ATTR                 = "use-default";
	
   /***
    * Holds the instance of the class that will provide the registration facilities to DWare
    */
   private static RegisterMBeanHandler register;
   /**
    * Empty Constructor
    */
   private RegisterMBeanFactory() {
      super();
   }

   /***
    * Singleton method to create/return MBean Regsitration Classes
    * 
    * @param className A full class name of MBean registration. This class must implement @linkplain RegisterMBeanHandler Interface
    * 
    * @return An instance of the Class
    */
   public static void config(Element config) {
	   if (register == null) {
		   // Handle register ELEMENT
		   String className = "";
		   boolean useDefault = false;
		   Element registerElement = DOMUtils.getElement(config, REGISTER_ELEMENT, false);
		   if (registerElement == null) {
			   className = DEFAULT_REGISTER;
		   } else {
			   className = DOMUtils.getAttribute(registerElement, CLASS_NAME_ATTR, false);
			   useDefault = DOMUtils.getBooleanAttribute(registerElement, USE_DEFAULT_ATTR);
			   if ((className == null) || (className.equals(""))) {
				   if (useDefault) {
					   className = DEFAULT_REGISTER;
				   } else {
					   log.error("register Element was specified, no class-name was specified and use-default is not true.No monitoring will take place");
					   return;
				   }
			   }
		   }
		   log.info("registering mbean factory " + className);
		   
		   try {
			   Class k = Class.forName(className);
			   register = (RegisterMBeanHandler) k.newInstance();
			   register.configure(registerElement);
		   } catch (ClassNotFoundException e) {
			   log.error("Error instantiating RegisterMBeanHandler for class ["+className+"]", e);
		   } catch (InstantiationException e) {
			   log.error("Error instantiating RegisterMBeanHandler for class ["+className+"]", e);
		   } catch (IllegalAccessException e) {
			   log.error("Error instantiating RegisterMBeanHandler for class ["+className+"]", e);
		   } catch (MBeanHandlerException e) {
			   log.error("Error configuring RegisterMBeanHandler for class ["+className+"]", e);
           }
	   }
   }
   
   public static RegisterMBeanHandler getRegisterMBean() {
      return register;
   }
   
   public static void registerMBean(AusterMBean mbean) {
      register.registerMBean(mbean);     
   }
}
 