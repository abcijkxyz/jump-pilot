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
 * Comando de posicion absoluta </p>
 * 
 * @author Eduardo Montero Ruiz
 * @since Kosmo 1.0.0
 */
public class AbsolutoLineCommand extends LineCommand {

    /** Nombre del comando */
    private final static String COMMAND_NAME = "abs"; 
    /** Sintaxis del comando */
    private final static String SINTAXIS = "'" 
            + I18NPlug
                    .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.AbsolutoLineCommand.x-value") 
            + "','" 
            + I18NPlug
                    .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.AbsolutoLineCommand.y-value") 
            + "'"; 

    private double parametro1;
    private double parametro2;
    private final String comando;

    public AbsolutoLineCommand(String comando) {
        this.comando = comando;
    }

    protected void setSecondPoint(SimpleLineDialog sld)
            throws LineCommandException {
        setParameters();
        sld.setSecondPointAbsoluta(parametro1, parametro2);
    }

    private void setParameters() throws LineCommandException {
        int posComa = comando.indexOf(","); 
        try {
            parametro1 = Double.parseDouble(comando.substring(0, posComa));
            parametro2 = Double.parseDouble(comando.substring(posComa + 1,
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
        return new Coordinate(parametro1, parametro2);
    }

    public static String getHelp() {
        return I18NPlug
                .getI18N("org.openjump.core.ui.tools.DrawLineStringCommandsTool.AbsolutoLineCommand.description"); 
    }

    public static String getName() {
        return COMMAND_NAME;
    }
}
