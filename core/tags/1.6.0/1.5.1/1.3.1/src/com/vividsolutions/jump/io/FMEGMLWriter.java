/*
 * FMEGMLWriter.java
 *
 * Created on June 18, 2002, 1:59 PM
 */
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package com.vividsolutions.jump.io;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vividsolutions.jump.feature.*;


/**
 * This class is a {@link JUMPWriter} specialized to write FMEGML.
 *
 * <p>
 * DataProperties for the JCSWriter write(featureSchema,DataProperties) 
 * interface: 
 * </p>
 * 
 *  <table border='1' cellspacing='0' cellpadding='4'>
 *     <tr>
 *       <th>Parameter</th>
 *       <th>Meaning</th>
 *     </tr>
 *     <tr>
 *       <td>OutputFile or DefaultValue</td>
 *       <td>File name for output .xml file</td>
 *     </tr>
 *     <tr>
 *       <td>FMEFormatVersion</td>
 *       <td>'2000' or '2001'</td>
 *     </tr>
 *  </table>
 *  <br>
 * </p>

 *  <p>
 *  The format version specifies which version of FME GML this
 *  should produce. 
 *  </p>
 *
 *  <table border='1' cellspacing='0' cellpadding='4'>
 *   <tr>
 *     <td> 2000 </td>
 *     <td><pre> 
             &lt;dataset 
               xmlns="http://www.safe.com/xml/namespaces/fmegml2" 
               xmlns:fme="http://www.safe.com/xml/namespaces/fmegml2" 
	       xmlns:gml="http://www.opengis.net/gml" 
	       xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance" 
	       xsi:schemaLocation="http://www.safe.com/xml/schemas/fmegml2.xsd" &gt; 
           </pre> 
       </td>
     </tr>
 *   <tr>
 *     <td> 2001 </td>
       <td><pre>
             &lt;dataset 
               xmlns="http://www.safe.com/xml/schemas/FMEFeatures" 
	       xmlns:fme="http://www.safe.com/xml/schemas/FMEFeatures" 
	       xmlns:gml="http://www.opengis.net/gml" 
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	       xsi:schemaLocation="http://www.safe.com/xml/schemas/FMEFeatures.xsd" &gt;
	   </pre>
       </td>
     </tr>
 * </table>
 *
 */
public class FMEGMLWriter implements JUMPWriter {
    int outputFormatType = 1; // 0 = 2000, 1 = 2001, others to come

    /** Creates new FMEGMLWriter */
    public FMEGMLWriter() {
    }

    /**
     *    Cause a featureCollection to be written using the outputfile (and format) specified in the dp parameter.<br>
     *    A GMLOutputTemplate will be autogenerated, then the write request passed off to the {@link GMLWriter}.
     *@param featureCollection set of features to be written
     *@param dp where to write and format
     */
    public void write(FeatureCollection featureCollection, DriverProperties dp)
        throws IllegalParametersException, Exception {
        GMLOutputTemplate gmlTemplate;
        GMLWriter gmlWriter;
        java.io.BufferedWriter w;
        String outputfname;

        outputfname = dp.getProperty("File");

        if (outputfname == null) {
            outputfname = dp.getProperty("DefaultValue");
        }

        if (outputfname == null) {
            throw new IllegalParametersException(
                "call to FMEGMLWriter.write() has DataProperties w/o a OutputFile specified");
        }

        if (dp.getProperty("FMEFormatVersion") != null) {
            if (dp.getProperty("FMEFormatVersion").equals("2000")) {
                outputFormatType = 0;
            }

            if (dp.getProperty("FMEFormatVersion").equals("2001")) {
                outputFormatType = 1;
            }
        }

        gmlTemplate = this.createOutputTemplate(featureCollection.getFeatureSchema());
        gmlWriter = new GMLWriter() {
            protected String format(Date date) {
                return fmeDateFormatter.format(date);
            }
        };
        gmlWriter.setOutputTemplate(gmlTemplate);

        w = new java.io.BufferedWriter(new java.io.FileWriter(outputfname));
        gmlWriter.write(featureCollection, w);
        w.close();
    }
    
    private SimpleDateFormat fmeDateFormatter = new SimpleDateFormat("yyyyMMdd");

    /**
     *Makes a {@link GMLOutputTemplate} from a featureSchema.  Has two different version ('2000' and '2001'). <br>
     *@param fs description of the column in the dataset.
     **/
    public GMLOutputTemplate createOutputTemplate(FeatureSchema fs)
        throws ParseException, Exception {
        GMLOutputTemplate result;
        String templateText;
        String column;
        int t;
        String colName;
        String colType;

        templateText = "";

        switch (outputFormatType) {
        case 0:
            templateText = "<?xml version='1.0' encoding='UTF-8'?>\n<dataset xmlns=\"http://www.safe.com/xml/namespaces/fmegml2\" xmlns:fme=\"http://www.safe.com/xml/namespaces/fmegml2\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\" xsi:schemaLocation=\"http://www.safe.com/xml/schemas/fmegml2.xsd\">\n";

            break;

        case 1:
            templateText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<dataset xmlns=\"http://www.safe.com/xml/schemas/FMEFeatures\" xmlns:fme=\"http://www.safe.com/xml/schemas/FMEFeatures\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.safe.com/xml/schemas/FMEFeatures FMEFeatures.xsd\">\n";

            break;
        }

        templateText = templateText +
            "<schemaFeatures>\n<gml:featureMember>\n<Feature>\n<featureType>JCSOutput</featureType>\n";

        for (t = 0; t < fs.getAttributeCount(); t++) {
            AttributeType attributeType;

            attributeType = fs.getAttributeType(t);

            if (t != fs.getGeometryIndex()) {
                try {
                    colName = fs.getAttributeName(t);
                    colType = JCSattributeType2FMEtype(attributeType.toString());
                    column = "";

                    switch (outputFormatType) {
                    case 0:
                        column = "<property fme:name=\"" + colName + "\">" +
                            colType + "</property>\n";

                        break;

                    case 1:
                        column = "<property name=\"" + colName + "\">" +
                            colType + "</property>\n";

                        break;
                    }

                    // column =  "<property fme:name=\""+colName+"\">"+colType+"</property>\n";
                    templateText = templateText + column;
                } catch (Exception e) {
                    //do nothing - just dont export that column
                }
            }
        }

        templateText = templateText +
            "</Feature>\n</gml:featureMember>\n</schemaFeatures>\n<dataFeatures>\n";
        templateText = templateText +
            "<% FEATURE %>\n<gml:featureMember>\n<Feature>\n<featureType>JCSOutput</featureType>\n";

        for (t = 0; t < fs.getAttributeCount(); t++) {
            colName = fs.getAttributeName(t);

            if (t != fs.getGeometryIndex()) {
                //not geometry
                switch (outputFormatType) {
                case 0:
                    templateText = templateText + "<property fme:name=\"" +
                        colName + "\">";

                    break;

                case 1:
                    templateText = templateText + "<property name=\"" +
                        colName + "\">";

                    break;
                }

                // templateText = templateText +"<property fme:name=\""+colName+"\">";
                templateText = templateText + "<%=COLUMN " + colName +
                    "%></property>\n";
            } else {
                //geometry
                switch (outputFormatType) {
                case 0:
                    templateText = templateText +
                        "<property fme:name=\"gml2_coordsys\"></property>\n";

                    break;

                case 1:
                    templateText = templateText +
                        "<property name=\"gml2_coordsys\"></property>\n";

                    break;
                }

                // templateText = templateText +"<property fme:name=\"gml2_coordsys\"></property>\n";
                templateText = templateText +
                    "<gml:<%=GEOMETRYTYPE%>Property>\n<%=GEOMETRY %>\n</gml:<%=GEOMETRYTYPE%>Property>\n";
            }
        }

        templateText = templateText +
            "</Feature>\n</gml:featureMember>\n<% ENDFEATURE %>\n</dataFeatures>\n</dataset>\n";

        java.io.StringReader stringreader = new java.io.StringReader(templateText);

        result = new GMLOutputTemplate();
        result.load(stringreader, "Auto Generated FME GML input template");
        stringreader.close();

        return result;
    }

    /**
     * Convert a JCS column type to FME type (two different versions). <br>
     *  ie. STRING -> 'fme_char(1024)'
     *
     *@param jcsType JCS column type (ie. 'STRING','DOUBLE', or 'INTEGER'
     */
    String JCSattributeType2FMEtype(String jcsType) throws ParseException {
        switch (outputFormatType) {
        case 0:

            if (jcsType.equalsIgnoreCase("STRING")) {
                return "fme_char(1024)";
            }

            if (jcsType.equalsIgnoreCase("INTEGER")) {
                return "long";
            }

            if (jcsType.equalsIgnoreCase("DOUBLE")) {
                return "fme_decimal(15,15)";
            }
            
            if (jcsType.equalsIgnoreCase("DATE")) {
                //There is no FME GML "date" type. [Jon Aquino]
                return "string";
            }            

            throw new ParseException("couldn't convert JCS type '" + jcsType +
                "' to a FME type.");

        case 1:

            if (jcsType.equalsIgnoreCase("STRING")) {
                return "string";
            }

            if (jcsType.equalsIgnoreCase("INTEGER")) {
                return "long";
            }

            if (jcsType.equalsIgnoreCase("DOUBLE")) {
                return "long"; //strange but true
            }
            
            if (jcsType.equalsIgnoreCase("DATE")) {
                //There is no FME GML "date" type. [Jon Aquino]
                return "string";
            }            

            throw new ParseException("couldn't convert JCS type '" + jcsType +
                "' to a FME type.");
        }

        throw new ParseException("couldn't convert JCS type '" + jcsType +
            "' to a FME type.");
    }
}
