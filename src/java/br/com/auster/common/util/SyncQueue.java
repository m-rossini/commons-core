/*
 * Copyright (c) 2004 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 16/11/2004
 */
package br.com.auster.common.util;

import java.util.NoSuchElementException;

/**
 * This class represents a synchronized queue. All the methods
 * implemented in this subclass may be used concurrently, so it is
 * possible to use them in multi-thread enviroment without worring
 * about concurrency.
 * 
 * @author Ricardo Barone
 * @version $Id: SyncQueue.java 91 2005-04-07 21:13:55Z framos $
 */
public class SyncQueue extends SyncAbstractLinkedList {

   /**
    * Put an item in the queue. A notify is sent to the sync object.
    * @param item an item
    */
   public final void put(Object item)
   {
      this.addLast(item);
   }

   /**
    * Remove and return the first item of the queue.
    * @return The first item of the queue.
    * @throws NoSuchElementException if the queue is empty.
    */
   public final Object get() throws NoSuchElementException
   {
      return this.removeFirst();
   }

}