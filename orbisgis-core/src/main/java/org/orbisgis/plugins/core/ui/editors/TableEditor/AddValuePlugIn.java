
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
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
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
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.plugins.core.ui.editors.TableEditor;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.gdms.data.DataSource;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.DoubleType;
import org.orbisgis.sif.multiInputPanel.IntType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.StringType;

public class AddValuePlugIn extends AbstractPlugIn {	
	
	private Integer selectedColumn;	
	private DataSource dataSource;
	private boolean isVisible ;	

	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor.getElement();		
		dataSource = element.getDataSource();
		MultiInputPanel mip = new MultiInputPanel("Add value");
		try {
			Metadata metadata = dataSource.getMetadata();
			Type type = metadata.getFieldType(selectedColumn);
			int typeCode = type.getTypeCode();
			switch (typeCode) {
			case Type.BOOLEAN:
				mip.addInput("value", "Choose a value", new ComboBoxChoice(
						"true", "false"));
				break;
			case Type.DOUBLE:
			case Type.FLOAT:
				mip.addInput("value", "Put a value", new DoubleType());
				break;
			case Type.INT:
			case Type.LONG:
			case Type.SHORT:
				mip.addInput("value", "Put a value", "0", new IntType());
				break;
			case Type.STRING:
				mip.addInput("value", "Put a value", " text ", new StringType(
						10));
				break;
			default:
				throw new IllegalArgumentException("Unknown data type: "
						+ typeCode);
			}
			int size = element.getSelection().getSelectedRows().length;
			if (size > 0) {
				mip.addInput("check", "Apply on selected row", null,
						new CheckBoxChoice(true));
			}
			if (UIFactory.showDialog(mip)) {
				int[] selectedRow = null;
				if (mip.getInput("check").equalsIgnoreCase("true")) {
					selectedRow = element.getSelection().getSelectedRows();

				} else {
					selectedRow = new int[(int) dataSource.getRowCount()];
				}

				switch (typeCode) {
				case Type.BOOLEAN:
					setValue(selectedRow, new Boolean(mip.getInput("value")),
							selectedColumn);
					break;
				case Type.DOUBLE:
				case Type.FLOAT:
					setValue(selectedRow, new Double(mip.getInput("value")),
							selectedColumn);
					break;
				case Type.INT:
				case Type.LONG:
				case Type.SHORT:
					setValue(selectedRow, new Integer(mip.getInput("value")),
							(int)selectedColumn);
					break;
				case Type.STRING:
					setValue(selectedRow, mip.getInput("value"),
							selectedColumn);
					break;
				default:
					throw new IllegalArgumentException("Unknown data type: "
							+ typeCode);
				}

			}

		} catch (DriverException e) {
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		}
		return true;
	}


	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench().getFrame().getTableEditor();	
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_ADDVALUE_PATH1 },
				Names.POPUP_TABLE_ADDVALUE_GROUP, false,
				getIcon(Names.POPUP_TABLE_ADDVALUE_ICON), wbContext);
	}


	@Override
	public void update(Observable o, Object arg) {isVisible(arg);}
	
	private void setValue(int[] selectedRow, String value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++) 
			dataSource.setString(i, selectedColumnIndex, value);
		dataSource.commit();
	}

	private void setValue(int[] selectedRow, Integer value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++)
			dataSource.setInt(i, selectedColumnIndex, value);
		dataSource.commit();
	}

	private void setValue(int[] selectedRow, double value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++) 
			dataSource.setDouble(i, selectedColumnIndex, value);
		dataSource.commit();
	}

	private void setValue(int[] selectedRow, boolean value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++)
			dataSource.setBoolean(i, selectedColumnIndex, value);
		dataSource.commit();
	}
	
	public boolean isEnabled() {
		return true;
	}	
	
	public boolean isVisible() {
		return isVisible;		
	}
	
	public boolean isVisible(Object arg) {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();		
		if("Table".equals(em.getEditorId(editor)) && editor!=null) {
			try {
				selectedColumn = (Integer)arg;
			}catch(Exception e){
				return isVisible = false;
			}			
			final TableEditableElement element = (TableEditableElement) editor.getElement();
			if(selectedColumn == null) return isVisible = false;		
			try {
				if ((selectedColumn != -1) && element.isEditable() ){
					Metadata metadata = element.getDataSource().getMetadata();
					Type type = metadata.getFieldType(selectedColumn);
					int typeCode = type.getTypeCode();
					if (typeCode !=Type.GEOMETRY) return isVisible = true;
				}

			} catch (DriverException e) {
				Services.getService(ErrorManager.class).error(
						"Cannot access field information", e);
			}
		}
		return isVisible = false;
	}
}
