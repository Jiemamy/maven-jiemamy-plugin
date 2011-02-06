/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/04/12
 *
 * This file is part of Jiemamy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.jiemamy.maven;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SimpleJmMetadata;
import org.jiemamy.composer.ImportException;
import org.jiemamy.composer.importer.SimpleDbImportConfig;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.utils.DbCleaner;
import org.jiemamy.utils.sql.DriverNotFoundException;
import org.jiemamy.utils.sql.DriverUtil;

/**
 * Goal which clean Database.
 * 
 * @goal clean
 * @author daisuke
 * @version $Id: CleanMojo.java 3674 2009-09-24 16:09:50Z yamkazu $
 * @since 0.2
 */
public class CleanMojo extends AbstractJiemamyMojo {
	
	private static final String DIALECT = "org.jiemamy.dialect.GenericDialect";
	
	/**
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String driver;
	
	/**
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String uri;
	
	/**
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String username;
	
	/**
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String password;
	

	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin:clean...");
		
		JiemamyContext context = newJiemamyContext();
		SimpleJmMetadata metadata = new SimpleJmMetadata();
		metadata.setDialectClassName(DIALECT);
		context.setMetadata(metadata);
		
		SimpleDbImportConfig config = new SimpleDbImportConfig();
		
		Connection connection = null;
		try {
			config.setDriverClassName(driver);
			config.setUsername(username);
			config.setPassword(password);
			config.setImportDataSet(false);
			config.setDialect((Dialect) Class.forName(DIALECT).newInstance());
			config.setUri(uri);
			
			Properties props = new Properties();
			props.setProperty("user", config.getUsername());
			props.setProperty("password", config.getPassword());
			
			URL[] paths = config.getDriverJarPaths();
			String className = config.getDriverClassName();
			
			Driver driver = DriverUtil.getDriverInstance(paths, className);
			
			connection = driver.connect(config.getUri(), props);
			
			if (connection == null) {
				getLog().error("connection failed");
				throw new MojoExecutionException("connection failed");
			}
			
			config.setDialect(context.findDialect());
			config.setSchema(context.getMetadata().getSchemaName());
			
			DbCleaner.clean(config);
		} catch (DriverNotFoundException e) {
			throw new MojoExecutionException("Driver not found: " + config.getDriverClassName(), e);
		} catch (InstantiationException e) {
			throw new MojoExecutionException("", e);
		} catch (IllegalAccessException e) {
			throw new MojoExecutionException("", e);
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		} catch (SQLException e) {
			throw new MojoExecutionException("", e);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("", e);
		} catch (ImportException e) {
			throw new MojoExecutionException("", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		getLog().info("<<<< Exit maven-jiemamy-plugin:clean successfully.");
	}
	
}
