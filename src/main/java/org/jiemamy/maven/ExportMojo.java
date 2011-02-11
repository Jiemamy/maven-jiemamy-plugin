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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import com.google.common.collect.Sets;

import org.apache.maven.plugin.MojoExecutionException;

import org.jiemamy.DiagramFacet;
import org.jiemamy.JiemamyContext;
import org.jiemamy.SqlFacet;
import org.jiemamy.composer.ExportConfig;
import org.jiemamy.composer.ExportException;
import org.jiemamy.composer.Exporter;
import org.jiemamy.composer.exporter.SimpleSqlExportConfig;
import org.jiemamy.composer.exporter.SqlExporter;
import org.jiemamy.maven.generator.MethodCodeGenerator;
import org.jiemamy.maven.generator.MethodCodeGeneratorFactory;
import org.jiemamy.serializer.JiemamySerializer;
import org.jiemamy.serializer.SerializationException;
import org.jiemamy.utils.reflect.ReflectionUtil;

/**
 * Goal which execute Exporter.
 * 
 * @goal export
 * @author daisuke
 * @author yamkazu
 * @version $Id$
 * @since 0.3
 */
public class ExportMojo extends AbstractJiemamyMojo {
	
	/**
	 * Location of the input model file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 * @since 0.3
	 */
	private File inputFile;
	
	/**
	 * Fully qualified class name of the exporter class.
	 * 
	 * @parameter 
	 * @since 0.3
	 */
	private String exporterClass;
	
	/**
	 * Parameter for exporter.
	 * 
	 * @parameter
	 * @since 0.3
	 */
	private Map<String, String> parameter;
	
	// FORMAT-OFF
	/** 戻り値としてサポートする型一覧 */
	private static final Set<Class<?>> SUPPORTED_RETURN_TYPES = Sets.<Class<?>>newHashSet(
			boolean.class, byte.class, short.class, int.class, long.class, float.class, double.class, char.class,
			Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class,
			File.class, Date.class, URL.class, String.class);
	// FORMAT-ON
	
	public void execute() throws MojoExecutionException {
		getLog().info(">>>> Starting maven-jiemamy-plugin:export...");
		
		try {
			getLog().info("Open Jiemamy model file " + inputFile.getName());
			FileInputStream inputStream = new FileInputStream(inputFile);
			
			getLog().info("Serializing stream to model.");
			JiemamySerializer serializer = JiemamyContext.findSerializer();
			JiemamyContext context = serializer.deserialize(inputStream, SqlFacet.PROVIDER, DiagramFacet.PROVIDER);
			getLog().debug(context.toString());
			
			if (exporterClass == null) {
				// v0.2互換モード
				executeJiemamy(context);
			} else {
				executeExport(context);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace(); // FIXME リリースまでに、このメソッド内の Exception#printStackTrace() を削除する
			throw new MojoExecutionException("Jiemamy model file \"" + inputFile.getName() + "\" is not found.", e);
		} catch (SerializationException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Cannot deserialize file \"" + inputFile.getName(), e);
		} catch (ExportException e) {
			e.printStackTrace();
			throw new MojoExecutionException("ExportException is thrown.", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Exporter \"" + exporterClass + "\" is not found.", e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Cannot instantiate Exporter \"" + exporterClass + "\"", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Cannot access Exporter \"" + exporterClass + "\"", e);
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Cannot compile generate ExportConfig class.", e);
		} catch (CannotCompileException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Cannot compile generate ExportConfig class.", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Exception is thrown.", e);
		} finally {
			getLog().info("done.");
		}
		
		getLog().info("<<<< Exit maven-jiemamy-plugin:export successfully.");
	}
	
	private void executeExport(JiemamyContext context) throws ClassNotFoundException, MojoExecutionException,
			InstantiationException, IllegalAccessException, ExportException, NotFoundException, CannotCompileException {
		Class<?> exportClass = Class.forName(exporterClass);
		Type genericType = getExporterGenericInterface(exportClass);
		
		// ExportConfigクラスを取得
		Class<?> exportConfigClass = Class.forName(((Class<?>) genericType).getName());
		
		// ExportConfgクラスから実装クラスを作成する
		Class<?> exportConfigImpl = generateExportConfigImpl(exportConfigClass);
		
		@SuppressWarnings("unchecked")
		Exporter<ExportConfig> exporter = (Exporter<ExportConfig>) exportClass.newInstance();
		ExportConfig exportConfig = (ExportConfig) exportConfigImpl.newInstance();
		exporter.exportModel(context, exportConfig);
	}
	
	private void executeJiemamy(JiemamyContext context) throws ExportException {
		getLog().info("Configure Exporter...");
		SimpleSqlExportConfig config = new SimpleSqlExportConfig();
		
		if (parameter.containsKey(SqlExporter.OUTPUT_FILE)) {
			config.setOutputFile(new File(parameter.get(SqlExporter.OUTPUT_FILE)));
		}
		if (parameter.containsKey(SqlExporter.OVERWRITE)) {
			config.setOverwrite(Boolean.valueOf(parameter.get(SqlExporter.OVERWRITE)));
		}
		if (parameter.containsKey(SqlExporter.DROP)) {
			config.setEmitDropStatements(Boolean.valueOf(parameter.get(SqlExporter.DROP)));
		}
		if (parameter.containsKey(SqlExporter.SCHEMA)) {
			config.setEmitCreateSchema(Boolean.valueOf(parameter.get(SqlExporter.SCHEMA)));
		}
		String indexObject = parameter.get(SqlExporter.DATA_SET_INDEX);
		config.setDataSetIndex(indexObject == null ? -1 : Integer.valueOf(indexObject));
		
		getLog().info("Executing Exporter...");
		
		SqlExporter exporter = new SqlExporter();
		exporter.exportModel(context, config);
	}
	
	/**
	 * {@link ExportConfig}の実装クラスを生成する。
	 * 
	 * @param clazz {@link ExportConfig}
	 * @return {@link ExportConfig}の実装クラス
	 * @throws NotFoundException 指定された{@link ExportConfig}が見つからなかった場合
	 * @throws CannotCompileException {@link ExportConfig}の実装クラスのコンパイルに失敗した場合
	 */
	private Class<?> generateExportConfigImpl(Class<?> clazz) throws NotFoundException, CannotCompileException {
		
		// Interfaceに定義されてるメソッドの一覧を取得する
		Set<Method> interfaceMethods = getInterfaceMethods(clazz);
		
		// サポートされている戻り値であるかチェックする
		for (Method method : interfaceMethods) {
			if (isSupportType(method.getReturnType()) == false) {
				throw new IllegalArgumentException("parameter type not supported: " + clazz);
			}
		}
		
		// 実装クラスを生成
		ClassPool classPool = ClassPool.getDefault();
		CtClass makeClass = classPool.makeClass(generateImplClassName(clazz));
		
		// Interfaceの設定を追加
		CtClass superInterface = classPool.get(clazz.getName());
		makeClass.setInterfaces(new CtClass[] {
			superInterface
		});
		
		// メソッドの追加
		for (Method method : interfaceMethods) {
			CtMethod m = CtNewMethod.make(generateMethodCode(method), makeClass);
			makeClass.addMethod(m);
		}
		
		return makeClass.toClass();
	}
	
	private String generateImplClassName(Class<?> clazz) {
		return clazz.getSimpleName() + "Impl";
	}
	
	private String generateMethodCode(Method method) {
		Object value = null;
		if (parameter != null) {
			List<String> searchNames = getSearchNames(method);
			for (String key : searchNames) {
				value = parameter.get(key);
				if (value != null) {
					break;
				}
			}
		}
		MethodCodeGenerator generator = MethodCodeGeneratorFactory.getFactory().getMethodCodeGenerator(method, value);
		return generator.generate();
	}
	
	/**
	 * パラメータ化された{@link Exporter}の要素型を取得する。
	 * <p>
	 * {@code clazz}に{@link Exporter}が見つからない場合は再帰的に
	 * {@link Class#getSuperclass()}を取得して{@link Exporter}を探す。
	 * 
	 * @param clazz {@link Exporter}の実装クラス
	 * @return {@link Exporter}の要素型
	 * @throws MojoExecutionException {@link Exporter}が見つからない場合
	 */
	private Type getExporterGenericInterface(Class<?> clazz) throws MojoExecutionException {
		for (Type type : clazz.getGenericInterfaces()) {
			if (type != null && type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type rawType = parameterizedType.getRawType();
				if (Exporter.class.equals(rawType)) {
					return ((ParameterizedType) type).getActualTypeArguments()[0];
				}
			}
		}
		if (clazz.getSuperclass() != null) {
			Type genericType = getExporterGenericInterface(clazz.getSuperclass());
			if (genericType != null) {
				return genericType;
			}
		}
		throw new MojoExecutionException("Not found Exporter Class");
	}
	
	/**
	 * 引数に指定された{@code Class}に定義されてる{@code Method}の一覧を取得する。
	 * 継承している{@code interface}が存在する場合は再帰的に、親{@code interface}を取得し、
	 * 同様に{@code Method}を取得する。
	 * 
	 * @param clazz 検査対象の{@code Class}
	 * @return {@code Method}の一覧
	 */
	private Set<Method> getInterfaceMethods(Class<?> clazz) {
		Set<Method> methods = new HashSet<Method>();
		methods.addAll(Arrays.asList(clazz.getMethods()));
		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaces != null) {
			for (Class<?> superInterface : interfaces) {
				methods.addAll(getInterfaceMethods(superInterface));
			}
		}
		return methods;
	}
	
	private List<String> getSearchNames(Method method) {
		List<String> result = new ArrayList<String>();
		result.add(method.getName());
		if (ReflectionUtil.isGetter(method)) {
			ReflectionUtil.convertAccessorToFieldName(method);
			result.add(ReflectionUtil.convertAccessorToFieldName(method));
		}
		return result;
	}
	
	/**
	 * サポートされているクラスかチェックする。
	 * 
	 * @param clazz 検査対象の{@link Class}
	 * @return サポートしている場合は{@code true}、そうでない場合は{@code false}
	 */
	private boolean isSupportType(Class<?> clazz) {
		return SUPPORTED_RETURN_TYPES.contains(clazz);
	}
	
}
