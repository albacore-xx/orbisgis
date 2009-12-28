package org.orbisgis.core.ui.views.geocatalog.filter;

import java.awt.Component;
import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gdms.source.SourceManager;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class GeocatalogFilterDecorator extends AbstractPlugIn {

	private String id;
	private String name;
	private AbstractPlugIn instance;

	public GeocatalogFilterDecorator(String id, String name,
			AbstractPlugIn instance) {
		this.id = id;
		this.name = name;
		this.instance = instance;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	
	public boolean accepts(SourceManager sm, String sourceName) {
		return instance.accepts(sm, sourceName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeocatalogFilterDecorator) {
			GeocatalogFilterDecorator f = (GeocatalogFilterDecorator) obj;
			return f.getId().equals(getId());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
