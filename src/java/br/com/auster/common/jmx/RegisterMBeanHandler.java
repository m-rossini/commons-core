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

import org.w3c.dom.Element;


/**
 * <p><b>Title:</b> RegisterMBeanHandler</p>
 * <p><b>Description:
 * Represents a DWare MBean Registration Service.
 * </b> </p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author mtengelm
 * @version $Id: RegisterMBeanHandler.java 296 2006-08-28 19:24:07Z framos $
 */
public interface RegisterMBeanHandler {

   /**
    * Configures this MBean handler
    * @param config
    * @throws MBeanHandlerException
    */
   public void configure(Element config) throws MBeanHandlerException;

   /**
    * @param mbean
    */
   public void registerMBean(AusterMBean mbean);
   
}
 