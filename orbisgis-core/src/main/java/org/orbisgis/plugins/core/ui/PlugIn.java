package org.orbisgis.plugins.core.ui;

import java.awt.Component;
import java.awt.Container;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;



public interface PlugIn extends Observer {
	//Implemented by all PlugIn
	void initialize(PlugInContext context) throws Exception;
	boolean execute(PlugInContext context) throws Exception;
	public boolean isVisible();		
}
