/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Created on Apr 8, 2005
 */
package br.com.auster.common.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;

import br.com.auster.common.security.ResourceReady;

/**
 * @author framos
 * @version $Id$
 */
public class LockGenerator {

	private int days;

	private ResourceReady rr;

	/**
	 * 
	 */
	public LockGenerator(String dias) {
		this.days = Integer.parseInt(dias);
	}

	private void generate() throws ParseException {
		Calendar currentCalendar = Calendar.getInstance();;
		Calendar limitCalendar = null;
		if (this.days >= 0) {
			limitCalendar = (Calendar) currentCalendar.clone();
			limitCalendar.add(Calendar.DATE, this.days);
		}

		Calendar firstRun = (Calendar) currentCalendar.clone();
		firstRun.add(Calendar.HOUR, 1);

		System.out.println("Current Date=" + currentCalendar);
		System.out.println("Limit Date=" + limitCalendar);
		System.out.println("First Run  Date=" + firstRun);

		ResourceReady rr = new ResourceReady(currentCalendar, limitCalendar);
		this.rr = rr;
		this.rr.setFirstRun(firstRun);

	}

	private ResourceReady getResource() {
		return rr;
	}

	/*
	 * Argumentos: 
   * 0 - Nome do arquivo (default "ready.rss") 
   * 1 - Dias a partir da Data Corrente permitidos (-1 = sem limite)
   * 2 - Product ID 
   * 3 - Count: Numero de Execuções Permitidas (-1 = sem limite) 
   * 4 - Mascara IP (De acordo com java.util.regex) de IP 
   *     onde a execução é permitida (-1 = não checar IP)
   * 5 - Mac Address (-1 = não checar Mac Address)
	 */
	public static void main(String[] args) throws IOException, ParseException {

		FileOutputStream fos = new FileOutputStream(args[0]);
		GZIPOutputStream gos = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gos);
		LockGenerator gr = new LockGenerator(args[1]);
		gr.generate();
		ResourceReady rr = gr.getResource();
		rr.setProductID(args[2]);
		rr.setCount(Integer.parseInt(args[3]));
		// do not set ip mask parameter to ignore such block
		if (args.length < 5) {
			rr.setIPMask(null);
		} else {
			rr.setIPMask(args[4]);
		}
    if (args.length < 6) {
      rr.setMacAddress(null);
    } else {
      rr.setMacAddress(args[5]);
    }
		oos.writeObject(rr);
		gos.flush();
		gos.close();
		fos.flush();
		fos.close();
	}

}
