package org.orbisgis.plugins.core.ui.workspace;

import java.io.IOException;
import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.workspace.WorkspaceFolderFilePanel;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.sif.UIFactory;

public class ChangeWorkspacePlugIn extends AbstractPlugIn {
	
	private JMenuItem menuItem;	
	
	@Override
	public boolean execute(PlugInContext context) throws Exception {
		Workspace ws = (Workspace) Services.getService(Workspace.class);
		WorkspaceFolderFilePanel panel = new WorkspaceFolderFilePanel(
				"Select the workspace folder", Services.getService(
						ApplicationInfo.class).getHomeFolder().getAbsolutePath());
		boolean accepted = UIFactory.showDialog(panel);
		if (accepted) {
			try {
				ws
						.setWorkspaceFolder(panel.getSelectedFile()
								.getAbsolutePath());
			} catch (IOException e) {
				Services.getErrorManager().error("Cannot change workspace", e);
			}
		}
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		menuItem = context.getFeatureInstaller().addMainMenuItem(this, new String[] { "File" }, 
				 "Change Workspace", false, getIcon("application_go.png"), null, null, null);		
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("SavePlugIn : " + arg);
		menuItem.setEnabled(isEnabled());
		menuItem.setVisible(isVisible());
		
	}
	
	@Override
	public boolean isEnabled() {		
		return true;
	}

	@Override
	public boolean isVisible() {		
		return true;
	}

}
