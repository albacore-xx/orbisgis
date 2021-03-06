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
package org.gdms.sql.function.spatial.geometry.properties;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Polygon;

public class NumInteriorRing extends AbstractSpatialPropertyFunction {

	public Value evaluateResult(final Value[] args) throws FunctionException {

		Geometry g = args[0].getAsGeometry();

		int holes = getHoles(g);

		return ValueFactory.createValue(holes);
	}

	private int getHoles(Geometry g) {
		int holes = 0;
		if (g instanceof GeometryCollection) {
			int geomCount = g.getNumGeometries();
			for (int i = 0; i < geomCount; i++) {
				holes += getHoles(g.getGeometryN(i));
			}
		} else if (g instanceof Polygon) {
			holes = ((Polygon) g).getNumInteriorRing();
		}
		return holes;
	}

	public String getName() {
		return "NumInteriorRing";
	}

	public Type getType(Type[] types) {
		return TypeFactory.createType(Type.INT);
	}

	public boolean isAggregate() {
		return false;
	}

	public String getDescription() {
		return "Return the number of holes in a geometry";
	}

	public String getSqlOrder() {
		return "select NumInteriorRing(the_geom) from myTable;";
	}

}