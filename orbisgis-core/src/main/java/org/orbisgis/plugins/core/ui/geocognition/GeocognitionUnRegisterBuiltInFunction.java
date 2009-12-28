
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

package org.orbisgis.plugins.core.ui.geocognition;

import java.util.Observable;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.sql.GeocognitionBuiltInFunction;
import org.orbisgis.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class GeocognitionUnRegisterBuiltInFunction extends AbstractPlugIn {
	
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().executeGeocognition();
		return true;
	}
	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocognition();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_GEOCOGNITION_UNREG_BUILT_PATH1 }, 
				Names.POPUP_GEOCOGNITION_UNREG_BUILT_GROUP, false,
				getIcon(Names.POPUP_GEOCOGNITION_UNREG_BUILT_ICON), wbContext);
	}
	
	public void update(Observable o, Object arg) {}
	
	@SuppressWarnings("unchecked")
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		if (GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID.equals(element
				.getTypeId())) {
			Class<? extends Function> fnc = (Class<? extends Function>) element
					.getObject();
			try {
				FunctionManager.remove(fnc.newInstance().getName());
			} catch (InstantiationException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			} catch (IllegalAccessException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			}
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().geocognitionIsVIsible();
	}

	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		if (GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID.equals(element
				.getTypeId())) {
			String registered = element.getProperties().get(
					GeocognitionBuiltInFunction.REGISTERED);
			if ((registered != null)
					&& registered
							.equals(GeocognitionBuiltInFunction.IS_REGISTERED)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}
}
