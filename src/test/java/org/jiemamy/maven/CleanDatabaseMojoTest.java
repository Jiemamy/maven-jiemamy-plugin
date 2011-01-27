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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link JiemamyMojo}のテストクラス。
 * 
 * @author daisuke
 * @version $Id: CleanDatabaseMojoTest.java 3630 2009-09-20 16:32:15Z daisuke_m $
 * @since 0.2
 */
public class CleanDatabaseMojoTest {
	
	/** テスト対象 */
	private CleanMojo cleanDatabaseMojo;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	public void setUp() throws Exception {
		cleanDatabaseMojo = new CleanMojo();
		
//		HashMap<String, Object> parameter = new HashMap<String, Object>();
//		parameter.put(SqlExporter.OUTPUT_FILE, outputFile);
//		parameter.put(SqlExporter.OVERWRITE, true);
//		
//		Field parameterField = JiemamyMojo.class.getDeclaredField("parameter");
//		parameterField.setAccessible(true);
//		parameterField.set(cleanDatabaseMojo, parameter);
//		
//		Field inputFileField = JiemamyMojo.class.getDeclaredField("inputFile");
//		inputFileField.setAccessible(true);
//		inputFileField.set(cleanDatabaseMojo, inputFile);
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
		
		// TODO CREATE TABLE
		
//		cleanDatabaseMojo.execute();
		
		// TODO assertion
	}
}
