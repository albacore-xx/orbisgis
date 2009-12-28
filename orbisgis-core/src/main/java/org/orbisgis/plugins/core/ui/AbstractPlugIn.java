package org.orbisgis.plugins.core.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.gdms.source.SourceManager;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.images.IconLoader;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public abstract class AbstractPlugIn implements PlugIn{

	private String name;	
	private PlugInContext plugIncontext;
	private UpdatePlugInFactory updatePlugInFactory;
	//Action component (Button) to run plugIn (In case that plugIn made up of several swing component)
	private JComponent actionComponent;	
	private String typeListener;	

	//Constructors
	public AbstractPlugIn() {		
    }	

	public AbstractPlugIn(String name) {
		this.name = name;
	}	
	
	//Default PlugIn methods
	public boolean execute(PlugInContext context) throws Exception {return false;}	
	public void initialize(PlugInContext context) throws Exception {}
	public void update(Observable arg0, Object arg1) {}	
	//implemented by PlugIns		
	//Factory for adapt PlugIn context (Visibility, Enabled)
	public void createUpdatePlugInFactory(WorkbenchContext workbenchContext) {	
		setPlugInContext(workbenchContext.createPlugInContext());
		updatePlugInFactory = new UpdatePlugInFactory(workbenchContext,this);			
	}
	public void createUpdatePlugInFactory(JComponent component,  String id, 
			ImageIcon icon, String[] editors, WorkbenchContext workbenchContext){		
	}	
	public void createUpdatePlugInFactory(Container container,  String id, 
			ImageIcon icon, String [] editors, WorkbenchContext workbenchContext){		
	}
	//Specific implemented methods by some PlugIn (use in UdpateFactory)    
	//test in TOC for swing visibility
	public void execute(MapContext mapContext, ILayer layer){}
	public boolean acceptsSelectionCount(int layerCount){return false;}
	public boolean accepts(MapContext mc, ILayer layer){return false;}
	public boolean acceptsAll(ILayer[] layers){return false;}
	//idem for Geocognition 
	public void execute(Geocognition geocognition, GeocognitionElement element){}
	public boolean accepts(Geocognition geocog, GeocognitionElement element){return false;}
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount){return false;}
	//idem for Geocatalog
	public void execute(SourceManager srcManager, String srcName){}
	public boolean accepts(SourceManager srcManager, String srcName){return false;}
	
	public boolean isVisible() {return false;}
	public boolean isEnabled() {return false;}	

	//listeners
	public static ActionListener toActionListener(final PlugIn plugIn,
		final WorkbenchContext workbenchContext) {		
		return new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				System.out.println("JButton : actionPerformed ");
				try {
					PlugInContext plugInContext = 
						workbenchContext.createPlugInContext();
					boolean executeComplete = plugIn.execute(plugInContext);
					if(executeComplete)
						workbenchContext.setLastAction("update");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}		
		};
	}
	
	public static ItemListener toItemListener(final PlugIn plugIn,
			final WorkbenchContext workbenchContext) {		
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				System.out.println("JComboBox : itemStateChanged ");
				try {
					PlugInContext plugInContext = 
						workbenchContext.createPlugInContext();
					boolean executeComplete = plugIn.execute(plugInContext);
					if(executeComplete)
						workbenchContext.setLastAction("update");
					//context.notifyObservers();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			
		};
	}
	//getters & setters
    public String getName() {
        return name == null ? createName(getClass()) : name;
    }    

    public static String createName(Class plugInClass) {
    	return plugInClass.getName();
    }
	
	protected PlugInContext getPlugIncontext() {
		return plugIncontext;
	}

	protected void setPlugInContext(PlugInContext context) {
		this.plugIncontext = context;		
	}
	
	public Component getActionComponent() {
		return actionComponent;
	}

	public void setActionComponent(JComponent actionComponent) {
		this.actionComponent = actionComponent;
	}	

	public String getTypeListener() {
		return typeListener;
	}

	public void setTypeListener(String typeListener) {
		this.typeListener = typeListener;
	}
	
	public UpdatePlugInFactory getUpdateFactory() {
		return updatePlugInFactory;
	}
	
	public static ImageIcon getIcon(String nameIcone) {		 
		return IconLoader.getIcon(nameIcone);
	}
}
