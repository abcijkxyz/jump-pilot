package com.cadplan.jump.plugins;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.openide.awt.DropDownButtonFactory;

import com.cadplan.jump.icon.IconLoader;
import com.cadplan.jump.utils.LoadSymbolFiles;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.MenuNames;
import com.vividsolutions.jump.workbench.ui.task.TaskMonitorManager;



public class StylerMenuPlugIn extends AbstractPlugIn {


	private PlugInContext context;
	private JToggleButton toolbarButton = null;
	public static JMenuItem mi;

	private TaskMonitorManager taskMonitorManager;
	final VertexSymbolsPlugIn vertexSymbolsPlugIn = new VertexSymbolsPlugIn();
	final VertexNotePlugin vertexnotePlugIn = new VertexNotePlugin();
	ImageIcon ICON1 = IconLoader.icon("vsicon.gif");
	ImageIcon ICON2 = IconLoader.icon("noteicon.gif");

	@Override
	public void initialize(final PlugInContext context) throws Exception {
		this.context = context;

		toolbarButton = DropDownButtonFactory.createDropDownToggleButton(
				GUIUtil.toSmallIcon(ICON1, 20), 
				initPopupLazily());
		toolbarButton.setToolTipText(vertexSymbolsPlugIn.getName());

		////
		final ActionListener listener = AbstractPlugIn.toActionListener(vertexSymbolsPlugIn,
				context.getWorkbenchContext(), taskMonitorManager);
		toolbarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(e);
			}
		});
		////	

		LoadSymbolFiles loadSymbols = new LoadSymbolFiles(context);
		loadSymbols.start();
		VertexParams.context = context.getWorkbenchContext();


		context.getWorkbenchContext().getWorkbench().getFrame().getToolBar().add(toolbarButton);


		EnableCheckFactory enableCheckFactory = new EnableCheckFactory(context.getWorkbenchContext());
		MultiEnableCheck multiEnableCheck = new MultiEnableCheck();
		multiEnableCheck.add(enableCheckFactory.createAtLeastNLayersMustExistCheck(1));
		multiEnableCheck.add(enableCheckFactory.createAtLeastNLayersMustBeSelectedCheck(1));
		String str = MenuNames.PLUGINS;
		context.getFeatureInstaller().addMainMenuPlugin(vertexSymbolsPlugIn, new String[]{str, MenuNames.STYLE}, 
				vertexSymbolsPlugIn.getName(), 
				false, ICON1, multiEnableCheck);

		EnableCheckFactory check = new EnableCheckFactory(context.getWorkbenchContext());
		EnableCheck scheck = check.createAtLeastNFeaturesMustBeSelectedCheck(1);
		MultiEnableCheck mcheck = new MultiEnableCheck();
		mcheck.add(check.createAtLeastNLayersMustExistCheck(1));
		mcheck.add(check.createAtLeastNLayersMustBeEditableCheck(1));

		context.getFeatureInstaller().addMainMenuPlugin(vertexnotePlugIn, new String[]{str, MenuNames.STYLE}, 
				vertexnotePlugIn.getName(), false, IconLoader.icon("noteicon.gif"), mcheck);
		context.getFeatureInstaller().addPopupMenuPlugin(LayerViewPanel.popupMenu(), vertexnotePlugIn, 
				vertexnotePlugIn.getName(), false,ICON2, scheck);


	}
	final JPopupMenu popup = new JPopupMenu();
	private JPopupMenu initPopupLazily() {

		popup.setLayout(new GridLayout(0, 1));

		/*	mi = new JMenuItem(vertexSymbolsPlugIn.getName(), 
				GUIUtil.toSmallIcon(ICON1, 20));
		mi.setToolTipText(vertexSymbolsPlugIn.getName());
		final ActionListener listener = AbstractPlugIn.toActionListener(vertexSymbolsPlugIn,
				context.getWorkbenchContext(), taskMonitorManager);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(e);
			}
		});
		popup.add(mi);*/


		mi = new JMenuItem(vertexnotePlugIn.getName(), 
				GUIUtil.toSmallIcon(ICON2, 20));
		mi.setToolTipText(vertexnotePlugIn.getName());
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