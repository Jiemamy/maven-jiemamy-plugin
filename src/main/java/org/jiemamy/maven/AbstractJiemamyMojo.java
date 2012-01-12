/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SqlFacet;
import org.jiemamy.utils.sql.DriverNotFoundException;
import org.jiemamy.utils.sql.DriverUtil;

/**
 * MojoのAbstractクラス。
 * 
 * @since 0.3
 * @version $Id$
 * @author yamkazu
 */
public abstract class AbstractJiemamyMojo extends AbstractMojo {
	
	/** デフォルトの出力先 */
	private static final String DEFAULT_DESTINATION = "./target";
	
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
	 * デフォルトの出力先を取得する。
	 * 
	 * @return 出力先
	 * @since 0.3
	 */
	public String getDefaultDestination() {
		return DEFAULT_DESTINATION;
	}
	
	/**
	 * {@link Connection}を取得する。
	 * 
	 * @return {@link Connection}
	 * @throws MojoExecutionException {@link Connection}が取得できなかった場合
	 * @since 0.3
	 */
	protected Connection getConnection() throws MojoExecutionException {
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		
		Connection connection = null;
		try {
			Driver jdbcDriver = DriverUtil.getDriverInstance(new URL[0], driver);
			getLog().info("connect to " + uri);
			getLog().debug("  username: " + username);
			getLog().debug("  password: ****");
			connection = jdbcDriver.connect(uri, props);
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
	 * JDCBドライバのFQCNを返す。
	 * 
	 * @return JDCBドライバのFQCN
	 */
	protected String getDriver() {
		return driver;
	}
	
	/**
	 * データベース接続パスワードを返す。
	 * 
	 * @return データベース接続パスワード
	 */
	protected String getPassword() {
		return password;
	}
	
	/**
	 * データベース接続URIを返す。
	 * 
	 * @return データベース接続URI
	 */
	protected String getUri() {
		return uri;
	}
	
	/**
	 * データベース接続ユーザ名を返す。
	 * 
	 * @return データベース接続ユーザ名
	 */
	protected String getUsername() {
		return username;
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
