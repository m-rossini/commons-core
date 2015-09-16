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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.xml.DOMUtils;

/**
 * <p>
 * <b>Title:</b> DwareMangementServices
 * </p>
 * <p>
 * <b>Description:</b>
 * </p>
 * <p>
 * <b>Copyright:</b> Copyright (c) 2004-2005
 * </p>
 * <p>
 * <b>Company:</b> Auster Solutions
 * </p>
 * 
 * @author mtengelm
 * @version $Id: AusterManagementServices.java 296 2006-08-28 19:24:07Z framos $
 */
public class AusterManagementServices {

   /****************************************************************************
    * DWare JMX Domain
    */
   private static final String      AUSTER_DOMAIN                     = "br.com.auster.commom.jmx:";

   /**
    * Default Monitoring Class Name. It is the Platform
    */
   private static final String      DEFAULT_REGISTER                 = "br.com.auster.commom.jmx.RegisterMBeanPlatform";

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

   /****************************************************************************
    * The name of mbean element where are defined the component to be monitored
    * and the respective MBEAN class of this componenet
    */
   private static final String      MBEAN_ELEMENT                    = "mbean";

   /****************************************************************************
    * The name of the attribute defining the name of the component to be
    * monitored.
    */
   private static final String      COMPONENT_ATTR                   = "mbean-component";

   private static final String      COMPONENT_TYPE_ATTR              = "mbean-type";

   /****************************************************************************
    * The name of the attribute holding the intance of MBEAN to be created by
    * the component
    */
   private static final String      MBEAN_CLASS_ATTR                 = "mbean-class-name";
   private static final String      PASS_REFERENCE_ATTR              = "pass-reference";

   public static final String       MANAGEMENT_CONFIGURATION_ELEMENT = "configuration";
   public static final String       MANAGEMENT_NAMESPACE_URI         = "http://www.auster.com.br/commom/management/";

   private Element                  config;
   private static ConcurrentHashMap mbeansMap                        = new ConcurrentHashMap();
   
   private static final Logger log = Logger.getLogger(AusterManagementServices.class);

   /****************************************************************************
    * Constructor with Configuration
    * 
    * @param config
    */
   public AusterManagementServices(Element config) {
      this.setConfig(config);
   }

   /**
    * If used, then the setConfig(Element config) method MUST be called.
    * Otherwise a NPE will occur when trying to register any MBean
    */
   public AusterManagementServices() {
      // Empty
   }

   /****************************************************************************
    * Returns the DWare Monitoring Domain to be used by all DWare compatible
    * MBeans
    * 
    * @return
    */
   public static String getDomain() {
      return AUSTER_DOMAIN;
   }

   /****************************************************************************
    * Returns the current register service
    * 
    * @return
    */
   public static RegisterMBeanHandler getRegister() {
      return RegisterMBeanFactory.getRegisterMBean();
   }

   /****************************************************************************
    * Configures this class instance. This is a complete Configuration of
    * Data-Aware monitoring. The configuration will work as below:
    * 
    * @param config
    */
   public void setConfig(Element config) {
      this.config = config;

      RegisterMBeanFactory.config(config);
   }

   public static boolean registerMBean(boolean keep, Element configMBean,
         Class classReference, Object instanceReference) {

      NodeList mbeans = DOMUtils.getElements(configMBean, MBEAN_ELEMENT);
      int qtd = mbeans.getLength();
      if (qtd == 0) {
         // No config setup. No monitoring will take place
         return false;
      }
      boolean retValue = false;
      for (int i = 0; i < qtd; i++) {
         Element config = (Element) mbeans.item(i);
         String componentName = DOMUtils.getAttribute(config, COMPONENT_ATTR, true);
         String componentType = DOMUtils.getAttribute(config, COMPONENT_TYPE_ATTR, false);
         if (componentType == null || componentType.equals("")) {
            componentType = componentName;
         }
         String mbeanClass = DOMUtils.getAttribute(config, MBEAN_CLASS_ATTR, true);
         // System.out.println("CN=[" + componentName + "] MC=[" + mbeanClass +
         // "]");
         boolean passReference = DOMUtils.getBooleanAttribute(config, PASS_REFERENCE_ATTR);
         Object iRefs = (passReference) ? instanceReference : null;
         Class cRefs = (passReference) ? classReference : null;
         boolean tempRet = registerMBean(keep, componentName, componentType, mbeanClass,
               cRefs, iRefs);
         retValue = (retValue == true) ? true : tempRet;
      }
      return retValue;
   }

   public static boolean registerMBean(AusterMBean instance) {
  	 RegisterMBeanHandler register = AusterManagementServices.getRegister();  	 
     if (register == null) { // No Monitoring in effect for this run
       log.warn("No registerMBeanHandler is in effect. Null return form getRegister()");
       return false;
    }  	 
  	 register.registerMBean(instance);
  	 return true;
   }
   
   public static boolean registerMBean(boolean keep, String componentName,
         String componentType, String componentClass, Class constructorClass,
         Object constructorParm) {
      RegisterMBeanHandler register = AusterManagementServices.getRegister();
      if (register == null) { // No Monitoring in effect for this run
         log.warn("No registerMBeanHandler is in effect. Null return form getRegister()");
         return false;
      }
      if (keep) {
         mbeansMap.put(componentName, componentClass);
      }
      AusterMBean instance = null;
      try {
         if (constructorParm != null) {
            Class[] c = { constructorClass };
            Object[] o = { constructorParm };
            instance = (AusterMBean) Class.forName(componentClass).getConstructor(c).newInstance(
                  o);
         } else {
            instance = (AusterMBean) Class.forName(componentClass).newInstance();
         }

      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

      if (instance != null) {
         ((AusterMBean) instance).setMBeanName(componentType, componentName);
         register.registerMBean(instance);
      }
      return (instance != null);
   }
}
