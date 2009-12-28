package org.orbisgis.plugins.core.ui.views;

import java.awt.Component;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.ui.views.geocognition.GeocognitionView;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;

public class GeocognitionViewPlugIn extends ViewPlugIn {

	private GeocognitionView panel;
	private JMenuItem menuItem;

	public GeocognitionView getPanel() {
		return panel;
	}

	public GeocognitionViewPlugIn() {

	}

	@Override
	public void delete() {
		panel.delete();
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new GeocognitionView();
		panel.initialize();
		menuItem = context.getFeatureInstaller().addMainMenuItem( this,
				new String[] { "View" }, "Geocognition", true,
				getIcon("mini_orbisgis.png"), null, panel,
				context.getWorkbenchContext());		
	}
	
	@Override
	public void update(Observable o, Object arg) {
		setSelected();
	}

	@Override
	public void loadStatus() throws PersistenceException {
		panel.loadStatus();		
	}

	@Override
	public void saveStatus() throws PersistenceException {
		panel.saveStatus();
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}
	
	public void setSelected(){
		menuItem.setSelected(isVisible());
	}	
	
	public boolean isVisible(){
		return getUpdateFactory().viewIsOpen(getId());
	}
}
