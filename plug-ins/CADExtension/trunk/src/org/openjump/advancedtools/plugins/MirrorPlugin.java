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
package org.openjump.advancedtools.plugins;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.openjump.advancedtools.icon.IconLoader;
import org.openjump.advancedtools.language.I18NPlug;
import org.openjump.advancedtools.tools.MirrorToDrawSegmentTool;
import org.openjump.advancedtools.tools.MirrorToSelectedSegmentTool;
import org.openjump.advancedtools.utils.WorkbenchUtils;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.TaskFrame;
import com.vividsolutions.jump.workbench.ui.cursortool.SelectFeaturesTool;

/**
 * Tools that allows to generate an axial symmetry of selected geoemtries.
 * Original code from Kosmo 3.0 SAIG - http://www.opengis.es/
 * 
 * <p>
 * Geometries can be mirrored to the clicked segment or to the drawn line
 * </p>
 * 
 * @author Gabriel Bellido Perez
 * @since Kosmo SAIG 1.0 (2006)
 * @author Giuseppe Aruta [Genuary 30th 2017] rewrite code to adapt to OpenJUMP
 *         1.10 (http://www.openjump.org/support.html)
 * @since OpenJUMP 1.10 (2017)
 */

public class MirrorPlugin extends AbstractPlugIn {

    /** Name of the plugin */
    public final static String NAME = I18NPlug
            .getI18N("org.openjump.core.ui.plugins.MirrorPlugin");

    /** description of the tool */
    public final static String DESCRIPTION = I18NPlug
            .getI18N("org.openjump.core.ui.plugins.MirrorPlugin.description");

    final static String errorSeeOutputWindow = I18NPlug
            .getI18N("org.openjump.core.ui.tools.General.Error");
    /** Icon of the plugin */
    public final static Icon ICON = IconLoader.icon("symmetry.png");

    /** Symmetry tool by drawing a segment */
    protected MirrorToDrawSegmentTool mrt = null;

    /** Symmetry too by selecting a segment */
    protected MirrorToSelectedSegmentTool mst = null;

    /** Selecting tool in case of no check conditions */
    protected SelectFeaturesTool select = null;

    EnableCheckFactory checkFactory = new EnableCheckFactory(JUMPWorkbench
            .getInstance().getFrame().getContext());

    /**
     * 
     *
     */
    public MirrorPlugin() {
        createTools();
    }

    protected void createTools() {
        mrt = new MirrorToDrawSegmentTool();
        mst = new MirrorToSelectedSegmentTool();
        select = new SelectFeaturesTool();
    }

    /**
     * 
     */
    @Override
    public boolean execute(PlugInContext context) {
        reportNothingToUndoYet(context);
        if (!(JUMPWorkbench.getInstance().getFrame().getActiveInternalFrame() instanceof TaskFrame)) {
            JUMPWorkbench
                    .getInstance()
                    .getFrame()
                    .warnUser(
                            I18N.get("com.vividsolutions.jump.workbench.plugin.A-Task-Window-must-be-active"));
            return false;
        } else {
            try {
                if (!WorkbenchUtils.check(checkFactory
                        .createAtLeastNFeaturesMustBeSelectedCheck(1))) {
                    context.getLayerViewPanel().setCurrentCursorTool(select);
                    return false;
                }

                int n;
                Object[] options = {
                        I18NPlug.getI18N("org.openjump.core.ui.plugins.MirrorPlugin.Draw"),
                        I18NPlug.getI18N("org.openjump.core.ui.plugins.MirrorPlugin.Select"),
                        I18NPlug.getI18N("org.openjump.core.ui.plugins.Dialog.Cancel") };
                n = JOptionPane
                        .showOptionDialog(
                                context.getWorkbenchFrame(),
                                I18NPlug.getI18N("org.openjump.core.ui.plugins.MirrorPlugin.Do-you-want-to-draw-the-symmetry-axis-or-select-it-from-another-geometry"),
                                NAME, JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[2]);

                if (n == 0) {
                    context.getLayerViewPanel().setCurrentCursorTool(mrt);
                } else if (n == 1) {
                    context.getLayerViewPanel().setCurrentCursorTool(mst);
                }
                return n != 2;
            } catch (Exception e) {

                WorkbenchUtils.Logger(this.getClass(), e);

                return false;
            }
        }
    }

    @Override
    public String getName() {
        String tooltip = "";
        tooltip = "<HTML><BODY>";
        tooltip += "<DIV style=\"width: 300px; text-justification: justify;\">";
        tooltip += "<b>" + NAME + "</b>" + "<br>";
        tooltip += DESCRIPTION + "<br>";
        tooltip += "</DIV></BODY></HTML>";
        return tooltip;
    }

    public Icon getIcon() {
        return ICON;
    }

}
