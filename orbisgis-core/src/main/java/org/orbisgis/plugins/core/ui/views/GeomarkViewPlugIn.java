package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.geomark.GeomarkPanel;
import org.orbisgis.core.ui.sql.customQuery.Geomark;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;

public class GeomarkViewPlugIn extends ViewPlugIn {

	private GeomarkPanel panel;
	private String editors[];
	private JMenuItem menuItem;

	public GeomarkViewPlugIn() {

	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		panel = new GeomarkPanel();
		editors = new String[1];
		editors[0] = "Map";
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { "View" }, "Geomark", true,
				getIcon("world_add.png"), editors, panel, 
				context.getWorkbenchContext());
		context.getFeatureInstaller().addRegisterCustomQuery(Geomark.class);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public boolean setEditor(IEditor editor) {
		panel.setEditor(editor);
		return true;
	}
	
	public void update(Observable o, Object arg) {
		setSelected();
	}
	
	public void setSelected(){
		menuItem.setSelected(isVisible());
	}	
	
	public boolean isVisible(){
		return getUpdateFactory().viewIsOpen(getId());
	}

}
