/* 
 * Kosmo - Sistema Abierto de Informaci�n Geogr�fica
 * Kosmo - Open Geographical Information System
 *
 * http://www.saig.es
 * (C) 2007, SAIG S.L.
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

import org.openjump.advancedtools.gui.SimpleLineDialog;
import org.openjump.advancedtools.language.I18NPlug;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Comando de linea con modulo/angulo
 * <p>
 * </p>
 * 
 * @author Eduardo Montero Ruiz
 * @since Kosmo 1.0.0
 */
public class AngLineCommand extends LineCommand {

    /** Nombre del comando */
    private final static String COMMAND_NAME = "@"; 
    /** Sintaxis del comando */
    private final static String SINTAXIS = "@ '" 
            + I18NPlug
                    .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.AngLineCommand.length") 
            + "'>'" 
            + I18NPlug
                    .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.AngLineCommand.angle") 
            + "'"; 

    private double parametro1;
    private double parametro2;
    private final String comando;

    public AngLineCommand(String comando) {
        this.comando = comando;
    }

    protected void setSecondPoint(SimpleLineDialog sld)
            throws LineCommandException {
        setParameters();
        sld.setSecondPointLongAngulo(parametro1, parametro2);

    }

    private void setParameters() throws LineCommandException {
        int posSimbolo = comando.indexOf(">"); 
        try {
            parametro1 = Double.parseDouble(comando.substring(1, posSimbolo));
            parametro2 = Double.parseDouble(comando.substring(posSimbolo + 1,
                    comando.length()));

        } catch (Exception e) {
            throw new LineCommandException();
        }
    }

    @Override
    public String getSintaxis() {
        return SINTAXIS;
    }

    @Override
    protected Coordinate getSecondPointRelativeTo(Coordinate coordinate)
            throws LineCommandException {
        setParameters();
        double angulo = (parametro2 * 2 * Math.PI) / 360.0;
        double x = parametro1 * Math.cos(angulo);
        double y = parametro1 * Math.sin(angulo);
        return new Coordinate(coordinate.x + x, coordinate.y + y);
    }

    public static String getHelp() {
        return I18NPlug
                .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.AngLineCommand.description"); 
    }

    public static String getName() {
        return COMMAND_NAME;
    }

}
