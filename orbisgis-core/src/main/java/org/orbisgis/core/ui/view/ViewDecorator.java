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
 */
/**
 *
 */
package org.orbisgis.core.ui.view;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.views.editor.DockingWindowUtil;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class ViewDecorator {
	private String id = "null" ;
	//private String title;
	private ImageIcon icon;
	private ViewPlugIn view;
	private View dockingView;
	private Component component;
	private String[] editors;
	private Component activeComponent = null;

	/**
	 * @param view
	 *            object to decorate
	 * @param id
	 *            id of the view extension
	 * @param title
	 *            Title to show in the panel
	 * @param icon
	 *            Icon to show in the panel
	 * @param editor
	 *            If this view is the one that contains the editors
	 * @param editors
	 *            The associated editors
	 */
	public ViewDecorator(ViewPlugIn view, String id, ImageIcon icon,
			String[] editors) {
		super();
		this.view = view;
		this.id = id;
		//this.title = title;
		this.icon = icon;
		this.editors = editors;
	}

	public ViewPlugIn getView() {
		return view;
	}

	public String getId() {
		return id;
	}

/*	public String getTitle() {
		return title;
	}*/

	public ImageIcon getIcon() {
		return icon;
	}

	public View getDockingView() {
		return dockingView;
	}

	public void close() {
		if (isOpen()) {
			dockingView.close();
		}
	}

	public void loadStatus(IEditor activeEditor, String activeEditorId) {
		try {
			view.loadStatus();
		} catch (PersistenceException e) {
			Services.getErrorManager().error(
					"Cannot recover previous status of view " + getId(), e);
		}
		component = view.getComponent();
		dockingView = new View(id, icon, component);
		editorChanged(activeEditor, activeEditorId);
	}

	public void open(RootWindow root, IEditor activeEditor,
			String activeEditorId) {
		if (dockingView == null) {
			component = view.getComponent();
			dockingView = new View(id, icon, component);
			DockingWindowUtil.addNewView(root, dockingView);
		} else {
			if (!isOpen()) {
				getDockingView().restore();
				if (!isOpen()) {
					dockingView = null;
					open(root, activeEditor, activeEditorId);
				}
			} else {
				dockingView.makeVisible();
			}
		}

		editorChanged(activeEditor, activeEditorId);
	}

/*	private Icon getImageIcon() {
		if (icon != null) {
			URL url = ViewDecorator.class.getResource(icon);
			if (url != null) {
				return new ImageIcon(url);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}*/

	public Component getViewComponent() {
		return component;
	}

	public boolean isOpen() {
		if (dockingView == null) {
			return false;
		} else {
			return getParent(dockingView) instanceof RootWindow;
		}
	}

	private DockingWindow getParent(DockingWindow window) {
		DockingWindow parent = window.getWindowParent();
		if (parent == null) {
			return window;
		} else {
			return getParent(parent);
		}
	}

	/**
	 * Shows the view or a message depending on the editor's id passed as an
	 * argument
	 *
	 * @param editorId
	 */
	public void editorChanged(IEditor editor, String editorId) {		
		if (this.editors.length == 0) {
			return;
		} else {
			if (dockingView != null) {				
				if ((editor == null) || (!isAssociatedEditor(editorId))) {
					disableView();
				} else {					
					if (activeComponent != null) {
						dockingView.setComponent(activeComponent);
						activeComponent = null;
						dockingView.repaint();
						
					}
					if (!((ViewPlugIn) view).setEditor(editor)) {
						disableView();
					}
				}
			}
		}
	}

	private void disableView() {
		if (activeComponent == null) {
			activeComponent = dockingView.getComponent();
		}
		dockingView.setComponent(new JLabel(
				"<html>View not available.<br/>Select an associated editor.</html>"));
	}

	public void editorClosed(String editorId) {
		if (this.editors.length == 0) {
			return;
		} else {
			if ((dockingView != null) && isAssociatedEditor(editorId)) {
				((ViewPlugIn) view).editorViewDisabled();
				WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
				wbContext.setLastAction("Editor closed");
			}
		}
	}

	private boolean isAssociatedEditor(String editorId) {
		for (String editor : editors) {
			if (editor.equals(editorId)) {
				return true;
			}
		}
		return false;
	}

}