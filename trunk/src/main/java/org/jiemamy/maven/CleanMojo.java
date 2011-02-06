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

import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SimpleJmMetadata;
import org.jiemamy.composer.ImportException;
import org.jiemamy.composer.importer.SimpleDbImportConfig;
import org.jiemamy.utils.DbCleaner;
import org.jiemamy.utils.sql.DriverNotFoundException;

/**
 * Goal which clean Database.
 * 
 * @goal clean
 * @author daisuke
 * @version $Id: CleanMojo.java 3674 2009-09-24 16:09:50Z yamkazu $
 * @since 0.2
 */
public class CleanMojo extends AbstractJiemamyMojo {
	
	/**
	 * dialect class.
	 * 
	 * @parameter default-value="org.jiemamy.dialect.GenericDialect"
	 * @since 0.3
	 */
	private String dialect;
	

	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin:clean...");
		
		try {
			JiemamyContext context = newJiemamyContext();
			SimpleJmMetadata metadata = new SimpleJmMetadata();
			metadata.setDialectClassName(dialect);
			context.setMetadata(metadata);
			
			SimpleDbImportConfig config = new SimpleDbImportConfig();
			config.setDriverClassName(getDriver());
			config.setUsername(getUsername());
			config.setPassword(getPassword());
			config.setImportDataSet(false);
			config.setUri(getUri());
			config.setDialect(context.findDialect());
			config.setSchema(context.getMetadata().getSchemaName());
			
			DbCleaner.clean(config);
		} catch (DriverNotFoundException e) {
			throw new MojoExecutionException("Driver not found: " + getDriver(), e);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("", e);
		} catch (ImportException e) {
			throw new MojoExecutionException("", e);
		}
		getLog().info("<<<< Exit maven-jiemamy-plugin:clean successfully.");
	}
	
}
