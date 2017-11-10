/* 
 * Kosmo - Sistema Abierto de Informaci�n Geogr�fica
 * Kosmo - Open Geographical Information System
 *
 * http://www.saig.es
 * (C) 2008, SAIG S.L.
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
package org.openjump.advancedtools.tools.cogo.commands;

import java.util.List;

import org.openjump.advancedtools.gui.SimpleLineDialog;
import org.openjump.advancedtools.language.I18NPlug;
import org.openjump.advancedtools.tools.cogo.DrawGeometryCommandsTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jump.workbench.JUMPWorkbench;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Juan Jose Macias Moron
 * @since Kosmo 1.1.1
 */
public class EndLineCommand extends LineCommand {

    /** Nombre del comando */
    private final static String COMMAND_NAME = "end"; 
    /** Sintaxis del comando */
    private final static String SINTAXIS = "end"; 

    @Override
    protected Coordinate getSecondPointRelativeTo(Coordinate coordinate)
            throws LineCommandException {
        return null;
    }

    @Override
    protected void setSecondPoint(SimpleLineDialog sld)
            throws LineCommandException {
        // Nada
    }

    public static String getHelp() {
        return I18NPlug
                .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.EndLineCommand.description"); 
    }

    public static String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getSintaxis() {
        return SINTAXIS;
    }

    @Override
    public void execute(DrawGeometryCommandsTool drawLineStringCommandsTool)
            throws LineCommandException {
        List<Coordinate> list = drawLineStringCommandsTool.getCoordinates();
        if (!list.isEmpty()) {
            drawLineStringCommandsTool.setGestureFinished();
        } else {
            JUMPWorkbench
                    .getInstance()
                    .getFrame()
                    .warnUser(
                            I18NPlug.getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.EndLineCommand.check")); 
        }
    }

}
