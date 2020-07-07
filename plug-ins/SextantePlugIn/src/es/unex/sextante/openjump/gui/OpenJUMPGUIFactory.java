package es.unex.sextante.openjump.gui;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.ui.WorkbenchFrame;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.gui.core.DefaultGUIFactory;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.openjump.toolbox.ToolboxFrame;

public class OpenJUMPGUIFactory
extends
DefaultGUIFactory {

	@Override
	public void showBatchProcessingDialog(final GeoAlgorithm alg,
			final JDialog parent) {

		JOptionPane.showMessageDialog(parent, "Batch processing not yet implemented");

	}




	@Override
	public void showToolBoxDialog() {
		WorkbenchFrame wFrame = JUMPWorkbench.getInstance().getFrame();
		SextanteGUI.getInputFactory().createDataObjects();

		for (JInternalFrame iFrame : wFrame
				.getInternalFrames()) {
			if (iFrame instanceof ToolboxFrame) {

				iFrame.toFront();
				return;

			}
		}
		ToolboxFrame additionalResultsFrame = new ToolboxFrame(wFrame);

		wFrame
		.addInternalFrame(additionalResultsFrame);
	} 



	@Override
	public void updateToolbox() {
		super.updateToolbox();
	}


}
