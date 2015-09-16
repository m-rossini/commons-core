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

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class represents a synchronized list. It implements common
 * methods found in the most popular lists. All the methods
 * implemented in this subclass may be used concurrently, so it is
 * possible to use them in multi-thread enviroment without worring
 * about concurrency.
 * 
 * @author Ricardo Barone
 * @version $Id: SyncAbstractLinkedList.java 91 2005-04-07 21:13:55Z framos $
 */
public abstract class SyncAbstractLinkedList {

   protected final LinkedList list = new LinkedList();

   /**
    * Puts an item in the list. Implementations of this method will
    * define the position in the list where it will be put.
    * @param item the item to be put in the list.
    */
   public abstract void put(Object item);

   /**
    * Gets an item from the list. The item must be removed from the
    * list. Implementations of this method will define the position
    * where the item will be get from the list.
    * @return an item from the list.
    * @throws NoSuchElementException if there is no element in the
    * list to be returned.
    */
   public abstract Object get() throws NoSuchElementException;

   /**
    * Put an item at the start of the list. A notify is sent to the
    * sync object.
    * @param item the item to be added to the list.
    * @throws NullPointerException if the item is null.
    */
   public final void addFirst(Object item)
   {
      if(item == null) throw new NullPointerException();
      synchronized(this.list) {
         this.list.addFirst(item);
         this.list.notify();
      }
   }

   /**
    * Put an item at the end of the list. A notify is sent to the
    * sync object.
    * @param item the item to be added to the list.
    * @throws NullPointerException if the item is null.
    */
   public final void addLast(Object item)
   {
      if(item == null) throw new NullPointerException();
      synchronized(this.list) {
         this.list.addLast(item);
         this.list.notify();
      }
   }

   /**
    * Remove and return the first item of the list.
    * @return The first item of the list.
    * @throws NoSuchElementException if the list is empty.
    */
   public final Object removeFirst() throws NoSuchElementException
   {
      synchronized(this.list) {
         return this.list.removeFirst();
      }
   }

   /**
    * Remove and return the last item of the list.
    * @return The last item of the list.
    * @throws NoSuchElementException if the list is empty.
    */
   public final Object removeLast() throws NoSuchElementException
   {
      synchronized(this.list) {
         return this.list.removeLast();
      }
   }

   /**
    * Gets the sync object. It is notified every time a object is
    * added to the list.
    * @return the synchronization object.
    */
   public final Object getSyncObject()
   {
      return this.list;
   }

   /**
    * Verifies if the list is empty.
    * @return true if the list is empty, false otherwise.
    */
   public final boolean isEmpty()
   {
      synchronized(this.list) {
         return this.list.isEmpty();
      }
   }

   /**
    * Gets the quantity of elements in the list.
    */
   public final int size()
   {
      synchronized(this.list) {
         return this.list.size();
      }
   }

   /**
    * Removes all the contents of the list.
    */
   public final void clear()
   {
      synchronized(this.list) {
         this.list.clear();
      }
   }

   /**
    * Return the linked list that is synchronized by this class.
    */
   protected LinkedList getLinkedList()
   {
      return this.list;
   }

   /**
    * Appends an AbstractLinkedList to this.
    */
   public final boolean append(SyncAbstractLinkedList l)
   {
      synchronized(this.list) {
         return list.addAll(l.getLinkedList());
      }
   }

   /**
    * Returns the element at specified position.
    */
   public Object elementAt(int index)
   {
      synchronized(this.list) {
         return list.get(index);
      }
   }
}