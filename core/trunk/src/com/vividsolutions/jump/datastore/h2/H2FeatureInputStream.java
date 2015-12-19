package com.vividsolutions.jump.datastore.h2;

import com.vividsolutions.jump.datastore.postgis.PostgisResultSetConverter;
import com.vividsolutions.jump.datastore.spatialdatabases.SpatialDatabasesFeatureInputStream;
import com.vividsolutions.jump.datastore.spatialdatabases.SpatialDatabasesResultSetConverter;
import com.vividsolutions.jump.workbench.JUMPWorkbench;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * H2FeatureInputStream
 */
public class H2FeatureInputStream extends SpatialDatabasesFeatureInputStream {

    public H2FeatureInputStream(Connection conn, String queryString) {
        this(conn, queryString, null);
    }

    public H2FeatureInputStream(Connection conn, String queryString, String externalIdentifier) {
        super(conn, queryString, externalIdentifier);
        try {
            JUMPWorkbench.getInstance().getFrame().log("creating a H2FeatureInputStream (class:" + this.getClass()
                    + " ) (driver: " + conn.getMetaData().getDriverName() + ") id"
                    + this.hashCode(), this.getClass());
        } catch (SQLException ex) {
            Logger.getLogger(H2FeatureInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a H2ResultSetConverter
     * @param rs
     * @return
     */
    @Override
    protected SpatialDatabasesResultSetConverter getResultSetConverter(ResultSet rs) {
        return new H2ResultSetConverter(conn, rs);
    }
}
