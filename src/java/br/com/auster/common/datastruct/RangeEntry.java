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
 * Created on May 3, 2005
 */
package br.com.auster.common.datastruct;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Title:</b> RangeEntry</p>
 * <p><b>Description:</b> An entry in the internal RangeMap tree</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: RangeEntry.java 168 2005-05-06 21:42:38Z etirelli $
 */
public class RangeEntry {
  protected long from;
  protected long to;
  protected List values;
  
  public RangeEntry() {
    this(Long.MIN_VALUE, Long.MAX_VALUE, (List) new ArrayList());
  }
  
  public RangeEntry(long from, long to) {
    this(from, to, (List) new ArrayList());
  }
  
  public RangeEntry(long from, long to, List values) {
    this.from  = from;
    this.to    = to;
    this.values = values;
  }

  /**
   * @return Returns the from.
   */
  public long getFrom() {
    return from;
  }

  /**
   * @param from The from to set.
   */
  public void setFrom(long from) {
    this.from = from;
  }

  /**
   * @return Returns the to.
   */
  public long getTo() {
    return to;
  }

  /**
   * @param to The to to set.
   */
  public void setTo(long to) {
    this.to = to;
  }

  /**
   * @return Returns the value.
   */
  public List getValues() {
    return values;
  }

  /**
   * @param value The value to set.
   */
  public void setValues(List values) {
    this.values = values;
  }
  
  /**
   * Add a value to the values list
   * @param value
   */
  public void addValue(Object value) {
    this.values.add(value);
  }
  
  /**
   * Removes a value from the values list
   * @param value
   */
  public void removeValue(Object value) {
    this.values.remove(value);
  }
  
  public String toString() {
    return "[ "+this.getFrom()+", "+this.getTo()+" ) => "+this.getValues().toString();
  }

}
