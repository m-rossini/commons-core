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
 * Created on 16/11/2004
 */
package br.com.auster.common.xml.sax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xalan.xsltc.trax.DOM2SAX;
import org.apache.xalan.xsltc.trax.SAX2DOM;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.util.I18n;

/**
 * This class is used to synchronize several sources of SAX events
 * into several outputs.
 * 
 * @author Ricardo Barone
 * @version $Id: ContentHandlerManager.java 322 2006-09-21 15:28:21Z rbarone $
 */
public final class ContentHandlerManager {

	/**
	 * This class is used to wrap the calls to the output content
	 * handler. It is needed to avoid calling the start and end
	 * document methods too many time when we are working with lots of
	 * inputs.
	 */
	private final class ContentHandlerOutput implements ContentHandler {

		private final ContentHandler output;

		public ContentHandlerOutput(ContentHandler output) {
			this.output = output;
		}

		/******************************************/
		/* Start of ContentHandler implementation */
		/******************************************/

		public final void characters(char[] ch, int start, int length)
				throws SAXException {
			this.output.characters(ch, start, length);
		}

		public final void endDocument() throws SAXException {
		}

		public final void realEndDocument() throws SAXException {
			this.output.endDocument();
		}

		public final void endElement(	String namespaceURI,
																	String localName,
																	String qName) throws SAXException {
			this.output.endElement(namespaceURI, localName, qName);
		}

		public final void endPrefixMapping(String prefix) throws SAXException {
			this.output.endPrefixMapping(prefix);
		}

		public final void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			this.output.ignorableWhitespace(ch, start, length);
		}

		public final void processingInstruction(String target, String data)
				throws SAXException {
			this.output.processingInstruction(target, data);
		}

		public final void setDocumentLocator(Locator locator) {
			this.output.setDocumentLocator(locator);
		}

		public final void skippedEntity(String name) throws SAXException {
			this.output.skippedEntity(name);
		}

		public final void startDocument() throws SAXException {
		}

		public final void realStartDocument() throws SAXException {
			this.output.startDocument();
		}

		public final void startElement(	String namespaceURI,
																		String localName,
																		String qName,
																		Attributes atts) throws SAXException {
			this.output.startElement(namespaceURI, localName, qName, atts);
		}

		public final void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			this.output.startPrefixMapping(prefix, uri);
		}
	}

	/**
	 * This class is used to receive SAX events and copy/replicate
	 * them to a list of output content handlers.
	 *
	 * It's possible to use the element "set" in the namespace
	 * "splitter" to set the output list using the input XML. In this
	 * element, use the attribute "output-list" to defined the list
	 * (separated by commas). The names in this list must be the
	 * output's name for each output appended using the
	 * <code>addOutput</code> method. Use the attribute
	 * "keep-last-list" with the value "true" to keep the last list
	 * and just append some others.
	 */
	private final class ContentHandlerSplitter implements ContentHandler {

		public static final String SPLITTER_NAMESPACE_URI = "http://www.auster.com.br/common/xml/splitter";

		public static final String DEFINE_OUTPUT_ELEMENT = "set";

		public static final String KEEP_LAST_OUTPUT_LIST_ATTR = "keep-last-list";

		public static final String OUTPUT_LIST_ATTR = "output-list";

		private final ContentHandlerManager manager;

		private Map outputMap;

		private final Stack outputStack = new Stack();

		private ContentHandler[] outputArray;

		private final I18n i18n = I18n.getInstance(ContentHandlerSplitter.class);

		private final Logger log = LogFactory
				.getLogger(ContentHandlerSplitter.class);

		// Identifies it the array of output content handlers is already set. This is
		//   needed to handle the setDocumentLocator() callback, which is only fired
		//   when were are handling serialized XML files. For streamed ones this method
		//	 is not called. Check XML spec. for more details!!!
		private boolean outputsSet = false;

		public ContentHandlerSplitter(ContentHandlerManager manager) {
			this.manager = manager;
		}

		/****************************************************************************************/
		/* Start of ContentHandler implementation to copy SAX events to others ContentHandlers. */
		/****************************************************************************************/

		public final void characters(char[] ch, int start, int length)
				throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].characters(ch, start, length);
			}
		}

		public final void endDocument() throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].endDocument();
			}
			this.manager.releaseOutput(this);
		}

		public final void endElement(	String namespaceURI,
																	String localName,
																	String qName) throws SAXException {
			if (namespaceURI.equals(SPLITTER_NAMESPACE_URI)
					&& localName.equals(DEFINE_OUTPUT_ELEMENT)) {
				this.outputArray = (ContentHandler[]) this.outputStack.pop();
			} else {
				for (int i = 0; i < this.outputArray.length; i++) {
					this.outputArray[i].endElement(namespaceURI, localName, qName);
				}
			}
		}

		public final void endPrefixMapping(String prefix) throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].endPrefixMapping(prefix);
			}
		}

		public final void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].ignorableWhitespace(ch, start, length);
			}
		}

		public final void processingInstruction(String target, String data)
				throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].processingInstruction(target, data);
			}
		}

		public final void setDocumentLocator(Locator locator) {
			try {
				if (!this.outputsSet) {
					prepareOutputHandlers();
				}
				for (int i = 0; i < this.outputArray.length; i++) {
					this.outputArray[i].setDocumentLocator(locator);
				}
			} catch (SAXException saxe) {
				log.warn(i18n.getString("xmlLocatorNotSet"), saxe);
			}
		}

		public final void skippedEntity(String name) throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].skippedEntity(name);
			}
		}

		public final void startDocument() throws SAXException {
			// Gets all the outputs
			if (!this.outputsSet) {
				prepareOutputHandlers();
			}
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].startDocument();
			}
		}

		public final void startElement(	String namespaceURI,
																		String localName,
																		String qName,
																		Attributes atts) throws SAXException {
			// If it was requested to redirect the output to only some content handlers
			if (namespaceURI.equals(SPLITTER_NAMESPACE_URI)
					&& localName.equals(DEFINE_OUTPUT_ELEMENT)) {
				final List outputList = new ArrayList();

				// If it was requested to add some new content handler to the output list, keep the last output list
				if (atts.getValue(KEEP_LAST_OUTPUT_LIST_ATTR).equals("true")) {
					outputList.addAll(Arrays.asList(this.outputArray));
				}

				// The output list is separated by commas
				final String[] chList = atts.getValue(OUTPUT_LIST_ATTR).split(",");
				for (int i = 0; i < chList.length; i++) {
					Object ch = this.outputMap.get(chList[i].trim());
					if (ch == null) {
						throw new SAXException(i18n.getString("chNotFound", chList[i]));
					}

					if (!outputList.contains(ch)) {
						outputList.add(ch);
					}
				}
				// At this point we already have the new output list
				this.outputStack.push(this.outputArray);
				this.outputArray = (ContentHandler[]) outputList.toArray(new ContentHandler[0]);
			} else {
				for (int i = 0; i < this.outputArray.length; i++) {
					this.outputArray[i].startElement(namespaceURI, localName, qName, atts);
				}
			}
		}

		public final void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			for (int i = 0; i < this.outputArray.length; i++) {
				this.outputArray[i].startPrefixMapping(prefix, uri);
			}
		}

		private void prepareOutputHandlers() throws SAXException {
			this.outputMap = this.manager.getOutputMap(this);

			// Starts the stack and content handler output list
			this.outputStack.clear();
			this.outputArray = (ContentHandler[]) 
				this.outputMap.values().toArray(new ContentHandler[0]);

			// marking the output content handlers were set as expected 
			this.outputsSet = true;
		}
	}

	/******************************************/
	/* CONTENT HANDLER MANAGER IMPLEMENTATION */
	/******************************************/

	private int currentOrder, contentHandlerCounter;

	private final String firstTag;

	private final boolean keepOrder; // Indicates that the output will respect the getContentHandler() calls order.

	private final boolean fastMode; // Indicates that this object will not wait to use the output,

	private boolean useLock; // Used to break some wait() loops, to help bug resolution.

	private boolean outputInUse; // Indicates if the output ContentHandler objects are being used by some thread.

	// caching data for later use.
	private volatile boolean autoEnd = true; // Indicates if the endDocument() method for all the output 

	// content handlers will be called when the last input content
	// handler finishes or when the method noMoreSources() is called.
	private volatile boolean noMoreSources = false; // If false the user may call the method getContentHandler().

	// If true, this instance may assume that the method will no more
	// be called before the end of the processing (call to reset()).
	private final List inputList = new ArrayList();

	private final Map outputMap = new HashMap();

	private final Map cacheByCH = new HashMap();

	private final Object syncObj = new Object();

	private final I18n i18n = I18n.getInstance(ContentHandlerManager.class);

	public ContentHandlerManager(	String firstTag,
																boolean keepOrder,
																boolean fastMode) {
		this.firstTag = firstTag;
		this.keepOrder = keepOrder;
		this.fastMode = fastMode;
		this.reset();
	}

	/**
	 * Sets if the <code>endDocument()</code> method for all the output
	 * ContentHandlers will be called when the last input ContentHandler finishes
	 * its events, or when the method noMoreSources() is called for this run.
	 * 
	 * Use <code>true</code> if all calls to <code>getContentHandler()</code>
	 * will be done before the events start to happen. This is the default value.
	 * 
	 * Use <code>false</code> if the method <code>getContentHandler()</code>
	 * may be called at any time. When no more ContentHandler input will be get,
	 * call the method <code>noMoreSource()</code> to indicate that to this
	 * instance.
	 */
	public final void setAutoEnd(boolean value) throws SAXException {
		synchronized (this.syncObj) {
			this.autoEnd = value;

			// If the auto end was set to true, tries to finish
			// the all the output documents
			if (this.autoEnd) {
				this.endOutputDocument();
			}
		}
	}

	/**
	 * This method is used to notify this instance that the method
	 * <code>getContentHandler()</code> will no more be called before the end of
	 * the input SAX events.
	 */
	public final void noMoreSources() throws SAXException {
		synchronized (this.syncObj) {
			this.noMoreSources = true;
			// Tries to finish all the output documents
			this.endOutputDocument();
		}
	}

	/**
	 * Adds an output for this ContentHandler manager for joining SAX events into.
	 * 
	 * @param outputName
	 *          the name of the ContentHandler added as output.
	 * @param output
	 *          the content handler to be used as an output for all the SAX events
	 *          generated by the ContentHandler's returned by the method
	 *          <code>getContentHandler()</code>.
	 * @throws IllegalStateException
	 *           if this method is called between the start and end document
	 *           events, i.e. while the engine is running.
	 */
	public final void addOutput(String outputName, ContentHandler output) {
		if (this.outputInUse || this.currentOrder > 0) {
			throw new IllegalStateException(i18n.getString("noMoreResults"));
		}

		if (outputName == null || outputName.length() == 0) {
			throw new IllegalArgumentException(i18n.getString("invalidOutputName"));
		}

		this.outputMap.put(outputName, new ContentHandlerOutput(output));
	}

	/**
	 * Calling this method, all the internal state of this object will be reset
	 * and the locks will be released. This is useful if it is wanted to reuse it.
	 */
	public final void reset() {
		this.releaseLocks();
		this.currentOrder = 0;
		this.contentHandlerCounter = 0;
		this.useLock = true;
		this.outputInUse = false;
		this.noMoreSources = false;
		this.inputList.clear();
		this.outputMap.clear();
		this.cacheByCH.clear();
	}

	/**
	 * Gets a new ContentHandler object to send SAX events. The events sent to it
	 * will be redirected to the output defined in the <code>setOutput</code>
	 * method.
	 * 
	 * @return a new ContentHandler object, to be used to send SAX events to the
	 *         output in a synchronized manner.
	 * @throws IllegalStateException
	 *           if the method <code>noMoreSources()</code> was called to
	 *           indicate that this method will no more be called before the
	 *           ending of the current processing (i.e. the ending of the input
	 *           SAX events).
	 */
	public final ContentHandler getContentHandler() {
		if (this.noMoreSources) {
			throw new IllegalStateException(i18n.getString("noMoreSources"));
		}

		ContentHandler ch = new ContentHandlerSplitter(this);
		this.inputList.add(ch);
		this.contentHandlerCounter++;
		return ch;
	}

	/**
	 * This method is used internally by the ContentHandler created by the method
	 * <code>getContentHandler()</code>. It returns the output that they will
	 * use, or block them until they may start sending SAX events to the output.
	 * It also starts the output document if this is the first call to it after a
	 * <code>reset()</code> call.
	 * 
	 * @param ch
	 *          the ContentHandler that is asking for the output.
	 * @throws IllegalArgumentException
	 *           if the argument was not gotten from the method
	 *           <code>getContentHandler()</code> of this object, or if a call
	 *           to <code>reset()</code> was done after a call to that method.
	 * @return the output map set in the <code>addOutput</code> method.
	 */
	private final Map getOutputMap(ContentHandlerSplitter ch)
			throws SAXException, IllegalArgumentException {
		final int order = this.inputList.indexOf(ch);
		// Sanity check
		if (order == -1) {
			throw new IllegalArgumentException(i18n.getString("chNotInList"));
		}

		// Synchronization block, used to decide which ContentHandler will use the output.
		synchronized (this.syncObj) {
			if (this.fastMode) {
				if (this.outputInUse || (this.keepOrder && order != this.currentOrder)) {
					// If in fast mode, let's store the output in an
					// internal structure if the output is being used
					// by other input ContentHandler or it is not the
					// moment to send its output.
					Map outputCacheMap = this.getOutputCacheMap(ch);
					this.cacheByCH.put(ch, outputCacheMap);
					return outputCacheMap;
				}
			} else {
				// We don't want to go so fast, so let's synchronize the content handlers.
				if (this.keepOrder) {
					// If wants to keep the content handler order, wait until it's its time.
					while (order != this.currentOrder && this.useLock) {
						try {
							this.syncObj.wait();
						} catch (InterruptedException e) {
						}
					}
				} else {
					// If not, just wait until no other content handler is using the output.
					while (this.outputInUse && this.useLock) {
						try {
							this.syncObj.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}

			// We found the first content handler, and no one is using
			// the output, so let's start the output document.
			if (this.currentOrder == 0) {
				// Starts the document for every content handler in the output list
				for (Iterator it = this.outputMap.values().iterator(); it.hasNext();) {
					ContentHandlerOutput output = (ContentHandlerOutput) it.next();
					output.realStartDocument();
					if (this.firstTag != null && this.firstTag.length() > 0) {
						output.startElement("", this.firstTag, this.firstTag,
																new AttributesImpl());
					}
				}
			}

			this.outputInUse = true;
		}

		return this.outputMap;
	}

	/**
	 * Creates an output cache map that corresponds to the real output map. This
	 * is used to simulate it and store the result for futher use. 
	 * 
	 * USED BY THE FAST/CACHED MODE.
	 */
	private final Map getOutputCacheMap(ContentHandler ch) throws SAXException {
		final Map outputCacheMap = new HashMap();

		// Duplicates the output map in a DOM version
		for (Iterator it = this.outputMap.keySet().iterator(); it.hasNext();) {
			String outputName = (String) it.next();

			try {
				outputCacheMap.put(outputName, new SAX2DOM());
			} catch (ParserConfigurationException e) {
				throw new SAXException(e);
			}
		}

		return outputCacheMap;
	}

	/**
	 * This method is used internally by the ContentHandler created by the method
	 * <code>getContentHandler()</code>. It notifies other ContentHandler's
	 * that are blocked in the <code>getOutput</code> method to try to the get
	 * output again. It also ends the output document if the last ContentHandler
	 * is calling this.
	 * 
	 * @param ch
	 *          the ContentHandler that is releasing the output.
	 * @throws IllegalArgumentException
	 *           if the argument was not gotten from the method
	 *           <code>getContentHandler()</code> of this object, or if a call
	 *           to <code>reset()</code> was done after a call to that method.
	 */
	private synchronized final void releaseOutput(ContentHandlerSplitter ch)
			throws SAXException {
		final int order = this.inputList.indexOf(ch);
		// Sanity check
		if (order == -1) {
			throw new IllegalArgumentException(i18n.getString("chNotInList"));
		}

		synchronized (this.syncObj) {
			if (this.fastMode) {
				boolean outputThisCache = false;
				// In fast mode we have one content handler wrinting
				// to the output and the others caching their results.
				if (this.cacheByCH.containsKey(ch)) {
					// We have a complete result cached.
					if (!(this.outputInUse || (this.keepOrder && order != this.currentOrder))) {
						// No content handler is using the output, and
						// it's time to send this cache to the output (if
						// so defined), then do it.
						this.outputInUse = true;
						this.flushOutputCacheMap((Map) this.cacheByCH.get(ch));
						this.cacheByCH.put(ch, null);
						this.outputInUse = false;
					}
				}
			} else {
				if (this.keepOrder) {
					// Sanity check
					if (order != this.currentOrder) {
						throw new SAXException(i18n.getString("chFinishedOutOfOrder", 
						                                      new Integer(order),
						                                      new Integer(this.currentOrder)));
					}
					// Not in fast mode, and the order must remain.
					this.syncObj.notifyAll();
				} else {
					// Not in fast mode, and the order does not matter.
					this.syncObj.notify();
				}

				this.outputInUse = false;
			}
			this.currentOrder++;

			if (this.autoEnd || this.noMoreSources) {
				this.endOutputDocument();
			}
		}
	}

	/**
	 * This method is used to send the SAX event <code>endDocument</code> to
	 * every ContentHandler output. After this call, no more events may be
	 * generated before a call to <code>reset()</code>.
	 * 
	 * @return true if the SAX event <code>endDocument()</code> was generated to
	 *         every output. False if some internal condition avoid to generate
	 *         the event (ex. the auto end is false, or there are some input
	 *         content handlers sending events).
	 */
	private final boolean endOutputDocument() throws SAXException {
		// We found the last content handler, so let's finish the output document.
		if (this.currentOrder == this.contentHandlerCounter) {
			// There is no more content handlers to finish their job, so we may flush all remaining caches.
			if (this.fastMode)
				for (Iterator it = this.cacheByCH.keySet().iterator(); it.hasNext();) {
					Object key = it.next();
					Map outputCacheMap = (Map) this.cacheByCH.get(key);
					this.flushOutputCacheMap(outputCacheMap);
					this.cacheByCH.put(key, null);
				}

			// Ends the root element, if needed, and ends the document for every output handler
			boolean endElement = (this.firstTag != null && this.firstTag.length() > 0);
			for (Iterator it = this.outputMap.values().iterator(); it.hasNext();) {
				ContentHandlerOutput output = (ContentHandlerOutput) it.next();
				if (endElement) {
					output.endElement("", this.firstTag, this.firstTag);
				}
				output.realEndDocument();
			}
			this.reset();
			return true;
		} else
			return false;
	}

	/**
	 * Outputs all the SAX events cached in the output cache map to the real
	 * output content handlers. 
	 * 
	 * USED BY THE FAST/CACHED MODE.
	 */
	private final void flushOutputCacheMap(Map outputCacheMap)
			throws SAXException {
		for (Iterator it = this.outputMap.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			ContentHandler output = (ContentHandler) this.outputMap.get(key);
			SAX2DOM cache = (SAX2DOM) outputCacheMap.get(key);
			if (cache != null && output != null) {
				DOM2SAX converter = new DOM2SAX(cache.getDOM());
				converter.setContentHandler(output);
				try {
					converter.parse((InputSource) null);
				} catch (IOException e) {
					throw new SAXException(e);
				}
			}
		}
	}

	/**
	 * This method is used to release the ContentHandlers that are locked. This is
	 * very useful if some error ocurrs and some threads remains blocked by these
	 * locks.
	 * 
	 * DO NOT CALL THIS METHOD IN NORMAL OPERATIONS!
	 */
	public final void releaseLocks() {
		synchronized (this.syncObj) {
			this.useLock = false;
			this.syncObj.notifyAll();
		}
	}
}
