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
 * Created on 26/10/2004
 */
package br.com.auster.common.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.safehaus.uuid.EthernetAddress;
import org.safehaus.uuid.NativeInterfaces;

/**
 * Change LOG
 * 
 * Version 1.1 (2004-Sep-11)
 * Added fistRun. Now, when first time run for this resource is done, the Calendar is Created
 *          and never updated.
 * Changed currentCount behavior. Now, whenever there are or not count limit,
 *          it counts the number of times canRun() was ran.
 * Changed return type. Instead of boolean it now returns an int.
 *          If greater than zero, means CANNOT run. If Zero or lower than Zero, means Can Run.
 * Added First Check to a new variable canrun (This variable is updated with true at allowed runs
 *          , and at first time it was updated with false, it is never updated again.
 * 
 * @author Marcos Tengelmann
 * @version $Id: ResourceReady.java 305 2006-09-01 22:48:04Z rbarone $
 */
public class ResourceReady implements Serializable, Cloneable {

  private static final long serialVersionUID = 374700630819228932L;

  private static final String DEFAULT_LICENSE_FILENAME = "license.bin";
  private static final String LICENSE_FILENAME_PROPERTY = "br.com.auster.license.path";
  public static final File LICENSE_FILE;
  static {
    String filename = System.getProperty(LICENSE_FILENAME_PROPERTY);
    if (filename == null) {
      filename = DEFAULT_LICENSE_FILENAME;
    }
    LICENSE_FILE = new File(filename);
  }

  private static final String JUG_LIBDIR_PROPERTY = "br.com.auster.juglib.dir";
  private static EthernetAddress[] NET_INTERFACES;
  private static final void setNetInterfaces() {
    String libdir = System.getProperty(JUG_LIBDIR_PROPERTY);
    if (libdir != null) {
      try {
        NativeInterfaces.setLibDir(new File(libdir));
        NET_INTERFACES = NativeInterfaces.getAllInterfaces();
      } catch (Error e) {
        System.err.println("Error while loading JUG library dir from '" + libdir
                           + "' - trying to load from system library path.");
        NativeInterfaces.setUseStdLibDir(true);
        NET_INTERFACES = NativeInterfaces.getAllInterfaces();
      }
    } else {
      NativeInterfaces.setUseStdLibDir(true);
      NET_INTERFACES = NativeInterfaces.getAllInterfaces();
    }
  }

  private Calendar generatedCalendar;
  private Calendar limitCalendar;
  private Calendar currentCalendar;
  private Calendar lastRun;
  private Calendar firstRun;
  private String productID;
  private Long count;
  private Long currentCount;
  private String ipMask;
  private Pattern ipPattern;
  private boolean hasIPMask = false;
  private String macAddress;

  //private boolean canrun;

  public ResourceReady(Calendar generatedCalendar, Calendar limitCalendar) {
    this.generatedCalendar = generatedCalendar;
    this.limitCalendar = limitCalendar;
  }

  public int canRun() {
    int returnValue = 0;

    if (this.limitCalendar != null) {
      this.currentCalendar = Calendar.getInstance();

      if (this.currentCalendar.after(this.limitCalendar)) {
        returnValue = 2;
      }
      if (this.currentCalendar.before(this.generatedCalendar)) {
        returnValue = 3;
      }

      if (this.firstRun == null) {
        this.firstRun = (Calendar) this.currentCalendar.clone();
      }
      if (this.lastRun == null) {
        this.lastRun = (Calendar) this.currentCalendar.clone();
      }
      if (this.lastRun.after(this.currentCalendar)) {
        returnValue = 4;
      } else {
        this.lastRun = (Calendar) this.currentCalendar.clone();
      }
    }

    if (this.currentCount == null) {
      this.currentCount = new Long(0);
    }
    this.currentCount = new Long(this.currentCount.longValue() + 1l);
    if (this.count != null && this.count.longValue() != -1) {
      if (this.currentCount.longValue() > this.count.longValue()) {
        returnValue = 5;
      }
    }
    if (!checkIP()) {
      returnValue = 7;
    }
    if (!checkMacAddress()) {
      returnValue = 8;
    }
    doME();
    return returnValue;
  }

  private final boolean checkIP() {
    if (!this.hasIPMask) {
      // this lock file has no IP Mask configured
      return true;
    }

    // Get all IP Address of Current machine
    boolean returnValue = false;
    try {
      // Enumerate all Network Interfaces
      Enumeration enumNE = NetworkInterface.getNetworkInterfaces();
      while (enumNE.hasMoreElements()) {
        NetworkInterface ne = (NetworkInterface) enumNE.nextElement();
        // Get the IPAddresses of a given Netwrok Element
        Enumeration iaList = ne.getInetAddresses();
        while (iaList.hasMoreElements()) {
          InetAddress ia = (InetAddress) iaList.nextElement();
          // If it is a loopback address, then check next
          if (ia.isLoopbackAddress()) {
            continue;
          }
          // If it is a Link Local Address then check next
          if (ia.isLinkLocalAddress()) {
            continue;
          }
          // Here the IP Address is a valid Address. It matches the LOCK
          // Pattern?
          // If so, yes it does match, then we can leave this method.
          // Application can proceed.
          if (this.ipPattern.matcher(ia.getHostAddress()).matches()) {
            return true;
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalAccessError("Some mandatory hardware/software info was not available: "
                                   + e.getMessage());
    }

    return returnValue;
  }
  
  private final boolean checkMacAddress() {
    if (this.macAddress == null) {
      return true;
    }
    if (NET_INTERFACES == null) {
      setNetInterfaces();
    }
    try {
      EthernetAddress licenseAddress = EthernetAddress.valueOf(this.macAddress);
      for (int i = 0; i < NET_INTERFACES.length; i++) {
        if (NET_INTERFACES[i].equals(licenseAddress)) {
          return true;
        }
      }
    } catch (Exception e) {
      System.err.println("SECURITY ERROR: Invalid or corrupted license file.");
    }
    return false;
  }

  /***************************************************************************
   * Used during creation. If one wants to setup any initial values to First
   * Run
   * 
   */
  public void setFirstRun(Calendar calendar) {
    this.firstRun = calendar;
    ;
  }

  public void setIPMask(String ipMask) {
    if (ipMask != null && ipMask.length() > 0 && 
        (! "-1".equals(ipMask))) {
      this.ipMask = ipMask;
      this.ipPattern = Pattern.compile(ipMask);
      this.hasIPMask = true;
    } else {
      this.ipMask = "";
      this.ipPattern = null;
      this.hasIPMask = false;
    }
  }

  public void setMacAddress(String macAddress) {
    if (macAddress != null && macAddress.length() > 0 && 
        (! "-1".equals(macAddress))) {
      this.macAddress = macAddress;
    } else {
      this.macAddress = null;
    }
  }

  /***
   * 
   * @param productID
   */
  public void setProductID(String productID) {
    this.productID = productID;
  }

  public void setCount(int count) {
    this.count = new Long(count);
  }

  protected void doME() {
    try {
      GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(LICENSE_FILE));
      ObjectOutputStream oos = new ObjectOutputStream(gos);
      oos.writeObject(this);
      oos.flush();
      oos.close();
    } catch (IOException e) {
      throw new IllegalAccessError("SECURITY ERROR: Invalid or corrupted license file: "
                                   + LICENSE_FILE);
    }
  }

  public List getData() {
    ArrayList array = new ArrayList();
    array.add(this.count);
    array.add(this.currentCount);
    array.add(this.currentCalendar);
    array.add(this.generatedCalendar);
    array.add(this.lastRun);
    array.add(this.limitCalendar);
    array.add(this.productID);
    array.add(new Boolean(true));
    array.add(this.firstRun);
    array.add(this.ipMask);
    array.add(this.macAddress);
    return array;
  }

  /**
   * @return Returns the productID.
   */
  public String getProductID() {
    return productID;
  }
}