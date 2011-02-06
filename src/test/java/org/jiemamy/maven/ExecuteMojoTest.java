/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/09/20
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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jiemamy.composer.importer.SimpleDbImportConfig;
import org.jiemamy.dialect.postgresql.PostgresqlDialect;
import org.jiemamy.test.AbstractDatabaseTest;
import org.jiemamy.utils.DbCleaner;

/**
 * {@link ExecuteMojo}のテストクラス。
 * 
 * @author yamkazu
 * @version $Id: ExecuteMojoTest.java 3662 2009-09-24 06:20:36Z yamkazu $
 * @since 0.3
 */
public class ExecuteMojoTest extends AbstractDatabaseTest {
	
	/** テスト用入力ファイル */
	private static final File inputFile = new File("src/test/resources/sample_pg.jiemamy.xml");
	
	/** テスト対象 */
	private ExecuteMojo executeMojo;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		executeMojo = new ExecuteMojo();
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(ExecuteMojo.SCHEMA, false);
		parameter.put(ExecuteMojo.DROP, false);
		Field parameterField = ExecuteMojo.class.getDeclaredField("parameter");
		parameterField.setAccessible(true);
		parameterField.set(executeMojo, parameter);
		
		Field inputFileField = ExecuteMojo.class.getDeclaredField("inputFile");
		inputFileField.setAccessible(true);
		inputFileField.set(executeMojo, inputFile);
		
		Field driverField = ExecuteMojo.class.getDeclaredField("driver");
		driverField.setAccessible(true);
		driverField.set(executeMojo, getDriverClassName());
		
		Field uriField = ExecuteMojo.class.getDeclaredField("uri");
		uriField.setAccessible(true);
		uriField.set(executeMojo, getConnectionUri());
		
		Field usernameFiled = ExecuteMojo.class.getDeclaredField("username");
		usernameFiled.setAccessible(true);
		usernameFiled.set(executeMojo, getUsername());
		
		Field passwordField = ExecuteMojo.class.getDeclaredField("password");
		passwordField.setAccessible(true);
		passwordField.set(executeMojo, getPassword());
		
	}
	
	/**
	 * テストの情報を破棄する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@After
	public void tearDown() throws Exception {
		executeMojo = null;
	}
	
	/**
	 * jiemamyファイルからSQLを生成しDBに適用する。
	 * 
	 * @throws Exception テストに失敗した場合
	 * @since 0.3
	 */
	@Test
	public void test01_jiemamyファイルからSQLを生成しDBに適用する() throws Exception {
		SimpleDbImportConfig config = newDatabaseImportConfig(new PostgresqlDialect(), new URL[] {
			new File("./src/test/resources/postgresql-8.3-603.jdbc3.jar").toURI().toURL()
		});
		DbCleaner.clean(config);
		executeMojo.execute();
		// TODO assertion
	}
	
	@Override
	protected String getPropertiesFilePath(String hostName) {
		if (hostName.equals("griffon.jiemamy.org")) {
			return "/postgresql_griffon.properties";
		}
		return "/postgresql_local.properties";
	}
	
}