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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.saig.jump.lang.I18N;

import com.vividsolutions.jump.workbench.model.Layer;

/**
 * <p>
 * </p>
 * 
 * @author
 * @since Kosmo 1.0
 */
public class SelectLayerDialog extends JDialog implements ActionListener {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;

    private JComboBox jcb = new JComboBox();
    private Layer selected = null;
    private JButton ok = new JButton(
            I18N.getString("org.openjump.core.ui.tools.SelectLayerDialog.Accept"));
    private JButton cancel = new JButton(
            I18N.getString("org.openjump.core.ui.tools.SelectLayerDialog.Cancel"));

    /**
     * @param parent
     * @param layers
     */
    public SelectLayerDialog(JFrame parent, Layer[] layers) {
        super(
                parent,
                I18N.getString("org.openjump.core.ui.tools.SelectLayerDialog.Choose-a-layer"),
                true);
        for (int i = 0; i < layers.length; i++) {
            jcb.addItem(layers[i]);
        }
        this.getContentPane().setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.add(new JLabel(
                I18N.getString("org.openjump.core.ui.tools.SelectLayerDialog.Choose-a-layer")
                        + ":"));
        p1.add(jcb);
        JPanel p2 = new JPanel();
        p2.add(ok);
        p2.add(cancel);
        this.getContentPane().add(p1, BorderLayout.CENTER);
        this.getContentPane().add(p2, BorderLayout.SOUTH);
        this.setIconImage(org.openjump.advancedtools.icon.IconLoader
                .image("cadTools.png"));
        pack();
        setLocationRelativeTo(parent);
        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    /**
	 * 
	 */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == ok) {
            selected = (Layer) jcb.getSelectedItem();
        }
        dispose();

    }

    /**
     * @return
     */
    public Layer getLayer() {
        return selected;
    }
}
