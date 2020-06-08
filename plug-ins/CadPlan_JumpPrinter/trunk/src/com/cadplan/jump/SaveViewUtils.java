package com.cadplan.jump;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.MenuElement;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.OKCancelDialog;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.plugin.FeatureInstaller;
import com.vividsolutions.jump.workbench.ui.renderer.LayerRenderer;
import com.vividsolutions.jump.workbench.ui.renderer.Renderer;
import com.vividsolutions.jump.workbench.ui.renderer.RenderingManager;
import com.vividsolutions.jump.workbench.ui.renderer.java2D.Java2DConverter;

public class SaveViewUtils {


	static WorkbenchContext wContext = JUMPWorkbench.getInstance().getContext();

	public static  void saveAsPDF(LayerViewPanel lvp,Envelope envelope, int extentInPixels, File file) 
			throws NoninvertibleTransformException {

		int extentInPixelsX;
		int extentInPixelsY;
		double width = envelope.getWidth();
		double height = envelope.getHeight();
		if (width > height)
		{
			extentInPixelsX = extentInPixels;
			extentInPixelsY = (int)Math.round(height / width * extentInPixels);
		}
		else
		{
			extentInPixelsY = extentInPixels;
			extentInPixelsX = (int)Math.round(width / height * extentInPixels);
		}
		lvp.setSize(extentInPixelsX, extentInPixelsY);
		lvp.getViewport().zoom(envelope);

		double xoffset = 50.0; //pageFormat.getImageableX();
		double yoffset = 50.0; //pageFormat.getImageableY();
		double xsize = 2*xoffset + lvp.getWidth();//imageSize.width;
		double ysize = 2*yoffset + lvp.getHeight();
		try
		{
			com.lowagie.text.Rectangle pageSize = new com.lowagie.text.Rectangle((float)xsize, (float)ysize);
			//System.out.println("xoffset="+xoffset+ "  yoffset="+yoffset+"  xsize="+xsize+"  ysize="+ysize);
			com.lowagie.text.Document document = new com.lowagie.text.Document(pageSize);
			document.addCreator("OpenJUMP Print PDF");
			document.addTitle(file.getName());
			document.addAuthor("Giuseppe Aruta giuseppe_aruta@yahoo.it");
			document.addSubject("PDF file");
			//com.itextpdf.text.Rectangle pages = document.getPageSize();
			//System.out.println("Page size = "+pages);
			try
			{
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
				document.open();
				PdfContentByte cb = writer.getDirectContent();
				//PdfTemplate tp = cb.createTemplate((int)xsize,(int)ysize);
				DefaultFontMapper fontMapper = new DefaultFontMapper();
				//fontMapper.insertDirectory("c:/windows/fonts");


				Graphics2D graphics2D = cb.createGraphics((int)xsize,(int)ysize, fontMapper);
				graphics2D.translate(xoffset,yoffset);
				lvp.paint(graphics2D);
				graphics2D.dispose();
			}
			catch(Exception ex)
			{
				System.out.println("ERROR creating pdf: "+ex);
				ex.printStackTrace();
			}
			document.close();
		}
		catch (Exception ex)
		{
			System.out.println("ERROR: "+ex);
			JOptionPane.showMessageDialog(null,"To save images as PDF files, the iText library must be available","Error...",
					JOptionPane.ERROR_MESSAGE);
		}




	}


	public static  void saveAsSVG(LayerViewPanel lvp, Envelope envelope, int extentInPixels,
			File selFile) throws Exception {
		int extentInPixelsX;
		int extentInPixelsY;
		double width = envelope.getWidth();
		double height = envelope.getHeight();
		if (width > height)
		{
			extentInPixelsX = extentInPixels;
			extentInPixelsY = (int)Math.round(height / width * extentInPixels);
		}
		else
		{
			extentInPixelsY = extentInPixels;
			extentInPixelsX = (int)Math.round(width / height * extentInPixels);
		}
		lvp.setSize(extentInPixelsX, extentInPixelsY);
		lvp.getViewport().zoom(envelope);

		// Get a DOMImplementation
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document
		Document document = domImpl.createDocument(null, "svg", null);

		// Create an instance of the SVG Generator
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		// This prevents the
		// "null incompatible with text-specific antialiasing enable key" error
		svgGenerator.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		svgGenerator.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_DEFAULT);

		//Set Size
		svgGenerator.setSVGCanvasSize(lvp.getSize());

		// --- Test with changed classes of Openjump and the new maxFeatures
		// field in FeatureCollectionRenderer.class


		RenderingManager rms = lvp.getRenderingManager();
		List<Layer> layers = wContext.getLayerManager().getVisibleLayers(false);
		//List<Layer> layers = context.getLayerManager().getVisibleLayers(false);
		// Check if there are many features to draw and warn the user
		int totalNumberOfFeatures = 0;
		Envelope view = wContext.getLayerViewPanel().getViewport()
				.getEnvelopeInModelCoordinates();

		//	Envelope view = context.getLayerViewPanel().getViewport()
		//			.getEnvelopeInModelCoordinates();

		for (Layer layer : layers) {
			FeatureCollection fc = layer.getFeatureCollectionWrapper();
			totalNumberOfFeatures += fc.query(view).size();

		}
		if (totalNumberOfFeatures > 100000) {
			JTextArea labelArea = new JTextArea();
			labelArea.setEditable(false);
			labelArea.setOpaque(false);
			labelArea.setFont(new JLabel().getFont());
			labelArea
			.setText(I18N
					.get("org.openjump.core.ui.plugin.file.SaveImageAsSVGPlugIn.large-dataset-message"));
			OKCancelDialog dialog = new OKCancelDialog(
					wContext.getWorkbench().getFrame(),
					I18N.get("org.openjump.core.ui.plugin.file.SaveImageAsSVGPlugIn.warning-message-title"),
					true, labelArea, null);
			dialog.setVisible(true);
			if (!dialog.wasOKPressed())
				return;
		}
		for (Layer layer : layers) {
			Renderer myR = rms.getRenderer(layer);
			if (myR instanceof LayerRenderer) {
				LayerRenderer myRnew = (LayerRenderer) myR;
				myRnew.setMaxFeatures(10000000);
			}
		}

		// Change drawing resolution to print to svg (0.5 pixel to 0.1 pixel)
		Viewport viewport = lvp.getViewport();
		Java2DConverter oldConverter = viewport.getJava2DConverter();
		viewport.setJava2DConverter(new Java2DConverter(viewport, 0.001));

		//svgGenerator.scale(0.746, 0.746); // rapport pour LibreOffice (0.72/0.96)
		svgGenerator.scale(0.90/0.96, 0.90/0.96); // rapport pour Inkscape
		lvp.paintComponent(svgGenerator);

		// Restore previous rendering resolution
		lvp.getViewport().setJava2DConverter(oldConverter);
		// ------------------------------
		// reset the old state of 100 features
		for (Layer layer : layers) {
			Renderer myR = rms.getRenderer(layer);
			if (myR instanceof LayerRenderer) {
				LayerRenderer myRnew = (LayerRenderer) myR;
				myRnew.setMaxFeatures(100);
			}
		}
		// ------------------------------

		// Finally, stream out SVG to the your file
		// Writer out = new FileWriter("MyMoMap.svg");
		// FileWriter out = new FileWriter(selFile);
		try {
			FileOutputStream fos = new FileOutputStream(selFile, false);
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			svgGenerator.stream(out, true);
			out.close();
		}
		catch (Exception e) {
			wContext.getWorkbench().getFrame().handleThrowable(e);
		}


	}

	public static void saveAsRaster(RenderedImage image, String format, File file)
			throws IOException {
		boolean writerFound = ImageIO.write(image, format, file);
		Assert.isTrue( writerFound, I18N.get("ui.plugin.SaveImageAsPlugIn.cannot-find-writer-for-image-format")+" '"
				+ format + "'");
	}



	public static void removeMainMenuItem(String[] menuPath, String menuItemName) {

		JMenu parentMenu = FeatureInstaller.getInstance().menuBarMenu(menuPath[0]);
		if (menuPath.length > 1)
			for (int i = 1; i < menuPath.length; i++) {
				String menuPathName = menuPath[i];
				FeatureInstaller.getInstance();
				parentMenu = (JMenu)  childMenuItem(menuPathName,  parentMenu);
			}  
		JMenuItem item =   childMenuItem(menuItemName,  parentMenu);
		if (item != null)
			parentMenu.remove(item); 
		if (parentMenu.getPopupMenu() != null && (
				parentMenu.getPopupMenu().getSubElements()).length == 0)
			parentMenu.getParent().remove(parentMenu); 
	}


	public static void removeMenu(String[] menuPath) {
		JMenu parentMenu = FeatureInstaller.getInstance().menuBarMenu(menuPath[0]);
		if (menuPath.length > 1)
			for (int i = 1; i < menuPath.length; i++) {
				String menuPathName = menuPath[i];
				parentMenu = (JMenu)  childMenuItem(menuPathName,  parentMenu);
			}  
		parentMenu.removeAll();
		//parentMenu.getParent().remove(parentMenu);
		//FeatureInstaller.getInstance().menuBar().remove(parentMenu);
	}

	// Copied from Kosmo FeatureInstaller.class

	public static JMenuItem childMenuItem(String childName, MenuElement menu) {
		if (menu instanceof JMenu)
			return childMenuItem(childName, ((JMenu)menu).getPopupMenu()); 
		MenuElement[] childMenuItems = menu.getSubElements();
		for (int i = 0; i < childMenuItems.length; i++) {
			if (childMenuItems[i] instanceof JMenuItem && (
					(JMenuItem)childMenuItems[i]).getText().equals(childName))
				return (JMenuItem)childMenuItems[i]; 
		} 
		return null;
	}


}
