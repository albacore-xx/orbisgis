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
package org.grap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.grap.io.BasicTest;
import org.grap.io.GeoreferencingTest;
import org.grap.io.NDVTest;
import org.grap.processing.operation.CANImplementationsTest;
import org.grap.processing.operation.CropTest;
import org.grap.processing.operation.IdentityTest;
import org.grap.processing.operation.hydrology.D8OpAccumulationTest;
import org.grap.processing.operation.hydrology.D8OpDirectionTest;
import org.grap.processing.operation.hydrology.D8OpSlopeTest;

public class AllTests extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for grap");
		// $JUnit-BEGIN$

		suite.addTestSuite(BasicTest.class);
		suite.addTestSuite(NDVTest.class);
		suite.addTestSuite(GeoreferencingTest.class);

		suite.addTestSuite(CropTest.class);
		suite.addTestSuite(IdentityTest.class);

		suite.addTestSuite(CANImplementationsTest.class);

		suite.addTestSuite(D8OpSlopeTest.class);
		suite.addTestSuite(D8OpDirectionTest.class);
		
		// TODO : re-enable following test asap!
//		suite.addTestSuite(D8OpAccumulationTest.class);

		// $JUnit-END$
		return suite;
	}
}