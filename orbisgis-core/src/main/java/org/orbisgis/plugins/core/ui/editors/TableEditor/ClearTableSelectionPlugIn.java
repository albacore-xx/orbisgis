
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

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.images.IconLoader;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class ClearTableSelectionPlugIn extends AbstractPlugIn {
	
	private JButton btn ;
	private boolean menuItemIsVisible;
	
	public ClearTableSelectionPlugIn() {
		btn = new JButton(getIcon());
	}	
	
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		TableEditableElement element = (TableEditableElement) editor.getElement();
		element.getSelection().clearSelection();
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbcontext.getWorkbench().getFrame().getTableEditor();
		wbcontext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(this, btn);
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_CLEAR_PATH1 },
				Names.POPUP_TABLE_CLEAR_GROUP, false,
				getIcon(Names.POPUP_TABLE_CLEAR_ICON), wbcontext);
	}
	
	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
		btn.setVisible(true);
		menuItemIsVisible = (arg instanceof MouseEvent) ? true : false;
	}
	
	public boolean isEnabled() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if("Table".equals(em.getEditorId(editor)) && editor!=null) {
			TableEditableElement element = (TableEditableElement) editor.getElement();
			return element.getSelection().getSelectedRows().length > 0;
		}
		return false;
	}

	public boolean isVisible() {
		return menuItemIsVisible && isEnabled();	
	}
	
	public static ImageIcon getIcon() {        
        return IconLoader.getIcon("edit-clear.png");
    }
}


