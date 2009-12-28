
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

package org.orbisgis.plugins.core.ui.editors.MapEditor;

import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.images.IconLoader;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class UndoMapPlugIn extends AbstractPlugIn {

	private JButton btn ;
	
	public UndoMapPlugIn() {
		btn = new JButton(getIcon());
	}
	
	@Override
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		MapContext mc = (MapContext) editor.getElement().getObject();
		ILayer activeLayer = mc.getActiveLayer();
		try {
			activeLayer.getDataSource().undo();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot undo", e);
		}
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getEditionMapToolBar().addPlugIn(this, btn);		
	}
	
	@Override
	public void update(Observable o, Object arg) {		
		btn.setEnabled(isEnabled());
		btn.setVisible(isVisible());		
	}
	
	public boolean isEnabled() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if("Map".equals(em.getEditorId(editor)) && editor!=null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			ILayer activeLayer = mc.getActiveLayer();
			if (activeLayer != null) {
				return activeLayer.getDataSource().canUndo();
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isVisible() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if("Map".equals(em.getEditorId(editor)) && editor!=null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			ILayer activeLayer = mc.getActiveLayer();
			return activeLayer != null;
		}		
		return false;
	}
	
	public static ImageIcon getIcon() {        
        return IconLoader.getIcon("edit-undo.png");
    }
}

