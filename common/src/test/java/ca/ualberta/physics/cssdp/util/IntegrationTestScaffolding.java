/* ============================================================
 * IntegrationTestScaffolding.java
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
package ca.ualberta.physics.cssdp.util;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.RestAssuredConfig.config;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;

import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.JSONObjectMapperProvider;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;

import com.coderod.db.migrations.Migrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import com.jayway.restassured.response.Response;

public abstract class IntegrationTestScaffolding {

	private static Server allServers;

	public IntegrationTestScaffolding() {
		
		ApplicationProperties.dropOverrides();
		
		// initialize default properties
		Common.properties();

		// override the database connection url to point to an in-memory
		// database
		Properties overrides = new Properties();
		overrides.put("common.logback.configuration",
				"src/test/resources/logback-test.xml");
		overrides
				.setProperty("common.hibernate.connection.url",
						"jdbc:h2:mem:test;DB_CLOSE_DELAY=1000;MODE=PostgreSQL;TRACE_LEVEL_FILE=0");
		ApplicationProperties.overrideDefaults(overrides);
	}
	
	// start an auth server for tests that depend on the auth resources
	@Before
	public void setup() {


		String url = Common.properties().getString("hibernate.connection.url");
		String driver = Common.properties().getString(
				"hibernate.connection.driver_class");
		String user = Common.properties().getString(
				"hibernate.connection.username");
		String password = Common.properties().getString(
				"hibernate.connection.password");
		String scriptsDir = "../database/migrations";

		Migrator migrator = new Migrator(url, driver, user, password,
				scriptsDir);
		migrator.initDb();
		migrator.migrateUpAll();

		config().objectMapperConfig(
				new ObjectMapperConfig()
						.jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {

							@Override
							public ObjectMapper create(
									@SuppressWarnings("rawtypes") Class cls,
									String charset) {
								return new JSONObjectMapperProvider().get();
							}
						}));

		if (allServers == null || !allServers.isStarted()) {
			// configure Jetty as an embedded web application server
			allServers = new Server();
			allServers.setStopAtShutdown(true);
			allServers.setGracefulShutdown(1000);

			SocketConnector connector = new SocketConnector();
			connector.setPort(8080);
			allServers.addConnector(connector);

			ContextHandlerCollection contexts = new ContextHandlerCollection();
			contexts.addHandler(new DefaultHandler());
			
			// this one is relative to the project we are testing
			WebAppContext context = new WebAppContext();
			context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
			context.setResourceBase("src/main/webapp");
			context.setInitParameter("application.properties", "unused");
			String thisContext = getComponetContext();
			context.setContextPath(thisContext);
			context.setParentLoaderPriority(true);
			contexts.addHandler(context);

			// and these blocks setup the other contexts that we talk to during tests and operations
			if (!thisContext.equals("/auth")) {
				WebAppContext auth = new WebAppContext();
				auth.setDescriptor("../auth/src/main/webapp/WEB-INF/web.xml");
				auth.setResourceBase("../auth/src/main/webapp");
				auth.setInitParameter("application.properties", "unused");
				auth.setContextPath("/auth");
				auth.setParentLoaderPriority(true);
				contexts.addHandler(auth);
			}

			if (!thisContext.equals("/file")) {
				WebAppContext file = new WebAppContext();
				file.setDescriptor("../file/src/main/webapp/WEB-INF/web.xml");
				file.setResourceBase("../file/src/main/webapp");
				auth.setInitParameter("application.properties", "unused");
				file.setContextPath("/file");
				file.setParentLoaderPriority(true);
				contexts.addHandler(file);
			}

			if (!thisContext.equals("/catalogue")) {

				WebAppContext catalogue = new WebAppContext();
				catalogue
						.setDescriptor("../catalogue/src/main/webapp/WEB-INF/web.xml");
				catalogue.setResourceBase("../catalogue/src/main/webapp");
				auth.setInitParameter("application.properties", "unused");
				catalogue.setContextPath("/catalogue");
				catalogue.setParentLoaderPriority(true);
				contexts.addHandler(catalogue);
			}

			if (!thisContext.equals("/vfs")) {
				WebAppContext vfs = new WebAppContext();
				vfs.setDescriptor("../vfs/src/main/webapp/WEB-INF/web.xml");
				vfs.setResourceBase("../vfs/src/main/webapp");
				auth.setInitParameter("application.properties", "unused");
				vfs.setContextPath("/vfs");
				vfs.setParentLoaderPriority(true);
				contexts.addHandler(vfs);
			}

			allServers.setHandler(contexts);

			try {
				allServers.start();
//				System.err.println(allServers.dump());
			} catch (Exception e) {
				Throwables.propagate(e);
			}

		}
	}

	protected abstract String getComponetContext();
	protected String baseUrl() {
		return getComponetContext() + "/api";
	}
	
	protected User setupDataManager() {

		User newDataManager = new User();
		newDataManager.setName("Data Manager");
		newDataManager.setDeleted(false);
		newDataManager.setEmail("datamanager@nowhere.com");
		newDataManager.setInstitution("institution");
		newDataManager.setPassword("password");
		newDataManager.setRole(Role.DATA_MANAGER);

		String authUrl = Common.properties().getString("auth.api.url");
		Response res = given().content(newDataManager).and()
				.contentType("application/json").post(authUrl + "/user.json");

		String location = res.getHeader("location");
		User dataManager = given().contentType(ContentType.JSON).get(location)
				.as(User.class);

		return dataManager;
	}

	/**
	 * Login into the system. Can't use cached users in this class directly
	 * because the response of getting the user info masks the password, so you
	 * need prior knowledge of the passwords (by looking at the setup users
	 * method to find them). Assumes Auth is running.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	protected String login(String username, String password) {
		String authUrl = Common.properties().getString("auth.api.url");
		return given().formParam("username", username)
				.formParam("password", password)
				.post(authUrl + "/session.json").asString();
	}
}
