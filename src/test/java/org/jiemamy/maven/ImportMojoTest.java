/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * {@link DbImportMojo}のテストクラス。
 * 
 * @author yamkazu
 * @version $Id$
 * @since 0.3
 */
public class ImportMojoTest extends AbstractMojoTestCase {
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * DBからimportを行いjiemamyファイルを生成するテスト。
	 * 
	 * @throws Exception importに失敗した場合
	 */
	@Test
	@Ignore
	public void test01_DBからimportを行いjiemamyファイルを生成する() throws Exception {
		// 何故かignoreできないのでコメントアウト
//		File target = new File("target/testresults/imported.jiemamy");
//		if (target.exists()) {
//			boolean deleted = target.delete();
//			assert deleted;
//		}
//		
//		File testPom = new File(getBasedir(), "src/test/resources/importmojo/pom.xml");
//		DbImportMojo mojo = (DbImportMojo) lookupMojo("import", testPom);
//		mojo.execute();
//		
//		assertThat(target.exists(), is(true));
	}
}
