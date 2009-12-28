package org.orbisgis.plugins.core.ui.workbench;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.MapControl;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.images.IconLoader;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugIn;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;

public class WorkbenchToolBar extends EnableableToolBar implements Observer{

	private WorkbenchContext context;
	private HashMap<String,WorkbenchToolBar> toolbars = new HashMap<String,WorkbenchToolBar>();
	

	public WorkbenchToolBar(WorkbenchContext workbenchContext) {
		this.context = workbenchContext;
	}

	public HashMap<String,WorkbenchToolBar> getToolbars() {
		return toolbars;
	}

	public boolean haveAnOtherToolBar() {
		return toolbars.size()>0 ? true : false;
	}

	public WorkbenchToolBar(WorkbenchContext workbenchContext, String name) {
		super(name);
		this.context = workbenchContext;	
	}

	public void addPlugIn(final PlugIn plugIn, Component c) {
		context.addObserver((Observer) plugIn);
		add(c, plugIn);
	}

	protected void addImpl(Component comp, final Object constraints, int index) {
		if (constraints instanceof Automaton) {

		} else {
			if (comp instanceof JComboBox) {
				//System.out.println("add JComboBox " + constraints.getClass());
				((JComboBox) comp).addItemListener(AbstractPlugIn
						.toItemListener((PlugIn) constraints, context));
			} else if (comp instanceof JToolBar) {
				//System.out.println("add JToolBar : " + comp.getName());
				toolbars.put(comp.getName(), (WorkbenchToolBar)comp);
				context.addObserver((WorkbenchToolBar)comp);
			}
			else if (comp instanceof JPanel) {				
				 //System.out.println("add JPanel " + comp.getName()); 
				 Component actionComponent = (Component)((AbstractPlugIn) constraints).getActionComponent(); 
				 String typeListener = (String)((AbstractPlugIn) constraints).getTypeListener(); 
				 //((JComponent) actionComponent).addActionListener(AbstractPlugIn.toActionListener((PlugIn) constraints, context));
				 if(actionComponent!=null) {
					 if(typeListener.equals("item"))
					 //((JPanel) comp).addMouseListener(AbstractPlugIn.toMouseListener((PlugIn) constraints, context));
						 ((JComboBox) actionComponent).addItemListener(AbstractPlugIn.toItemListener((AbstractPlugIn) constraints, context));
					 else if(typeListener.equals("action"))
						 ((JButton) actionComponent).addActionListener(AbstractPlugIn.toActionListener((AbstractPlugIn) constraints, context));
					 
				 }
			} else {				
				//System.out.println("add JButton : " + constraints.getClass());
				((JButton) comp).addActionListener(AbstractPlugIn
						.toActionListener((PlugIn) constraints, context));				
			}
		}
		super.addImpl(comp, constraints, index);
	}

	//TOOLBAR Automaton
	public JToggleButton addAutomaton(final Automaton automaton, String icon) {
		JToggleButton jtoggle = new JToggleButton() {
			public String getToolTipText(MouseEvent event) {				
				return automaton.getName();
			}
		};
		jtoggle.setIcon(IconLoader.getIcon(icon));
		return addCursorAutomaton(automaton.getName(), automaton, jtoggle);
	}

	private JToggleButton addCursorAutomaton(String tooltip,
			final Automaton automaton, JToggleButton button) {
		add(button, tooltip, new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				EditorManager em = (EditorManager) Services
						.getService(EditorManager.class);
				IEditor editor = em.getActiveEditor();
				MapEditorPlugIn mapEditor = (MapEditorPlugIn) editor;
				if (mapEditor.getComponent() != null) {
					try {					
						automaton.setMouseCursor(automaton.getMouseCursor());
						((MapControl) mapEditor.getComponent()).setTool(automaton);
						WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
						wbContext.setLastAction("Set Tool");
					} catch (TransitionException e1) {
						Services.getErrorManager().error("cannot add Automaton", e1);
					}
				}
			}
		}, automaton);
		context.addObserver((Observer) automaton);
		automaton.setButton(button);
		return button;
	}

	@Override
	public void update(Observable o, Object arg) {
		//System.out.println("update toolbar " + this.getName());		
		for(int i=0 ; i<getComponentCount(); i++) {
			if(getComponent(i).isEnabled() 
					&& getComponent(i).isVisible()) {				
				this.setVisible(true);
				break;
			}				
			this.setVisible(false);
		}		
	}
}
