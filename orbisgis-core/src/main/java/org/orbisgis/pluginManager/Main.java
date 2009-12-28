/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 *
 *   Part of this code has been imported from GELAT a fork of OrbisGIS.
 *
 */

package org.orbisgis.pluginManager;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.OrbisGISApplicationInfo;
import org.orbisgis.core.OrbisgisCoreServices;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.workspace.DefaultSwingWorkspace;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.errorManager.DefaultErrorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.JobQueue;
import org.orbisgis.plugins.core.ui.errors.CacheMessages;
import org.orbisgis.plugins.core.ui.errors.FilteringErrorListener;
import org.orbisgis.plugins.core.ui.workspace.PluginsWorkspace;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class);		
	private static boolean clean = false;
	private static CacheMessages cacheMessages = null;
	

	public static void main(String[] args) throws Exception {
		Splash splash = new Splash();		
		init(splash);	
		new PluginsWorkspace();		
		new FilteringErrorListener();
		cacheMessages.printCacheMessages();
		splash.setVisible(false);
		splash.dispose();		
	}

	private static void init(Splash splash) {
		initServices();	
		initProperties();
		splash.setVisible(true);		
		splash.updateVersion();
		splash.updateText("Loading");
		initLogger();	
		Workspace wrsk = Services.getService(Workspace.class);		
		wrsk.init(clean);		
		logger.info("OrbisGIS start");
		cacheMessages = new CacheMessages();
	}

	private static void initServices() {
		Services.registerService(
				BackgroundManager.class,
				"Execute tasks in background processes, "
				+ "showing progress bars. Gives access to the job queue",
				new JobQueue());

		Services.registerService(ErrorManager.class,
				"Notification of errors to the system",
				new DefaultErrorManager());		
		DefaultWorkspace defaultWorkspace = new DefaultSwingWorkspace();		
		Services.registerService(Workspace.class,
				"Change workspace, save files in the workspace, etc.",
				defaultWorkspace);			
		
		ApplicationInfo applicationInfo = new OrbisGISApplicationInfo();
		Services.registerService(ApplicationInfo.class,
				"Gets information about the application: "
						+ "name, version, etc.", applicationInfo);
		// Install OrbisGIS core services
		OrbisgisCoreServices.installServices();
	}

	private static void initProperties() {
		PropertyConfigurator.configure(Main.class
				.getResource("log4j.properties"));		
	}

	private static  void initLogger() {
		PatternLayout l = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
		RollingFileAppender fa;
		try {
			fa = new RollingFileAppender(l, Services
					.getService(ApplicationInfo.class).getLogFile());
			fa.setMaxFileSize("256KB");
			Logger.getRootLogger().addAppender(fa);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}
