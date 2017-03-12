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

import java.util.Collection;

import org.openjump.advancedtools.gui.SimpleLineDialog;
import org.saig.jump.lang.I18N;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;

/**
 * Comando de linea perpendicular </p>
 * 
 * @author Eduardo Montero Ruiz - emontero@saig.es
 * @since Kosmo 1.0
 */
public class PerpLineCommand extends LineCommand {

    /** Command name */
    private final static String COMMAND_NAME = "perp"; 

    /** Command sintaxis */
    private final static String SINTAXIS = "perp('" 
            + I18N.getString(PerpLineCommand.class, "length") 
            + "'[, '" 
            + I18N.getString(PerpLineCommand.class, "sense") 
            + "']) <br> " 
            + I18N.getString(PerpLineCommand.class,
                    "sense-true-by-default-clockwise-false-counterclockwise"); 

    private String comando;

    public PerpLineCommand(String comando) {
        this.comando = comando;
    }

    protected void setSecondPoint(SimpleLineDialog sld)
            throws LineCommandException {
        double parametro1 = LineCommandUtils.getParametroNAsDouble(comando, 1);
        boolean sentido = true;
        // Si existe el segundo parametro, lo capturamos
        try {
            sentido = LineCommandUtils.getParametroNAsBoolean(comando, 2);
        } catch (LineCommandException e) {
        }

        double anguloRadianes = getAnguloRadianes();
        double anguloGrados = anguloRadianes * 180 / Math.PI;
        if (sentido) {
            anguloGrados = anguloGrados - 90;
        } else {
            anguloGrados = anguloGrados + 90;
        }
        sld.setSecondPointLongAngulo(parametro1, anguloGrados);
    }

    /**
     * Obtiene el angulo de la linea seleccionada, en radianes
     */
    private double getAnguloRadianes() {
        WorkbenchContext context = JUMPWorkbench.getInstance().getContext();

        // Obtenemos las features seleccionadas
        Collection<Feature> selectedFeatures = context.getLayerViewPanel()
                .getSelectionManager().getFeatureSelection()
                .getFeaturesWithSelectedItems();
        // Obtenemos la geometria de la feature seleccionada
        Feature feat = (Feature) selectedFeatures.iterator().next();
        Geometry geom = feat.getGeometry();
        LineString line = (LineString) geom;
        LineSegment segment = new LineSegment(line.getStartPoint()
                .getCoordinate(), line.getEndPoint().getCoordinate());
        return segment.angle();
    }

    public String getSintaxis() {
        return SINTAXIS;
    }

    public static String getName() {
        return COMMAND_NAME;
    }

    @Override
    protected Coordinate getSecondPointRelativeTo(Coordinate coordinate) {
        return null;
    }
}