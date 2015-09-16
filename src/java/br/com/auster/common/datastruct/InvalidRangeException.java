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
 * Created on Aug 12, 2005
 */
package br.com.auster.common.datastruct;

/**
 * <p><b>Title:</b> InvalidRangeException</p>
 * <p><b>Description:</b> An exception that is thrown when an invalid range is used</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: InvalidRangeException.java 228 2005-08-12 17:50:42Z etirelli $
 */
public class InvalidRangeException extends RuntimeException {
	
	/**
	 * Default generated serial version id 
	 */
	private static final long serialVersionUID = 6219085945567137414L;

	/**
	 * @inheritDoc
	 */
	public InvalidRangeException() {
		super();
	}
	
	/**
	 * @inheritDoc
	 */
	public InvalidRangeException(String msg) {
		super(msg);
	}

	/**
	 * @inheritDoc
	 */
	public InvalidRangeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @inheritDoc
	 */
	public InvalidRangeException(Throwable cause) {
		super(cause);
	}

}
