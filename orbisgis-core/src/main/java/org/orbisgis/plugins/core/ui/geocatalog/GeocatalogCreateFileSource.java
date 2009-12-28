
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

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.InitializationException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.NotDriverFilter;
import org.gdms.source.RasterDriverFilter;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.views.geocatalog.actions.create.MetadataCreation;
import org.orbisgis.core.ui.wizards.OpenGdmsFilePanel;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.sif.SaveFilePanel;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.utils.FileUtils;

public class GeocatalogCreateFileSource extends AbstractPlugIn {
	
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().executeGeocatalog();
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "New", "File" }, "geocatalog.new", false,
				null, wbContext);
		
	}
	
	public void update(Observable o, Object arg) {}
	
	public void execute(SourceManager sourceManager, String sourceName) {
		// Get the non raster writable drivers
		DataManager dm = (DataManager) Services.getService(DataManager.class);
		DriverManager driverManager = sourceManager.getDriverManager();

		Driver[] filtered = driverManager.getDrivers(new AndDriverFilter(
				new FileDriverFilter(), new NotDriverFilter(
						new RasterDriverFilter())));

		createSource(dm, driverManager, filtered);
	}

	static void createSource(DataManager dm, DriverManager driverManager,
			Driver[] filtered) {
		String[] typeNames = new String[filtered.length];
		String[] driverNames = new String[filtered.length];
		SourceManager sourceManager = dm.getSourceManager();
		for (int i = 0; i < filtered.length; i++) {
			driverNames[i] = filtered[i].getDriverId();
			ReadOnlyDriver rod = (ReadOnlyDriver) filtered[i];
			typeNames[i] = rod.getTypeDescription();
		}

		ChoosePanel cp = new ChoosePanel("Select the type of source",
				typeNames, driverNames);
		if (UIFactory.showDialog(cp)) {
			// Create wizard
			UIPanel[] wizardPanels = new UIPanel[2];
			ReadWriteDriver driver = (ReadWriteDriver) driverManager
					.getDriver((String) cp.getSelected());
			boolean file;
			if ((driver.getType() & SourceManager.FILE) == SourceManager.FILE) {
				file = true;
			} else if ((driver.getType() & SourceManager.DB) == SourceManager.DB) {
				file = false;
			} else {
				Services.getErrorManager().error(
						"Unsupported source type: " + cp.getSelected());
				return;
			}
			SaveFilePanel saveFilePanel = new SaveFilePanel(null,
					"Select the file to create");
			if (file) {
				saveFilePanel.setFileMustNotExist(true);
				saveFilePanel.addFilter(((FileDriver) driver)
						.getFileExtensions(), driver.getTypeDescription());
				wizardPanels[0] = saveFilePanel;
			} else {
				throw new UnsupportedOperationException("Not implemented yet");
			}
			MetadataCreation mc = new MetadataCreation(driver);
			wizardPanels[1] = mc;
			if (UIFactory.showDialog(wizardPanels)) {
				DataSourceCreation dsc = null;
				DataSourceDefinition dsd = null;
				String name = null;
				if (file) {
					File selectedFile = saveFilePanel.getSelectedFile();
					String selectedPath = selectedFile.getAbsolutePath();
					boolean hasExtension = false;
					String[] extensions = ((FileDriver) driver)
							.getFileExtensions();
					for (String extension : extensions) {
						if (selectedPath.toLowerCase().endsWith(
								extension.toLowerCase())) {
							hasExtension = true;
							break;
						}
					}
					if (!hasExtension) {
						selectedFile = new File(selectedPath + "."
								+ extensions[0]);
					}
					dsc = new FileSourceCreation(selectedFile, mc.getMetadata());
					dsd = new FileSourceDefinition(selectedFile);
					name = FileUtils.getFileNameWithoutExtensionU(selectedFile);
				} else {
					throw new UnsupportedOperationException(
							"Not implemented yet");
				}

				try {
					dm.getDSF().createDataSource(dsc);
				} catch (DriverException e) {
					Services.getErrorManager().error(
							"Cannot create source: " + dsc, e);
					return;
				}

				name = sourceManager.getUniqueName(name);
				try {
					sourceManager.register(name, dsd);
				} catch (SourceAlreadyExistsException e) {
					Services.getService(ErrorManager.class).error(
							"Name collision", e);
				} catch (InitializationException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot initialize source", e);
				}
			}
		}
	}
	
	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().geocatalogIsVisible();
	}
	
	public boolean accepts(SourceManager sourceManager, String sourceName) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return true;
	}
}
