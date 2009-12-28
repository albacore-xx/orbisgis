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
/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2006, Geotools Project Managment Committee (PMC)
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.gdms.driver.shapefile;

import java.io.IOException;

import org.gdms.driver.ReadBufferManager;
import org.gdms.driver.WriteBufferManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;

/*
 * $Id: MultiLineHandler.java 20881 2006-08-07 13:24:35Z jgarnett $ @author
 * aaime @author Ian Schneider
 */
/**
 * The default JTS handler for shapefile. Currently uses the default JTS
 * GeometryFactory, since it doesn't seem to matter.
 *
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/MultiLineHandler.java $
 */
public class MultiLineHandler implements ShapeHandler {
	final ShapeType shapeType;

	GeometryFactory geometryFactory = new GeometryFactory();

	/** Create a MultiLineHandler for ShapeType.ARC */
	public MultiLineHandler() {
		shapeType = ShapeType.ARC;
	}

	/**
	 * Create a MultiLineHandler for one of: <br>
	 * ShapeType.ARC,ShapeType.ARCM,ShapeType.ARCZ
	 *
	 * @param type
	 *            The ShapeType to use.
	 * @throws ShapefileException
	 *             If the ShapeType is not correct (see constructor).
	 */
	public MultiLineHandler(ShapeType type) throws ShapefileException {
		if ((type != ShapeType.ARC) && (type != ShapeType.ARCM)
				&& (type != ShapeType.ARCZ)) {
			throw new ShapefileException(
					"MultiLineHandler constructor - expected type to be 3,13 or 23");
		}

		shapeType = type;
	}

	/**
	 * Get the type of shape stored
	 * (ShapeType.ARC,ShapeType.ARCM,ShapeType.ARCZ)
	 */
	public ShapeType getShapeType() {
		return shapeType;
	}

	/** */
	public int getLength(Object geometry) {
		MultiLineString multi = (MultiLineString) geometry;

		int numlines;
		int numpoints;
		int length;

		numlines = multi.getNumGeometries();
		numpoints = multi.getNumPoints();

		if (shapeType == ShapeType.ARC) {
			length = 44 + (4 * numlines) + (numpoints * 16);
		} else if (shapeType == ShapeType.ARCM) {
			length = 44 + (4 * numlines) + (numpoints * 16) + 8 + 8
					+ (8 * numpoints);
		} else if (shapeType == ShapeType.ARCZ) {
			length = 44 + (4 * numlines) + (numpoints * 16) + 8 + 8
					+ (8 * numpoints) + 8 + 8 + (8 * numpoints);
		} else {
			throw new IllegalStateException("Expected ShapeType of Arc, got "
					+ shapeType);
		}

		return length;
	}

	public Geometry read(ReadBufferManager buffer, ShapeType type)
			throws IOException {
		if (type == ShapeType.NULL) {
			return null;
		}
		int dimensions = (shapeType == ShapeType.ARCZ) ? 3 : 2;
		// read bounding box (not needed)
		buffer.skip(4 * 8);

		int numParts = buffer.getInt();
		int numPoints = buffer.getInt(); // total number of points

		int[] partOffsets = new int[numParts];

		// points = new Coordinate[numPoints];
		for (int i = 0; i < numParts; i++) {
			partOffsets[i] = buffer.getInt();
		}
		// read the first two coordinates and start building the coordinate
		// sequences
		PackedCoordinateSequence[] lines = new PackedCoordinateSequence[numParts];
		int finish, start = 0;
		int length = 0;
		boolean clonePoint = false;
		for (int part = 0; part < numParts; part++) {
			start = partOffsets[part];

			if (part == (numParts - 1)) {
				finish = numPoints;
			} else {
				finish = partOffsets[part + 1];
			}

			length = finish - start;
			if (length == 1) {
				length = 2;
				clonePoint = true;
			} else {
				clonePoint = false;
			}

			// TODO With next version of JTS uncomment this line
			// PackedCoordinateSequence builder = new
			// PackedCoordinateSequence.Double(
			// length, dimension);
			PackedCoordinateSequence builder = new PackedCoordinateSequence.Double(
					length, 3);
			for (int i = 0; i < length; i++) {
				builder.setOrdinate(i, 0, buffer.getDouble());
				builder.setOrdinate(i, 1, buffer.getDouble());
				// TODO With next version of JTS remove this line
				builder.setOrdinate(i, 2, Double.NaN);
			}

			if (clonePoint) {
				builder.setOrdinate(1, 0, builder.getOrdinate(0, 0));
				builder.setOrdinate(1, 1, builder.getOrdinate(1, 0));
				// TODO With next version of JTS remove this line
				builder.setOrdinate(1, 2, Double.NaN);
			}

			lines[part] = builder;
		}

		// if we have another coordinate, read and add to the coordinate
		// sequences
		if (dimensions == 3) {
			// z min, max
			buffer.skip(2 * 8);
			for (int part = 0; part < numParts; part++) {
				start = partOffsets[part];

				if (part == (numParts - 1)) {
					finish = numPoints;
				} else {
					finish = partOffsets[part + 1];
				}

				length = finish - start;
				if (length == 1) {
					length = 2;
					clonePoint = true;
				} else {
					clonePoint = false;
				}

				for (int i = 0; i < length; i++) {
					lines[part].setOrdinate(i, 2, buffer.getDouble());
				}

			}
		}

		// Prepare line strings and return the multilinestring
		LineString[] lineStrings = new LineString[numParts];
		for (int part = 0; part < numParts; part++) {
			lineStrings[part] = geometryFactory.createLineString(lines[part]);
		}

		return geometryFactory.createMultiLineString(lineStrings);
	}

	public void write(WriteBufferManager buffer, Object geometry)
			throws IOException {
		MultiLineString multi = (MultiLineString) geometry;

		Envelope box = multi.getEnvelopeInternal();
		buffer.putDouble(box.getMinX());
		buffer.putDouble(box.getMinY());
		buffer.putDouble(box.getMaxX());
		buffer.putDouble(box.getMaxY());

		int numParts = multi.getNumGeometries();

		buffer.putInt(numParts);
		int npoints = multi.getNumPoints();
		buffer.putInt(npoints);

		LineString[] lines = new LineString[numParts];
		int idx = 0;

		for (int i = 0; i < numParts; i++) {
			lines[i] = (LineString) multi.getGeometryN(i);
			buffer.putInt(idx);
			idx = idx + lines[i].getNumPoints();
		}

		Coordinate[] coords = multi.getCoordinates();

		for (int t = 0; t < npoints; t++) {
			buffer.putDouble(coords[t].x);
			buffer.putDouble(coords[t].y);
		}

		if (shapeType == ShapeType.ARCZ) {
			double[] zExtreame = JTSUtilities.zMinMax(coords);

			if (Double.isNaN(zExtreame[0])) {
				buffer.putDouble(0.0);
				buffer.putDouble(0.0);
			} else {
				buffer.putDouble(zExtreame[0]);
				buffer.putDouble(zExtreame[1]);
			}

			for (int t = 0; t < npoints; t++) {
				double z = coords[t].z;

				if (Double.isNaN(z)) {
					buffer.putDouble(0.0);
				} else {
					buffer.putDouble(z);
				}
			}
		}

		if (shapeType == ShapeType.ARCZ) {
			buffer.putDouble(-10E40);
			buffer.putDouble(-10E40);

			for (int t = 0; t < npoints; t++) {
				buffer.putDouble(-10E40);
			}
		}
	}

}

/*
 * $Log: MultiLineHandler.java,v $ Revision 1.4 2003/11/13 22:10:35 jmacgill
 * cast a null to avoid ambigous call with JTS1.4
 *
 * Revision 1.3 2003/07/23 23:41:09 ianschneider more testing updates
 *
 * Revision 1.2 2003/07/23 00:59:59 ianschneider Lots of PMD fix ups
 *
 * Revision 1.1 2003/05/14 17:51:20 ianschneider migrated packages
 *
 * Revision 1.3 2003/04/30 23:19:45 ianschneider Added construction of multi
 * geometries for default return values, even if only one geometry. This could
 * have effects through system.
 *
 * Revision 1.2 2003/03/30 20:21:09 ianschneider Moved buffer branch to main
 *
 * Revision 1.1.2.3 2003/03/12 15:30:14 ianschneider made ShapeType final for
 * handlers - once they're created, it won't change.
 *
 * Revision 1.1.2.2 2003/03/07 00:36:41 ianschneider
 *
 * Added back the additional ShapeType parameter in ShapeHandler.read.
 * ShapeHandler's need return their own special "null" shape if needed. Fixed
 * the ShapefileReader to not throw exceptions for "null" shapes. Fixed
 * ShapefileReader to accomodate junk after the last valid record. The theory
 * goes, if the shape number is proper, that is, one greater than the previous,
 * we consider that a valid record and attempt to read it. I suppose, by chance,
 * the junk could coincide with the next record number. Stupid ESRI. Fixed some
 * record-length calculations which resulted in writing of bad shapefiles.
 *
 * Revision 1.1.2.1 2003/03/06 01:16:34 ianschneider
 *
 * The initial changes for moving to java.nio. Added some documentation and
 * improved exception handling. Works for reading, may work for writing as of
 * now.
 *
 * Revision 1.1 2003/02/27 22:35:50 aaime New shapefile module, initial commit
 *
 * Revision 1.2 2003/01/22 18:31:05 jaquino Enh: Make About Box configurable
 *
 * Revision 1.3 2002/10/30 22:36:11 dblasby Line reader now returns
 * LINESTRING(..) if there is only one part to the arc polyline.
 *
 * Revision 1.2 2002/09/09 20:46:22 dblasby Removed LEDatastream refs and
 * replaced with EndianData[in/out]putstream
 *
 * Revision 1.1 2002/08/27 21:04:58 dblasby orginal
 *
 * Revision 1.2 2002/03/05 10:23:59 jmacgill made sure geometries were created
 * using the factory methods
 *
 * Revision 1.1 2002/02/28 00:38:50 jmacgill Renamed files to more intuitve
 * names
 *
 * Revision 1.3 2002/02/13 00:23:53 jmacgill First semi working JTS version of
 * Shapefile code
 *
 * Revision 1.2 2002/02/11 18:42:45 jmacgill changed read and write statements
 * so that they produce and take Geometry objects instead of specific MultiLine
 * objects changed parts[] array name to partOffsets[] for clarity and
 * consistency with ShapePolygon
 *
 * Revision 1.1 2002/02/11 16:54:43 jmacgill added shapefile code and
 * directories
 *
 */
