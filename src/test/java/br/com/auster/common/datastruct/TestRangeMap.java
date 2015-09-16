/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
package br.com.auster.common.datastruct;

import java.util.Collections;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author mtengelm
 *
 */
public class TestRangeMap extends TestCase {

	private RangeMap rm;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		this.rm = new RangeMap();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link br.com.auster.common.datastruct.RangeMap#add(long, long, java.lang.Object)}.
	 */
	public void testAdd() {
		assertNotNull(rm);
		assertEquals(rm.getSize(), 0);		
		rm.add(0, 16, rm);
		assertEquals(rm.getSize(), 1);
		
		rm.add(0, 8, rm);
		assertEquals(rm.getSize(), 2);		
		assertEquals(rm.getOverlappingRanges().size(), 1);
		
		rm.add(0, 4, rm);
		assertEquals(rm.getSize(), 3);
		assertEquals(rm.getOverlappingRanges().size(),1);
		
		rm.add(5, 9, rm);
		assertEquals(rm.getSize(), 5);
		
		rm.add(5, 10, rm);
		assertEquals(rm.getSize(), 6);
		
		rm.add(6, 10, rm);
		assertEquals(rm.getSize(), 7);

		rm.add(7, 10, rm);
		assertEquals(rm.getSize(), 8);

		rm.add(8, 10, rm);
		assertEquals(rm.getSize(), 8);

		rm.add(9, 10, rm);
		assertEquals(rm.getSize(), 8);

		rm.add(1, 10, rm);
		assertEquals(rm.getSize(), 9);

		rm.add(2, 10, rm);
		assertEquals(rm.getSize(), 10);
		
		rm.add(3, 10, rm);
		assertEquals(rm.getSize(), 11);
		
		for (Iterator itr =  rm.iterator(); itr.hasNext();) {
			System.out.println("ITR:" + itr.next());
		}
		System.out.println("Overlap Size:" + rm.getOverlappingRanges().size() + ".Overlap:"+ rm.getOverlappingRanges());
	}

}
