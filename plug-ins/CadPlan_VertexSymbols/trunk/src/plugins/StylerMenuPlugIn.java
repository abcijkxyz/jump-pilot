package com.cadplan.jump.plugins;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.openide.awt.DropDownButtonFactory;

import com.cadplan.jump.icon.IconLoader;
import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.utils.LoadSymbolFiles;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.task.TaskMonitorManager;



public class StylerMenuPlugIn extends AbstractPlugIn {


	private PlugInContext context;

	// the JToggleButton in the WorkbenchToolBar
	private JToggleButton toolbarButton = null;

	public static JMenuItem mi;
	public static final String COLOR = "COLOR";
	public static final String R_G_B = BasicStyle.RGB_ATTRIBUTE_NAME;
	private TaskMonitorManager taskMonitorManager;
	@Override
	public void initialize(final PlugInContext context) throws Exception {
		this.context = context;

		toolbarButton = DropDownButtonFactory.createDropDownToggleButton(
				GUIUtil.toSmallIcon(IconLoader.icon("Palette.png"), 20), 
				initPopupLazily());

		// init popup takes a long time, defer it after workbench is shown
		/*	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				colorPickerPopup = initPopupLazily();
			}
		});*/

		LoadSymbolFiles loadSymbols = new LoadSymbolFiles(context);
		loadSymbols.start();
		VertexParams.context = context.getWorkbenchContext();


		context.getWorkbenchContext().getWorkbench().getFrame().getToolBar().add(toolbarButton);

	}
	final JPopupMenu popup = new JPopupMenu();
	private JPopupMenu initPopupLazily() {

		popup.setLayout(new GridLayout(0, 1));

		mi = new JMenuItem(I18NPlug.getI18N("VertexSymbols.MenuItem"), 
				GUIUtil.toSmallIcon(IconLoader.icon("vsicon.gif"), 20));
		final VertexSymbolsPlugIn vertexSymbolsPlugIn = new VertexSymbolsPlugIn();
		mi.setToolTipText(I18NPlug.getI18N("VertexSymbols.MenuItem"));
		final ActionListener listener = AbstractPlugIn.toActionListener(vertexSymbolsPlugIn,
				context.getWorkbenchContext(), taskMonitorManager);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(e);
			}
		});
		popup.add(mi);


		mi = new JMenuItem(I18NPlug.getI18N("VertexNote.MenuItem"), 
				GUIUtil.toSmallIcon(IconLoader.icon("noteicon.gif"), 20));
		final VertexNotePlugin vertexnotePlugIn = new VertexNotePlugin();
		mi.setToolTipText(I18NPlug.getI18N("VertexNote.MenuItem"));
		final ActionListener listener2 = AbstractPlugIn.toActionListener(vertexnotePlugIn,
				context.getWorkbenchContext(), taskMonitorManager);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener2.actionPerformed(e);
			}
		});
		popup.add(mi);
		// the Button for the ToolBar

		return popup;
	}



	@Override
	public boolean execute(PlugInContext context) throws Exception {
		return true;
	}

	public static EnableCheck createEnableCheck(
			WorkbenchContext workbenchContext, boolean b) {
		final EnableCheckFactory checkFactory = new EnableCheckFactory(
				workbenchContext);

		return new MultiEnableCheck().add(
				checkFactory.createWindowWithLayerViewPanelMustBeActiveCheck())
				.add(checkFactory.createAtLeastNLayersMustBeEditableCheck(1));
	}




}