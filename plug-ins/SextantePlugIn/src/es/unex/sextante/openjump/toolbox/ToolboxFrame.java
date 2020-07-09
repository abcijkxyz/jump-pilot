package es.unex.sextante.openjump.toolbox;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;

import org.openjump.core.ui.swing.DetachableInternalFrame;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.toolbox.IToolboxDialog;
import es.unex.sextante.gui.toolbox.ToolboxPanel;

//@Deprecated
///**
// * Deprecated. The frame should be defined at Sextante-GUI.class level
//*/
//[Giuseppe Aruta 2020-07-07] Removed. Better to use this class otherwise we 
//need to modify classes on GvSigCe repository. Defined class as DetachableInternalFrame
public class ToolboxFrame extends DetachableInternalFrame implements IToolboxDialog {

	/**
	 * 
	 */
	// [Giuseppe Aruta 2017-12-11] modified class from
	// es.unex.sextante.gui.toolbox.ToolboxDialog
	// in order to open as an OpenJUMP internal frame
	private static final long serialVersionUID = -6608836827062468343L;
	private ToolboxPanel m_Panel;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent frame
	 */
	public ToolboxFrame(final Frame parent) {

		// super("SEXTANTE", true);
		setTitle("SEXTANTE");
		setResizable(true);
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);

		setSize(400, 600);
		setLayer(JLayeredPane.MODAL_LAYER);
		//
		// this.setResizable(false);

		initialize();
		// this.setLocationRelativeTo(null);
	}

	public void initialize() {

		// final URL res = getClass().getClassLoader().getResource(
		// "images/sextante_toolbox.gif");
		// if (res != null) {
		// } else {
		// }
		// [Giuseppe Aruta 2017-12-11] adopted internal Sextante
		final ImageIcon icon = new ImageIcon(getClass().getResource(
				"sextante_toolbox2.gif"));

		m_Panel = new ToolboxPanel(this, null, icon);
		setContentPane(m_Panel);

		m_Panel.fillTreesWithAllAlgorithms();

	}

	/**
	 * Returns the toolbox panel contained in this dialog
	 * 
	 * @return the toolbox panel contained in this dialog
	 */
	public ToolboxPanel getToolboxPanel() {

		return m_Panel;

	}

	@Override
	public void setAlgorithmsCount(final int iCount) {

		// setTitle(Sextante.getText("Processing") + " - "
		setTitle("SEXTANTE" + " " + Sextante.getVersionNumber()+//Sextante.getText("Toolbox") + 
				" - "
				+ Integer.toString(iCount) + Sextante.getText(" Tools") );

	}


	@Override
	public JDialog getDialog() {
		// TODO Auto-generated method stub
		return null;
	}

}
