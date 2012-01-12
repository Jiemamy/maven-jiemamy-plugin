/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
 * Created on 2009/04/12
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

import java.io.PrintStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;

/**
 * Print help of Jiemamy maven plugin.
 * 
 * @goal help
 * @author daisuke
 * @version $Id$
 * @since 0.3
 */
public class HelpMojo extends AbstractMojo {
	
	public void execute() {
		Log log = getLog();
		log.info(">>>> Starting maven-jiemamy-plugin:help...");
		log.info("");
		log.info("The Jiemamy Plugin is used to execute/import/export Jiemamy XML model.");
		
		PrintStream out = System.out;
		out.println();
		
		out.println("jiemamy:execute");
		out.println("  Execute SQLs which generated from Jiemamy XML model.");
		out.println();
		
		out.println("jiemamy:export");
		out.println("  Export Jiemamy XML model to certain resources by executing Exporter.");
		out.println();
		
		out.println("jiemamy:dbimport");
		out.println("  Import Jiemamy XML model from database.");
		out.println();
		
		out.println("jiemamy:clean");
		out.println("  Clean database.  Drop all tables, views and so on.");
		out.println();
		
		out.println("jiemamy:help");
		out.println("  Print (this) help of Jiemamy maven plugin.");
		out.println();
		
//		out.println("jiemamy:import");
		
		log.info("<<<< Exit maven-jiemamy-plugin:help successfully.");
	}
	
}
