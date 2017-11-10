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

import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;

import org.openjump.advancedtools.config.CADToolsOptionsPanel;
import org.openjump.advancedtools.gui.CircleDialog;
import org.openjump.advancedtools.icon.IconLoader;
import org.openjump.advancedtools.language.I18NPlug;
import org.openjump.advancedtools.tools.CircleByDefinedRadiusTool;
import org.openjump.advancedtools.tools.CircleByDiameterTool;
import org.openjump.advancedtools.tools.CircleByRadiusTool;
import org.openjump.advancedtools.tools.CircleByTangentTool;
import org.openjump.advancedtools.tools.CircleBythreePointsTool;
import org.openjump.advancedtools.tools.EllipseByDraggingTool;
import org.openjump.advancedtools.utils.EditUtils;
import org.openjump.advancedtools.utils.WorkbenchUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.feature.FeatureUtil;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.model.StandardCategoryNames;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.EditTransaction;
import com.vividsolutions.jump.workbench.ui.LayerNamePanelProxy;
import com.vividsolutions.jump.workbench.ui.TaskFrame;
import com.vividsolutions.jump.workbench.ui.cursortool.CursorTool;

/**
 * Draw a circle
 * <p>
 * The circle may be drawn completely, indicate its center and radius or
 * configure its parameters through an option dialog
 * </p>
 * 
 * @author Gabriel Bellido P&eacute;rez - gbp@saig.es
 * @since Kosmo 1.0
 * @author Giuseppe Aruta, modified to add more options
 * @since OpenJUMP 1.10
 */
public class CirclePlugIn extends AbstractPlugIn {

    /** Plugin name */
    public final static String NAME = I18NPlug
            .getI18N("org.openjump.core.ui.plugins.Circle");

    /** Plugin name */
    public final static String NAME2 = I18NPlug
            .getI18N("org.openjump.core.ui.plugins.Circle.description");

    /** Plugin icnon */
    public static final Icon ICON = IconLoader.icon("drawCircle.png");
    // IconLoader.icon("DrawCircleConstrained.gif");

    /** Circle number of vertexes */
    public static final int NUM_VERTICES = 100;

    /**
     * 
     */
    public CirclePlugIn() {

    }

    @Override
    public String getName() {

        String tooltip = "";
        tooltip = "<HTML><BODY>";
        tooltip += "<DIV style=\"width: 200px; text-justification: justify;\">";
        tooltip += "<b>" + NAME + "</b>" + "<br>";
        tooltip += NAME2 + "<br>";
        tooltip += "</DIV></BODY></HTML>";
        return tooltip;
    }

    public Icon getIcon() {
        return ICON;
    }

    @Override
    public boolean execute(PlugInContext context) throws Exception {
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
                action(context);
            } catch (Exception ex) {
                WorkbenchUtils.Logger(this.getClass(), ex);
            }
            return true;
        }
    }

    public void action(PlugInContext context) throws Exception {
        CursorTool tool = null;
        CircleDialog cd = new CircleDialog(JUMPWorkbench.getInstance()
                .getFrame());
        cd.setName(NAME);
        cd.setVisible(true);
        if (!cd.cancelado) {
            if (cd.raton) {
                if ((CircleDialog.circleCombo.getSelectedItem().toString()
                        .equals(CircleDialog.radius))) {
                    tool = WorkbenchUtils
                            .addStandardQuasimodes(CircleByRadiusTool
                                    .create((LayerNamePanelProxy) context
                                            .getActiveInternalFrame()));
                    context.getLayerViewPanel().setCurrentCursorTool(tool);
                } else if (CircleDialog.circleCombo.getSelectedItem()
                        .toString().equals(CircleDialog.diameter)) {
                    tool = WorkbenchUtils
                            .addStandardQuasimodes(CircleByDiameterTool
                                    .create((LayerNamePanelProxy) context
                                            .getActiveInternalFrame()));
                    context.getLayerViewPanel().setCurrentCursorTool(tool);
                } else if (CircleDialog.circleCombo.getSelectedItem()
                        .toString().equals(CircleDialog.tangent)) {
                    tool = WorkbenchUtils
                            .addStandardQuasimodes(CircleByTangentTool
                                    .create((LayerNamePanelProxy) context
                                            .getActiveInternalFrame()));
                    context.getLayerViewPanel().setCurrentCursorTool(tool);
                } else if (CircleDialog.circleCombo.getSelectedItem()
                        .toString().equals(CircleDialog.threepoints)) {
                    tool = WorkbenchUtils
                            .addStandardQuasimodes(CircleBythreePointsTool
                                    .create((LayerNamePanelProxy) context
                                            .getActiveInternalFrame()));

                    context.getLayerViewPanel().setCurrentCursorTool(tool);
                }
            } else if (cd.radio) {
                tool = WorkbenchUtils
                        .addStandardQuasimodes(CircleByDefinedRadiusTool
                                .create((LayerNamePanelProxy) context
                                        .getActiveInternalFrame()));
                context.getLayerViewPanel().setCurrentCursorTool(tool);
            } else if (cd.absoluto) {
                doCircle(context);
            } else if (cd.ellipse) {
                tool = WorkbenchUtils
                        .addStandardQuasimodes(EllipseByDraggingTool
                                .create((LayerNamePanelProxy) context
                                        .getActiveInternalFrame()));
                context.getLayerViewPanel().setCurrentCursorTool(tool);
            }
        }
        return;

    }

    protected static GeometryFactory geomFac = new GeometryFactory();

    public Layer getLayer(PlugInContext context) {
        return (context.getWorkbenchContext().getLayerViewPanel()
                .getLayerManager().getEditableLayers().iterator().next());
    }

    protected Point getCenter() throws NoninvertibleTransformException {

        double X = Double.parseDouble(CircleDialog.jsAbsolutaX.getText());
        double Y = Double.parseDouble(CircleDialog.jsAbsolutaY.getText());
        Coordinate center = new Coordinate(X, Y);
        return new GeometryFactory().createPoint(center);
    }

    public static LineString getCircleRadius()
            throws NoninvertibleTransformException {

        double X = Double.parseDouble(CircleDialog.jsAbsolutaX.getText());
        double Y = Double.parseDouble(CircleDialog.jsAbsolutaY.getText());
        Coordinate center = new Coordinate(X, Y);
        double radius = Double.parseDouble(CircleDialog.jsRadio2.getText());
        Coordinate[] coords = EditUtils.createCircle(center, radius,
                CirclePlugIn.NUM_VERTICES);

        return geomFac.createLineString(coords);
        // return circle.getPoly();// .getLineString();
    }

    protected Polygon getPolygonCircleRadius()
            throws NoninvertibleTransformException {

        double X = Double.parseDouble(CircleDialog.jsAbsolutaX.getText());
        double Y = Double.parseDouble(CircleDialog.jsAbsolutaY.getText());
        Coordinate center = new Coordinate(X, Y);
        double radius = Double.parseDouble(CircleDialog.jsRadio2.getText());
        Coordinate[] coords = EditUtils.createCircle(center, radius,
                CirclePlugIn.NUM_VERTICES);
        LinearRing lr = geomFac.createLinearRing(coords);
        Geometry geom;
        geom = geomFac.createPolygon(lr, null);
        return (Polygon) geom;
        // return circle.getPoly();// .getLineString();
    }

    private Layer itemlayer = null;
    boolean newLayer = true;

    private boolean doCircle(PlugInContext context) throws Exception {
        this.itemlayer = editableLayer(context);
        EditTransaction transaction = new EditTransaction(new ArrayList(),
                this.getName(), this.itemlayer,
                this.isRollingBackInvalidEdits(context), true,
                context.getWorkbenchFrame());
        FeatureSchema fschema = this.itemlayer.getFeatureCollectionWrapper()
                .getFeatureSchema();
        Geometry geom = null;
        if (CADToolsOptionsPanel.isGetAsPolygon()) {
            geom = getPolygonCircleRadius();
        } else {
            geom = getCircleRadius();
        }
        Feature newFeature = FeatureUtil.toFeature(geom, fschema);
        transaction.createFeature(newFeature);
        transaction.commit();
        if (CADToolsOptionsPanel.isGetCentroid()) {
            Geometry geom2 = getCenter();
            Feature newFeature2 = FeatureUtil.toFeature(geom2, fschema);
            transaction.createFeature(newFeature2);
            transaction.commit();
        }

        return true;

    }

    public static FeatureCollection createBlankFeatureCollection() {
        FeatureSchema featureSchema = new FeatureSchema();
        featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
        return new FeatureDataset(featureSchema);
    }

    public Layer editableLayer(PlugInContext context) {
        Layer layer = null;
        final WorkbenchContext wbcontext = context.getWorkbenchContext();
        LayerManager layerManager = wbcontext.getLayerManager();
        Collection layerCollection = layerManager.getLayers();
        if (layerCollection.isEmpty()) {
            FeatureSchema featureSchema = new FeatureSchema();
            featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
            FeatureCollection myCollA = new FeatureDataset(featureSchema);
            layer = context
                    .addLayer(
                            StandardCategoryNames.WORKING,
                            I18N.get("org.openjump.core.ui.plugins.edit.ReplicateSelectedItemsPlugIn.new"),
                            myCollA);
        } else {
            layer = wbcontext.createPlugInContext().getSelectedLayer(0);
        }
        layer.setEditable(true);
        return layer;

    }

}