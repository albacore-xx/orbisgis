
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
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
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
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.plugins.core.ui.geocatalog;

import java.io.File;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.core.ui.views.geocatalog.filters.All;
import org.orbisgis.core.ui.views.geocatalog.filters.Alphanumeric;
import org.orbisgis.core.ui.views.geocatalog.filters.DBs;
import org.orbisgis.core.ui.views.geocatalog.filters.Files;
import org.orbisgis.core.ui.views.geocatalog.filters.Raster;
import org.orbisgis.core.ui.views.geocatalog.filters.Vectorial;
import org.orbisgis.core.ui.views.geocatalog.filters.WMS;
import org.orbisgis.core.ui.views.geocognition.actions.OpenGeocognitionElementJob;
import org.orbisgis.core.ui.wizards.OpenGdmsFilePanel;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.utils.FileUtils;

public class NewGeocatalogFile extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		OpenGdmsFilePanel filePanel = new OpenGdmsFilePanel(
		"Select the file to add");
		if (UIFactory.showDialog(new UIPanel[] { filePanel })) {
		
			File[] files = filePanel.getSelectedFiles();
			for (File file : files) {
				DataManager dm = (DataManager) Services
						.getService(DataManager.class);
				SourceManager sourceManager = dm.getSourceManager();
				try {
					String name = sourceManager.getUniqueName(FileUtils
							.getFileNameWithoutExtensionU(file));
					sourceManager.register(name, file);
				} catch (SourceAlreadyExistsException e) {
					Services.getErrorManager().error(
							"The source is already registered: "
									+ e.getMessage());
				}
			}
		}
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Add", "File" }, "Add", false,
				getIcon("page_white_add.png"), wbContext);
		
/*		context.getFeatureInstaller().addFilter(frame, new All(), wbContext);
		context.getFeatureInstaller().addFilter(frame, new Files(), wbContext);
		context.getFeatureInstaller().addFilter(frame, new DBs(), wbContext);
		context.getFeatureInstaller().addFilter(frame, new Alphanumeric(), wbContext);
		context.getFeatureInstaller().addFilter(frame, new WMS(), wbContext);
		context.getFeatureInstaller().addFilter(frame, new Vectorial(), wbContext);
		context.getFeatureInstaller().addFilter(frame, new Raster(), wbContext);*/
		
	}
	
	public void update(Observable o, Object arg) {}
	public boolean isEnabled() {return true;}
	public boolean isVisible() {return true;}
}
