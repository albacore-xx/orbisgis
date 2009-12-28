package org.orbisgis.plugins.core.ui.actions;

import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.orbisgis.core.Services;
import org.orbisgis.images.IconLoader;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workspace.PluginsWorkspace;

public class ExitPlugIn extends AbstractPlugIn {

	private JButton btn;
	private JMenuItem menuItem;

	public ExitPlugIn() {
		btn = new JButton(getIcon());
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this,
				btn);
		menuItem = context.getFeatureInstaller().addMainMenuItem(
				this, new String[] { "File" }, "Exit", false,
				getIcon("exit.png"), null, null , wbcontext);

	}

	public boolean execute(PlugInContext context) throws Exception {
		execute();
		return true;
	}
	
	public static void execute(){
		int answer = JOptionPane.showConfirmDialog(null, "Really quit?",
				"OrbisGIS", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			PluginsWorkspace psm = (PluginsWorkspace) Services
					.getService(PluginsWorkspace.class);
			psm.stopPlugins();
		}		
	}

	public static ImageIcon getIcon() {
		return IconLoader.getIcon("exit.png");
	}

	@Override
	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
		btn.setVisible(isVisible());
		
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
