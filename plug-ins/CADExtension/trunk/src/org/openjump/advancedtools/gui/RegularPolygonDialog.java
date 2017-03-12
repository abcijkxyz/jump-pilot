/* 
 * Kosmo - Sistema Abierto de Informaci�n Geogr�fica
 * Kosmo - Open Geographical Information System
 *
 * http://www.saig.es
 * (C) 2006, SAIG S.L.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, contact:
 * 
 * Sistemas Abiertos de Informaci�n Geogr�fica, S.L.
 * Avnda. Rep�blica Argentina, 28
 * Edificio Domocenter Planta 2� Oficina 7
 * C.P.: 41930 - Bormujos (Sevilla)
 * Espa�a / Spain
 *
 * Tel�fono / Phone Number
 * +34 954 788876
 * 
 * Correo electr�nico / Email
 * info@saig.es
 *
 */
package org.openjump.advancedtools.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.openjump.advancedtools.icon.IconLoader;
import org.openjump.advancedtools.language.I18NPlug;
import org.openjump.advancedtools.utils.WorkbenchUtils;

/**
 * 
 * 
 * <p>
 * T
 * </p>
 * 
 * @author
 * @since Kosmo 1.0.0
 */
public class RegularPolygonDialog extends JDialog implements ActionListener {
    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;
    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    JPanel pCenterFilled = new JPanel();
    JPanel pButtons = new JPanel();
    public static ImageIcon ICON = org.openjump.advancedtools.icon.IconLoader
            .icon("cad.png");
    /** Plugin name */
    public final static String NAME = I18NPlug
            .getI18N("org.openjump.core.ui.plugins.Regularpolygon");

    JButton jb1 = new JButton(
            I18NPlug.getI18N("org.openjump.core.ui.plugins.Dialog.Accept"));
    JButton jb2 = new JButton(
            I18NPlug.getI18N("org.openjump.core.ui.plugins.Dialog.Cancel"));

    JPanel jOptionPanel = new JPanel();
    public static JFormattedTextField jsRadius = WorkbenchUtils
            .getUSFormatedNumberTextField(100);
    JLabel jlRotation = new JLabel();
    JLabel jlSides = new JLabel(
            I18NPlug.getI18N("org.openjump.core.ui.plugins.Regularpolygon.Number-of-sides")
                    + ":");
    JLabel jlRadius = new JLabel(
            I18NPlug.getI18N("org.openjump.core.ui.plugins.Circle.Radius") + ":");
    JRadioButton jrbindicarRaton = new JRadioButton(
            I18NPlug.getI18N("org.openjump.core.ui.plugins.Circle.Draw-with-the-mouse"));

    JRadioButton jrbindicarRadio = new JRadioButton(
            I18NPlug.getI18N("org.openjump.core.ui.plugins.Circle.Point-out-radius"));

    ButtonGroup group1 = new ButtonGroup();
    public static SpinnerModel sidesModel = new SpinnerNumberModel(3, // initial
            // value
            3, // min
            400, // max
            1);
    public static JSpinner sidesSpinner = new JSpinner();

    public static SpinnerModel rotationModel = new SpinnerNumberModel(0, // initial
            // value
            0, // min
            359, // max
            1);

    public static JSpinner rotationSpinner = new JSpinner();

    public RegularPolygonDialog(JFrame parent) {
        super(parent, RegularPolygonDialog.NAME, true);

        pCenterFilled.setLayout(new FlowLayout(FlowLayout.LEFT));
        pButtons.add(jb1);
        pButtons.add(jb2);

        group1.add(jrbindicarRaton);
        group1.add(jrbindicarRadio);

        jb1.addActionListener(this);
        jb2.addActionListener(this);

        jOptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sidesSpinner = new JSpinner(sidesModel);
        rotationSpinner = new JSpinner(rotationModel);
        rotationSpinner.setEditor(new JSpinner.NumberEditor(rotationSpinner,
                "0000"));

        jlRotation = new JLabel("rotation(�)");
        jlRotation.setToolTipText("define a clockwise rotation in degree");
        jlRotation.setIcon(IconLoader.icon("rotate2.png"));

        jOptionPanel.add(jlRadius);
        jOptionPanel.add(jsRadius);
        jOptionPanel.add(jlSides);
        jOptionPanel.add(sidesSpinner);
        // jOptionPanel.add(jlRotation);
        // jOptionPanel.add(rotationSpinner);

        this.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        this.getContentPane().add(jOptionPanel, c);

        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        this.getContentPane().add(pCenterFilled, c);

        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        this.getContentPane().add(pButtons, c);

        this.setIconImage(org.openjump.advancedtools.icon.IconLoader
                .image("cadTools.png"));
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setName(NAME);

    }

    public boolean raton = false;
    public boolean radio = false;
    public boolean absoluto = false;
    public static int sides;
    public static double radius;
    public double r1;
    public double r2;
    public boolean cancelado = true;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb1) {
            performAccept();
        } else if (e.getSource() == jb2) {
            dispose();
        }

    }

    private void performAccept() {
        cancelado = false;

        absoluto = true;
        sides = ((Number) sidesSpinner.getValue()).intValue();
        radius = ((Number) jsRadius.getValue()).doubleValue();

        dispose();
    }

}
