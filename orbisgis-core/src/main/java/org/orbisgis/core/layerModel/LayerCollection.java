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
package org.orbisgis.core.layerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.NullCRS;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.layerModel.persistence.LayerCollectionType;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

import com.vividsolutions.jts.geom.Envelope;

public class LayerCollection extends AbstractLayer {
	private List<ILayer> layerCollection;

	public LayerCollection(String name) {
		super(name);
		layerCollection = new ArrayList<ILayer>();
	}

	List<ILayer> getLayerCollection() {
		return layerCollection;
	}

	public int getIndex(ILayer layer) {
		return layerCollection.indexOf(layer);
	}

	public ILayer getLayer(final int index) {
		return layerCollection.get(index);
	}

	private void setNamesRecursively(final ILayer layer,
			final Set<String> allLayersNames) throws LayerException {
		layer.setName(provideNewLayerName(layer.getName(), allLayersNames));
		if (layer instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) layer;
			if (null != lc.getLayerCollection()) {
				for (ILayer layerItem : lc.getChildren()) {
					setNamesRecursively(layerItem, allLayersNames);
				}
			}
		}
	}

	private String provideNewLayerName(final String name,
			final Set<String> allLayersNames) {
		String tmpName = name;
		if (allLayersNames.contains(tmpName)) {
			int i = 1;
			while (allLayersNames.contains(tmpName + "_" + i)) {
				i++;
			}
			tmpName += "_" + i;
		}
		allLayersNames.add(tmpName);
		return tmpName;
	}

	public void addLayer(final ILayer layer) throws LayerException {
		addLayer(layer, false);
	}

	// Allows to put a layer at a specific index
	public void insertLayer(final ILayer layer, int index)
			throws LayerException {		
		insertLayer(layer, index, false);	
		//WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
		//wbContext.setLastAction("Insert layer");
	}

	/**
	 * Removes the layer from the collection
	 *
	 * @param layerName
	 * @return the layer removed or null if the layer does not exists
	 * @throws LayerException
	 *
	 */
	public ILayer remove(final String layerName) throws LayerException {
		for (int i = 0; i < size(); i++) {
			if (layerName.equals(layerCollection.get(i).getName())) {
				return remove(layerCollection.get(i));
			}
		}
		return null;
	}

	public ILayer[] getChildren() {
		if (null != layerCollection) {
			ILayer[] result = new ILayer[size()];
			return layerCollection.toArray(result);
		} else {
			return null;
		}
	}

	private int size() {
		return layerCollection.size();
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		for (ILayer layer : getChildren()) {
			if (layer.isVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @throws LayerException
	 * @see org.orbisgis.core.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) throws LayerException {
		for (ILayer layer : getChildren()) {
			layer.setVisible(isVisible);
		}
		fireVisibilityChanged();
	}

	/**
	 *
	 * @see org.orbisgis.core.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return (0 < size()) ? getLayer(0).getCoordinateReferenceSystem()
				: NullCRS.singleton;
	}

	/**
	 *
	 * @throws LayerException
	 * @see org.orbisgis.core.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public void setCoordinateReferenceSystem(
			final CoordinateReferenceSystem coordinateReferenceSystem)
			throws LayerException {
		for (ILayer layer : getChildren()) {
			layer.setCoordinateReferenceSystem(coordinateReferenceSystem);
		}
	}

	public static void processLayersLeaves(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			ILayer lc = (ILayer) root;
			ILayer[] layers = lc.getChildren();
			for (ILayer layer : layers) {
				processLayersLeaves(layer, action);
			}
		} else {
			action.action(root);
		}
	}

	public static void processLayersNodes(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			ILayer lc = (ILayer) root;
			ILayer[] layers = lc.getChildren();
			for (ILayer layer : layers) {
				processLayersNodes(layer, action);
			}
		}
		action.action(root);
	}

	private class GetEnvelopeLayerAction implements ILayerAction {
		private Envelope globalEnvelope;

		public void action(ILayer layer) {
			if (null == globalEnvelope) {
				globalEnvelope = new Envelope(layer.getEnvelope());
			} else {
				globalEnvelope.expandToInclude(layer.getEnvelope());
			}
		}

		public Envelope getGlobalEnvelope() {
			return globalEnvelope;
		}
	}

	public Envelope getEnvelope() {
		final GetEnvelopeLayerAction tmp = new GetEnvelopeLayerAction();
		processLayersLeaves(this, tmp);
		return tmp.getGlobalEnvelope();
	}

	private static class MyILayerAction implements ILayerAction {
		private int numberOfLeaves = 0;

		public void action(ILayer layer) {
			numberOfLeaves++;
		}

		public int getNumberOfLeaves() {
			return numberOfLeaves;
		}
	}

	public static int getNumberOfLeaves(final ILayer root) {
		MyILayerAction ila = new MyILayerAction();
		LayerCollection.processLayersLeaves(root, ila);
		return ila.getNumberOfLeaves();
	}

	public ILayer remove(ILayer layer) throws LayerException {
		return remove(layer, false);
	}

	public boolean acceptsChilds() {
		return true;
	}

	public void addLayerListenerRecursively(LayerListener listener) {
		this.addLayerListener(listener);
		for (ILayer layer : layerCollection) {
			layer.addLayerListenerRecursively(listener);
		}
	}

	public void removeLayerListenerRecursively(LayerListener listener) {
		this.removeLayerListener(listener);
		for (ILayer layer : layerCollection) {
			layer.removeLayerListenerRecursively(listener);
		}
	}

	public void close() throws LayerException {
		for (ILayer layer : layerCollection) {
			layer.close();
		}
	}

	public void open() throws LayerException {
		for (ILayer layer : layerCollection) {
			layer.open();
		}
	}

	public void addLayer(ILayer layer, boolean isMoving) throws LayerException {
		if (null != layer) {
			if (isMoving) {
				layerCollection.add(layer);
				layer.setParent(this);
			} else {
				if (0 < size()) {
					if (!layer.getCoordinateReferenceSystem().equals(
							getCoordinateReferenceSystem())) {
						throw new LayerException(
								"new layer don't share LayerCollection's CRS");
					}
				}
				setNamesRecursively(layer, getRoot().getAllLayersNames());
				layerCollection.add(layer);
				layer.setParent(this);
				fireLayerAddedEvent(new ILayer[] { layer });
			}
		}
	}

	public ILayer remove(ILayer layer, boolean isMoving) throws LayerException {
		if (layerCollection.contains(layer)) {
			if (isMoving) {
				if (layerCollection.remove(layer)) {
					return layer;
				} else {
					return null;
				}
			} else {
				ILayer[] toRemove = new ILayer[] { layer };
				if (fireLayerRemovingEvent(toRemove)) {
					if (layerCollection.remove(layer)) {
						fireLayerRemovedEvent(toRemove);
						return layer;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	public void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException {
		if (null != layer) {
			if (isMoving) {
				layerCollection.add(index, layer);
				layer.setParent(this);
			} else {
				if (0 < size()) {
					if (!layer.getCoordinateReferenceSystem().equals(
							getCoordinateReferenceSystem())) {
						throw new LayerException(
								"new layer don't share LayerCollection's CRS");
					}
				}
				setNamesRecursively(layer, getRoot().getAllLayersNames());
				layerCollection.add(index, layer);
				layer.setParent(this);
				fireLayerAddedEvent(new ILayer[] { layer });
			}
		}

	}

	public int getLayerCount() {
		return layerCollection.size();
	}

	public LayerType saveLayer() {
		LayerCollectionType xmlLayer = new LayerCollectionType();
		xmlLayer.setName(getName());
		for (ILayer child : layerCollection) {
			LayerType xmlChild = child.saveLayer();
			xmlLayer.getLayer().add(xmlChild);
		}

		return xmlLayer;
	}

	public void restoreLayer(LayerType layer) throws LayerException {
	}

	public ILayer getLayerByName(String layerName) {
		for (ILayer layer : layerCollection) {
			if (layer.getName().equals(layerName)) {
				return layer;
			} else {
				ILayer ret = layer.getLayerByName(layerName);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}

	public ILayer[] getRasterLayers() throws DriverException {
		ILayer[] allLayers = getLayersRecursively();

		ArrayList<ILayer> filterLayer = new ArrayList<ILayer>();

		for (int i = 0; i < allLayers.length; i++) {
			if (allLayers[i].isRaster()) {
				filterLayer.add(allLayers[i]);

			}
		}

		return filterLayer.toArray(new ILayer[0]);
	}

	public ILayer[] getVectorLayers() throws DriverException {
		ILayer[] allLayers = getLayersRecursively();

		ArrayList<ILayer> filterLayer = new ArrayList<ILayer>();

		for (int i = 0; i < allLayers.length; i++) {
			if (allLayers[i].isVectorial()) {
				filterLayer.add(allLayers[i]);

			}
		}

		return filterLayer.toArray(new ILayer[0]);
	}

	public boolean isRaster() {
		return false;
	}

	public boolean isVectorial() {
		return false;
	}

	public SpatialDataSourceDecorator getDataSource() {
		return null;
	}

	public RasterLegend[] getRasterLegend() throws DriverException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public RasterLegend[] getRasterLegend(String fieldName)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public Legend[] getVectorLegend() throws DriverException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public Legend[] getVectorLegend(String fieldName)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public void setLegend(String fieldName, Legend... legends)
			throws DriverException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public void setLegend(Legend... l) throws DriverException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public GeoRaster getRaster() throws DriverException {
		throw new UnsupportedOperationException("Cannot do this "
				+ "operation on a layer collection");
	}

	public int[] getSelection() {
		return new int[0];
	}

	public void setSelection(int[] newSelection) {
	}

	public Legend[] getRenderingLegend() throws DriverException {
		throw new UnsupportedOperationException(
				"Cannot draw a layer collection");
	}

	@Override
	public WMSConnection getWMSConnection()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot do this "
				+ "operation on a layer collection");
	}

	@Override
	public boolean isWMS() {
		return false;
	}
}