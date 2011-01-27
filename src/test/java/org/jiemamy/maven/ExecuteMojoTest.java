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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * {@link ExecuteMojo}のテストクラス。
 * 
 * @author yamkazu
 * @version $Id: ExecuteMojoTest.java 3662 2009-09-24 06:20:36Z yamkazu $
 * @since 0.3
 */
public class ExecuteMojoTest {
	
	/** Database Username */
	private static final String DB_USER = "postgres";
	
	/** Database Password */
	private static final String DB_PASSWORD = "postgres";
	
	/** Database Url */
	private static final String DB_URL = "jdbc:postgresql:test";
	
	/** Database Driver */
	private static final String DB_DRIVER = "org.postgresql.Driver";
	
	/** テスト用入力ファイル */
	private static final File inputFile = new File("src/test/resources/jiemamy.jer");
	
	/** テスト対象 */
	private ExecuteMojo executeMojo;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	public void setUp() throws Exception {
		executeMojo = new ExecuteMojo();
		
//		HashMap<String, Object> parameter = new HashMap<String, Object>();
		
//		Field parameterField = ExecuteMojo.class.getDeclaredField("parameter");
//		parameterField.setAccessible(true);
//		parameterField.set(executeMojo, parameter);
		
		Field inputFileField = ExecuteMojo.class.getDeclaredField("inputFile");
		inputFileField.setAccessible(true);
		inputFileField.set(executeMojo, inputFile);
		
		Field driverField = ExecuteMojo.class.getDeclaredField("driver");
		driverField.setAccessible(true);
		driverField.set(executeMojo, DB_DRIVER);
		
		Field uriField = ExecuteMojo.class.getDeclaredField("uri");
		uriField.setAccessible(true);
		uriField.set(executeMojo, DB_URL);
		
		Field usernameFiled = ExecuteMojo.class.getDeclaredField("username");
		usernameFiled.setAccessible(true);
		usernameFiled.set(executeMojo, DB_USER);
		
		Field passwordField = ExecuteMojo.class.getDeclaredField("password");
		passwordField.setAccessible(true);
		passwordField.set(executeMojo, DB_PASSWORD);
		
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
	 * JiemamyModelからSQLを生成しDBに適用する。
	 * 
	 * @throws Exception テストに失敗した場合
	 * @since 0.3
	 */
	@Test
	@Ignore
	public void test01_JiemamyModelからSQLを生成しDBに適用する() throws Exception {
		// TODO 環境依存のテストのためいったんコメントアウト
		// executeMojo.execute();
	}
	
}
