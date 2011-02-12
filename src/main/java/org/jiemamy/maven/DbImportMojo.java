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
import java.sql.Connection;

import org.apache.commons.dbutils.DbUtils;
import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SimpleJmMetadata;
import org.jiemamy.composer.ImportException;
import org.jiemamy.composer.importer.DbImporter;
import org.jiemamy.composer.importer.SimpleDbImportConfig;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.serializer.JiemamySerializer;
import org.jiemamy.serializer.SerializationException;
import org.jiemamy.utils.sql.DriverNotFoundException;

/**
 * Import Jiemamy XML model from database.
 * 
 * @goal dbimport
 * @since 0.3
 * @version $Id$
 * @author yamkazu
 */
public class DbImportMojo extends AbstractJiemamyMojo {
	
	/**
	 * dialect class.
	 * 
	 * @parameter default-value="org.jiemamy.dialect.GenericDialect"
	 * @since 0.3
	 */
	private String dialect;
	
	/**
	 * Location of the output model file.
	 * 
	 * @parameter default-value="target/imported.jiemamy"
	 * @since 0.3
	 */
	private File outputFile;
	

	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin:import...");
		
		JiemamyContext context = newJiemamyContext();
		SimpleJmMetadata metadata = new SimpleJmMetadata();
		metadata.setDialectClassName(dialect);
		context.setMetadata(metadata);
		
		Connection connection = null;
		try {
			File parent = outputFile.getParentFile();
			if (parent.exists() == false && parent.mkdirs() == false) {
				throw new IOException("cannot create directory: " + parent);
			}
			
			connection = getConnection();
			
			if (connection == null) {
				getLog().error("connection failed");
				throw new MojoExecutionException("connection failed");
			}
			
			SimpleDbImportConfig config = new SimpleDbImportConfig();
			config.setDriverClassName(getDriver());
			config.setUsername(getUsername());
			config.setPassword(getPassword());
			config.setImportDataSet(false);
			config.setDialect((Dialect) Class.forName(dialect).newInstance());
			config.setUri(getUri());
			
			DbImporter dbImporter = new DbImporter();
			dbImporter.importModel(context, config);
			
			JiemamySerializer serializer = JiemamyContext.findSerializer();
			serializer.serialize(context, new FileOutputStream(outputFile));
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
		} catch (SerializationException e) {
			throw new MojoExecutionException("", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		
		getLog().info("<<<< Exit maven-jiemamy-plugin:import successfully.");
	}
}
