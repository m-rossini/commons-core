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
 * Created on 08/09/2006
 */
//TODO Comment this Class
package br.com.auster.common.lang;

/**
 * @author mtengelm
 * @version $Id: NamedDouble.java 320 2006-09-19 14:02:38Z mtengelm $
 */
public class NamedDouble {

	private String	name;
	private double	dbl;

	/**
	 * 
	 */
	public NamedDouble(String name) {
		this(name, 0);
	}

	public NamedDouble(String name, double dbl) {
		this.name = name;
		this.dbl = dbl;
	}

	public double getValue() {
		return dbl;
	}

	public void setValue(double dbl) {
		this.dbl = dbl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double subtractFrom(double dbl) {
		this.setValue(this.getValue() - dbl);
		return this.getValue();
	}

	public double addTo(double dbl) {
		this.setValue(this.getValue() + dbl);
		return this.getValue();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof NamedDouble) {
			NamedDouble input = (NamedDouble) obj;
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
		return "Name=[" + this.getName() + "].Double=[" + this.getValue() + "]";
	}

	
}
