/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 21/10/2006
 */
package br.com.auster.common.tests.datastruct;

import java.util.Iterator;
import java.util.Set;

import br.com.auster.common.datastruct.RangeEntry;
import br.com.auster.common.datastruct.RangeMap;
import br.com.auster.common.datastruct.RangeRecursionExaustedException;

/**
 * @author framos
 * @version $Id$
 */
public class TestRangeMapLimits {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			RangeMap map = new RangeMap(1000, false);
			for (long i=1; i < 200; i++) {
				System.out.println("Starting iteration " + i);
				for (long j=2; j < 2000; j++) {
					long start = i*1000;
					long end = (i+j)*1000;
					try {
						map.add(start, end, null);//new MyRange(start, end));
					} catch (RangeRecursionExaustedException rree) {
						System.out.println("Exausted interaction in " + j);

//						Set<RangeEntry> ranges = map.getOverlappingRanges();
//						for (RangeEntry entry : ranges) {
//							System.out.println("overlaped [ "+entry.getFrom()+", "+entry.getTo()+" ) => " + entry.getValues().size());
//						}
						continue;
					}
					//System.out.println("Ended iteration " + j);
				}
				Set ranges = map.getOverlappingRanges();
				for (Iterator it = ranges.iterator(); it.hasNext();) {
					RangeEntry entry = (RangeEntry)it.next();
					System.out.println("overlaped [ "+entry.getFrom()+", "+entry.getTo()+" ) => " + entry.getValues().size());
				}
//				map.add(i*1000, (i+1015)*1000, null);//new MyRange(start, end));
//				System.out.println("Ended iteration " + i);
			}
			System.out.println("finished.");
		} catch (Exception e) {
			try {
				Thread.sleep(3000);
			} catch (Exception e1) {}
			e.printStackTrace();
		}
	}

}

class MyRange {
	
	private long start;
	private long end;
	public MyRange(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
	public String toString() {
		return "MyRange (" + this.start + "-" + this.end + ")";
	}
}