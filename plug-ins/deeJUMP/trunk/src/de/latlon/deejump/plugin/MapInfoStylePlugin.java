//$HeadURL: https://sushibar/svn/deegree/base/trunk/resources/eclipse/svn_classfile_header_template.xml $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package de.latlon.deejump.plugin;

import static com.vividsolutions.jump.workbench.ui.MenuNames.LAYER;
import static com.vividsolutions.jump.workbench.ui.plugin.PersistentBlackboardPlugIn.get;
import static de.latlon.deejump.i18n.I18N.get;
import static java.awt.Color.decode;
import static java.lang.Integer.parseInt;
import static java.lang.System.out;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static org.deegree.framework.log.LoggerFactory.getLogger;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;

import org.deegree.framework.log.ILogger;
import org.deegree.io.mapinfoapi.MIFStyle2SLD;
import org.deegree.io.mapinfoapi.MapInfoReader;

import com.vividsolutions.jump.util.Blackboard;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.FillPatternFactory;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;

import de.latlon.deejump.plugin.style.BitmapVertexStyle;

/**
 * <code>MapInfoStylePlugin</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 */
public class MapInfoStylePlugin extends AbstractPlugIn {

    private static final ILogger LOG = getLogger( MapInfoStylePlugin.class );

    private static File POINTS;

    static {
        try {
            POINTS = File.createTempFile( "points", ".zip" );
            POINTS.deleteOnExit();

            InputStream in = MIFStyle2SLD.class.getResourceAsStream( "points.zip" );
            FileOutputStream out = new FileOutputStream( POINTS );

            byte[] buf = new byte[16384];

            int read;
            if ( in != null ) {
                while ( ( read = in.read( buf ) ) != -1 ) {
                    out.write( buf, 0, read );
                }
                in.close();
            }
            out.close();
        } catch ( IOException e ) {
            LOG.logError( "Could not find the points zip file", e );
        }
    }

    @Override
    public void initialize( PlugInContext context ) {
        EnableCheckFactory enableCheckFactory = new EnableCheckFactory( context.getWorkbenchContext() );

        MultiEnableCheck enableCheck = new MultiEnableCheck();
        enableCheck.add( enableCheckFactory.createWindowWithLayerManagerMustBeActiveCheck() );
        enableCheck.add( enableCheckFactory.createExactlyNLayerablesMustBeSelectedCheck( 1, Layerable.class ) );

        context.getFeatureInstaller().addMainMenuItem( this, new String[] { LAYER },
                                                       get( "MapInfoStylePlugin.name" ) + "{pos:13}", false, null,
                                                       enableCheck );
    }

    private static void applySymbolStyle( HashMap<String, String> map, Layer layer )
                            throws IOException {
        VertexStyle style = null;
        Color c = decode( map.get( "color" ) );
        int size = parseInt( map.get( "size" ) );
        int symbol = parseInt( map.get( "shape" ) );

        File tmp = File.createTempFile( "mapinfosymbol", null );

        // if it's not found in the zip file, or no zip file, well, too bad...
        ZipFile zip = new ZipFile( POINTS );
        ZipEntry entry = zip.getEntry( symbol + ".svg" );
        InputStream in = zip.getInputStream( entry );
        FileOutputStream out = new FileOutputStream( tmp );
        tmp.deleteOnExit();

        byte[] buf = new byte[16384];
        int read;
        while ( ( read = in.read( buf ) ) != -1 ) {
            out.write( buf, 0, read );
        }

        out.close();
        in.close();
        zip.close();

        style = new BitmapVertexStyle( tmp.toString() );
        style.setSize( size );
        style.setFillColor( c );
        style.setLineColor( c );

        if ( style != null ) {
            style.setEnabled( true );
            layer.removeStyle( layer.getVertexStyle() );
            layer.addStyle( style );
            layer.getBasicStyle().setEnabled( false );
        }
    }

    private static void applyPenStyle( HashMap<String, String> map, Layer layer ) {
        BasicStyle style = layer.getBasicStyle();
        int pattern = parseInt( map.get( "pattern" ) );
        Color c = decode( map.get( "color" ) );
        int width = parseInt( map.get( "width" ) );
        style.setLineWidth( width );
        style.setLineColor( c );
        style.setRenderingLine( true );
        style.setRenderingLinePattern( true );
        switch ( pattern ) {
        case 1:
            style.setRenderingLine( false );
            break;
        case 2:
            style.setRenderingLinePattern( false );
            break;
        case 3:
            style.setLinePattern( "1,1" );
            break;
        case 4:
            style.setLinePattern( "2,2" );
            break;
        case 5:
            style.setLinePattern( "3,1" );
            break;
        case 6:
            style.setLinePattern( "5,1" );
            break;
        case 7:
            style.setLinePattern( "10,3" );
            break;
        case 8:
            style.setLinePattern( "20,5" );
            break;
        case 9:
            style.setLinePattern( "5,5" );
            break;
        case 10:
            style.setLinePattern( "1,5" );
            break;
        case 11:
            style.setLinePattern( "3,5" );
            break;
        case 12:
            style.setLinePattern( "7,7" );
            break;
        case 13:
            style.setLinePattern( "10,10" );
            break;
        case 14:
            style.setLinePattern( "9,3,1,3" );
            break;
        case 15:
            style.setLinePattern( "12,2,1,2" );
            break;
        case 16:
            style.setLinePattern( "12,2,2,2" );
            break;
        case 17:
            style.setLinePattern( "20,10,5,10" );
            break;
        case 18:
            style.setLinePattern( "20,4,4,4,4,4" );
            break;
        case 19:
            style.setLinePattern( "20,4,4,4,4,4,4,4" );
            break;
        case 20:
            style.setLinePattern( "9,3,1,3,1,3" );
            break;
        case 21:
            style.setLinePattern( "12,3,1,3,1,3" );
            break;
        case 22:
            style.setLinePattern( "12,3,1,3,1,3,1,3" );
            break;
        case 23:
            style.setLinePattern( "5,1,1,1" );
            break;
        case 24:
            style.setLinePattern( "5,1,1,1,1,1" );
            break;
        case 25:
            style.setLinePattern( "9,1,1,1,3,1,1,1" );
            break;
        default:
            LOG.logWarning( "Ignoring fancy pen style, as OpenJUMP cannot display it." );
            style.setRenderingLinePattern( false );
            break;
        }
    }

    private void applyBrushStyle( HashMap<String, String> map, Layer layer ) {
        BasicStyle style = layer.getBasicStyle();
        int pattern = parseInt( map.get( "pattern" ) );
        Color fore = decode( map.get( "forecolor" ) );

        style.setRenderingFillPattern( true );
        style.setRenderingFill( true );
        style.setFillColor( fore );

        Paint[] patterns = new FillPatternFactory().createFillPatterns();

        // the mapping could be better, but the better way is to use direct MIF2SLD export from
        // deegree anyway
        switch ( pattern ) {
        case 1:
            style.setRenderingFillPattern( false );
            style.setRenderingFill( false );
            break;
        case 2:
            style.setRenderingFillPattern( false );
            break;
        case 3:
            style.setFillPattern( patterns[19] );
            break;
        case 4:
            style.setFillPattern( patterns[25] );
            break;
        case 5:
            style.setFillPattern( patterns[7] );
            break;
        case 6:
            style.setFillPattern( patterns[1] );
            break;
        case 7:
            style.setFillPattern( patterns[31] );
            break;
        case 8:
            style.setFillPattern( patterns[13] );
            break;
        case 12:
            // style.setFillPattern( patterns[] );
            break;
        case 13:
            // style.setFillPattern( patterns[] );
            break;
        case 14:
            // style.setFillPattern( patterns[] );
            break;
        case 15:
            // style.setFillPattern( patterns[] );
            break;
        case 16:
            // style.setFillPattern( patterns[] );
            break;
        case 17:
            // style.setFillPattern( patterns[] );
            break;
        case 18:
            // style.setFillPattern( patterns[] );
            break;
        case 23:
            style.setFillPattern( patterns[18] );
            break;
        // WKTFillPattern.createVerticalHorizontalStripePattern(2, 4,true, false ).createImage( )
        case 24:
            // style.setFillPattern( );
            break;
        case 25:
            // style.setFillPattern( patterns[] );
            break;
        case 26:
            // style.setFillPattern( patterns[] );
            break;
        case 27:
            // style.setFillPattern( patterns[] );
            break;
        case 28:
            // style.setFillPattern( patterns[] );
            break;
        case 29:
            // style.setFillPattern( patterns[] );
            break;
        }
    }

    @Override
    public boolean execute( PlugInContext context )
                            throws Exception {
        Blackboard bb = get( context.getWorkbenchContext() );

        String fileName = (String) bb.get( "MapInfoStylePlugin.filename" );

        JFileChooser chooser = new JFileChooser();
        if ( fileName != null ) {
            chooser.setCurrentDirectory( new File( fileName ).getParentFile() );
        }
        int res = chooser.showOpenDialog( context.getWorkbenchFrame() );
        if ( res == APPROVE_OPTION ) {
            File f = chooser.getSelectedFile();
            bb.put( "MapInfoStylePlugin.filename", f.getAbsoluteFile().toString() );
            Layer l = context.getSelectedLayer( 0 );

            MapInfoReader reader = new MapInfoReader( f.getAbsolutePath() );
            reader.parseFeatures();

            HashMap<String, HashSet<HashMap<String, String>>> styles = reader.getStyles();

            for ( String type : styles.keySet() ) {
                if ( "symbol".equals( type ) ) {
                    for ( HashMap<String, String> map : styles.get( type ) ) {
                        out.println( "could use this symbol:" + map );
                        applySymbolStyle( map, l );
                    }
                }

                if ( "pen".equals( type ) ) {
                    for ( HashMap<String, String> map : styles.get( type ) ) {
                        out.println( "could use this pen:" + map );
                        applyPenStyle( map, l );
                    }
                }

                if ( "brush".equals( type ) ) {
                    for ( HashMap<String, String> map : styles.get( type ) ) {
                        out.println( "could use this brush:" + map );
                        applyBrushStyle( map, l );
                    }
                }

                l.fireAppearanceChanged();
            }

        }

        return false;
    }

    @Override
    public String getName() {
        return get( "MapInfoStylePlugin.name" );
    }

}
