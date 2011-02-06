/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/09/22
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

import java.io.File;
import java.io.FileOutputStream;
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
import org.jiemamy.composer.importer.DbImporter;
import org.jiemamy.composer.importer.SimpleDbImportConfig;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.serializer.SerializationException;
import org.jiemamy.utils.sql.DriverNotFoundException;
import org.jiemamy.utils.sql.DriverUtil;

/**
 * Import from Database to Jiemamy Model.
 * 
 * @goal import
 * @since 0.3
 * @version $Id: ImportMojo.java 3676 2009-09-24 16:24:35Z yamkazu $
 * @author yamkazu
 */
public class ImportMojo extends AbstractJiemamyMojo {
	
	/**
	 * dialect class.
	 * 
	 * @parameter default-value="org.jiemamy.dialect.generic.GenericDialect"
	 * @since 0.3
	 */
	private String dialect;
	
	/**
	 * Database Driver
	 * 
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String driver;
	
	/**
	 * Database Uri
	 * 
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String uri;
	
	/**
	 * Database Username
	 * 
	 * @parameter 
	 * @required
	 * @since 0.3
	 */
	private String username;
	
	/**
	 * Database Password
	 * 
	 * @parameter
	 * @required
	 * @since 0.3
	 */
	private String password;
	
	/**
	 * Location of the output model file.
	 * 
	 * @parameter default-value="target/imported.jiemamy"
	 * @since 0.3
	 */
	private File outputFile;
	

	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin:export...");
		
		JiemamyContext context = newJiemamyContext();
		SimpleJmMetadata metadata = new SimpleJmMetadata();
		metadata.setDialectClassName(dialect);
		context.setMetadata(metadata);
		
		SimpleDbImportConfig config = new SimpleDbImportConfig();
		
		Connection connection = null;
		try {
			config.setDriverClassName(driver);
			config.setUsername(username);
			config.setPassword(password);
			config.setImportDataSet(false);
			config.setDialect((Dialect) Class.forName(dialect).newInstance());
			config.setUri(uri);
			
			Properties props = new Properties();
			props.setProperty("user", config.getUsername());
			props.setProperty("password", config.getPassword());
			
			URL[] paths = config.getDriverJarPaths();
			String className = config.getDriverClassName();
			
			Driver driverClass = DriverUtil.getDriverInstance(paths, className);
			
			connection = driverClass.connect(config.getUri(), props);
			
			if (connection == null) {
				getLog().error("connection failed");
				throw new MojoExecutionException("connection failed");
			}
			
			DbImporter dbImporter = new DbImporter();
			dbImporter.importModel(context, config);
			
			JiemamyContext.findSerializer().serialize(context, new FileOutputStream(outputFile));
		} catch (ImportException e) {
			throw new MojoExecutionException("", e);
		} catch (InstantiationException e) {
			throw new MojoExecutionException("", e);
		} catch (IllegalAccessException e) {
			throw new MojoExecutionException("", e);
		} catch (DriverNotFoundException e) {
			throw new MojoExecutionException("", e);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("", e);
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		} catch (SQLException e) {
			throw new MojoExecutionException("", e);
		} catch (SerializationException e) {
			throw new MojoExecutionException("", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		
		getLog().info("<<<< Exit maven-jiemamy-plugin:import successfully.");
	}
}
