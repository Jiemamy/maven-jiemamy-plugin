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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.collect.Maps;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jiemamy.composer.exporter.SqlExporter;

/**
 * {@link ExportMojo}のテストクラス。
 * 
 * @author daisuke
 * @version $Id$
 * @since 0.3
 */
public class ExportMojoTest {
	
	/** テスト用入力ファイル */
	private static final File INPUT_FILE = new File("src/test/resources/sample.jiemamy.xml");
	
	/** テスト用出力ファイル */
	private static final File OUTPUT_FILE = new File("target/testresult/sample.sql");
	
	/** テスト対象 */
	private ExportMojo exportMojo;
	
	@SuppressWarnings("unused")
	private String exporterClass = "org.jiemamy.composer.exporter.SqlExporter";
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	public void setUp() throws Exception {
		exportMojo = new ExportMojo();
		
		Map<String, Object> parameter = createParameter();
		Field exportParameterField = ExportMojo.class.getDeclaredField("parameter");
		exportParameterField.setAccessible(true);
		exportParameterField.set(exportMojo, parameter);
		
//		Field exportClassField = ExportMojo.class.getDeclaredField("exporterClass");
//		exportClassField.setAccessible(true);
//		exportClassField.set(exportMojo, exporterClass);
		
		Field inputFileField = ExportMojo.class.getDeclaredField("inputFile");
		inputFileField.setAccessible(true);
		inputFileField.set(exportMojo, INPUT_FILE);
	}
	
	/**
	 * テストの情報を破棄する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@After
	public void tearDown() throws Exception {
		exportMojo = null;
	}
	
	/**
	 * エクスポート先にファイルが生成される。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test01_エクスポート先にファイルが生成される() throws Exception {
		if (OUTPUT_FILE.exists()) {
			boolean deleted = OUTPUT_FILE.delete();
			assertThat(deleted, is(true));
		}
		assertThat(OUTPUT_FILE.exists(), is(false));
		
		exportMojo.execute();
		
		assertThat(OUTPUT_FILE.exists(), is(true));
	}
	
	private Map<String, Object> createParameter() {
		Map<String, Object> paramaters = Maps.newHashMap();
		paramaters.put(SqlExporter.OVERWRITE, "true");
		paramaters.put(SqlExporter.SCHEMA, "true");
		paramaters.put(SqlExporter.DROP, "true");
		paramaters.put(SqlExporter.OUTPUT_FILE, "target/testresult/sample.sql");
		paramaters.put(SqlExporter.DATA_SET_INDEX, "1");
		return paramaters;
	}
}
