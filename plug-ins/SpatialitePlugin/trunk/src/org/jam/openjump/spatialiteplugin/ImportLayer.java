/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * JUMP is Copyright (C) 2003 Vivid Solutions
 *
 * This program implements extensions to JUMP and is
 * Copyright (C) 2010 Jorge Almaraz.
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
 * Jukka Rahkonen
 * jukka.rahkonen@latuviitta.fi
 * 
 */
package org.jam.openjump.spatialiteplugin;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jump.datastore.jdbc.ValueConverter;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;



public class ImportLayer {

	static final Logger logger = Logger.getLogger(ImportLayer.class);

	public static boolean ImportSql(PlugInContext context, SpatialiteDb db, String sqlstr) {
		try {
			StringBuffer tableName = new StringBuffer();
			FeatureCollection fc = loadResultSet(db, sqlstr, tableName);
            System.out.println("size" + fc.size());
			if (fc != null && context != null) {
                System.out.println("size");
                context.getLayerManager().addLayer("Spatialite", tableName.toString(), fc);
            }
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		return true;
	}


	private static FeatureCollection loadResultSet(SpatialiteDb db, String sql, StringBuffer tableName) throws SQLException{
		Statement st = db.createStatement();
		FeatureCollection fc = null;
		try{
		   ResultSet rs = st.executeQuery(sql);
		   rs.setFetchDirection(ResultSet.FETCH_FORWARD);
		   try{
			   //rs.first();
               rs.next();
			   int geomCol = SpatialiteGeometryBlob.getGeometryIndex(rs);
			   if (geomCol<0){
				   JOptionPane.showMessageDialog(null,"No Geometry blob found");
				   return null;
			   }
			   SpatialiteValueConverterFactory svcf =
				   new SpatialiteValueConverterFactory(geomCol);
			   ResultSetMetaData md = rs.getMetaData();
			   if (md.getTableName(1) != null)
				   tableName.append(md.getTableName(1));
			   else tableName.append("SpatialLiteQuery");

			   FeatureSchema fs = new FeatureSchema();
			   int ncols=md.getColumnCount();
			   ValueConverter[] vc= new ValueConverter[ncols];
			   for (int i=0;i<ncols;i++){
				   vc[i]=svcf.getConverter(md, i+1);
				   fs.addAttribute(md.getColumnName(i+1), vc[i].getType());
				   logger.info(String.format("add [%d]%s  %s ",i,md.getColumnName(i+1),vc[i].getType().toString()));
			   }

			   fc = new FeatureDataset(fs);

               // [mmichaud 2013-03-17] use the first row to avoid reverse fetching (which is not possible)
               Feature ft = new BasicFeature(fs);
               try {
                   for (int i=0;i<ncols;i++){
                       ft.setAttribute(i,vc[i].getValue(rs, i+1));
                   }
                   fc.add(ft);
               } catch (ParseException e) {
                   System.out.println(String.format("Can't load feature %d",rs.getRow() ));
                   e.printStackTrace();
               } catch (Exception e) {
                   System.out.println(String.format("Can't load feature %d",rs.getRow() ));
                   e.printStackTrace();
               }

			   //rs.beforeFirst();
			   while (rs.next()){
				   ft = new BasicFeature(fs);
					try {
						for (int i=0;i<ncols;i++){
							ft.setAttribute(i,vc[i].getValue(rs, i+1));
						}
						fc.add(ft);
					} catch (ParseException e) {
						System.out.println(String.format("Can't load feature %d",rs.getRow() ));
						e.printStackTrace();
					} catch (Exception e) {
						System.out.println(String.format("Can't load feature %d",rs.getRow() ));
						e.printStackTrace();
					}
				    //System.out.println(ft.getID() + ":" + ft.getGeometry());
			   }
		   } finally {db.closeDbObject(rs);}
		}  
		finally	{db.closeDbObject(st);}
		return fc;
	}


}

