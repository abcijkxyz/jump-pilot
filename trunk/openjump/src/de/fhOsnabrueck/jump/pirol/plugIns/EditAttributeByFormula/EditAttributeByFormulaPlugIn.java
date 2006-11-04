/*
 * Created on 20.06.2005
 *
 * CVS information:
 *  $Author$
 *  $Date$
 *  $ID$
 *  $Rev: 2509 $
 *  $Id$
 *  $Log$
 *  Revision 1.2  2006/11/04 19:11:58  mentaer
 *  *** empty log message ***
 *
 *  Revision 1.1  2006/11/04 19:09:34  mentaer
 *  added Pirol Plugin for Attribute Calculations for testing, which needs the baseclasses.jar
 *
 *  Revision 1.12  2006/05/09 14:31:39  orahn
 *  kleiner GUI versch�nerung
 *
 *  Revision 1.11  2006/02/01 17:35:56  orahn
 *  + Unterst�tzung f. Attributnamen mit Leerzeichen
 *  + kleines, generelles Update f�r das PlugIn
 *
 *  Revision 1.10  2005/08/03 13:50:44  orahn
 *  +i18n
 *  -warnings
 *
 *  Revision 1.9  2005/07/13 10:12:55  orahn
 *  Einsatz: MetaInformationHandler
 *
 *  Revision 1.8  2005/07/12 16:33:56  orahn
 *  +Nutzung des PropertiesHandler
 *
 *  Revision 1.7  2005/06/30 10:42:12  orahn
 *  besseres Fehler-Feedback bei der Formeleingabe
 *
 *  Revision 1.6  2005/06/30 08:37:40  orahn
 *  misslungene Formel besch�digt nicht mehr das Layer
 *
 *  Revision 1.5  2005/06/29 16:03:57  orahn
 *  aufgemotzt
 *
 *  Revision 1.4  2005/06/28 15:35:18  orahn
 *  fast soweit: es fehlt noch eine "taste" wie backspace f�r ganze operatoren bzw. operanden
 *  und das Formel-Text-Feld mu� gegen direkte User-Eingaben gesch�tzt werden
 *
 *  Revision 1.3  2005/06/23 13:57:20  orahn
 *  nutzt jetzt uniqueAttributeName
 *
 *  Revision 1.2  2005/06/23 13:41:17  orahn
 *  erste BETA des Formel-Parser-PlugIns
 *
 *  Revision 1.1  2005/06/20 18:17:34  orahn
 *  erster Ansatz
 *
 */
package de.fhOsnabrueck.jump.pirol.plugIns.EditAttributeByFormula;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.MenuNames;

import de.fhOsnabrueck.jump.pirol.utilities.FormulaParsing.FormulaValue;
import de.fhOsnabrueck.jump.pirol.utilities.Properties.PropertiesHandler;
import de.fhOsnabrueck.jump.pirol.utilities.apiTools.FeatureCollectionTools;
import de.fhOsnabrueck.jump.pirol.utilities.attributes.AttributeInfo;
import de.fhOsnabrueck.jump.pirol.utilities.debugOutput.DebugUserIds;
import de.fhOsnabrueck.jump.pirol.utilities.debugOutput.PersonalLogger;
import de.fhOsnabrueck.jump.pirol.utilities.i18n.PirolPlugInMessages;
import de.fhOsnabrueck.jump.pirol.utilities.metaData.MetaInformationHandler;
import de.fhOsnabrueck.jump.pirol.utilities.plugIns.StandardPirolPlugIn;
import de.fhOsnabrueck.jump.pirol.utilities.settings.PirolPlugInSettings;

/**
 * 
 * PlugIn that creates a new attribute and assigns values to it, that are 
 * calculated by processing a formula that was created by the user.
 *
 * @author Ole Rahn
 * <br>
 * <br>FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck,
 * <br>Project: PIROL (2005),
 * <br>Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2509 $
 */
public class EditAttributeByFormulaPlugIn extends StandardPirolPlugIn {

    protected static PropertiesHandler storedFormulas = null;
    protected static final String storedFormulasFileName = "Formula.properties"; //$NON-NLS-1$
    
    public void initialize(PlugInContext context) throws Exception {
	    context.getFeatureInstaller().addMainMenuItemWithJava14Fix(this,
		        new String[] {MenuNames.TOOLS, MenuNames.EDIT },
				"Attribute Calculator", 
				false, 
				null, 
				createEnableCheck(context.getWorkbenchContext()));
    }
    
    public static MultiEnableCheck createEnableCheck(WorkbenchContext workbenchContext) {
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);

        return new MultiEnableCheck()
                        .add(checkFactory.createAtLeastNItemsMustBeSelectedCheck(1));
    }
    
    public EditAttributeByFormulaPlugIn(){
        super(new PersonalLogger(DebugUserIds.OLE));        
    }
    
    /**
     * @inheritDoc
     */
    public String getIconString() {
        return null;
    }
    
    /**
     *@inheritDoc
     */
    public String getCategoryName() {
        return PirolPlugInSettings.getName_AttributeMenu();
    }

    /**
     * @inheritDoc
     */
    public boolean execute(PlugInContext context) throws Exception {
        Layer layer = StandardPirolPlugIn.getSelectedLayer(context);
        
        if (layer==null){
            StandardPirolPlugIn.warnUser(context,PirolPlugInMessages.getString("no-layer-selected")); //$NON-NLS-1$
            return this.finishExecution(context, false);
        } else if (!layer.isEditable()) {
            StandardPirolPlugIn.warnUser(context,PirolPlugInMessages.getString("layer-not-editable")); //$NON-NLS-1$
            return this.finishExecution(context, false);
        }
        
        
        try {
            EditAttributeByFormulaPlugIn.storedFormulas = new PropertiesHandler(EditAttributeByFormulaPlugIn.storedFormulasFileName);
            EditAttributeByFormulaPlugIn.storedFormulas.load();
        } catch (FileNotFoundException e1) {
            this.logger.printWarning(e1.getMessage());
        } catch (IOException e1) {
            this.logger.printWarning(e1.getMessage());
        }
        
        EditAttributeByFormulaDialog dialog = new EditAttributeByFormulaDialog(context.getWorkbenchFrame(), PirolPlugInMessages.getString("specify-attribute-and-formular"), true, PirolPlugInMessages.getString("editByFormula-explaining-text"), layer.getFeatureCollectionWrapper().getFeatureSchema(), EditAttributeByFormulaPlugIn.storedFormulas); //$NON-NLS-1$ //$NON-NLS-2$
        
        dialog.setVisible(true);
        
        String formula = dialog.getFormula();
        
        if (!dialog.wasOkClicked() || formula==null || formula.length()==0 ){
            return this.finishExecution(context, false);
        }
        
        
        AttributeInfo attrInfo = dialog.getAttributeInfo();
        
        FeatureCollection oldFc = layer.getFeatureCollectionWrapper().getUltimateWrappee();
        
        attrInfo.setUniqueAttributeName(FeatureCollectionTools.getUniqueAttributeName(oldFc, attrInfo.getAttributeName()));
        
        try {
            FormulaValue parsedFormula = dialog.getParsedFormula();
            
            FeatureCollection newFc = FeatureCollectionTools.applyFormulaToFeatureCollection( oldFc, attrInfo, parsedFormula, true );
            layer.setFeatureCollection(newFc);
            
            MetaInformationHandler metaInfHandler = new MetaInformationHandler(layer);
            metaInfHandler.addMetaInformation(PirolPlugInMessages.getString("formula-for") + attrInfo.getUniqueAttributeName(), formula); //$NON-NLS-1$

            if (storedFormulas != null){
                storedFormulas.setProperty(attrInfo.toString(), formula);
                storedFormulas.store(PirolPlugInMessages.getString("editByFormula-properties-comment")); //$NON-NLS-1$
            }
            
            
        } catch (Exception e){
            this.handleThrowable(e);
            this.logger.printError(e.getMessage());
            e.printStackTrace();
            return this.finishExecution(context, false);
        }
        
        return this.finishExecution(context, true);
    }

}
