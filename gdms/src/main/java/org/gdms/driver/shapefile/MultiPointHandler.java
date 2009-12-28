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
import com.vividsolutions.jts.geom.MultiPoint;



/**
 *
 * @author aaime
 * @author Ian Schneider
 * @source $URL: http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/MultiPointHandler.java $
 *
 */
public class MultiPointHandler implements ShapeHandler {
  final ShapeType shapeType;
  GeometryFactory geometryFactory = new GeometryFactory();

  /** Creates new MultiPointHandler */
  public MultiPointHandler() {
    shapeType = ShapeType.POINT;
  }

  public MultiPointHandler(ShapeType type) throws ShapefileException {
    if ((type != ShapeType.MULTIPOINT) && (type != ShapeType.MULTIPOINTM) && (type != ShapeType.MULTIPOINTZ)) {
      throw new ShapefileException(
      "Multipointhandler constructor - expected type to be 8, 18, or 28");
    }

    shapeType = type;
  }



  /**
   * Returns the shapefile shape type value for a point
   * @return int Shapefile.POINT
   */
  public ShapeType getShapeType() {
    return shapeType;
  }

  /**
   * Calcuates the record length of this object.
   * @return int The length of the record that this shapepoint will take up in a shapefile
   **/
  public int getLength(Object geometry) {
    MultiPoint mp = (MultiPoint) geometry;

    int length;

    if (shapeType == ShapeType.MULTIPOINT) {
      // two doubles per coord (16 * numgeoms) + 40 for header
      length = (mp.getNumGeometries() * 16) + 40;
    } else if (shapeType == ShapeType.MULTIPOINTM) {
      // add the additional MMin, MMax for 16, then 8 per measure
      length = (mp.getNumGeometries() * 16) + 40 + 16 + (8 * mp.getNumGeometries());
    } else if (shapeType == ShapeType.MULTIPOINTZ) {
      // add the additional ZMin,ZMax, plus 8 per Z
      length = (mp.getNumGeometries() * 16) + 40 + 16 + (8 * mp.getNumGeometries()) + 16 +
      (8 * mp.getNumGeometries());
    } else {
      throw new IllegalStateException("Expected ShapeType of Arc, got " + shapeType);
    }

    return length;
  }

  public Geometry read(ReadBufferManager buffer, ShapeType type) throws IOException {
    if (type == ShapeType.NULL) {
      return null;
    }

    //read bounding box (not needed)
    buffer.skip(4 * 8);

    int numpoints = buffer.getInt();
    Coordinate[] coords = new Coordinate[numpoints];

    for (int t = 0; t < numpoints; t++) {
      double x = buffer.getDouble();
      double y = buffer.getDouble();
      coords[t] = new Coordinate(x, y);
    }

    if (shapeType == ShapeType.MULTIPOINTZ) {
      buffer.skip(2 * 8);

      for (int t = 0; t < numpoints; t++) {
        coords[t].z = buffer.getDouble(); //z
      }
    }

    return geometryFactory.createMultiPoint(coords);
  }

  public void write(WriteBufferManager buffer, Object geometry) throws IOException {
    MultiPoint mp = (MultiPoint) geometry;

    Envelope box = mp.getEnvelopeInternal();
    buffer.putDouble(box.getMinX());
    buffer.putDouble(box.getMinY());
    buffer.putDouble(box.getMaxX());
    buffer.putDouble(box.getMaxY());


    buffer.putInt(mp.getNumGeometries());


    for (int t = 0, tt = mp.getNumGeometries(); t < tt; t++) {
      Coordinate c = (mp.getGeometryN(t)).getCoordinate();
      buffer.putDouble(c.x);
      buffer.putDouble(c.y);
    }


    if (shapeType == ShapeType.MULTIPOINTZ) {
      double[] zExtreame = JTSUtilities.zMinMax(mp.getCoordinates());

      if (Double.isNaN(zExtreame[0])) {
        buffer.putDouble(0.0);
        buffer.putDouble(0.0);
      } else {
        buffer.putDouble(zExtreame[0]);
        buffer.putDouble(zExtreame[1]);
      }


      for (int t = 0; t < mp.getNumGeometries(); t++) {
        Coordinate c = (mp.getGeometryN(t)).getCoordinate();
        double z = c.z;

        if (Double.isNaN(z)) {
          buffer.putDouble(0.0);
        } else {
          buffer.putDouble(z);
        }
      }
    }



    if (shapeType == ShapeType.MULTIPOINTM || shapeType == ShapeType.MULTIPOINTZ) {
      buffer.putDouble(-10E40);
      buffer.putDouble(-10E40);


      for (int t = 0; t < mp.getNumGeometries(); t++) {
        buffer.putDouble(-10E40);
      }
    }
  }

}
