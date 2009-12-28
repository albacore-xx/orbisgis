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
package org.orbisgis.plugins.core.ui.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.orbisgis.core.Services;

import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugIn;
import org.orbisgis.plugins.core.ui.menu.IMenu;
import org.orbisgis.plugins.core.ui.menu.MenuSeparator;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class Menu implements IMenu {

	private static final String DEFAULT_MENU_GROUP = "_default";
	private String parent;
	private String id;
	private String text;
	private String group;
	private ImageIcon icon;
	private PlugIn plugin;

	private ArrayList<IMenu> children = new ArrayList<IMenu>();	
	private HashMap<String, ArrayList<IMenu>> groups = new HashMap<String, ArrayList<IMenu>>();
	
	
	public Menu(String parent, String id, String group, String text, 
			ImageIcon icon, PlugIn plugin) {
		super();
		this.parent = parent;
		this.id = id;
		this.text = text;		
		this.group = group;
		this.icon = icon;
		this.plugin = plugin;		
	}
	
	public JComponent getJMenuItem() {
		JMenuItem ret ;			
		if (children.size() > 0) {	
			ret = new JMenu();
			for (int i = 0; i < children.size(); i++) {
				ret.add(children.get(i).getJMenuItem());
			}
		}
		else {
			ret = new JMenuItem();
			if(icon!=null)
				ret.setIcon(icon);
			if(plugin!=null) {
				WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
				ret.addActionListener(AbstractPlugIn.toActionListener(plugin,wbContext));				
			}
		}
		ret.setName(id);
		ret.setText(text);
		if(plugin!=null)
			ret.setVisible(plugin.isVisible());		
		return ret;
	}
	
	public void addChild(IMenu menu) {
		boolean exists = false;
		
		Iterator<String> it = groups.keySet().iterator();		
		while (it.hasNext()) {
			String group = it.next();
			ArrayList<IMenu> menusInGroup = groups.get(group);
			for (IMenu menuInGroup : menusInGroup) {
				for(int i=0 ; i<menuInGroup.getChildren().length && !exists ; i++) {
					if(((IMenu)menuInGroup.getChildren()[i]).getId() == menu.getId()) {
						exists = true;
					}				
				}
			}
		}
		
		for(int i=0 ; i< children.size(); i++) {
			if(children.get(i).getId() ==  menu.getId())
				exists = true;
		}
		
		if(!exists) {
			children.add(menu);	
			String menuGroup = menu.getGroup();
			if (menuGroup == null) {
				menuGroup = DEFAULT_MENU_GROUP;
			}
			ArrayList<IMenu> menusInGroup = groups.get(menuGroup);
			if (menusInGroup == null) {
				menusInGroup = new ArrayList<IMenu>();
			}
			menusInGroup.add(menu);
			groups.put(menuGroup, menusInGroup);
		}
		
	}
	
	
	public boolean jmenuHasElments(ArrayList<IMenu> subMenus) {		
		boolean hasElement = false;
		int i = 0;
		while (subMenus.size() > i ) {			
			if(subMenus.get(i).getJMenuItem() instanceof JMenu) {
				ArrayList<IMenu> newSubMenus = new ArrayList<IMenu>();
				for(int j=0; j<subMenus.get(i).getChildren().length;j++)
					newSubMenus.add(subMenus.get(i).getChildren()[j]);
				hasElement = jmenuHasElments(newSubMenus);				
			}
			else {	
				if(!(subMenus.get(i) instanceof MenuSeparator))
					hasElement = hasElement || subMenus.get(i).getPlugin().isVisible();	
				//subMenus.get(i).getComponent().setVisible(subMenus.get(i).getPlugin().isVisible());
				
			}
			i++;				
		}
		return hasElement;
	}
	
	void removeLastSeparatorIfNecessary(ArrayList<IMenu> components) {	
		if(components.size() >= 1) {
			IMenu lastComponent = components.get(components.size()-1);			
			if(lastComponent instanceof MenuSeparator)
				components.remove(components.size()-1);
		}
	}
	
	public void groupMenus() {
		ArrayList<IMenu> newChilds = new ArrayList<IMenu>();
		Iterator<String> it = groups.keySet().iterator();	
		boolean separator = false;		
		while (it.hasNext()) {
			String group = it.next();
			ArrayList<IMenu> menusInGroup = groups.get(group);
			if(jmenuHasElments(menusInGroup)) {					
				for (IMenu menu : menusInGroup) {
					newChilds.add(menu);
					menu.groupMenus();
				}
				if(it.hasNext())
					newChilds.add(new MenuSeparator());
			}			
		}
		removeLastSeparatorIfNecessary(newChilds);
		children = newChilds;
	}	

	public IMenu[] getChildren() {
		return children.toArray(new IMenu[0]);
	}

	public void remove(IMenu menuToDelete) {
		children.remove(menuToDelete);
		String menuGroup = menuToDelete.getGroup();
		if (menuGroup == null) {
			menuGroup = DEFAULT_MENU_GROUP;
		}
		groups.get(menuGroup).remove(menuToDelete);
		
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getGroup() {
		return group;
	}
	
	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	
	public void setGroup(String group) {
		this.group = group;
	}

	public PlugIn getPlugin() {		
		return plugin;
	}
	
	public String toString() {
		return getText();
	}
}
