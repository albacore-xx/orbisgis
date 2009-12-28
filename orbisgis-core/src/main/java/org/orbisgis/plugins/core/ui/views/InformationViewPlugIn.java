package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.views.information.InformationManager;
import org.orbisgis.core.ui.views.information.Table;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;

public class InformationViewPlugIn extends ViewPlugIn {

	private Table panel;
	private JMenuItem menuItem;

	public InformationViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new Table();
		Services.registerService(InformationManager.class,
				"Service to show tabular information to the user.", panel);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { "View" }, "Information", true,
				getIcon("information_geo.png"), null, panel, 
				context.getWorkbenchContext());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
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
