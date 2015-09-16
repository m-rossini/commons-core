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
package br.com.auster.common.res;

import java.util.ListResourceBundle;

/**
 * @author Ricardo Barone
 * @version $Id: MessagesBundle.java 298 2006-08-28 19:56:54Z framos $
 */
public class MessagesBundle extends ListResourceBundle {

   public final Object[][] getContents()
   {
      return contents;
   }

   static final Object[][] contents = {
     // ConnectionManager.java
     {"reloadConnPoolConf", "Reloading Connection Pool Manager Configuration..."},
     {"done", "Done."},
     {"addingServer", "Adding the server to the list of servers: {0}"},
     {"couldntAddServer", "Could not add the server named {0} to the list of servers."},
     {"errorConnecting", "Error opening connection to the server {0}."},
     {"serversDown", "Servers down or unreachable."},
     {"problemDisconnectingServer", "Problems disconnecting from the server {0}."},

     // ConnectionPoolManager.java
     {"closingOpenedServerConns", "Closing all opened Server connections..."},
     {"problemClosingConnection", "Problems closing the connection {0}"},

     // SQLConnectionManager.java
     {"couldntFindStat", "Could not find the prepared statement named \"{0}\"."},
     {"noInitialContext", "Could not get an initial context. Trying the DBCP directly."},

     // SQLStatement.java
     {"invalidParamValue", "The value for the parameter \"{0}\" is invalid for statement \"{1}\"."},
     {"twoParamSameIndex", "There is 2 parameters with the same index for statement \"{0}\"."},
     {"missingParam", "The parameter \"{0}\" is missing in the statement \"{1}\"."},
     {"paramWrongType", "The parameter {0} have a wrong type ({1}) for statement \"{2}\"."},
     {"missingParamStat", "Some parameter is missing in the statement \"{0}\"."},
     {"wrongNumParam", "Wrong number of parameters for statement \"{0}\". Needed: {1}, received: {2}."},
     {"name", "Name: {0}"},
     {"invalidFileName", "Invalid file name: {0}"},
     {"addStatFile", "Adding the following PREPARED STATEMENTS from the file {0}:"},
     {"fileAlreadyParsed", "File already parsed: {0}"},
     {"sqlError", "Got an error while executing statement \"{0}\" with params \"{1}\"."},
  	 
     // DateFormat.java
     { "invalidPattern", "The given pattern is invalid: {0}" },

     // DOMUtils
     { "attrNotInt",
     "The attribute \"{0}\" inside the element \"{1}\" is not an integer." },
     { "attrNotDefined",
     "The attribute \"{0}\" was not defined inside the element \"{1}\"." },
     { "elementNotFound",
     "The element \"{0}\" was not found inside the element \"{1}\"." },
     { "NSelementNotFound",
     "The element \"{0}\" in namespace \"{1}\" was not found inside the element \"{2}\"." },

     // ContentHandlerManager.java
     { "noMoreResults",
     "No more ContentHandlers may be added to the output before the ending of the current processing." },
     { "noMoreSources",
     "No more ContentHandlers may be created before the ending of the current processing." },
     { "invalidOutputName", "The output name must not be null or empty." },
     { "chNotFound", "The content handler named \"{0}\" was not found." },
     { "chNotInList",
     "The argument is not in the ContentHandler list for this manager." },
     { "chFinishedOutOfOrder",
     "ContentHandler of order {0} finished before current ContentHandler of order {1}." },
     { "xmlLocatorNotSet", "XML Document locator not set due to SAX exception"},

     { "jndiExpected", "Expected JNDI path to mail service"},
     { "mailServerConfigExpected", "Expected properties for locating mail server"} };

}