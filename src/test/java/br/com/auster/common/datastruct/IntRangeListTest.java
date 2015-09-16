/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 25/09/2007
 */
package br.com.auster.common.datastruct;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import br.com.auster.common.datastruct.IntRangeList.IntRangeNode;

/**
 * @author framos
 * @version $Id$
 *
 */
public class IntRangeListTest extends TestCase {


	private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss yyyy");

	private static final String[][] DATES = {
		{ "Nov 07 22:10:00 2006", "Nov 07 23:35:00 2006" },
		{ "Nov 07 22:10:00 2006", "Nov 07 23:35:00 2006" },
		{ "Nov 07 23:36:00 2006", "Nov 08 01:01:00 2006" },
		{ "Nov 08 01:02:00 2006", "Nov 08 02:27:00 2006" },
		{ "Nov 08 02:28:00 2006", "Nov 08 03:53:00 2006" },
		{ "Nov 09 22:10:00 2006", "Nov 09 23:35:00 2006" },
		{ "Nov 09 22:10:00 2006", "Nov 09 23:35:00 2006" },
		{ "Nov 09 23:36:00 2006", "Nov 10 01:01:00 2006" },
		{ "Nov 10 01:02:00 2006", "Nov 10 02:27:00 2006" },
		{ "Nov 10 02:28:00 2006", "Nov 10 03:53:00 2006" },
		{ "Nov 11 22:10:00 2006", "Nov 11 23:35:00 2006" },
		{ "Nov 11 22:10:00 2006", "Nov 11 23:35:00 2006" },
		{ "Nov 11 23:36:00 2006", "Nov 12 01:01:00 2006" },
		{ "Nov 12 01:02:00 2006", "Nov 12 02:27:00 2006" },
		{ "Nov 12 02:28:00 2006", "Nov 12 03:53:00 2006" }
	};


	private static final String[][] OVERLAP_DATES = {
		{ "Nov 07 22:10:00 2006", "Nov 07 23:35:00 2006" },
		{ "Nov 07 22:10:00 2006", "Nov 07 23:35:00 2006" },
		{ "Nov 09 22:10:00 2006", "Nov 09 23:35:00 2006" },
		{ "Nov 09 22:10:00 2006", "Nov 09 23:35:00 2006" },
		{ "Nov 11 22:10:00 2006", "Nov 11 23:35:00 2006" },
		{ "Nov 11 22:10:00 2006", "Nov 11 23:35:00 2006" }
	};

	public void testSimple() {
		try {
			System.getProperties().put("user.timezone", "GMT-3");
			IntRangeList list = new IntRangeList();
			for (int i=DATES.length-1; i >= 0; i--) {
				CallInfo f = buildCall(DATES[i][0], DATES[i][1]);
				int from = (int) (f.getFrom().getTime() / 1000);
				int to = (int) (f.getTo().getTime() / 1000);
				System.out.println("Adding from " + from + " to " + to);
				list.add(from, to, f);
			}

			List<IntRangeNode> overlaps = list.getOverlaps();
      for (IntRangeNode node : overlaps) {
        System.out.println("Returned: from " + node.getFrom() + " to " + node.getTo());
        System.out.println("duplicated " + node.getIsDuplicated());
        System.out.println("value " + node.getValue());
      }
      
			assertEquals(6, overlaps.size());
			int j=OVERLAP_DATES.length - 1;
			for (IntRangeNode node : overlaps) {
				CallInfo onList = (CallInfo)node.getValue();
				CallInfo loaded = buildCall(OVERLAP_DATES[j][0], OVERLAP_DATES[j][1]);
        System.out.println("Comparing FROM: " + onList.getFrom() + " with " + loaded.getFrom());
				assertTrue(onList.getFrom().equals(loaded.getFrom()));
        System.out.println("Comparing TO: " + onList.getTo() + " with " + loaded.getTo());
				assertTrue(onList.getTo().equals(loaded.getTo()));
        j--;
			}

		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}


	public static final CallInfo buildCall(String _date1, String _date2) throws Exception {

		return new CallInfo(sdf.parse(_date1), sdf.parse(_date2));
	}
}

class CallInfo {

	protected Date from;
	protected Date to;

	public CallInfo(Date _d1, Date _d2) {
		from = _d1;
		to = _d2;
	}

	public Date getFrom() { return from; }
	public Date getTo() { return to; }
  
  public String toString() {
    return "CallInfo from " + from + " to " + to;
  }
}

