/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
 * Created on 2009/09/24
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
package org.jiemamy.maven.generator;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;

/**
 * {@link MethodCodeGenerator}のファクトリークラス。
 * 
 * @since 0.3
 * @version $Id$
 * @author yamkazu
 */
public class MethodCodeGeneratorFactory {
	
	private static final MethodCodeGeneratorFactory FACTORY = new MethodCodeGeneratorFactory();
	
	
	/**
	 * {@link MethodCodeGeneratorFactory}を取得します。
	 * 
	 * @return {@link MethodCodeGeneratorFactory}
	 * @since 0.3
	 */
	public static MethodCodeGeneratorFactory getFactory() {
		return FACTORY;
	}
	
	private static boolean isType(Class<?> returnType, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			if (returnType.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	private MethodCodeGeneratorFactory() {
	}
	
	/**
	 * {@link Method#getReturnType()}の型に応じて{@link MethodCodeGenerator}を生成します。
	 * 
	 * @param method {@link Method}
	 * @param value 生成するコードの戻り値
	 * @return {@link MethodCodeGenerator}
	 * @since 0.3
	 */
	public MethodCodeGenerator getMethodCodeGenerator(Method method, Object value) {
		Class<?> returnType = method.getReturnType();
		if (isType(returnType, int.class, Integer.class, long.class, Long.class, float.class, Float.class,
				double.class, Double.class, short.class, Short.class, byte.class, Byte.class, boolean.class,
				Boolean.class)) {
			return new MethodCodeGeneratorReturnGeneric(method, value);
		} else if (isType(returnType, float.class, Float.class)) {
			return new MethodCodeGeneratorReturnFloat(method, value);
		} else if (isType(returnType, double.class, Double.class)) {
			return new MethodCodeGeneratorReturnDouble(method, value);
		} else if (isType(returnType, char.class, Character.class)) {
			return new MethodCodeGeneratorReturnChar(method, value);
		} else if (isType(returnType, String.class)) {
			return new MethodCodeGeneratorReturnString(method, value);
		} else if (isType(returnType, Date.class)) {
			return new MethodCodeGeneratorReturnDate(method, value);
		} else if (isType(returnType, File.class)) {
			return new MethodCodeGeneratorReturnFile(method, value);
		} else if (isType(returnType, URL.class)) {
			return new MethodCodeGeneratorReturnUrl(method, value);
		}
		throw new IllegalArgumentException("return type " + returnType + " not supported.");
	}
	
	
	abstract static class AbstractMethodCodeGenerator implements MethodCodeGenerator {
		
		private Method method;
		
		private Object value;
		
		
		AbstractMethodCodeGenerator(Method method, Object value) {
			this.method = method;
			this.value = value;
		}
		
		public String generate() {
			StringBuilder sb = new StringBuilder();
			sb.append(generatePrefix());
			if (value == null) {
				sb.append(generateBodyNoValue());
			} else {
				sb.append(generateBody());
			}
			sb.append(generateSuffix());
			return sb.toString();
		}
		
		abstract String generateBody();
		
		abstract String generateBodyNoValue();
		
		String generatePrefix() {
			StringBuilder sb = new StringBuilder();
			sb.append("public ");
			sb.append(method.getReturnType().getName() + " ");
			sb.append(method.getName() + "(){");
			return sb.toString();
		}
		
		String generateSuffix() {
			return "}";
		}
		
		Object getValue() {
			return value;
		}
	}
	
	static class MethodCodeGeneratorReturnChar extends AbstractMethodCodeGenerator {
		
		MethodCodeGeneratorReturnChar(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBody() {
			return "return '" + getValue() + "';";
		}
		
		@Override
		String generateBodyNoValue() {
			return "return '\u0000';";
		}
		
	}
	
	static class MethodCodeGeneratorReturnDate extends AbstractMethodCodeGenerator {
		
		MethodCodeGeneratorReturnDate(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBody() {
			StringBuilder sb = new StringBuilder();
			sb.append("java.util.Date result = null;");
			sb.append("java.text.DateFormatSymbols symbols = java.text.DateFormatSymbols.getInstance();");
			sb.append("symbols.setAmPmStrings(new String[] {\"AM\",\"PM\"});");
			sb.append("java.text.SimpleDateFormat format "
					+ "= new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss.S a\",symbols);");
			sb.append("try {");
			sb.append("  format.parse(\"" + getValue() + "\");");
			sb.append("} catch (java.text.ParseException e) {");
			sb.append("	 format = new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss.a\",symbols);");
			sb.append("  format.parse(\"" + getValue() + "\");");
			sb.append("}");
			sb.append("return result;");
			return sb.toString();
		}
		
		@Override
		String generateBodyNoValue() {
			return "return null;";
		}
	}
	
	static class MethodCodeGeneratorReturnDouble extends MethodCodeGeneratorReturnGeneric {
		
		MethodCodeGeneratorReturnDouble(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBodyNoValue() {
			return "return -1.0;";
		}
	}
	
	static class MethodCodeGeneratorReturnFile extends AbstractMethodCodeGenerator {
		
		MethodCodeGeneratorReturnFile(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBody() {
			return "return new java.io.File(\"" + getValue() + "\");";
		}
		
		@Override
		String generateBodyNoValue() {
			return "return null;";
		}
		
	}
	
	static class MethodCodeGeneratorReturnFloat extends MethodCodeGeneratorReturnGeneric {
		
		MethodCodeGeneratorReturnFloat(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBodyNoValue() {
			return "return -1f;";
		}
	}
	
	static class MethodCodeGeneratorReturnGeneric extends AbstractMethodCodeGenerator {
		
		MethodCodeGeneratorReturnGeneric(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBody() {
			return "return " + getValue().toString() + ";";
		}
		
		@Override
		String generateBodyNoValue() {
			return "return -1;";
		}
	}
	
	static class MethodCodeGeneratorReturnString extends AbstractMethodCodeGenerator {
		
		MethodCodeGeneratorReturnString(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBody() {
			return "return \"" + getValue() + "\";";
		}
		
		@Override
		String generateBodyNoValue() {
			return "return null;";
		}
	}
	
	static class MethodCodeGeneratorReturnUrl extends AbstractMethodCodeGenerator {
		
		MethodCodeGeneratorReturnUrl(Method method, Object value) {
			super(method, value);
		}
		
		@Override
		String generateBody() {
			StringBuilder sb = new StringBuilder();
			sb.append("java.net.URL result = null;");
			sb.append("try {");
			sb.append("  result = new URL(\"" + getValue() + "\");");
			sb.append("} catch (java.net.MalformedURLException e) {");
			// TODO Errorハンドリングは再考する
			sb.append("  throw new RuntimeException(e);");
			sb.append("}");
			sb.append("return result;");
			return sb.toString();
		}
		
		@Override
		String generateBodyNoValue() {
			return "return null;";
		}
	}
}
