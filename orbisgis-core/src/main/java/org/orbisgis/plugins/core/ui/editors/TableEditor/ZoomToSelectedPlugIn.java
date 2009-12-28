
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

package org.orbisgis.plugins.core.ui.editors.TableEditor;

import java.awt.event.MouseEvent;
import java.util.Observable;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.progress.IProgressMonitor;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ZoomToSelectedPlugIn extends AbstractPlugIn {	
	
	private boolean isVisible ;
	
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor.getElement();		
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
				try {
					Selection selection = element.getSelection();
					int[] selectedRow = selection.getSelectedRows();

					SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
							element.getDataSource());

					Envelope rect = null;
					Geometry geometry = null;
					Envelope geometryEnvelope = null;
					for (int i = 0; i < selectedRow.length; i++) {
						if (sds.isDefaultVectorial()) {
							geometry = sds.getGeometry(selectedRow[i]);
							if (geometry != null) {
								geometryEnvelope = geometry.buffer(0.01)
										.getEnvelopeInternal();
							}
						} else if (sds.isDefaultRaster()) {
							geometryEnvelope = sds.getRaster(selectedRow[i]).getMetadata()
									.getEnvelope();
						}

						if (rect == null) {
							rect = new Envelope(geometryEnvelope);
						} else {
							rect.expandToInclude(geometryEnvelope);
						}

					}

					EditorManager em = (EditorManager) Services
							.getService(EditorManager.class);
					IEditor[] editors = em
							.getEditors("Map", element
									.getMapContext());
					for (IEditor mapEditorPlugIn : editors) {
						((MapEditorPlugIn) mapEditorPlugIn).getMapTransform().setExtent(
								rect);
					}

				} catch (DriverException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot compute envelope", e);
				}
			}

			@Override
			public String getTaskName() {
				return "Calculating selected extent";
			}
		});
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench().getFrame().getTableEditor();	
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_ZOOMTOSELECTED_PATH1 },
				Names.POPUP_TABLE_ZOOMTOSELECTED_GROUP, false,
				getIcon(Names.POPUP_TABLE_ZOOMTOSELECTED_ICON), wbContext);
	}
	
	public void update(Observable o, Object arg) {isVisible(arg);}	
	public boolean isEnabled() {return true;}	
	public boolean isVisible() {return isVisible;}

	public boolean isVisible(Object arg) {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();		
		if("Table".equals(em.getEditorId(editor)) && editor!=null) {
			try {
				MouseEvent event = (MouseEvent)arg;
			}catch(Exception e){
				return isVisible = false;
			}
			final TableEditableElement element = (TableEditableElement) editor.getElement();
			if (element.getMapContext() != null){
				if (element.getSelection().getSelectedRows().length>0){
					return isVisible = true;
				}
			}
		}
		return isVisible = false;
	}
}


