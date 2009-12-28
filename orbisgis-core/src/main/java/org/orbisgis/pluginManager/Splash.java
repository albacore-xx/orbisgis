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
package org.orbisgis.pluginManager;

// Upadates: 2004.04.02, 2004.01.09

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;

/**
 * A splash screen to show while the main program is loading. A typical use is:
 *
 * <pre>
 * public static void main(String[] args) {
 * 	Splash s = new Splash(delay1);
 * 	new MainProgram();
 * 	s.dispose(delay2);
 * }
 * </pre>
 *
 * The first line creates a Splash that will appear until another frame hides it
 * (MainProgram), but at least during "delay1" milliseconds.<br>
 * To distroy the Splash you can either call "s.dispose()" or
 * "s.dispose(delay2)", that will actually show the Splash for "delay2"
 * milliseconds and only then hide it.<br>
 * The picture to show must be in a file called "splash.png".
 */
public class Splash extends JFrame {

	private JLabel versionLabel;

	/**
	 * Creates a Splash that will appear until another frame hides it, but at
	 * least during "delay" milliseconds.
	 *
	 * @param delay
	 *            the delay in milliseconds
	 */
	public Splash() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		Image image = new ImageIcon(getClass().getResource("logo_orbisgis.png"))
				.getImage();
		p.add(new JLabel(new ImageIcon(image)), BorderLayout.CENTER);
		versionLabel = new JLabel("Starting...");
		p.add(versionLabel, BorderLayout.SOUTH);
		p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		getContentPane().add(p);
		setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
	}

	public void updateVersion() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					updateVersion();
				}

			});
		}

		ApplicationInfo ai = Services.getService(ApplicationInfo.class);
		String text = ai.getName() + " " + ai.getVersionNumber() + "("
				+ ai.getVersionName() + ")";
		String organization = ai.getOrganization();
		if (organization != null) {
			text += " - " + organization;
		}
		versionLabel.setText(text);

	}


	public void updateText(String text) {

		versionLabel.setText(text);

	}
}