/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This file is based on an origional contained in the GISToolkit project:
 *    http://gistoolkit.sourceforge.net/
 */
package org.gdms.driver.dbf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.gdms.data.values.Value;

/**
 * A DbaseFileReader is used to read a dbase III format file. The general use of
 * this class is: <CODE><PRE>
 * DbaseFileHeader header = ...
 * WritableFileChannel out = new FileOutputStream(&quot;thefile.dbf&quot;).getChannel();
 * DbaseFileWriter w = new DbaseFileWriter(header,out);
 * while ( moreRecords ) {
 *   w.write( getMyRecord() );
 * }
 * w.close();
 * </PRE></CODE> You must supply the <CODE>moreRecords</CODE> and
 * <CODE>getMyRecord()</CODE> logic...
 *
 * @author Ian Schneider
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/data/shapefile/dbf/DbaseFileWriter.java $
 */
public class DbaseFileWriter {

	private DbaseFileHeader header;
	private DbaseFileWriter.FieldFormatter formatter;
	WritableByteChannel channel;
	private ByteBuffer buffer;
	private final Number NULL_NUMBER = new Integer(0);
	private final String NULL_STRING = "";
	private final Date NULL_DATE = new Date();
	private Charset charset;

	/**
	 * Create a DbaseFileWriter using the specified header and writing to the
	 * given channel.
	 *
	 * @param header
	 *            The DbaseFileHeader to write.
	 * @param out
	 *            The Channel to write to.
	 * @throws IOException
	 *             If errors occur while initializing.
	 */
	public DbaseFileWriter(DbaseFileHeader header, WritableByteChannel out)
			throws IOException {
		this(header, out, null);
	}

	/**
	 * Create a DbaseFileWriter using the specified header and writing to the
	 * given channel.
	 *
	 * @param header
	 *            The DbaseFileHeader to write.
	 * @param out
	 *            The Channel to write to.
	 * @param charset
	 *            The charset the dbf is (will be) encoded in
	 * @throws IOException
	 *             If errors occur while initializing.
	 */
	public DbaseFileWriter(DbaseFileHeader header, WritableByteChannel out,
			Charset charset) throws IOException {
		header.writeHeader(out);
		this.header = header;
		this.channel = out;
		this.charset = charset == null ? Charset.defaultCharset() : charset;
		this.formatter = new DbaseFileWriter.FieldFormatter(this.charset);
		init();
	}

	private void init() throws IOException {
		buffer = ByteBuffer.allocateDirect(header.getRecordLength());
	}

	private void write() throws IOException {
		buffer.position(0);
		int r = buffer.remaining();
		while ((r -= channel.write(buffer)) > 0) {
			; // do nothing
		}
	}

	/**
	 * Write a single dbase record.
	 *
	 * @param record
	 *            The entries to write.
	 * @throws IOException
	 *             If IO error occurs.
	 * @throws DbaseFileException
	 *             If the entry doesn't comply to the header.
	 */
	public void write(Object[] record) throws IOException, DbaseFileException {

		if (record.length != header.getNumFields()) {
			throw new DbaseFileException("Wrong number of fields "
					+ record.length + " expected " + header.getNumFields());
		}

		buffer.position(0);

		// put the 'not-deleted' marker
		buffer.put((byte) ' ');

		for (int i = 0; i < header.getNumFields(); i++) {
			String fieldString = fieldString(record[i], i);
			if (header.getFieldLength(i) != fieldString
					.getBytes(charset.name()).length) {
				// System.out.println(i + " : " + header.getFieldName(i)+" value
				// = "+fieldString+"");
				buffer.put(new byte[header.getFieldLength(i)]);
			} else {
				buffer.put(fieldString.getBytes(charset.name()));
			}

		}

		write();
	}

	private String fieldString(Object obj, final int col) {
		String o;
		final int fieldLen = header.getFieldLength(col);
		switch (header.getFieldType(col)) {
		case 'C':
		case 'c':
			o = formatter.getFieldString(fieldLen, obj == null ? NULL_STRING
					: obj.toString());
			break;
		case 'L':
		case 'l':
			o = (obj == null ? "F" : ((Value) obj).getAsBoolean() ? "T" : "F");
			break;
		case 'M':
		case 'G':
			o = formatter.getFieldString(fieldLen, obj == null ? NULL_STRING
					: obj.toString());
			break;
		case 'N':
		case 'n':
			// int?
			if (header.getFieldDecimalCount(col) == 0) {

				o = formatter.getFieldString(fieldLen, 0,
						(Number) (obj == null ? NULL_NUMBER : ((Value) obj)
								.getAsDouble()));
				break;
			}
		case 'F':
		case 'f':
			o = formatter.getFieldString(fieldLen, header
					.getFieldDecimalCount(col),
					(Number) (obj == null ? NULL_NUMBER : ((Value) obj)
							.getAsDouble()));
			break;
		case 'D':
		case 'd':
			o = formatter.getFieldString((Date) (obj == null ? null
					: ((Value) obj).getAsDate()));
			break;
		default:
			throw new RuntimeException("Unknown type "
					+ header.getFieldType(col));
		}

		return o;
	}

	/**
	 * Release resources associated with this writer. <B>Highly recommended</B>
	 *
	 * @throws IOException
	 *             If errors occur.
	 */
	public void close() throws IOException {
		// IANS - GEOT 193, bogus 0x00 written. According to dbf spec, optional
		// eof 0x1a marker is, well, optional. Since the original code wrote a
		// 0x00 (which is wrong anyway) lets just do away with this :)
		// - produced dbf works in OpenOffice and ArcExplorer java, so it must
		// be okay.
		// buffer.position(0);
		// buffer.put((byte) 0).position(0).limit(1);
		// write();
		if (channel.isOpen()) {
			channel.close();
		}

		buffer = null;
		channel = null;
		formatter = null;
	}

	/** Utility for formatting Dbase fields. */
	public static class FieldFormatter {
		private StringBuffer buffer = new StringBuffer(255);
		private NumberFormat numFormat = NumberFormat
				.getNumberInstance(Locale.US);
		private Calendar calendar = Calendar.getInstance(Locale.US);
		private String emptyString;
		private static final int MAXCHARS = 255;
		private Charset charset;

		public FieldFormatter(Charset charset) {
			// Avoid grouping on number format
			numFormat.setGroupingUsed(false);

			// build a 255 white spaces string
			StringBuffer sb = new StringBuffer(MAXCHARS);
			sb.setLength(MAXCHARS);
			for (int i = 0; i < MAXCHARS; i++) {
				sb.setCharAt(i, ' ');
			}

			this.charset = charset;

			emptyString = sb.toString();
		}

		public String getFieldString(int size, String s) {
			try {
				buffer.replace(0, size, emptyString);
				buffer.setLength(size);
				// international characters must be accounted for so size !=
				// length.
				int maxSize = size;
				if (s != null) {
					buffer.replace(0, size, s);
					int currentBytes = s.substring(0,
							Math.min(size, s.length()))
							.getBytes(charset.name()).length;
					if (currentBytes > size) {
						char[] c = new char[1];
						for (int index = size - 1; currentBytes > size; index--) {
							if (buffer.length() > index) {
								c[0] = buffer.charAt(index);
								String string = new String(c);
								buffer.deleteCharAt(index);
								currentBytes -= string.getBytes().length;
								maxSize--;
							}
						}
					} else {
						if (s.length() < size) {
							maxSize = size - (currentBytes - s.length());
							for (int i = s.length(); i < size; i++) {
								buffer.append(' ');
							}
						}
					}
				}

				buffer.setLength(maxSize);

				return buffer.toString();
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("This error should never occurr", e);
			}
		}

		public String getFieldString(Date d) {

			if (d != null) {
				buffer.delete(0, buffer.length());

				calendar.setTime(d);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1; // returns 0
				// based month?
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				if (year < 1000) {
					if (year >= 100) {
						buffer.append("0");
					} else if (year >= 10) {
						buffer.append("00");
					} else {
						buffer.append("000");
					}
				}
				buffer.append(year);

				if (month < 10) {
					buffer.append("0");
				}
				buffer.append(month);

				if (day < 10) {
					buffer.append("0");
				}
				buffer.append(day);
			} else {
				buffer.setLength(8);
				buffer.replace(0, 8, emptyString);
			}

			buffer.setLength(8);
			return buffer.toString();
		}

		public String getFieldString(int size, int decimalPlaces, Number n) {
			buffer.delete(0, buffer.length());

			if (n != null) {
				numFormat.setMaximumFractionDigits(decimalPlaces);
				numFormat.setMinimumFractionDigits(decimalPlaces);
				numFormat.format(n, buffer, new FieldPosition(
						NumberFormat.INTEGER_FIELD));
			}

			int diff = size - buffer.length();
			if (diff >= 0) {
				while (diff-- > 0) {
					buffer.insert(0, ' ');
				}
			} else {
				buffer.setLength(size);
			}
			return buffer.toString();
		}
	}

}
