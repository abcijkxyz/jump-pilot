/*
 * Created on 06.12.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.latlon.deejump.io;

import java.util.Iterator;

import org.deegree_impl.io.shpapi.ShapeFile;
import org.deegree_impl.model.feature.FeatureFactory;
import org.deegree_impl.model.geometry.JTSAdapter;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.FeatureType;
import org.deegree.model.feature.FeatureTypeProperty;
import org.deegree.model.geometry.GM_Exception;

import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.IllegalParametersException;
import com.vividsolutions.jump.io.JUMPWriter;

import de.latlon.deejump.util.data.JUMPFeatureFactory;


/**
 * @author hamammi
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DeegreeShapeFileWriter implements JUMPWriter {

	public void write(
			com.vividsolutions.jump.feature.FeatureCollection featureCollection,
			DriverProperties dp) throws IllegalParametersException, Exception {
		String fileRoot = dp.getProperty("File");

		if (fileRoot == null) {
			throw new Exception("fileRoot cannot not be null");
		}
		fileRoot = fileRoot.substring(0, dp.getProperty("File").length() - 4);

		FeatureCollection deegreeFC = 
            JUMPFeatureFactory.createFromJUMPFeatureCollection(featureCollection);
		//Save a Shapefile forever.
		ShapeFile sf = new ShapeFile(fileRoot, "rw");
		sf.writeShape(deegreeFC);
		sf.close();

	}
}

	