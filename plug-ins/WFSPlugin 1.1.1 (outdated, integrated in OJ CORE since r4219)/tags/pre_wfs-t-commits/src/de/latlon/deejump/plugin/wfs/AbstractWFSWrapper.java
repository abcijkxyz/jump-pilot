/*
 * (c) 2007 by lat/lon GmbH
 *
 * @author Ugo Taddei (taddei@latlon.de)
 *
 * This program is free software under the GPL (v2.0)
 * Read the file LICENSE.txt coming with the sources for details.
 */

package de.latlon.deejump.plugin.wfs;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.feature.schema.GMLSchemaDocument;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.xml.sax.SAXException;

import de.latlon.deejump.ui.DeeJUMPException;

/**
 * Superclass that wraps the basic functionality of a (simple) WFS. This class 
 * encapsulates the behaviour of a WFS, and allows subclasses to change behaviour
 * according to WFS version. 
 *
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author$
 *
 * @version $Revision$, $Date$
 */
public abstract class AbstractWFSWrapper {
    
    public static final String WFS_PREFIX = "wfs";
    
    private static Logger LOG = Logger.getLogger( AbstractWFSWrapper.class );    
    
    protected String baseURL;
    
    protected Map<String, WFSFeatureType> ftNameToWfsFT;
    
    /**
     * Maps a feature type to its schem. Geometry property is not held here!
     */
    private Map featureTypeToSchema;

    //hmmm, this is repating the above, really...
    private Map featureTypeToSchemaXML;

    /**
     * Maps a feature type to its geometry!
     */
    private Map<String, QualifiedName[]> geoPropsNameToQNames;

    private HttpClient httpClient;
    
    public abstract String getServiceVersion();
    
    public abstract String[] getFeatureTypes();
    
//    public abstract String[] getProperties(String featureType);
    
//    public abstract QualifiedName[] getGeometryProperties(String featureType);
    
    public abstract String getGetFeatureURL();
    
//    public abstract QualifiedName getQualiNameByFeatureTypeName( String ftName );

    //abstract protected String createCapabilitiesOnlineResource(); 

    //perhaps use a map for these???
    abstract protected String createDescribeFTOnlineResource(); 

    public String getBaseWfsURL(){
        return this.baseURL;
    }
    
    //not abs
    public abstract String getCapabilitesAsString();
    
    protected AbstractWFSWrapper( String baseUrl ){
        if( baseUrl == null || baseUrl.length() == 0 ){
            throw new IllegalArgumentException("The URL for the WFServer cannot be null or empty.");
        }
        this.baseURL = baseUrl;
        this.featureTypeToSchema = new HashMap( 10 );
        this.featureTypeToSchemaXML = new HashMap( 10 );
        this.geoPropsNameToQNames = new HashMap<String, QualifiedName[]> ( 10 );
        createHttpClient();
        
    }
    
    public String getCapabilitiesURL() {
        
        StringBuffer sb = new StringBuffer(OWSUtils.validateHTTPGetBaseURL( this.baseURL ) );
        sb.append( "SERVICE=WFS&REQUEST=GetCapabilities&VERSION=" );
        sb.append( getServiceVersion() );
                
        return sb.toString();
    }
    
    
    public String getDescribeTypeURL( QualifiedName typename){
        
        String url = OWSUtils.validateHTTPGetBaseURL(createDescribeFTOnlineResource()) + "SERVICE=WFS&REQUEST=DescribeFeatureType&version="
            +  getServiceVersion() + "&TYPENAME=" 
            + typename.getPrefix() + ":" + typename.getLocalName()  
            + "&NAMESPACE=xmlns(" + typename.getPrefix()+"="+typename.getNamespace()+")";
        
        return url;
    }

    public GMLSchema getSchemaForFeatureType( String featureType ){
        return (GMLSchema)this.featureTypeToSchema.get( featureType );
    }

    public String getRawSchemaForFeatureType( String featureType ){
        return (String)this.featureTypeToSchemaXML.get( featureType );
    }

    protected String loadSchemaForFeatureType(String featureType) throws DeeJUMPException {

        String descrFtUrl = createDescribeFTOnlineResource();

        if( descrFtUrl == null ){
            throw new RuntimeException( "Service does not have a DescribeFeatureType operation accessible by HTTP GET or POST." );
        }
        
        WFSFeatureType wfsFt = getFeatureTypeByName( featureType );
        if ( wfsFt == null ){
            return null;
        }
        
        QualifiedName ft = wfsFt.getName();
        String serverReq = getDescribeTypeURL( ft );
  
        try {
            GMLSchemaDocument xsdDoc = new GMLSchemaDocument();
            xsdDoc.load( new URL(serverReq ) );
            return DOMPrinter.nodeToString( xsdDoc.getRootElement(), null );
        } catch ( Exception e ) {
            e.printStackTrace();            
            String mesg = "Error fetching FeatureType description";
            LOG.error( mesg + " for " + featureType + " using " + serverReq);
            throw new DeeJUMPException( mesg,e);
        } 

    }
    
    protected String loadSchemaForFeatureType2(String featureType) throws DeeJUMPException {
        
        //unfortunately no time for moving this out of here
        boolean isGet = true;
        
        String descrFtUrl = createDescribeFTOnlineResource();

        if( descrFtUrl == null ){
            throw new RuntimeException( "Service does not have a DescribeFeatureType operation accessible by HTTP GET or POST." );
        }
        
        WFSFeatureType wfsFt = getFeatureTypeByName( featureType );
        if ( wfsFt == null ){
            return null;
        }
        
        QualifiedName ft = wfsFt.getName();

        String serverReq = getDescribeTypeURL( ft );
        String httpProtocolMethod = isGet ? "HTTP_GET" : "HTTP_POST" ;

        LOG.debug( "Using " + httpProtocolMethod + " to get feature type description from " + descrFtUrl + serverReq);
        
        HttpMethod httpMethod = createHttpMethod( httpProtocolMethod );//new GetMethod( serverUrl );
        URI uri;
        try {
     
            uri = new URI( getBaseWfsURL(), true );
            httpMethod.setURI( uri );

        } catch ( URIException e ) {
            throw new DeeJUMPException(e);
        } 
        
        //only input here what's after the '?'
//        httpMethod.setQueryString( serverReq.split( "\\?" )[1] );
        try {
            httpClient.executeMethod(httpMethod);
            GMLSchemaDocument xsdDoc = new GMLSchemaDocument();
            xsdDoc.load( new URL(serverReq ) );//httpMethod.getResponseBodyAsStream(), serverReq );
            
            return DOMPrinter.nodeToString( xsdDoc.getRootElement(), null );
        } catch ( Exception e ) {
            e.printStackTrace();            
            String mesg = "Error fetching FeatureType description";
            LOG.error( mesg + " for " + featureType + " from " 
                       + uri + " using " + serverReq);
            throw new DeeJUMPException( mesg,e);
        } 
        
    }
    
        
    
    /**Creates an String[] containing the attributes of a given feature type
     * @param featureTypeName the name of the feature type
     * @throws Exception 
     * */
    protected void createSchemaForFeatureType(String featureTypeName){
        
        try {
            //GMLSchema xsd = loadSchemaForFeatureType( featureTypeName );
            String rawXML = loadSchemaForFeatureType( featureTypeName );
            if( rawXML == null ){
                return;
            }
            GMLSchemaDocument xsdDoc = new GMLSchemaDocument();
            xsdDoc.load( new StringReader(rawXML), "http://empty" );
            GMLSchema xsd = xsdDoc.parseGMLSchema(); 
            
            this.featureTypeToSchema.put( featureTypeName, xsd);
            this.featureTypeToSchemaXML.put( featureTypeName, rawXML);
            
            QualifiedName[] geoProp = guessGeomProperty( xsd );
            
            this.geoPropsNameToQNames.put( featureTypeName, geoProp );                 

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public String[] getProperties(String featureType) {
        
        List propsList = new ArrayList<String>();
        try {
            createSchemaForFeatureType( featureType );
            
            GMLSchema schema = (GMLSchema)this.featureTypeToSchema.get( featureType );
            if( schema != null ){
                FeatureType[] fts = schema.getFeatureTypes();
                for ( int i = 0; i < fts.length; i++ ) {
                    PropertyType[] props = fts[i].getProperties();
                    for ( int j = 0; j < props.length; j++ ) {
                        if( !(props[j].getType() == Types.GEOMETRY || props[j].getType()  == 10014) ){
                            propsList.add(  props[j].getName().getAsString() );
                        }
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            propsList = new ArrayList<String>();
        }
        
        return (String[])propsList.toArray( new String[ propsList.size() ] );
    }

    public WFSFeatureType getFeatureTypeByName( String ftName ){
        return (WFSFeatureType)ftNameToWfsFT.get( ftName );
    }
    
    /**
     * guess which property might be "the" geometry property
     * @param propNames
     * @return
     */
    protected QualifiedName[] guessGeomProperty2( GMLSchema schema ){

        QualifiedName[] geoPropNames = null;
        List tmpList = new ArrayList( 20 );
        
        
        FeatureType[] fts = schema.getFeatureTypes();
        for ( int i = 0; i < fts.length; i++ ) {
            PropertyType[] props = fts[i].getProperties();
            for ( int j = 0; j < props.length; j++ ) {

                if( props[j].getType() == Types.GEOMETRY ){
                    tmpList.add( props[j].getName() );
                    
                }
            }
        }

        geoPropNames = 
            (QualifiedName[])tmpList.toArray( new QualifiedName[ tmpList.size() ] );
        
        return geoPropNames;
    }
    
    protected static QualifiedName[] guessGeomProperty( GMLSchema schema ){

        QualifiedName[] geoPropNames = null;
        List tmpList = new ArrayList( 20 );
        
        FeatureType[] fts = schema.getFeatureTypes();
        for ( int i = 0; i < fts.length; i++ ) {
            PropertyType[] props = fts[i].getProperties();
            for ( int j = 0; j < props.length; j++ ) {
                if( props[j].getType() == Types.GEOMETRY || props[j].getType() == 10014 ){
                    tmpList.add( props[j].getName() );
                    
                }
            }
        }

        geoPropNames = 
            (QualifiedName[])tmpList.toArray( new QualifiedName[ tmpList.size() ] );
        
        return geoPropNames;
    }    
    
    public QualifiedName[] getGeometryProperties(String featureType) {
        return (QualifiedName[])this.geoPropsNameToQNames.get( featureType );
    }
    
    protected void createHttpClient(){
        httpClient = new HttpClient();
        
        HttpClientParams clientPars = new HttpClientParams();
        clientPars.setConnectionManagerTimeout( 60000 );
        
        httpClient.setParams( clientPars );
        
    }
    
    protected HttpMethod createHttpMethod( String methodName ){
        
        HttpMethod httpMethod = null;
        
        if( "HTTP_GET".equals( methodName ) ){
            httpMethod = new GetMethod();
        } else if( "HTTP_POST".equals( methodName ) ){
            httpMethod = new PostMethod();
        } else {
            throw new IllegalArgumentException( "method mame must be either 'HTTP_GET' or 'HTTP_POST'" );
        }
        
        return httpMethod;
    }    
}

/* ********************************************************************
Changes to this class. What the people have been up to:

$Log$
Revision 1.8  2007/05/14 10:07:00  taddei
removed println

Revision 1.7  2007/05/14 09:05:23  taddei
attempt to fix problem of DescribeFeatureType request not being loaded through the proxy.

Revision 1.6  2007/05/14 08:50:53  taddei
Fix for the problems of null prefixes and false namespaces of misbehaving WFS

Revision 1.5  2007/05/10 07:36:45  taddei
Added hack for reading gml:GeometryAssociationProperty from 2.1.2 schemas

Revision 1.4  2007/05/02 13:50:11  taddei
fixed URL problem when loading schema.

Revision 1.3  2007/05/02 13:27:11  taddei
Use now WFSFeatureType instead of QualifiedName.

Revision 1.2  2007/04/27 13:07:12  taddei
added wfs prefix.

Revision 1.1  2007/04/26 09:19:26  taddei
Added initial working version of classes and complementary files.

********************************************************************** */