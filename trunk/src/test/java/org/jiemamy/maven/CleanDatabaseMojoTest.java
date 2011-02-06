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

import java.lang.reflect.Field;
import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jiemamy.test.AbstractDatabaseTest;
import org.jiemamy.utils.sql.SqlExecutor;

/**
 * {@link CleanMojo}のテストクラス。
 * 
 * @author daisuke
 * @version $Id: CleanDatabaseMojoTest.java 3630 2009-09-20 16:32:15Z daisuke_m $
 * @since 0.2
 */
public class CleanDatabaseMojoTest extends AbstractDatabaseTest {
	
	/** テスト対象 */
	private CleanMojo cleanDatabaseMojo;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		cleanDatabaseMojo = new CleanMojo();
		
		Field usernameField = AbstractJiemamyMojo.class.getDeclaredField("username");
		usernameField.setAccessible(true);
		usernameField.set(cleanDatabaseMojo, getUsername());
		
		Field passwordField = AbstractJiemamyMojo.class.getDeclaredField("password");
		passwordField.setAccessible(true);
		passwordField.set(cleanDatabaseMojo, getPassword());
		
		Field driverField = AbstractJiemamyMojo.class.getDeclaredField("driver");
		driverField.setAccessible(true);
		driverField.set(cleanDatabaseMojo, getDriverClassName());
		
		Field uriField = AbstractJiemamyMojo.class.getDeclaredField("uri");
		uriField.setAccessible(true);
		uriField.set(cleanDatabaseMojo, getConnectionUri());
		
		Field dialectField = CleanMojo.class.getDeclaredField("dialect");
		dialectField.setAccessible(true);
		dialectField.set(cleanDatabaseMojo, "org.jiemamy.dialect.GenericDialect");
	}
	
	/**
	 * テストの情報を破棄する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@After
	public void tearDown() throws Exception {
		cleanDatabaseMojo = null;
	}
	
	/**
	 * DBがクリーンされる。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test01_DBがクリーンされる() throws Exception {
		Connection connection = getConnection();
		
		SqlExecutor ex = new SqlExecutor(connection);
		ex.execute("CREATE TABLE XYZZY (ID VARCHAR(32) PRIMARY KEY);");
		connection.close();
		
		cleanDatabaseMojo.execute();
		
		// TODO assertion
	}
	
	@Override
	protected String getPropertiesFilePath(String hostName) {
		if (hostName.equals("griffon.jiemamy.org")) {
			return "/mysql_griffon.properties";
		}
		return "/mysql_local.properties";
	}
}
