package com.cadplan.jump;

import java.awt.Dimension;
import java.awt.FlowLayout;
//import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
//import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import org.openjump.core.apitools.IOTools;
import org.openjump.core.ui.plugin.file.LayerPrinter2;
import org.openjump.core.ui.plugin.file.WorldFileWriter;
import org.openjump.core.ui.util.ScreenScale;
import org.saig.core.gui.swing.sldeditor.util.FormUtils;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.plugin.ThreadedBasePlugIn;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.MenuNames;
import com.vividsolutions.jump.workbench.ui.ValidatingTextField;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;
import com.vividsolutions.jump.workbench.ui.plugin.PersistentBlackboardPlugIn;

/**
 * Save the view to a PNG or a JPG image file.
 * The exported image can have a size different from the original view.
 */
public class SaveViewPlugIn extends ThreadedBasePlugIn {
	//ImageIO doesn't know about the "gif" format. I guess it's a copyright
	// issue [Jon Aquino 11/6/2003]
	//Don't use TYPE_INT_ARGB for jpegs -- they will turn pink [Jon Aquino
	// 11/6/2003]
	//ImageIO can probably write gif images from java 6, but we do we really 
	// need that ? [mmichaud 2012-09-02]
	//Extended capability to export to SVG and PDF and 
	//to save images at defined scales. [Giuseppe Aruta 2020-06-07]

	private List<MyFileFilter> myFileFilters;
	//Arrays.asList(
	/*		createFileFilter("PNG - Portable Network Graphics", "png",
					BufferedImage.TYPE_INT_ARGB),
			createFileFilter("JPEG - Joint Photographic Experts Group", "jpg",
					BufferedImage.TYPE_INT_RGB));*/


	private JFileChooser fileChooser = null;
	private WorkbenchContext workbenchContext;
	private JCheckBox worldFileCheckBox = null;
	private final ImageIcon icon = IconLoader.icon("Box.gif");
	private Geometry fence = null;


	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton perLength = new JRadioButton();
	private JRadioButton perScale = new JRadioButton();
	private JFormattedTextField scaleField;

	static WorkbenchContext wContext = JUMPWorkbench.getInstance().getContext();

	public Icon getIcon() {
		return IconLoader.icon("Camera.gif");
	}

	private ValidatingTextField pixelSizeField = new ValidatingTextField("9999",5,
			new ValidatingTextField.Validator() {
		@Override
		public boolean isValid(String text) {
			if (text.length() == 0) {
				return true;
			}
			try {
				int i = Integer.parseInt(text);
				long maxMemory = Runtime.getRuntime().maxMemory();
				// max = 5000 px for 1 G
				// max = 10000 px for 4 G
				int maxSize = 5*(int)Math.sqrt(maxMemory/1000);
				return i <= maxSize;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	});



	@Override
	public String getName() {
		return I18N.get("ui.MenuNames.FILE.SAVEVIEW");
	}

	double oldHorizontalScale;


	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new GUIUtil.FileChooserWithOverwritePrompting() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public File getSelectedFile() {
					File file = super.getSelectedFile();
					return file == null ? null : new File(addExtension(file.getPath(),
							((MyFileFilter) getFileFilter()).getFormat()));
				}
			};
			fileChooser.setDialogTitle(getName());
			//Remove *.* [Jon Aquino 11/6/2003]
			GUIUtil.removeChoosableFileFilters(fileChooser);

			Map<String,MyFileFilter> formatToFileFilterMap = new HashMap<>();

			for (MyFileFilter fileFilter : myFileFilters) {
				fileChooser.addChoosableFileFilter(fileFilter);
				formatToFileFilterMap.put(fileFilter.getFormat(), fileFilter);
			}
			String lastFilename = (String) PersistentBlackboardPlugIn
					.get(workbenchContext).get(LAST_FILENAME_KEY);
			if (lastFilename != null) {
				fileChooser.setSelectedFile(new File(lastFilename));
			}
			fileChooser.setFileFilter(formatToFileFilterMap.get(
					PersistentBlackboardPlugIn.get(workbenchContext)
					.get(FORMAT_KEY, "png")));
			buttonGroup.add(perLength);
			buttonGroup.add(perScale);
			perScale.setSelected(true);
			pixelSizeField.setEnabled(false);
			perLength.setText(I18N.get("ui.plugin.SaveImageAsPlugIn.width-in-pixels"));
			perScale.setText(I18N.get("ui.WorkbenchFrame.scale")+" 1:");
			scaleField = new JFormattedTextField();
			Viewport port = wContext.getLayerViewPanel().getViewport();
			oldHorizontalScale = ScreenScale.getHorizontalMapScale(port);
			scaleField.setValue( Math.floor(oldHorizontalScale));
			Box box = new Box(BoxLayout.Y_AXIS);
			JPanel jPanelSize = new JPanel(new GridBagLayout());
			String size ="<html><font color=black size=3>"
					+ "<b>" + I18N.get("org.openjump.core.ui.plugin.raster.RasterImageLayerPropertiesPlugIn.dimension")+ "</b></html>";
			I18N.get("org.openjump.core.ui.plugin.raster.RasterImageLayerPropertiesPlugIn.dimension");
			FormUtils.addRowInGBL(jPanelSize, 0, 0, new JLabel(size),true,true);
			FormUtils.addRowInGBL(jPanelSize, 1, 0, perScale,scaleField );
			FormUtils.addRowInGBL(jPanelSize, 2, 0, perLength,pixelSizeField );
			JPanel jPanelWF   = new JPanel(new FlowLayout(FlowLayout.LEFT));
			worldFileCheckBox = new javax.swing.JCheckBox();
			worldFileCheckBox.setText(I18N.get("ui.plugin.SaveImageAsPlugIn.write-world-file"));
			if (fence != null){
				JLabel fenceIcon = new JLabel(icon);
				FormUtils.addRowInGBL(jPanelSize, 3, 0, fenceIcon, true, true);
			}
			jPanelWF.add(worldFileCheckBox);
			box.add(jPanelSize);
			box.add(jPanelWF);
			box.add(Box.createRigidArea(new Dimension(5,180)));
			fileChooser.setAccessory(box);
			perScale.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED){
						scaleField.setEnabled(true);
						// textField.setText("Enabled");
					}
					else if(e.getStateChange() == ItemEvent.DESELECTED){
						scaleField.setEnabled(false);
						// textField.setText("Disabled");
					}

					box.validate();
					box.repaint();
				}
			});
			perLength.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED){
						pixelSizeField.setEnabled(true);
						// textField.setText("Enabled");
					}
					else if(e.getStateChange() == ItemEvent.DESELECTED){
						pixelSizeField.setEnabled(false);
						// textField.setText("Disabled");
					}

					box.validate();
					box.repaint();
				}
			});
		}
		return fileChooser;
	}

	private int getSizeInPixel() {
		String text="";
		int pixelSize=0;
		if(perLength.isSelected()) {
			text = pixelSizeField.getText();
			try {
				pixelSize = Integer.parseInt(text);
			} catch (NumberFormatException e) {
				pixelSize = 800;  //some reasonable default
			}   	
		}
		else if(perScale.isSelected()) {
			int newScale = ((Number)scaleField.getValue()).intValue();
			int screenScale =  (int) Math.floor(oldHorizontalScale);
			int viewPanelWidth = wContext.getLayerViewPanel().getWidth();

			try {
				//pixelSize = (newScale/screenScale)*viewPanelWidth;
				pixelSize = (screenScale/newScale)*viewPanelWidth;
			} catch (NumberFormatException e) {
				pixelSize = 800;  //some reasonable default
			}  
		}
		return pixelSize;
	}

	private MyFileFilter createFileFilter(String description, String format,
			int bufferedmageType) {
		return new MyFileFilter(description, format);
	}


	private static class MyFileFilter extends FileFilter {
		private FileFilter fileFilter;
		private String format;
		MyFileFilter(String description, String format) {
			fileFilter = GUIUtil.createFileFilter(description,
					new String[]{format});
			this.format = format;
		}
		@Override
		public boolean accept(File f) {
			return fileFilter.accept(f);
		}

		@Override
		public String getDescription() {
			return fileFilter.getDescription();
		}

		public String getFormat() {
			return format;
		}
	}

	I18NPlug iPlug;
	private static final String FORMAT_KEY = "FORMAT";
	private static final String LAST_FILENAME_KEY = "LAST FILENAME";

	@Override
	public void initialize(PlugInContext context) throws Exception {
		super.initialize(context);
		iPlug = new I18NPlug("JumpPrinter","language.JumpPrinterPlugin");
		SaveViewUtils.removeMenu(new String[] {
				MenuNames.FILE, MenuNames.FILE_SAVEVIEW });
		context.getFeatureInstaller().addMainMenuPlugin(this, new String[] {
				MenuNames.FILE, MenuNames.FILE_SAVEVIEW },
				iPlug.get("JumpPrinter.Setup.SaveImage"),false,getIcon(),
				createEnableCheck(context.getWorkbenchContext()), 9);
	}


	boolean fenceFound;

	protected BufferedImage image(LayerViewPanel layerViewPanel) {
		//Don't use TYPE_INT_ARGB, which makes JPEGs pinkish (presumably because
		//JPEGs don't support transparency [Jon Aquino 11/6/2003]
		BufferedImage image = new BufferedImage(layerViewPanel.getWidth(),
				layerViewPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		layerViewPanel.paintComponent(image.getGraphics());
		return image;
	}


	@Override
	public boolean execute(PlugInContext context) throws Exception {
		this.workbenchContext = context.getWorkbenchContext();
		myFileFilters = new ArrayList<MyFileFilter>();
		myFileFilters.add(createFileFilter("PNG - Portable Network Graphics", "png",
				BufferedImage.TYPE_INT_ARGB));
		myFileFilters.add(createFileFilter("JPEG - Joint Photographic Experts Group", "jpg",
				BufferedImage.TYPE_INT_RGB));
		myFileFilters.add(createFileFilter("GeoTIFF - Tagged Image File Format", "tif",
				BufferedImage.TYPE_INT_ARGB));

		myFileFilters.add(createFileFilter("SVG - Scalable Vector Graphics", "svg",
				BufferedImage.TYPE_INT_ARGB));	

		myFileFilters.add(createFileFilter("PDF - Portable Document Format", "pdf",
				BufferedImage.TYPE_INT_ARGB));	
		fence = context.getLayerViewPanel().getFence();
		fenceFound = fence != null;
		if (fenceFound){
			pixelSizeField.setText("800");
		}
		else {
			pixelSizeField.setText(context.getLayerViewPanel().getWidth() + "");
		}
		if (JFileChooser.APPROVE_OPTION != getFileChooser()
				.showSaveDialog(context.getWorkbenchFrame())) {
			fileChooser = null; //rebuild next invocation
			return false;
		}
		return true;
	}





	@Override
	public void run(TaskMonitor monitor, PlugInContext context) throws Exception {
		monitor.allowCancellationRequests();
		monitor.report(I18N.get("ui.plugin.SaveDatasetAsPlugIn.saving"));
		MyFileFilter fileFilter = (MyFileFilter) getFileChooser().getFileFilter();
		BufferedImage image;
		LayerViewPanel viewPanel = context.getLayerViewPanel();
		Envelope envelope;
		Envelope envelope2 = new Envelope();
		if (!fenceFound && (getSizeInPixel() == context.getLayerViewPanel().getWidth())) {
			image = image(viewPanel);  
			envelope2 = workbenchContext.getLayerViewPanel()
					.getViewport().getEnvelopeInModelCoordinates();
		}
		else {
			LayerPrinter2 layerPrinter = new LayerPrinter2();
			if (fenceFound)
			{
				envelope = fence.getEnvelopeInternal(); 
				String fenceLayerName = I18N.get("model.FenceLayerFinder.fence");
				Layer fenceLayer = workbenchContext.getLayerableNamePanel()
						.getLayerManager().getLayer(fenceLayerName);
				fenceLayer.setVisible(false);
			}
			else {
				envelope = workbenchContext.getLayerViewPanel()
						.getViewport().getEnvelopeInModelCoordinates();
			}
			image = layerPrinter.print(context.getLayerManager()
					.getLayerables(Layerable.class), envelope, getSizeInPixel());
			viewPanel = layerPrinter.getLayerViewPanel();
			envelope2 = new Envelope(envelope.getMinX(), envelope.getMaxX(), envelope.getMinY(), envelope.getMaxY());

		}

		String filename = addExtension(getFileChooser().getSelectedFile()
				.getPath(), fileFilter.getFormat());
		File imageFile = new File(filename);


		if (fileFilter.format.equals("tif")) {
			IOTools.saveGeoTIFF(image, envelope2, imageFile);
		}
		if (fileFilter.format.equals("pdf")) {

			SaveViewUtils.saveAsPDF(viewPanel, envelope2, getSizeInPixel(),imageFile);
		} 
		if (fileFilter.format.equals("svg")) {


			SaveViewUtils.saveAsSVG(viewPanel, envelope2, getSizeInPixel(),imageFile);
		} else if (fileFilter.format.equals("png") ||fileFilter.format.equals("jpg") ){

			SaveViewUtils.saveAsRaster(image, fileFilter.getFormat(), imageFile);}
		PersistentBlackboardPlugIn.get(workbenchContext)
		.put(FORMAT_KEY, fileFilter.getFormat());
		PersistentBlackboardPlugIn.get(workbenchContext)
		.put(LAST_FILENAME_KEY, filename);
		if ((worldFileCheckBox != null) && (worldFileCheckBox.isSelected()))
			WorldFileWriter.writeWorldFile( imageFile,  viewPanel );
		fileChooser = null; 
		workbenchContext.getWorkbench().getFrame().setStatusMessage(I18N.get("org.openjump.core.ui.plugin.raster.RasterImageLayerPropertiesPlugIn.file.saved"));


	}

	public static MultiEnableCheck createEnableCheck(WorkbenchContext workbenchContext) {
		EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);
		return new MultiEnableCheck().add(checkFactory.createTaskWindowMustBeActiveCheck()).add(checkFactory.createAtLeastNLayerablesMustExistCheck(1));
	}


	private String addExtension(String path, String extension) {
		if (path.toUpperCase().endsWith(extension.toUpperCase())) {
			return path;
		}
		if (path.endsWith(".")) {
			return path + extension;
		}
		return path + "." + extension;
	}



}