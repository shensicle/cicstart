/* ============================================================
 * AuthServer.java
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

package ca.ualberta.physics.cssdp.auth;

import ca.ualberta.physics.cssdp.configuration.ComponentProperties;


public class AuthServer extends ComponentProperties {

	public static AuthServer properties() {
		return (AuthServer) ComponentProperties.properties(AuthServer.class);
	}
	
}
