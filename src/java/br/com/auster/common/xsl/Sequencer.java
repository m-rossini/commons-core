package br.com.auster.common.xsl;
/*
 * Copyright (c) 2004 Auster Solutions do Brasil LTDA. All Rights Reserved.
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
 * Created on 30/10/2004
 */
/**
 * This class represents a sequence and has some convenience methods to perform related operations
 * It has a method to initialize a sequence to a given value and increment.
 * 
 * @author Marcos Tengelmann
 * 30/10/2004
 * @version $Id$
 */
public class Sequencer {
   private double current;
   private double add;
   private double max;
   private double starter;
   
   /**
    * Hidden Constructor 
    */
   public Sequencer() {
      super();
   }

   /***
    * Initializes the Sequencer with default Values.
    * Default Values are:
    * initial(=current) = 0
    * increment=1
    * maximum=Double.MAX_VALUE 
    * @return Return the Current Value
    */
   public double init() {
      return init(0,1,Double.MAX_VALUE);
   }
   
   /***
    * Initializes the Sequencer with default Values, except for current
    * Default Values are:
    * increment=1
    * maximum=Double.MAX_VALUE 
    *   
    * @param current starting value
    * @return Return the Current Value
    */
   public double init(double current) {
      return init(current,1, Double.MAX_VALUE);
   }

   /***
    * Initializes the Sequencer with default Values, except for current and incremental
    * Default Values are:
    * maximum=Double.MAX_VALUE    
    *  
    * @param current starting value
    * @param add the incremental value
    * @return Return the Current Value
    */
   public double init(double current, double add) {
      return init(current,add,Double.MAX_VALUE);      
   }
   
   /***
    * Initializes the Sequencer
    * 
    * @param current, the starting value
    * @param add, the incremental value
    * @param max, the maximum value
    * @return Return the Current Value
    */
   public double init(double current, double add, double max) {
      this.starter = current;
      this.current = current;
      this.add = add;
      this.max = max;
      return current;
   }
   
   /***
    * Returns the next element of the sequencer.
    * The next element will be:
    * current + add IF current < max
    * or
    * start IF current = max
    * 
    * @return next element of the sequencer
    */
   public double next() {
      return ( (this.current == this.max) ? this.starter : (this.current += add)) ;
   }
   
   /***
    * Returns the current element of the sequencer
    * 
    * @return The current element of the sequencer
    */
   public double current() {
      return current;
   }
   
   /***
    * Based on the max value of the sequencer,returns the number of empty values to be returned 
    * 
    * @return The number of empty values left to be returned.
    */
   public double left() {
      return (max-current)/add;
   }
}



