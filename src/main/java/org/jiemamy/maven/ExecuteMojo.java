/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/03/03
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.DiagramFacet;
import org.jiemamy.JiemamyContext;
import org.jiemamy.SqlFacet;
import org.jiemamy.composer.importer.DefaultDatabaseImportConfig;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.EmitConfig;
import org.jiemamy.model.sql.SqlStatement;
import org.jiemamy.serializer.SerializationException;
import org.jiemamy.utils.sql.DriverNotFoundException;
import org.jiemamy.utils.sql.DriverUtil;

/**
 * jerファイルから生成したSQLをDBに適用するゴール。
 * 
 * @goal execute
 * @author daisuke
 * @author yamkazu
 * @version $Id: ExecuteMojo.java 3674 2009-09-24 16:09:50Z yamkazu $
 * @since 0.3
 */
public class ExecuteMojo extends AbstractJiemamyMojo {
	
	/** ConfigKey: 出力データセット番号 (Integer) */
	private static final String DATA_SET_INDEX = "dataSetIndex";
	
	/** ConfigKey: DROP文を出力するかどうか (Boolean) */
	private static final String DROP = "drop";
	
	/** ConfigKey: DROP文を出力するかどうか (Boolean) */
	private static final String SCHEMA = "schema";
	
	/**
	 * Location of the input model file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 * @since 0.3
	 */
	private File inputFile;
	
	/**
	 * Parameter for exorter.
	 * 
	 * @parameter
	 * @since 0.3
	 */
	private Map<String, Object> parameter;
	
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
	 * {@link JiemamyContext}をよりSQLを生成し、DatabaseにSQLを適用する。
	 */
	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin...");
		
		try {
			getLog().info("Open Jiemamy model file " + inputFile.getName());
			FileInputStream inputStream = new FileInputStream(inputFile);
			
			getLog().info("Serializing stream to model.");
			JiemamyContext context =
					JiemamyContext.findSerializer().deserialize(inputStream, SqlFacet.PROVIDER, DiagramFacet.PROVIDER);
			getLog().debug(context.toString());
			
			getLog().info("Exec Sql...");
			Dialect dialect = context.findDialect();
			List<SqlStatement> sqlStatements = dialect.getSqlEmitter().emit(context, newEmitConfig());
			execSqlStatment(sqlStatements);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("can not found input file: " + inputFile.getName(), e);
		} catch (SerializationException e) {
			throw new MojoExecutionException("can not serialization jiemamy model.", e);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("can not get Dialect from input file.", e);
		}
		
		getLog().info("<<<< Exit maven-jiemamy-plugin successfully.");
	}
	
	/**
	 * {@link Connection}を取得する。
	 * 
	 * @return {@link Connection}
	 * @throws MojoExecutionException {@link Connection}が取得できなかった場合
	 * @since 0.3
	 */
	protected Connection getConnection() throws MojoExecutionException {
		Connection connection = null;
		DefaultDatabaseImportConfig config = new DefaultDatabaseImportConfig();
		config.setDriverClassName(driver);
		config.setUsername(username);
		config.setPassword(password);
		config.setUri(uri);
		
		Properties props = new Properties();
		props.setProperty("user", config.getUsername());
		props.setProperty("password", config.getPassword());
		
		URL[] paths = config.getDriverJarPaths();
		String className = config.getDriverClassName();
		
		try {
			Driver jdbcDriver = DriverUtil.getDriverInstance(paths, className);
			connection = jdbcDriver.connect(config.getUri(), props);
		} catch (DriverNotFoundException e) {
			throw new MojoExecutionException("", e);
		} catch (InstantiationException e) {
			throw new MojoExecutionException("", e);
		} catch (IllegalAccessException e) {
			throw new MojoExecutionException("", e);
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		} catch (SQLException e) {
			throw new MojoExecutionException("", e);
		}
		
		if (connection == null) {
			throw new MojoExecutionException("can not create connection");
		}
		return connection;
	}
	
	/**
	 * {@link SqlStatement}の{@link List}からSQLを取得し、実行します。
	 * 
	 * @param sqlStatements {@link SqlStatement}のリスト
	 * @throws MojoExecutionException SQLの実行に失敗した場合
	 */
	private void execSqlStatment(List<SqlStatement> sqlStatements) throws MojoExecutionException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			try {
				for (SqlStatement sqlStatement : sqlStatements) {
					getLog().debug(sqlStatement.toString());
					statement.execute(sqlStatement.toString());
				}
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			throw new MojoExecutionException("", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T>T getConfig(String key, T defaultValue) {
		if (parameter == null) {
			return defaultValue;
		}
		Object value = parameter.get(key);
		return value == null ? defaultValue : (T) value;
	}
	
	/**
	 * {@link #parameter}より{@link EmitConfig}の設定ファイルを生成する。
	 * 
	 * @return {@link EmitConfig}
	 */
	private EmitConfig newEmitConfig() {
		boolean schema = getConfig(SCHEMA, true);
		boolean drop = getConfig(DROP, true);
		int dataSetIndex = getConfig(DATA_SET_INDEX, -1);
		return new ExecuteEmitConfig(schema, drop, dataSetIndex);
	}
	

	/**
	 * {@link ExecuteMojo}で使用する{@link EmitConfig}。
	 * 
	 * @author yamkazu
	 */
	private static class ExecuteEmitConfig implements EmitConfig {
		
		boolean emitCreateSchemaStatement;
		
		boolean emitDropStatements;
		
		int dataSetIndex;
		

		/**
		 * インスタンスを生成する。
		 * 
		 * @param emitCreateSchemaStatement スキーマを出力する場合は{@code true}
		 * @param emitDropStatements ドロップ文を出力する場合は{@code ture}
		 * @param dataSetIndex データセットのインデックス
		 */
		ExecuteEmitConfig(boolean emitCreateSchemaStatement, boolean emitDropStatements, int dataSetIndex) {
			super();
			this.emitCreateSchemaStatement = emitCreateSchemaStatement;
			this.emitDropStatements = emitDropStatements;
			this.dataSetIndex = dataSetIndex;
		}
		
		public boolean emitCreateSchemaStatement() {
			return emitCreateSchemaStatement;
		}
		
		public boolean emitDropStatements() {
			return emitDropStatements;
		}
		
		public int getDataSetIndex() {
			return dataSetIndex;
		}
	}
	
}