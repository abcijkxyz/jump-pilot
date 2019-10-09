package org.openjump.ojworldwind;

import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.ShapefileWriter;
import com.vividsolutions.jump.io.datasource.DataSource;
import com.vividsolutions.jump.io.geojson.GeoJSONConstants;
import com.vividsolutions.jump.io.geojson.GeoJSONFeatureCollectionWrapper;
import com.vividsolutions.jump.util.FileUtil;
import com.vividsolutions.jump.workbench.imagery.ReferencedImagesLayer;
import com.vividsolutions.jump.workbench.model.Layerable;
import de.latlon.deejump.plugin.style.BitmapVertexStyle;
import com.vividsolutions.jump.workbench.Logger;

import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.data.BufferedImageRaster;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.DataRasterReader;
import gov.nasa.worldwind.data.DataRasterReaderFactory;
import gov.nasa.worldwind.formats.shapefile.ShapefileLayerFactory;
import gov.nasa.worldwind.formats.shapefile.ShapefileRenderable;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.util.ExampleUtil;
import org.openjump.ojworldwind.ww.GeoJSONLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;


import java.util.List;
import java.util.Set;

public class LayerConverter {
    protected Sector sector;

    public LayerConverter() {
    }

    /**
     * Converts the given array of OJ Layerable into a List of WW Layer.
     * Currently supports Vector layer, ReferencedImagesLayer, WMSLayer
     * @param ojLayers layers to convert
     * @return list of converted WW layers
     * @throws Exception
     */
    public List<Layer> convert(Layerable[] ojLayers) throws Exception {
        ArrayList<Layer> wwLayers = new ArrayList<>();

        if (ojLayers != null && ojLayers.length > 0) {
            for (Layerable ojLayer : ojLayers) {
                wwLayers.add(this.convert(ojLayer));
            }
        }

        return wwLayers;
    }

    public Layer convert(Layerable ojLayer) throws Exception {
        Layer l = null;

        if (ojLayer != null) {
            if (ojLayer instanceof ReferencedImagesLayer) {
                l = this.makeImageLayers((ReferencedImagesLayer)ojLayer);
            } else if (ojLayer instanceof com.vividsolutions.jump.workbench.model.WMSLayer) {
                l = this.makeWmsLayers(
                        (com.vividsolutions.jump.workbench.model.WMSLayer)ojLayer);
            } else {
                // which format converted to use
                if (WorldWindOptionsPanel.getConverterFormat().equals(WorldWindOptionsPanel.GEOJSON)) {
                    l = this.makeGeojsonLayers(
                            (com.vividsolutions.jump.workbench.model.Layer)ojLayer);
                    this.styleLayer(l, (com.vividsolutions.jump.workbench.model.Layer)ojLayer);
                } else {
                    Logger.info("LayerConverter: using shapefile format converter");
                    l = this.makeShapefileLayers(
                            (com.vividsolutions.jump.workbench.model.Layer)ojLayer);
                }
            }
            l.setName(ojLayer.getName());
            // TODO: from OJ conf
            l.setPickEnabled(true);
            Logger.info(String.format("converting OJ Layer: %s to WW layer: %s",
                    ojLayer.getClass().getName(),
                    l.getClass().getName()));
        }
        return l;
    }

    /**
     * returns a WW Layer from the given OpenJump WMSLayer
     * TODO: better mechanism: currently read the capabilities and gets a named layer from it
     * From WW examples
     * @param wmsLayer the OJ WMS Layer to convert
     * @return a WW WMS Layer built from given WMSLayer
     */
    protected Layer makeWmsLayers(
            com.vividsolutions.jump.workbench.model.WMSLayer wmsLayer) {

        Layer ret = null;
        WMSCapabilities caps;

        try {
            URI serverURI = new URI(wmsLayer.getServerURL());
            caps = WMSCapabilities.retrieve(serverURI);
            caps.parse();

            // Gather up all the named layers and make a world wind layer for each.
            final WMSLayerCapabilities namedLayerCaps = caps.getLayerByName(wmsLayer.getName());
            if (namedLayerCaps != null) {
                // build component layer from capa:
                AVListImpl params = new AVListImpl();
                params.setValue(AVKey.LAYER_NAMES, namedLayerCaps.getName());
                //TODO: handle styles
                String abs = namedLayerCaps.getLayerAbstract();
                if (!WWUtil.isEmpty(abs)) {
                    params.setValue(AVKey.LAYER_ABSTRACT, abs);
                }
                params.setValue(AVKey.DISPLAY_NAME, wmsLayer.getName());

                // Some wms servers are slow, so increase the timeouts and limits used by world wind's retrievers.
                params.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
                params.setValue(AVKey.URL_READ_TIMEOUT, 30000);
                params.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);
                String factoryKey = getFactoryKeyForCapabilities(caps);
                Factory factory = (Factory) WorldWind.createConfigurationComponent(factoryKey);
                Object component = factory.createFromConfigSource(caps, params);
                ret = (Layer) component;
            }
        } catch (Exception e) {
            Logger.error(String.format("LayerConverter: exception when converting to WMS layer: %s",
                    e.getMessage()));
        }
        return ret;
    }

    /**
     * builds a list of layers from this.ojLayers, converting it to geojson
     * this.ojLayers is FeatureCollection here.
     *
     * @return
     */
    protected Layer makeGeojsonLayers(
            com.vividsolutions.jump.workbench.model.Layer jsonLayer) {

        Layer ret = null;

        // TODO: writes json into a writer and pipes it to WW geojson loader
        // did not succeed with piped stream
        // Height: from OJ user conf
        String height = WorldWindOptionsPanel.getHeightAttributeName();
        GeoJSONLoader loader = new GeoJSONLoader(height);
        Writer w = null;
        FileOutputStream fileStream = null;
        FileInputStream f = null;
        try {
            // works with file if closed before calling read
            GeoJSONFeatureCollectionWrapper fcw = new GeoJSONFeatureCollectionWrapper(
                    (FeatureCollection) jsonLayer.getFeatureCollectionWrapper().getWrappee());
            File tmpFile = File.createTempFile("layername", ".geojson");
            fileStream = new FileOutputStream(tmpFile);
            w = new OutputStreamWriter(fileStream, GeoJSONConstants.CHARSET);
            fcw.writeJSONString(w);
            FileUtil.close(w);
            FileUtil.close(fileStream);

            Logger.info(String.format("OJ layer written to JSON file: %s", tmpFile.getAbsolutePath()));

            f = new FileInputStream(tmpFile.getAbsoluteFile());
            ret = loader.createLayerFromSource(f);

            // style layer according to input ojLayers
            // TODO: this.ojLayers: have to be an array of layers.
            this.styleLayer(ret, jsonLayer);
            // delete tmp file
            tmpFile.delete();
        } catch (Exception e) {
            Logger.error(String.format("LayerConverter: exception when converting to GeoJSON layer: %s",
                    e.getMessage()));
        } finally{
            FileUtil.close(w);
            FileUtil.close(fileStream);
            FileUtil.close(f);
        }
        return ret;
    }

    protected Layer makeShapefileLayers(
            com.vividsolutions.jump.workbench.model.Layer shpLayer) {

        Layer ret = null;
        // TODO: shapefile in memory ? zip stream ?
        ShapefileWriter writer = new ShapefileWriter();
        DriverProperties dp = new DriverProperties();
        try {
            final File tmpFile = File.createTempFile("layername", ".shp");
            dp.set(DataSource.FILE_KEY, tmpFile.getAbsolutePath());
            writer.write(shpLayer.getFeatureCollectionWrapper(), dp);

            ShapefileLayerFactory factory = new ShapefileLayerFactory();
            ret = (Layer)factory.createFromShapefileSource(tmpFile, new ShapefileLayerFactory.CompletionCallback() {
                @Override
                public void completion(Object result) {
                    final Layer layer = (Layer) result; // the result is the layer the factory created

                    // Add the layer to the World Window's layer list on the Event Dispatch Thread.
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run(){
                            styleLayer(layer, shpLayer);
                              //TODO: Does not work: file must exists
//                            if (!tmpFile.delete()) {
//                                Logger.error(String.format("LayerConverter cannot delete tmp file: %s",
//                                        tmpFile.getAbsolutePath()));
//                            }
                        }
                    });
                }

                @Override
                public void exception(Exception e) {
                    Logger.error(String.format("LayerConverter: exception when converting to shapefile layer: %s",
                            e.getMessage()));
                }
            });
        } catch (Exception e) {
            Logger.error(String.format("LayerConverter: exception when converting to shapefile layer: %s",
                    e.getMessage()));
        }
        return ret;
    }

    /**
     * From WW examples
     * @return
     * @throws Exception
     */
    protected Layer makeImageLayers(ReferencedImagesLayer imgLayer) throws Exception {
        SurfaceImageLayer ret;
        // gets file through first feature:
        Object url = imgLayer.getFeatureCollectionWrapper().getWrappee().getFeatures().get(0).getAttribute(1);

        File sourceFile = ExampleUtil.saveResourceToTempFile((String)url, ".tif");

        // Create a raster reader to read this type of file. The reader is created from the currently
        // configured factory. The factory class is specified in the Configuration, and a different one can be
        // specified there.
        DataRasterReaderFactory readerFactory
                = (DataRasterReaderFactory) WorldWind.createConfigurationComponent(
                AVKey.DATA_RASTER_READER_FACTORY_CLASS_NAME);
        DataRasterReader reader = readerFactory.findReaderFor(sourceFile, null);

        // Before reading the raster, verify that the file contains imagery.
        AVList metadata = reader.readMetadata(sourceFile, null);
        if (metadata == null || !AVKey.IMAGE.equals(metadata.getStringValue(AVKey.PIXEL_FORMAT)))
            throw new Exception("Not an image file.");

        // Read the file into the raster. read() returns potentially several rasters if there are multiple
        // files, but in this case there is only one so just use the first element of the returned array.
        DataRaster[] rasters = reader.read(sourceFile, null);
        if (rasters == null || rasters.length == 0)
            throw new Exception("Can't read the image file.");

        DataRaster raster = rasters[0];

        // Determine the sector covered by the image. This information is in the GeoTIFF file or auxiliary
        // files associated with the image file.
        this.sector = (Sector) raster.getValue(AVKey.SECTOR);
        if (sector == null)
            throw new Exception("No location specified with image.");

        // Request a sub-raster that contains the whole image. This step is necessary because only sub-rasters
        // are reprojected (if necessary); primary rasters are not.
        int width = raster.getWidth();
        int height = raster.getHeight();

        // getSubRaster() returns a sub-raster of the size specified by width and height for the area indicated
        // by a sector. The width, height and sector need not be the full width, height and sector of the data,
        // but we use the full values of those here because we know the full size isn't huge. If it were huge
        // it would be best to get only sub-regions as needed or install it as a tiled image layer rather than
        // merely import it.
        DataRaster subRaster = raster.getSubRaster(width, height, sector, null);

        // Tne primary raster can be disposed now that we have a sub-raster. Disposal won't affect the
        // sub-raster.
        raster.dispose();

        // Verify that the sub-raster can create a BufferedImage, then create one.
        if (!(subRaster instanceof BufferedImageRaster))
            throw new Exception("Cannot get BufferedImage.");
        BufferedImage image = ((BufferedImageRaster) subRaster).getBufferedImage();

        // The sub-raster can now be disposed. Disposal won't affect the BufferedImage.
        subRaster.dispose();

        // Create a SurfaceImage to display the image over the specified sector.
        final SurfaceImage si1 = new SurfaceImage(image, sector);

        ret = new SurfaceImageLayer();
        ret.addRenderable(si1);
        return ret;
    }

    /**
     * Applies ojLayer style to wwLayer
     * @param wwLayer
     * @param ojLayer
     */
    protected void styleLayer(Layer wwLayer,
                              com.vividsolutions.jump.workbench.model.Layer ojLayer) {

        RenderableLayer rl = (RenderableLayer)wwLayer;

        // prepare different styles for point and surface/line
        // TODO: proper style converter (labels, symbols, etc.)
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(new Material(ojLayer.getBasicStyle().getFillColor()));
        attrs.setInteriorOpacity(ojLayer.getBasicStyle().getAlpha() / 255.0);

        attrs.setOutlineMaterial(new Material(ojLayer.getBasicStyle().getLineColor()));
        //attrs.setOutlineOpacity(ojLayer.getBasicStyle().getAlpha() / 255.0);
        attrs.setOutlineWidth(ojLayer.getBasicStyle().getLineStroke().getLineWidth() * 2.0);
        attrs.setEnableAntialiasing(true);
        attrs.setDrawInterior(true);
        attrs.setDrawOutline(true);
        attrs.setEnableLighting(true);

        PointPlacemarkAttributes ptAttrs = new PointPlacemarkAttributes();
        ptAttrs.setLineMaterial(new Material(ojLayer.getBasicStyle().getFillColor()));
        ptAttrs.setLineWidth(ojLayer.getBasicStyle().getLineWidth() * 2.0);
        // force image to be null in order to display a point symbol
        ptAttrs.setDrawImage(true);
        ptAttrs.setUsePointAsDefaultImage(true);
        ptAttrs.setImage(null);
        if (ojLayer.getVertexStyle() != null) {
            // scale for pointsymbol is the size in pixel:
            ptAttrs.setScale((double)ojLayer.getVertexStyle().getSize());
            // TODO: symbol point
            if (ojLayer.getVertexStyle() instanceof BitmapVertexStyle) {
                Image img = ((BitmapVertexStyle)ojLayer.getVertexStyle()).getImage();
                BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D bGr = bimage.createGraphics();
                bGr.drawImage(img, 0, 0, null);
                bGr.dispose();
                ptAttrs.setImage(bimage);
                ptAttrs.setDrawImage(true);
            }
        }
        if (ojLayer.getLabelStyle() != null) {
            // TODO label
            ptAttrs.setDrawLabel(true);
            ptAttrs.setLabelFont(ojLayer.getLabelStyle().getFont());
            ptAttrs.setLabelMaterial(new Material(ojLayer.getLabelStyle().getColor()));
        }


        // Apply style to all renderables : todo: right way ?
        rl.getRenderables().forEach((renderable)->{
            if (renderable instanceof AbstractSurfaceShape) {
                ((AbstractSurfaceShape)renderable).setAttributes(attrs);
            } else if (renderable instanceof AbstractShape) {
                ((AbstractShape) renderable).setAttributes(attrs);
            } else if (renderable instanceof ShapefileRenderable) {
                ShapefileRenderable shpr = (ShapefileRenderable) renderable;
                for (int i = 0; i < shpr.getRecordCount(); i++) {
                    shpr.getRecord(i).setAttributes(attrs);
                }
            } else if (renderable instanceof PointPlacemark) {
                ((PointPlacemark) renderable).setAttributes(ptAttrs);
            } else {
                Logger.error(String.format("LayerConverter: renderable class not handled: %s",
                        renderable.getClass().getName()));
            }
        });
    }

    protected static String getFactoryKeyForCapabilities(WMSCapabilities caps) {
        boolean hasApplicationBilFormat = false;

        Set<String> formats = caps.getImageFormats();
        for (String s : formats)
        {
            if (s.contains("application/bil"))
            {
                hasApplicationBilFormat = true;
                break;
            }
        }
        return hasApplicationBilFormat ? AVKey.ELEVATION_MODEL_FACTORY : AVKey.LAYER_FACTORY;
    }
}
