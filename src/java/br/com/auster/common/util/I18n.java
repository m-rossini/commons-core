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
 * Created on 13/10/2004
 */
package br.com.auster.common.util;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

/**
 * @author Ricardo Barone
 * @version $Id: I18n.java 91 2005-04-07 21:13:55Z framos $
 */
public final class I18n
{

   protected static final Map i18nHash = new Hashtable();
   protected static final String defaultMessagesFile = "MessagesBundle";

   /**
    * The MessageFormat's instances are hashed by its ResourceBundle message
    * key, improving performance.
    */
   protected final Map messagesByKey = Collections.synchronizedMap(new WeakHashMap());
   protected ResourceBundle resBundle;

   /**
    * The instances of this class are hashed by package name and message file
    * name, improving memory and CPU usage.
    */
   protected I18n(ResourceBundle resBundle)
   {
      this.resBundle = resBundle;
   }

   /**
    * Given a key, searches for the internationalized message in the internal
    * resource bundle and returns an <code>MessageFormat</code> instance
    * containing it. For the same key, the same instance will be returned, but
    * synchronized access is provided by the
    * <code>getString(String,Object[])</code> method.
    * 
    * @param key
    *           the resouce bundle key for a internationalized string message.
    * @return a MessageFormat instance that may be shared by other objects.
    * @throws MissingResourceException
    *            if could not find the message in the resource bundle.
    */
   private final MessageFormat getMessageFormat(String key)
   {
      MessageFormat mf = (MessageFormat) messagesByKey.get(key);
      if(mf == null)
      {
         mf = new MessageFormat(this.resBundle.getString(key));
         mf.setLocale(resBundle.getLocale());
         messagesByKey.put(key, mf);
      }
      return mf;
   }

   /**
    * Gets an internationalized string given the key and the arguments.
    * 
    * @param key
    *           the string key.
    * @param args
    *           the arguments to be put in the string to be returned.
    * @return an internationalized string for the given key, using the given
    *         argument.
    * @throws MissingResourceException
    *            if could not find the message in the resource bundle.
    */
   public final String getString(String key, Object[] args)
         throws MissingResourceException
   {
      MessageFormat mf = this.getMessageFormat(key);
      synchronized(mf)
      {
         return mf.format(args, new StringBuffer(), null).toString();
      }
   }

   /**
    * Gets an internationalized string given the key and the argument.
    * 
    * @param key
    *           the string key.
    * @param arg1
    *           the first argument to be put in the string to be returned.
    * @param arg2
    *           the second argument to be put in the string to be returned.
    * @param arg3
    *           the third argument to be put in the string to be returned.
    * @return an internationalized string for the given key, using the given
    *         argument.
    * @throws MissingResourceException
    *            if could not find the message in the resource bundle.
    */
   public final String getString(String key, Object arg1, Object arg2, Object arg3)
         throws MissingResourceException
   {
      return this.getString(key, new Object[] { arg1, arg2, arg3 });
   }

   /**
    * Gets an internationalized string given the key and the argument.
    * 
    * @param key
    *           the string key.
    * @param arg1
    *           the first argument to be put in the string to be returned.
    * @param arg2
    *           the second argument to be put in the string to be returned.
    * @return an internationalized string for the given key, using the given
    *         argument.
    * @throws MissingResourceException
    *            if could not find the message in the resource bundle.
    */
   public final String getString(String key, Object arg1, Object arg2)
         throws MissingResourceException
   {
      return this.getString(key, new Object[] { arg1, arg2 });
   }

   /**
    * Gets an internationalized string given the key and the argument.
    * 
    * @param key
    *           the string key.
    * @param arg
    *           the argument to be put in the string to be returned.
    * @return an internationalized string for the given key, using the given
    *         argument.
    * @throws MissingResourceException
    *            if could not find the message in the resource bundle.
    */
   public final String getString(String key, Object arg) throws MissingResourceException
   {
      return this.getString(key, new Object[] { arg });
   }

   /**
    * Gets an internationalized string given the key.
    * 
    * @param key
    *           the string key.
    * @return an internationalized string for the given key, using no arguments.
    * @throws MissingResourceException
    *            if could not find the message in the resource bundle.
    */
   public final String getString(String key) throws MissingResourceException
   {
      return this.resBundle.getString(key);
   }

   /** ************************************************************** */
   /* Static methods that are used to manipulate the i18n instances */
   /** ************************************************************** */

   /**
    * Gets a I18n instance for the default locale. The messages class/properties
    * name that will be searched will be the default one.
    * 
    * @param cl
    *           the class which package is the starting point to look for the
    *           messages class/properties.
    * @param messagesFile
    *           the class/properties name to look for.
    * @return a I18n object, that will convert a key and some arguments to the
    *         internationalized message.
    * @throws MissingResourceException
    *            if could not find the message file.
    */
   public static final I18n getInstance(Class cl) throws MissingResourceException
   {
      return getInstance(cl, Locale.getDefault());
   }

   /**
    * Gets a I18n instance for the given locale. The messages class/properties
    * name that will be searched will be the default one.
    * 
    * @param cl
    *           the class which package is the starting point to look for the
    *           messages class/properties.
    * @param locale
    *           the locale to be used. If null the default locale will be used.
    * @param messagesFile
    *           the class/properties name to look for.
    * @return a I18n object, that will convert a key and some arguments to the
    *         internationalized message.
    * @throws MissingResourceException
    *            if could not find the message file.
    */
   public static final I18n getInstance(Class cl, Locale locale)
         throws MissingResourceException
   {
      return getInstance(cl, locale, defaultMessagesFile);
   }

   /**
    * Gets a I18n instance for the default locale. The messages class/properties
    * name searched will be strictly the informed.
    * 
    * @param cl
    *           the class which package is the starting point to look for the
    *           messages class/properties.
    * @param messagesFile
    *           the class/properties name to look for.
    * @return a I18n object, that will convert a key and some arguments to the
    *         internationalized message.
    * @throws MissingResourceException
    *            if could not find the message file.
    */
   public static final I18n getInstance(Class cl, String messagesFile)
         throws MissingResourceException
   {
      return getInstance(cl, Locale.getDefault(), messagesFile, false);
   }

   /**
    * Gets a I18n instance for the given locale. The messages class/properties
    * name searched will be strictly the informed.
    * 
    * @param cl
    *           the class which package is the starting point to look for the
    *           messages class/properties.
    * @param locale
    *           the locale to be used. If null the default locale will be used.
    * @param messagesFile
    *           the class/properties name to look for.
    * @return a I18n object, that will convert a key and some arguments to the
    *         internationalized message.
    * @throws MissingResourceException
    *            if could not find the message file.
    */
   public static final I18n getInstance(Class cl, Locale locale, String messagesFile)
         throws MissingResourceException
   {
      return getInstance(cl, locale, messagesFile, false);
   }

   /**
    * Gets a I18n instance that contains all the messages for the default
    * locale, starting at <code>cl</code> package going all the way up to the
    * root package. The messages class/properties searched will be strictly the
    * informed, if <code>searchDefault</code> is false. Otherwise, if not
    * found, the default one will be searched. This method will use a hash
    * mapping package name to I18n objects. So the objects returned may be
    * shared by other threads.
    * 
    * @param cl
    *           the class which package is the starting point to look for the
    *           messages class/properties.
    * @param messagesFile
    *           the class/properties name to look for.
    * @param searchDefault
    *           if true and it could not find the class/properties named
    *           <code>messagesFile</code>, it looks for the default name.
    * @return a I18n object, that will convert a key and some arguments to the
    *         internationalized message.
    * @throws MissingResourceException
    *            if could not find the message file.
    */
   public static final I18n getInstance(Class cl,
                                        String messagesFile,
                                        boolean searchDefault)
         throws MissingResourceException
   {
      return getInstance(cl, Locale.getDefault(), messagesFile, searchDefault);
   }

   /**
    * Gets a I18n instance that contains all the messages for the given locale,
    * starting at <code>cl</code> package going all the way up to the root
    * package. The messages class/properties searched will be strictly the
    * informed, if <code>searchDefault</code> is false. Otherwise, if not
    * found, the default one will be searched. This method will use a hash
    * mapping package name to I18n objects. So the objects returned may be
    * shared by other threads.
    * 
    * @param cl
    *           the class which package is the starting point to look for the
    *           messages class/properties.
    * @param locale
    *           the locale to be used. If null the default locale will be used.
    * @param messagesFile
    *           the class/properties name to look for.
    * @param searchDefault
    *           if true and it could not find the class/properties named
    *           <code>messagesFile</code>, it looks for the default name.
    * @return a I18n object, that will convert a key and some arguments to the
    *         internationalized message.
    * @throws MissingResourceException
    *            if could not find the message file.
    */
   public static final I18n getInstance(Class cl,
                                        Locale locale,
                                        String messagesFile,
                                        boolean searchDefault)
         throws MissingResourceException
   {
      if(cl == null || messagesFile == null)
            throw new IllegalArgumentException("The arguments must not be null.");

      if(locale == null) locale = Locale.getDefault();

      ResourceBundle rb = null;
      String packName = cl.getPackage().getName();
      String i18nKey = packName + "|" + messagesFile + "|" + locale;

      // quick path - it is already in the hash
      I18n i18n = (I18n) i18nHash.get(i18nKey);
      if(i18n == null)
      {
         try
         {
            rb = searchResourceBundle(packName, messagesFile, locale);
         }
         catch(MissingResourceException e)
         {
            // If the messagesFile is equal to the defaultMessagesFile we don't
            // need to search for the
            // default if the messagesFile is not found.
            if(!searchDefault || (messagesFile == defaultMessagesFile)) throw e;
            rb = searchResourceBundle(packName, defaultMessagesFile, locale);
         }

         // add to hash
         i18n = new I18n(rb);
         i18nHash.put(i18nKey, i18n);
      }
      return i18n;
   }

   /**
    * Searches for the class/properties that contains all the internationalized
    * messages for the given locale, starting at <code>packName</code> going
    * all the way up to the root package.
    * 
    * @param packName
    *           the name of the package to start looking for the messages
    *           class/properties.
    * @param messagesFile
    *           the class/properties name to look for.
    * @param locale
    *           the locale that the messages must be internationalized for.
    * @return a ResourceBundle object, that contains all the internationalized
    *         messages hashed by string keys.
    * @throws MissingResourceException
    *            if could not find the messages file.
    */
   public static final ResourceBundle searchResourceBundle(String packName,
                                                           String messagesFile,
                                                           Locale locale)
         throws MissingResourceException
   {
      ResourceBundle rb = null;
      boolean found = false;

      // search for the messages file
      while(!found)
      {
         try
         {
            String className = packName + ".res." + messagesFile;
            rb = ResourceBundle.getBundle(className, locale);
            found = true;
         }
         catch(MissingResourceException e)
         {
            // Could not find the messages file at the last package. So let's
            // get upper in the package tree.
            int lastIndex = packName.lastIndexOf(".");

            if(lastIndex == -1)
                  throw new MissingResourceException("Can't find bundle for base name "
                        + messagesFile + ", locale " + locale, messagesFile + "_"
                        + locale, "");
            packName = packName.substring(0, lastIndex);
         }
      }

      return rb;
   }

}