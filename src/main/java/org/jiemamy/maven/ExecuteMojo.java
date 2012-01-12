/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.Validate;
import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.DiagramFacet;
import org.jiemamy.JiemamyContext;
import org.jiemamy.SqlFacet;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.EmitConfig;
import org.jiemamy.model.ModelConsistencyException;
import org.jiemamy.model.sql.SqlStatement;
import org.jiemamy.serializer.JiemamySerializer;
import org.jiemamy.serializer.SerializationException;
import org.jiemamy.utils.sql.SqlExecutor;

/**
 * Execute SQLs which generated from Jiemamy XML model.
 * 
 * @goal execute
 * @author daisuke
 * @author yamkazu
 * @version $Id$
 * @since 0.3
 */
public class ExecuteMojo extends AbstractJiemamyMojo {
	
	/**
	 * Location of the input model file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 * @since 0.3
	 */
	private File inputFile;
	
	/**
	 * Emit create schema statement or not.
	 * 
	 * @parameter default-value="false"
	 * @since 0.3
	 */
	private boolean createSchema;
	
	/**
	 * Emit drop statement or not.
	 * 
	 * @parameter default-value="false"
	 * @since 0.3
	 */
	private boolean drop;
	
	/**
	 * DataSet index.
	 * 
	 * @parameter default-value="-1"
	 * @since 0.3
	 */
	private int dataSetIndex;
	
	
	/**
	 * {@link JiemamyContext}をよりSQLを生成し、DatabaseにSQLを適用する。
	 */
	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin:execute...");
		
		try {
			getLog().info("Open Jiemamy model file " + inputFile.getName());
			FileInputStream inputStream = new FileInputStream(inputFile);
			
			getLog().info("Serializing stream to model.");
			JiemamySerializer serializer = JiemamyContext.findSerializer();
			JiemamyContext context = serializer.deserialize(inputStream, SqlFacet.PROVIDER, DiagramFacet.PROVIDER);
			
			getLog().info("Execute SQLs...");
			EmitConfig emitConfig = new ExecuteEmitConfig(createSchema, drop, dataSetIndex);
			Dialect dialect = context.findDialect();
			List<SqlStatement> sqlStatements = dialect.getSqlEmitter().emit(context, emitConfig);
			execSqlStatment(sqlStatements);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("can not found input file: " + inputFile.getName(), e);
		} catch (SerializationException e) {
			throw new MojoExecutionException("can not serialization jiemamy model.", e);
		} catch (IllegalStateException e) {
			throw new MojoExecutionException("can not found dialect", e);
		} catch (ModelConsistencyException e) {
			throw new MojoExecutionException("can not emit SQL.", e);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("can not get Dialect from input file.", e);
		}
		
		getLog().info("<<<< Exit maven-jiemamy-plugin:execute successfully.");
	}
	
	/**
	 * {@link SqlStatement}の{@link List}からSQLを順次取得し、実行する。
	 * 
	 * @param sqlStatements {@link SqlStatement}のリスト
	 * @throws MojoExecutionException SQLの実行に失敗した場合
	 * @throws IllegalArgumentException 引数に{@code null}または{@code null}要素を与えた場合
	 */
	private void execSqlStatment(List<SqlStatement> sqlStatements) throws MojoExecutionException {
		Validate.noNullElements(sqlStatements);
		Connection connection = null;
		try {
			connection = getConnection();
			SqlExecutor ex = new SqlExecutor(connection);
			for (SqlStatement sqlStatement : sqlStatements) {
				getLog().info("execute: " + sqlStatement.toString());
				ex.execute(sqlStatement.toString());
			}
		} catch (SQLException e) {
			throw new MojoExecutionException("", e);
		} finally {
			DbUtils.closeQuietly(connection);
			getLog().info("connection closed.");
		}
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
