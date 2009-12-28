package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.views.jobs.JobPanel;
import org.orbisgis.pluginManager.background.BackgroundListener;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.Job;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;

public class JobViewPlugIn extends ViewPlugIn {

	private JobPanel panel;
	private JMenuItem menuItem;

	public JobViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new JobPanel();

		BackgroundManager bm = (BackgroundManager) Services
				.getService(BackgroundManager.class);

		bm.addBackgroundListener(new BackgroundListener() {

			public void jobAdded(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.addJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.addJob(job);
						}

					});
				}
			}

			public void jobRemoved(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.removeJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.removeJob(job);
						}

					});
				}
			}

			public void jobReplaced(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.replaceJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.replaceJob(job);
						}

					});
				}
			}
		});

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { "View" }, "Jobs", true,
				getIcon("hourglass.png"), null, panel, context.getWorkbenchContext());

	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}
	
	public void update(Observable o, Object arg) {
		setSelected();
	}
	
	public void setSelected(){
		menuItem.setSelected(isVisible());
	}	
	
	public boolean isVisible(){
		return getUpdateFactory().viewIsOpen(getId());
	}

}
