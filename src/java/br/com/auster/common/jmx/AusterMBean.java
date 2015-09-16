/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 08/08/2006
 */
//TODO Comment this Class
package br.com.auster.common.jmx;

/**
 * @author mtengelm
 * @version $Id: AusterMBean.java 296 2006-08-28 19:24:07Z framos $
 */
public interface AusterMBean {

	public static final String MBEAN_NAME_FORMAT = "type={0},name={1}";
	
	/***
	 * Returns a String that will be concatenated with DOMAIN name, in order to create the ObjectName
	 * that will be created and registered with the MBean
	 * 
	 * @return A String representing the "after domain" parameters of MBean Registration
	 */
	public  String getMBeanName();

	/**
	 * Defines the name of the current MBean. This String will be used as name and type, when registering the
	 * 	MBean
	 * 
	 * @param _name the type and name of the MBean
	 */
	public abstract void setMBeanName(String _name);

	/**
	 * Same as previous <code>setMBeanName()</code> but now the type attribute of the MBean can be set to a 
	 * 	different value from the name.
	 * 
	 * @param _type the type of the MBean
	 * @param _name the name of the MBean
	 */
	public abstract void setMBeanName(String _type, String _name);

}