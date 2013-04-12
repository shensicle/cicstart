/* ============================================================
 * ProtocolException.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.file.remote.protocol;

public final class ProtocolException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final boolean retryable;

	public ProtocolException(String message, boolean retryable) {
		super(message);
		this.retryable = retryable;
	}

	public ProtocolException(String message, boolean retryable, Exception cause) {
		super(message, cause);
		this.retryable = retryable;
	}

	public boolean canRetry() {
		return retryable;
	}
}
