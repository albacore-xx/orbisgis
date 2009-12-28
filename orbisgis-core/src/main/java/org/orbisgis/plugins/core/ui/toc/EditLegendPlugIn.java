package org.orbisgis.plugins.core.ui.toc;

import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendsPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.EPLegendHelper;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.sif.UIFactory;

public class EditLegendPlugIn extends AbstractPlugIn {	
	
	public boolean execute(PlugInContext context) {
		getUpdateFactory().executeLayers();
		return true;
	}	
	
    public void initialize(PlugInContext context) throws Exception {     	
    	WorkbenchContext wbContext = context.getWorkbenchContext();    	
    	WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();    	
		context.getFeatureInstaller().addPopupMenuItem(frame,
				this, new String[] { Names.POPUP_TOC_LEGEND_PATH },
				Names.POPUP_TOC_LEGEND_GROUP, false,
				getIcon(Names.POPUP_TOC_LEGEND_ICON), wbContext);
    }	
	
	public void update(Observable o, Object arg) {}
	
	public void execute(MapContext mapContext, ILayer layer) {
		try {	
			Type typ = layer.getDataSource().getMetadata().getFieldType(
					layer.getDataSource().getSpatialFieldIndex());
			GeometryConstraint cons = (GeometryConstraint) typ
					.getConstraint(Constraint.GEOMETRY_TYPE);
	
			LegendsPanel pan = new LegendsPanel();
			// Obtain MapTransform
			EditorManager em = (EditorManager) Services
					.getService(EditorManager.class);
			MapTransform mt = null;
			// Find the map editor editing mapContext
			IEditor editor = em.getEditors("Map",
					mapContext)[0];
			mt = ((MapEditorPlugIn) editor).getMapTransform();
			if (mt == null) {
				JOptionPane.showMessageDialog(null,Names.ERROR_EDIT_LEGEND_EDITOR);
			}
	
			Legend[] legend = layer.getVectorLegend();
			Legend[] copies = new Legend[legend.length];
			for (int i = 0; i < copies.length; i++) {
				Object obj = legend[i].getJAXBObject();
				Legend copy = legend[i].newInstance();
				copy.setJAXBObject(obj);
				copies[i] = copy;
				copies[i].setVisible(legend[i].isVisible());
			}
			ILegendPanel[] legends = EPLegendHelper.getLegendPanels(pan);
			ISymbolEditor[] symbolEditors = EPLegendHelper.getSymbolPanels();
			pan.init(mt, cons, copies, legends, symbolEditors, layer);
			if (UIFactory.showDialog(pan)) {
				try {
					layer.setLegend(pan.getLegends());
				} catch (DriverException e) {
					Services.getErrorManager().error(Names.ERROR_EDIT_LEGEND_DRIVER, e);
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error(Names.ERROR_EDIT_LEGEND_LAYER,
					e);
		}
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}
	
	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}
	
	public boolean acceptsSelectionCount(int layerCount) {
		return layerCount == 1;
	}   
}
