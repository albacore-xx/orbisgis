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
package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Division extends ArithmeticOperator {

	public Division(Expression... children) {
		super(children);
	}

	public Value evaluateExpression() throws EvaluationException,
			IncompatibleTypesException {
		return getLeftOperator().evaluate().producto(
				getRightOperator().evaluate().inversa());
	}

	@Override
	public Type getType() throws DriverException {
		int rightCode = getRightOperator().getType().getTypeCode();
		int leftCode = getLeftOperator().getType().getTypeCode();
		switch (leftCode) {
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
		case Type.LONG:
			if ((rightCode == Type.BYTE) || (rightCode == Type.SHORT)
					|| (rightCode == Type.INT) || (rightCode == Type.LONG)) {
				return TypeFactory.createType(leftCode);
			} else {
				return TypeFactory.createType(Type.DOUBLE);
			}
		case Type.FLOAT:
		case Type.DOUBLE:
			return TypeFactory.createType(Type.DOUBLE);
		}
		return TypeFactory.createType(rightCode);
	}

	@Override
	protected String getOperatorSymbol() {
		return "/";
	}

	public Expression cloneExpression() {
		return new Division(getChildren());
	}

}
