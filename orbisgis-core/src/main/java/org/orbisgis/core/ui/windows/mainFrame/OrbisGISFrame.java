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
package org.orbisgis.core.ui.windows.mainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;

import org.apache.log4j.Logger;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.Toc;
import org.orbisgis.core.ui.editors.table.TableComponent;
import org.orbisgis.core.ui.view.ViewDecorator;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.core.ui.views.geocatalog.Catalog;
import org.orbisgis.core.ui.views.geocognition.GeocognitionView;
import org.orbisgis.core.ui.window.IWindow;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.images.IconLoader;
import org.orbisgis.language.I18N;
import org.orbisgis.plugins.core.ui.actions.ExitPlugIn;
import org.orbisgis.plugins.core.ui.views.GeoCatalogViewPlugIn;
import org.orbisgis.plugins.core.ui.views.GeocognitionViewPlugIn;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.TableEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.TocViewPlugIn;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.OrbisWorkbench;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchToolBar;


public class OrbisGISFrame  extends JFrame 
							implements IWindow{

	private static final Logger logger = Logger.getLogger(OrbisGISFrame.class);
	private static final String LAYOUT_PERSISTENCE_FILE = "org.orbisgis.core.ui.ViewLayout.obj";	
	private JMenuBar actionMenuBar;	
	public JMenuBar getActionMenuBar() {
		return actionMenuBar;
	}
	
	private WorkbenchToolBar workbenchToolBar;
	public WorkbenchToolBar getWorkbenchToolBar() {
		return workbenchToolBar;
	}

	private ArrayList<ViewDecorator> views = new ArrayList<ViewDecorator>();	
	private RootWindow root;
	private MyViewSerializer viewSerializer = new MyViewSerializer();
	private boolean perspectiveLoaded = false;		
	private WorkbenchContext workbenchContext;	
	//TODO: le menuTree de la popup TableComponent est pê à déplacer
	private org.orbisgis.plugins.core.ui.menu.MenuTree menuTableTreePopup;
	public org.orbisgis.plugins.core.ui.menu.MenuTree getMenuTableTreePopup() {
		return menuTableTreePopup;		
	}	
	public void  setTableMenuTreePopup() {
		this.menuTableTreePopup = getTableEditor().getMenuTreePopup();		
	}	

	public ArrayList<ViewDecorator> getViews() {
		return views;
	}
	
	private org.orbisgis.plugins.core.ui.menu.MenuTree menuMapTreePopup;
	public org.orbisgis.plugins.core.ui.menu.MenuTree getMenuMapTreePopup() {
		return menuMapTreePopup;		
	}	
	public void  setMapMenuTreePopup() {
		this.menuMapTreePopup = getMapEditor().getMenuTreePopup();		
	}

	public WorkbenchToolBar getToolBar(String id) {		
		return workbenchToolBar.getToolbars().get(id);
	}	
	public WorkbenchToolBar getMainToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_NAME);
	}
	
	public WorkbenchToolBar getRasterToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_RASTER);
	}	
	public WorkbenchToolBar getNavigationToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_NAVIGATION);
	}	
	public WorkbenchToolBar getInfoToolBar() {	
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_INFO);		
	}	
	public WorkbenchToolBar getFenceToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_FENCE);
	}	
	public WorkbenchToolBar getMesureToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_MESURE);
	}	
	public WorkbenchToolBar getEditionMapToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_MAP);
	}	
	public WorkbenchToolBar getEditionTableToolBar() {		
		return workbenchToolBar.getToolbars().get(Names.TOOLBAR_TABLE);
	}	
	
	
	public Toc getToc () {
		return ((TocViewPlugIn)getViewDecorator("Toc").getView()).getPanel();		
	}
	
	public GeocognitionView getGeocognition() {
		return ((GeocognitionViewPlugIn)getViewDecorator("Geocognition").getView()).getPanel();		
	}
	
	public Catalog getGeocatalog() {
		return ((GeoCatalogViewPlugIn)getViewDecorator("Geocatalog").getView()).getPanel();		
	}
	
	public TableComponent getTableEditor() {
		return ((TableEditorPlugIn)getViewDecorator("Table").getView()).getPanel();		
	}
	
	public MapEditorPlugIn getMapEditor() {
		return (MapEditorPlugIn)getViewDecorator("Map").getView();		
	}	
	
	public OrbisGISFrame() {
		OrbisWorkbench orbisWorkbench = new OrbisWorkbench(this);
		this.workbenchContext = orbisWorkbench.getWorkbenchContext();		
		workbenchToolBar = new WorkbenchToolBar(this.workbenchContext,"OrbisGIS main tools");		
		workbenchToolBar.setFloatable(true);
		
		actionMenuBar = new JMenuBar();
		this.setLayout(new BorderLayout());		
		this.getContentPane().add(workbenchToolBar, BorderLayout.PAGE_START);

		// Initialize views
		root = new RootWindow(viewSerializer);

		root.getRootWindowProperties().getSplitWindowProperties()
				.setContinuousLayoutEnabled(false);

		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getFocusedProperties()
				.getComponentProperties().setBackgroundColor(
						new Color(100, 140, 190));

		//Some options for window properties
		root.getRootWindowProperties().getTabWindowProperties()
		.getCloseButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);

		DockingWindowsTheme theme = new ShapedGradientDockingTheme();
		// Apply theme
		root.getRootWindowProperties().addSuperObject(
		  theme.getRootWindowProperties());

		this.getContentPane().add(root, BorderLayout.CENTER);

		// Prepare menu and toolbar
		this.setJMenuBar(actionMenuBar);		
		
		Services.registerService(RootWindow.class,
				"Root window", root);		
		orbisWorkbench.runWorkbench(); //PYF
		initializeViews();		
	
		I18N.loadFile("fr");
		this.setTitle(I18N.get("orbisgis.platform"));
		//this.setTitle("OrbisGIS Platform");
		this.setIconImage(IconLoader.getIcon("mini_orbisgis.png").getImage());
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		this.setSize((int) screenSize.width, (int) screenSize.height);
		this.setLocationRelativeTo(null);
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				ExitPlugIn.execute();
			}

		});
		//EPConfigHelper.loadAndApplyConfigurations();
				
	}

	private void initializeViews() {
		for (ViewDecorator view : views) {
			try {
				view.getView().initialize(workbenchContext);
			} catch (Exception e) {
				Services.getErrorManager().error(
						"Error initializating view " + view.getId(), e);
			}
		}

		final EditorManager em = Services.getService(EditorManager.class);
		if (em == null) {
			throw new RuntimeException(
					"A view must initialize the EditorManager service");
		}

		em.addEditorListener(new EditorListener() {

			public void activeEditorChanged(IEditor previous, IEditor current) {
				WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
				wbContext.setLastAction("Editor Changed");
				IEditor activeEditor = em.getActiveEditor();
				for (ViewDecorator view : views) {
					view.editorChanged(activeEditor, em
							.getEditorId(activeEditor));
				}
			}

			public void activeEditorClosed(IEditor editor, String editorId) {
				for (ViewDecorator view : views) {
					view.editorClosed(editorId);
				}
			}

			@Override
			public boolean activeEditorClosing(IEditor editor, String editorId) {
				return true;
			}

		});
	}

	public void showWindow() {
		if (!perspectiveLoaded) {
			// Load default perspective
			loadPerspective(OrbisGISFrame.class
					.getResourceAsStream(LAYOUT_PERSISTENCE_FILE));
		}
		this.setVisible(true);	
		
	}

	public Component getView(String viewId) {
		ViewDecorator ret = getViewDecorator(viewId);
		if (ret != null) {
			if (!ret.isOpen()) {
				EditorManager em = Services.getService(EditorManager.class);
				IEditor activeEditor = em.getActiveEditor();
				ret.open(root, activeEditor, em.getEditorId(activeEditor));
			}
			return ret.getViewComponent();
		} else {
			return null;
		}
	}

	public void showView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			view.open(root, activeEditor, em.getEditorId(activeEditor));
		}
	}

	public void hideView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			view.close();
		}
	}

	public ViewDecorator getViewDecorator(String id) {
		for (ViewDecorator view : views) {
			if (view.getId().equals(id)) {
				return view;
			}
		}

		return null;
	}
	
	public void delete() {
		this.setVisible(false);
		this.dispose();
		for (ViewDecorator vd : views) {
			if (vd.getViewComponent() != null) {
				vd.getView().delete();
			}
		}
	}

	public RootWindow getRoot() {
		return root;
	}

	public JFrame getMainFrame() {
		return this;
	}	

	public Rectangle getPosition() {
		return this.getBounds();
	}

	public boolean isOpened() {
		return this.isVisible();
	}

	public void load(Map<String, String> properties)
			throws PersistenceException {
		// we override the default layout
		this.getContentPane().remove(root);
		root = new RootWindow(viewSerializer);

		DockingWindowsTheme theme = new ShapedGradientDockingTheme();

		// Apply theme
		root.getRootWindowProperties().addSuperObject(
		  theme.getRootWindowProperties());

		this.getContentPane().add(root, BorderLayout.CENTER);

		Workspace ws = (Workspace) Services.getService(Workspace.class);
		FileInputStream layoutStream;
		try {
			layoutStream = new FileInputStream(ws
					.getFile(LAYOUT_PERSISTENCE_FILE));
			loadPerspective(layoutStream);
		} catch (FileNotFoundException e) {
			logger.error("Could not recover perspective, missing file", e);
		}
	}

	private void loadPerspective(InputStream layoutStream) {
		try {
			ObjectInputStream ois = new ObjectInputStream(layoutStream);
			root.read(ois);
			perspectiveLoaded = true;
			ois.close();
		} catch (Exception e) {
			Services.getErrorManager().error(
					"Cannot recover the layout of the window", e);
		}
	}

	public Map<String, String> save() throws PersistenceException {
		try {
			Workspace ws = (Workspace) Services.getService(Workspace.class);
			FileOutputStream fos = new FileOutputStream(ws
					.getFile(LAYOUT_PERSISTENCE_FILE));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			root.write(oos);
			oos.close();
		} catch (IOException e) {
			throw new PersistenceException(e);
		}

		return null;
	}

	public void setPosition(Rectangle position) {
		this.setBounds(position);
	}

	/**
	 * Writes the id of the view and then writes the status. Reads the id,
	 * obtains the data from the extension xml and reads the status
	 *
	 * @author Fernando Gonzalez Cortes
	 */
	private class MyViewSerializer implements ViewSerializer {

		public View readView(ObjectInputStream ois) throws IOException {
			String id = ois.readUTF();
			ViewDecorator vd = OrbisGISFrame.this.getViewDecorator(id);
			if (vd != null) {
				try {
					EditorManager em = Services.getService(EditorManager.class);
					IEditor activeEditor = em.getActiveEditor();
					vd.loadStatus(activeEditor, em.getEditorId(activeEditor));
					return vd.getDockingView();
				} catch (Throwable t) {
					Services.getErrorManager().error(
							"Cannot recover view " + id, t);
				}
			}

			return null;
		}

		public void writeView(View view, ObjectOutputStream oos)
				throws IOException {
			ViewDecorator vd = getViewDecorator(view);
			if (vd!=null){
			oos.writeUTF(vd.getId());
			try {
				vd.getView().saveStatus();
			} catch (Throwable e) {
				Services.getErrorManager().error(
						"Cannot save view " + vd.getId(), e);
			}
			}

		}

		private ViewDecorator getViewDecorator(View view) {
			for (ViewDecorator viewDecorator : views) {
				if (viewDecorator.getDockingView() == view) {
					return viewDecorator;
				}

			}

			return null;
		}

	}
}