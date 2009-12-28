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
package org.orbisgis.core.ui.views.geocognition.wizard;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.views.editor.EditorManager;

public class EPGeocognitionWizardHelper  {

	public void addElements(NewGeocognitionObject[] objs, String parentPath) {
		Geocognition geocog = Services.getService(Geocognition.class);
		EditorManager em = Services.getService(EditorManager.class);
		for (NewGeocognitionObject object : objs) {
			String id;
			if (object.isFixedName()) {
				id = parentPath + "/" + object.getBaseName();
			} else {
				if (object.isUniqueId()) {
					id = geocog.getUniqueId(object.getBaseName());
					id = parentPath + "/" + id;
				} else {
					id = parentPath + "/" + object.getBaseName();
					id = geocog.getUniqueIdPath(id);
				}
			}
			try {
				geocog.addElement(id, object.getObject());
				GeocognitionElement geocognitionElement = geocog
						.getGeocognitionElement(id);
				if (em.hasEditor(geocognitionElement)) {
					em.open(geocognitionElement);
				}
			} catch (IllegalArgumentException e) {
				Services.getErrorManager()
						.error("Cannot add element: " + id, e);
			}
		}
	}

}