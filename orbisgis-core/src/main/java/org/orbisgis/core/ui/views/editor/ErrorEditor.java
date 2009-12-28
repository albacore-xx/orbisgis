/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.views.editor;

import java.awt.Component;

import javax.swing.JLabel;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.edition.EditableElementListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.progress.IProgressMonitor;

//TODO : What's this editor (Is it a View Plugin to do?)
public class ErrorEditor implements IEditor {
	
	private String message;
	private String documentName;

	public ErrorEditor(String documentName, String message) {
		this.message = message;
		this.documentName = documentName;
	}

	public boolean acceptElement(String typeId) {
		return false;
	}

	public EditableElement getElement() {
		return new DummyElement();
	}

	public String getTitle() {
		return documentName;
	}

	public void setElement(EditableElement obj) {
	}

	public void delete() {
	}

	public Component getComponent() {
		return new JLabel(message);
	}	

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	private class DummyElement implements EditableElement {

		@Override
		public void close(IProgressMonitor progressMonitor)
				throws UnsupportedOperationException {

		}

		@Override
		public Object getObject() throws UnsupportedOperationException {
			return null;
		}

		@Override
		public String getTypeId() {
			return "org.orbisgis.geocognition.Error";
		}

		@Override
		public void open(IProgressMonitor progressMonitor)
				throws UnsupportedOperationException, EditableElementException {

		}

		@Override
		public void save() throws UnsupportedOperationException {

		}

		@Override
		public boolean isModified() {
			return false;
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public void addElementListener(EditableElementListener listener) {
		}

		@Override
		public boolean removeElementListener(EditableElementListener listener) {
			return true;
		}

	}
	
	public ViewPlugIn getView() {		
		return null;
	}

	@Override
	public void initialize(PlugInContext wbContext, Automaton automaton)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
