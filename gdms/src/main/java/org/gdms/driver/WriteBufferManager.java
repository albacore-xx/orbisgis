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
package org.gdms.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * Class to write files using nio
 *
 * @author Fernando Gonzalez Cortes
 */
public class WriteBufferManager {

	private static final int BUFFER_SIZE = 1024 * 128;

	private FileChannel channel;

	private ByteBuffer buffer;

	/**
	 * Creates a new WriteBufferManager that writes to the specified file
	 * channel
	 *
	 * @param channel
	 * @throws IOException
	 */
	public WriteBufferManager(FileChannel channel) throws IOException {
		this.channel = channel;
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
	}

	/**
	 * Puts the specified byte at the current position
	 *
	 * @param b
	 * @throws IOException
	 */
	public void put(byte b) throws IOException {
		prepareToAddBytes(1);
		buffer.put(b);
	}

	/**
	 * Moves the window
	 *
	 * @param numBytes
	 * @throws IOException
	 */
	private void prepareToAddBytes(int numBytes) throws IOException {
		if (buffer.remaining() < numBytes) {
			buffer.flip();
			channel.write(buffer);

			int bufferCapacity = Math.max(BUFFER_SIZE, numBytes);
			if (bufferCapacity != buffer.capacity()) {
				ByteOrder order = buffer.order();
				buffer = ByteBuffer.allocate(bufferCapacity);
				buffer.order(order);
			} else {
				buffer.clear();
			}
		}
	}

	/**
	 * Puts the specified bytes at the current position
	 *
	 * @param bs
	 * @throws IOException
	 */
	public void put(byte[] bs) throws IOException {
		prepareToAddBytes(bs.length);
		buffer.put(bs);
	}

	/**
	 * flushes the cached contents into the channel. It is mandatory to call
	 * this method to finish the writing of the channel
	 *
	 * @throws IOException
	 */
	public void flush() throws IOException {
		buffer.flip();
		channel.write(buffer);
	}

	/**
	 * Specifies the byte order. One of the constants in {@link ByteBuffer}
	 *
	 * @param order
	 */
	public void order(ByteOrder order) {
		this.buffer.order(order);
	}

	/**
	 * Puts the specified int at the current position
	 *
	 * @param value
	 * @throws IOException
	 */
	public void putInt(int value) throws IOException {
		prepareToAddBytes(4);
		buffer.putInt(value);
	}

	/**
	 * Puts the specified double at the current position
	 *
	 * @param d
	 * @throws IOException
	 */
	public void putDouble(double d) throws IOException {
		prepareToAddBytes(8);
		buffer.putDouble(d);
	}

}
