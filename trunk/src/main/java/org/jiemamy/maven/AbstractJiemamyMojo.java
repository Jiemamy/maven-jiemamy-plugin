/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/09/25
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

import org.apache.maven.plugin.AbstractMojo;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SqlFacet;

/**
 * MojoのAbstractクラス。
 * 
 * @since 0.3
 * @version $Id: AbstractJiemamyMojo.java 3675 2009-09-24 16:15:35Z yamkazu $
 * @author yamkazu
 */
public abstract class AbstractJiemamyMojo extends AbstractMojo {
	
	/** デフォルトの出力先 */
	private static final String DEFAULT_DESTINATION = "./target";
	

	/**
	 * デフォルトの出力先を取得する。
	 * 
	 * @return 出力先
	 * @since 0.3
	 */
	public String getDefaultDestination() {
		return DEFAULT_DESTINATION;
	}
	
	/**
	 * 新しい {@link JiemamyContext} を生成して返す。
	 * 
	 * <p>このcontextは SQL-Facet を持っている。</p>
	 * 
	 * @return 新しい {@link JiemamyContext}
	 */
	protected JiemamyContext newJiemamyContext() {
		return new JiemamyContext(SqlFacet.PROVIDER);
	}
	
}
