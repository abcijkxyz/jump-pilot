/**
 * @author sstein
 * license GPL
 * 
 * created: 3. July 2013
 */
package org.openjump.core.ui.plugin.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import org.openjump.core.ui.plugin.AbstractThreadedUiPlugIn;
import org.openjump.core.ui.plugin.file.openstreetmap.OJOsmReader;
import org.openjump.core.ui.plugin.file.openstreetmap.OjOsmPrimitive;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.StandardCategoryNames;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.MenuNames;

public class LoadOSMFilePlugIn extends AbstractThreadedUiPlugIn{
	
	public static FileFilter OSM_FILE_FILTER = null; 

	private JFileChooser fileChooser;  
	String filePath = "";
	File selFile = null;

	public void initialize(PlugInContext context) throws Exception {	
		
		LoadOSMFilePlugIn.OSM_FILE_FILTER = GUIUtil.createFileFilter("OpenStreetMap osm file", new String[]{"osm"});

        context.getFeatureInstaller().addMainMenuPlugin(
        		this,
                new String[] {MenuNames.PLUGINS}, 	//menu path
                "Load OSM File ...", 
                false,
                null,
                createEnableCheck(context.getWorkbenchContext()), -1);     

		fileChooser = GUIUtil.createJFileChooserWithExistenceChecking();
		fileChooser.setDialogTitle("Choose OSM *.osm file");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		GUIUtil.removeChoosableFileFilters(fileChooser);
		fileChooser.addChoosableFileFilter(OSM_FILE_FILTER);
		fileChooser.addChoosableFileFilter(GUIUtil.ALL_FILES_FILTER);
		fileChooser.setFileFilter(OSM_FILE_FILTER);
	}
	
	public static MultiEnableCheck createEnableCheck(WorkbenchContext workbenchContext) {
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);
        return new MultiEnableCheck()
            .add(checkFactory.createTaskWindowMustBeActiveCheck());
}
	
	public String getName(){
		//return I18N.get("org.openjump.plugin.loadOTPgraph");
		return "Load OSM File";
	}
	
	public boolean execute(PlugInContext context) throws Exception{
		reportNothingToUndoYet(context);
		
		if (JFileChooser.APPROVE_OPTION != fileChooser.showOpenDialog(context
					.getWorkbenchFrame())) {
			return false;
		}
		
        this.selFile = fileChooser.getSelectedFile();
		this.filePath = selFile.getAbsolutePath();
		
		return true;
	}
	
	public void run(TaskMonitor monitor, PlugInContext context) throws Exception {
	
		monitor.allowCancellationRequests();
		
		monitor.report("reading OSM file");

        FileInputStream in = null;
        boolean worked = false;
        ArrayList data = null;
        try {
            in = new FileInputStream(selFile);
            OJOsmReader osmr = new OJOsmReader();
            worked = osmr.doParseDataSet(in);
            if(worked){
            	data = osmr.getDataset();
            }
            else{
            	return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IOException("File " + selFile.getName() + " does not exist.");
        } finally {
            close(in);
        }
		//create the FeatureSchema
		FeatureSchema fsvx = new FeatureSchema();
		fsvx.addAttribute("Geometry", AttributeType.GEOMETRY);

		String sfieldID = "osm_id";		
		AttributeType t0 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldID, t0);
		
		String sfieldType = "type";		
		AttributeType t1 = AttributeType.STRING;
		fsvx.addAttribute(sfieldType, t1);
	
		String sfieldTime = "timestamp";		
		AttributeType t2 = AttributeType.DATE;
		fsvx.addAttribute(sfieldTime, t2);
		
		String sfieldUser = "osm_user";		
		AttributeType t3 = AttributeType.STRING;
		fsvx.addAttribute(sfieldUser, t3);
		
		String sfieldUserID = "osm_userid";		
		AttributeType t4 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldUserID, t4);
		
		String sfieldVersion = "osm_version";		
		AttributeType t5 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldVersion, t5);
		
		String sfieldVisible = "visible";		
		AttributeType t6 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldVisible, t6);
		
		String sfieldDelete = "action_deleted";		
		AttributeType t7 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldDelete, t7);
		
		String sfieldModify = "action_modified";		
		AttributeType t8 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldModify, t8);
		
		String sfieldChangeID = "osm_changeset_id";		
		AttributeType t9 = AttributeType.INTEGER;
		fsvx.addAttribute(sfieldChangeID, t9);
		
		String sTags = "osm_tags";		
		AttributeType t10 = AttributeType.STRING;
		fsvx.addAttribute(sTags, t10);
		
		FeatureDataset fdVertices = new FeatureDataset(fsvx);	
		
        for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			OjOsmPrimitive osmPrim = (OjOsmPrimitive) iterator.next();
			
			Feature fNew = new BasicFeature(fsvx);

			fNew.setGeometry(osmPrim.getGeom());
			Long lid = osmPrim.getId();
			fNew.setAttribute(sfieldID, new Integer(lid.intValue()));
			fNew.setAttribute(sfieldType, new String(osmPrim.getOsmTypeAsString()));
			fNew.setAttribute(sfieldTime, osmPrim.getTimestamp());
			fNew.setAttribute(sfieldUser, new String(osmPrim.getUser().getName()));
			Long uid = osmPrim.getUser().getId();
			fNew.setAttribute(sfieldUserID, new Integer(uid.intValue()));
			fNew.setAttribute(sfieldVersion, new Integer(osmPrim.getVersion()));
			int valVis = osmPrim.isVisible() ? 1 : 0;
			fNew.setAttribute(sfieldVisible, new Integer(valVis));
			int valDel = osmPrim.isDeleted() ? 1 : 0;
			fNew.setAttribute(sfieldDelete, new Integer(valDel));
			int valMod = osmPrim.isModified() ? 1 : 0;
			fNew.setAttribute(sfieldModify, new Integer(valMod));
			fNew.setAttribute(sfieldChangeID, new Integer(osmPrim.getChangesetId()));
			String tagText = "";
			if (osmPrim.hasKeys()){
				tagText = osmPrim.getAllKeyValueTagsAsOneString();
			}
			fNew.setAttribute(sTags, new String(tagText));
			
			fdVertices.add(fNew);
			
			if(monitor.isCancelRequested()){
				if(fdVertices.size() > 0){
					context.addLayer(StandardCategoryNames.RESULT, "OSM - stopped", fdVertices);
				}
			}
		}
				
		//display the result FCs
		if(fdVertices.size() > 0){
			context.addLayer(StandardCategoryNames.RESULT, "OSM_" + this.selFile.getName() , fdVertices);
		}
		System.gc(); 
	}
	
    /**
     * <p>Utility method for closing a {@link Closeable} object.</p>
     *
     * @param c the closeable object. May be null.
     */
    public static void close(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch(IOException e) {
            // ignore
        }
    }
	
}
