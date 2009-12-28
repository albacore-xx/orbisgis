package org.orbisgis.plugins.core.ui.editors.MapEditor;

import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.images.IconLoader;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class FullExtentPlugIn extends AbstractPlugIn {
	
	private JButton btn ;
	
	public FullExtentPlugIn() {
		btn = new JButton(getIcon());		
	}
	
	public void initialize(PlugInContext context) throws Exception {	
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getNavigationToolBar().addPlugIn(this, btn);
	}
	
	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor =  Services.getService(EditorManager.class).getActiveEditor();		
		MapContext mc = (MapContext) editor.getElement().getObject();
		((MapEditorPlugIn) editor).getMapTransform().setExtent(mc.getLayerModel().getEnvelope());
		return true;
	}	
	
	public boolean isEnabled(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();
		return mc.getLayerModel().getLayerCount() > 0;
	}

	public boolean isVisible(IEditor editor) {
		return true;
	}
	
    public static ImageIcon getIcon() {        
        return IconLoader.getIcon("world.png");
    }

	@Override
	public void update(Observable o, Object arg) {		
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();			
		if (editor!=null && editor instanceof MapEditorPlugIn) 
			btn.setEnabled(isEnabled(editor));		
		else
			btn.setEnabled(false);		
	}
}
