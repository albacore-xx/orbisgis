
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

package org.orbisgis.plugins.core.ui.export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.core.ui.views.geocognition.wizards.NewFolder;

import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.sif.SaveFilePanel;
import org.orbisgis.sif.UIFactory;

import com.vividsolutions.jts.geom.Envelope;

public class ExportMapAsImagePlugIn extends AbstractPlugIn {	
	
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		MapEditorPlugIn mapEditor = (MapEditorPlugIn) em.getActiveEditor();
		BufferedImage image = mapEditor.getMapTransform().getImage();
		MapContext mc = (MapContext) mapEditor.getElement().getObject();

		ILayer[] allSelectedLayers = mc.getLayers();
		Envelope envelope = new Envelope();

		for (int i = 0; i < allSelectedLayers.length; i++) {
			Envelope env = allSelectedLayers[i].getEnvelope();
			if (env.intersects(mapEditor.getMapTransform().getExtent())) {
				envelope.expandToInclude(env);
			}
		}

		Envelope intersectEnv = envelope.intersection(mapEditor
				.getMapTransform().getAdjustedExtent());

		Envelope layerPixelEnvelope = mapEditor.getMapTransform().toPixel(
				intersectEnv);

		BufferedImage subImg = image.getSubimage((int) layerPixelEnvelope
				.getMinX(), (int) layerPixelEnvelope.getMinY(),
				(int) layerPixelEnvelope.getWidth(), (int) layerPixelEnvelope
						.getHeight());

		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.core.ui.editors.map.actions.ExportMapAsImage",
				"Choose a file format");
		outfilePanel.addFilter("png", "Portable Network Graphics (*.png)");

		if (UIFactory.showDialog(outfilePanel)) {
			final File savedFile = new File(outfilePanel.getSelectedFile()
					.getAbsolutePath());

			try {
					ImageIO.write(subImg, "png", savedFile);


					JOptionPane.showMessageDialog(null,
							"The file has been saved.");



			} catch (IOException e) {
				Services.getErrorManager().error("Cannot write image", e);
			}

		}		
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		//TODO trouver MapEditorPlugIn.getMapCOntrol.getToolManager -> contient MenbuTree
		//& addPopupMenuItem ajoute à ce MenuTree le menu
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getMapEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Export map as Image..." }, 
				"org.orbisgis.core.ui.editors.map.actions.LayerActionGroup", false,
				null, wbContext);
	}
	
	public void update(Observable o, Object arg) {}

	public boolean isEnabled() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if("Map".equals(em.getEditorId(editor)) && editor!=null) {			
			MapContext map = (MapContext) editor.getElement().getObject();
			return map.getLayerModel().getLayerCount() > 0;
		}
		return false;
	}

	public boolean isVisible() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if("Map".equals(em.getEditorId(editor)) && editor!=null) {			
			return true;
		}
		return false;
	}
}

