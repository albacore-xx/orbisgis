package org.orbisgis.plugins.core.ui.workbench;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import org.orbisgis.core.ui.views.geocatalog.SourceListModel;
import org.orbisgis.plugins.core.ui.menu.MenuTree;

public interface WorkbenchFrame {	
	public MenuTree getMenuTreePopup();	
	//TODO : filters appel depuis FeaureInstaller
	//public SourceListModel getSourceListModel();
}
