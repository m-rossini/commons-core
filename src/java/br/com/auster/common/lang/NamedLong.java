/*
 * Copyright (c) 2004-2008 Auster Solutions. All Rights Reserved.
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
 * Created on 06/03/2008
 */
package br.com.auster.common.lang;

/**
 * Named attribute like NamedDouble, but using long type.
 *
 * @author William Soares
 * @version $Id$
 * @since JDK1.4
 */
public class NamedLong {

	private String	name;
	private long	lng;

	/**
	 * 
	 */
	public NamedLong(String name) {
		this(name, 0);
	}

	public NamedLong(String name, long lng) {
		this.name = name;
		this.lng = lng;
	}

	public long getValue() {
		return lng;
	}

	public void setValue(long lng) {
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long subtractFrom(long lng) {
		this.setValue(this.getValue() - lng);
		return this.getValue();
	}

	public long addTo(long lng) {
		this.setValue(this.getValue() + lng);
		return this.getValue();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof NamedLong) {
			NamedLong input = (NamedLong) obj;
			if (input.getName().equals(this.getName())) {
				if (input.getValue() == this.getValue()) {
					return true;
				}
			}
		}		
		return false;
	}

	public int hashCode() {
		int retval = (this.getName().hashCode()) * 37;
		retval += (37*this.getValue());
		return retval;
	}

	public String toString() {
		return "Name=[" + this.getName() + "].Long=[" + this.getValue() + "]";
	}	
	
}
