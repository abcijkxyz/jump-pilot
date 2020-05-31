package com.cadplan.jump.plugins.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.openjump.core.apitools.IOTools;
import org.openjump.core.ui.io.file.FileNameExtensionFilter;
import org.openjump.core.ui.plugin.file.open.JFCWithEnterAction;
import org.openjump.core.ui.plugin.layer.LayerPropertiesPlugIn.PropertyPanel;
import org.openjump.sextante.gui.additionalResults.AdditionalResults;
import org.saig.core.gui.swing.sldeditor.util.FormUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.plugins.VertexSymbolsClassificationDialog;
import com.cadplan.jump.ui.PreviewPanel;
import com.cadplan.jump.utils.DataWrapper;
import com.cadplan.jump.utils.StyleUtils;
import com.cadplan.jump.utils.VertexParams;
import com.cadplan.jump.utils.VertexStyler;
import com.cadplan.vertices.renderer.style.ExternalSymbolsImplType;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.util.FileUtil;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugIn;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelProxy;
import com.vividsolutions.jump.workbench.ui.WorkbenchToolBar;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.task.TaskMonitorManager;

public class VertexColorThemingPanel extends JPanel implements PropertyPanel {
	private static final long serialVersionUID = 1L;
	public JPanel upperPane;
	//private JScrollPane scrollPane = new JScrollPane();
	private JScrollPane scrollPane0 = new JScrollPane();
	private Map<String, DataWrapper> map = new LinkedHashMap<String, DataWrapper>();
	public JPanel mainPanel = new JPanel();
	String otherValues = I18N.get("ui.renderer.style.DiscreteColorThemingState.all-other-values");



	public JScrollPane getScrollPane() {
		return this.scrollPane0;
	}

	public Map<String, DataWrapper> getMap() {
		return this.map;
	}

	public void getStyle() {
		Layer layer = VertexParams.selectedLayer;
		VertexStyler symbols = new VertexStyler();
		symbols.setLayer(layer);
		List<String> attributeNameList = layer.getFeatureCollectionWrapper().getFeatureSchema().getAttributeNames();
		List<String> keys;
		String key;
		Iterator<String> it;
		if (VertexParams.classification != null & attributeNameList.contains(VertexParams.classification)) {
			keys = StyleUtils.availableValuesList(layer, ColorThemingStyle.get(layer).getAttributeName(), 256);
			//Map<String, DataWrapper> records values(DataWrapper(symbol name, symbol dimension, line distance between symbols, l
			//line offset of the symbol and rotation of the symbol to the line) by an attribute value. If this
			//map is empty. A custom map with fixed value will be used
			if (VertexParams.getClassificationMap().isEmpty()) {
				it = keys.iterator();
				while(it.hasNext()) {
					key = it.next();
					this.map.put(key, new DataWrapper(VertexParams.symbolName, 
							VertexParams.size, 
							VertexParams.distance,
							VertexParams.offset, 
							VertexParams.rotate));
				}
			}

			if (!VertexParams.getClassificationMap().isEmpty()) {
				it = keys.iterator();

				label45:
					while(true) {
						while(true) {
							if (!it.hasNext()) {
								break label45;
							}

							key = it.next();
							if (VertexParams.getClassificationMap().containsKey(key)) {
								this.map.put(key, VertexParams.getClassificationMap().get(key));
							} else if (!key.isEmpty() && !key.equals("") && key != null) {
								this.map.put(key, new DataWrapper(VertexParams.symbolName, 
										VertexParams.size, 
										VertexParams.distance,
										VertexParams.offset, 
										VertexParams.rotate));
							} else {
								this.map.put(this.otherValues, 
										new DataWrapper(VertexParams.symbolName, 
												VertexParams.size, 
												VertexParams.distance,
												VertexParams.offset, 
												VertexParams.rotate));
							}
						}
					}
			}
		} else {
			keys = StyleUtils.availableValuesList(layer, ColorThemingStyle.get(layer).getAttributeName(), 256);
			it = keys.iterator();

			while(it.hasNext()) {
				key = it.next();
				this.map.put(key, new DataWrapper(VertexParams.symbolName,
						VertexParams.size, 
						VertexParams.distance,
						VertexParams.offset, 
						VertexParams.rotate));
			}
		}

		VertexParams.setClassificationMap(this.map);
	}

	public VertexColorThemingPanel() {
		//this.setLayout(new GridBagLayout());
		this.setLayout(new BorderLayout());
		Layer layer = VertexParams.selectedLayer;
		FeatureSchema schema = layer.getFeatureCollectionWrapper().getFeatureSchema();
		List<String> attributeNameList = layer.getFeatureCollectionWrapper().getFeatureSchema().getAttributeNames();
		ColorThemingStyle styles = ColorThemingStyle.get(layer);
		new JLabel();
		int numberOfAttributesUsableInColorTheming = schema.getAttributeCount() - 1;
		if (schema.hasAttribute("R_G_B")) {
			--numberOfAttributesUsableInColorTheming;
		}

		JLabel lab;
		if (numberOfAttributesUsableInColorTheming >= 1) {
			if (!styles.isEnabled()) {
				lab = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Warning7"));
				this.add(lab);
			} else if (ColorThemingStyle.get(layer).isEnabled()) {
				if (attributeNameList.contains(styles.getAttributeName())) {
					this.getStyle();
					toolBar.removeAll();
					JButton button1 = new JButton();
					JButton button2 = new JButton();
					toolBar.add(button1, this.saveAsResultPlugIn.getName(), this.saveAsResultPlugIn.getIcon(), AbstractPlugIn.toActionListener(this.saveAsResultPlugIn, StyleUtils.frameInstance.getContext(), (TaskMonitorManager)null), (EnableCheck)null);
					toolBar.add(button2, this.saveStylePlugIn.getName(), this.saveStylePlugIn.getIcon(), AbstractPlugIn.toActionListener(this.saveStylePlugIn, StyleUtils.frameInstance.getContext(), (TaskMonitorManager)null), (EnableCheck)null);
					this.upperPane = new JPanel(new GridBagLayout());
					this.upperPane.add(new JLabel(I18NPlug.getI18N("VertexSymbols.VertexColorThemingDualog.manual")), new GridBagConstraints(1, 0, 3, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
					this.upperPane.add(toolBar, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 13, 2, new Insets(0, 0, 0, 0), 0, 0));
					try {
						TreeMap<String, DataWrapper> tmap = new TreeMap<String, DataWrapper>(getMap());//this.map);
						this.scrollPane0 = this.classificationScrollPanel(tmap);
					} catch (IOException e) {
						e.printStackTrace();
					}
					TransparPanel transparency = new TransparPanel((Color)null);
					this.add(this.upperPane,BorderLayout.NORTH);
					this.add(this.scrollPane0,BorderLayout.CENTER);
					this.add(transparency,BorderLayout.SOUTH);

				} else if (!attributeNameList.contains(styles.getAttributeName())) {
					lab = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Warning8"));
					this.add(lab);
				}
			}
		} else {
			lab = new JLabel(I18N.get("ui.style.ChangeStylesPlugIn.this-layer-has-no-attributes"));
			this.add(lab);
		}

	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JPanel classificationPanel(Layer layer, Map<String, DataWrapper> map) {
		Font plainFont = new Font(this.mainPanel.getFont().getName(), 0, this.mainPanel.getFont().getSize());
		this.mainPanel.setLayout(new GridBagLayout());
		this.mainPanel.setPreferredSize(new Dimension(600, 500));
		int gridx = 0;
		int gridy = 0;
		String STYLE ="<html><font color=black size=4><b><u>"+
				I18N.get("ui.renderer.style.ColorThemingTableModel.style")+ "</b></u></html>";
		String VALUE ="<html><font color=black size=4><b><u>"+
				I18N.get("ui.renderer.style.ColorThemingTableModel.attribute-value")+ "</b></u></html>";
		StyleUtils.addRowInGBL(this.mainPanel, gridy, gridx, 
				new JLabel(STYLE), 
				new JLabel(VALUE));

		int row = gridy + 1;
		FormUtils.addFiller(this.mainPanel, gridy, gridx);

		for(Iterator it = map.entrySet().iterator(); it.hasNext(); ++row) {
			Entry<String, DataWrapper> entry = (Entry)it.next();
			String symbol = entry.getValue().getSimbol();
			Integer dimension = entry.getValue().getdimension();
			Integer distance = entry.getValue().getDistance();
			Double offset= entry.getValue().getOffset();
			boolean rotate = entry.getValue().getRotate();
			String nome_simbolo = entry.getKey();
			//JLabel entryName = StyleUtils.label(nome_simbolo, plainFont, 0, true);
			JTextArea entryName = StyleUtils.area(nome_simbolo, plainFont, 0);
			Color line = Color.BLACK;
			Color inner = Color.WHITE;
			ExternalSymbolsImplType newStyle = new ExternalSymbolsImplType();
			newStyle.setSymbolName(symbol);
			newStyle.setShowFill(true);
			newStyle.setShowLine(true);
			BasicStyle style = new BasicStyle();

			try {
				Map<Object, BasicStyle> attributeValueToBasicStyleMap = VertexParams.classificationStyle.getAttributeValueToBasicStyleMap();
				style = attributeValueToBasicStyleMap.get(nome_simbolo);
				line = GUIUtil.alphaColor(style.getLineColor(), style.getAlpha());
				inner = GUIUtil.alphaColor(style.getFillColor(), style.getAlpha());
			} catch (Exception e) {
				line = GUIUtil.alphaColor(layer.getBasicStyle().getLineColor(), layer.getBasicStyle().getAlpha());
				inner = GUIUtil.alphaColor(layer.getBasicStyle().getFillColor(), layer.getBasicStyle().getAlpha());
			}

			newStyle.setColors(line, inner);
			newStyle.setAlpha(layer.getBasicStyle().getAlpha());
			newStyle.setSize(32);
			newStyle.setEnabled(true);
			PreviewPanel previewPanel = this.symbolPanel(newStyle, style,
					nome_simbolo, 
					dimension, 
					distance, 
					offset, 
					rotate);
			Double viewScale = previewPanel.getViewport().getScale();
			VertexParams.actualScale = viewScale;
			FormUtils.addRowInGBL(this.mainPanel, row++, gridx, previewPanel, entryName);
			FormUtils.addFiller(this.mainPanel, row++, gridx);
		}

		return this.mainPanel;
	}

	private PreviewPanel symbolPanel(final ExternalSymbolsImplType style, final BasicStyle bStyle,
			final String value, 
			final Integer dimension,
			Integer distance, 
			double offset, 
			boolean rotate) {
		final PreviewPanel previewPanel = new PreviewPanel(style, bStyle, offset);
		previewPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					previewPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
					VertexColorThemingPanel.this.mainPanel.repaint();
					VertexSymbolsClassificationDialog classDialog = new VertexSymbolsClassificationDialog(value, 
							style, 
							dimension, 
							distance, 
							offset, 
							rotate);
					StyleUtils.leftScreen(classDialog);
					classDialog.setVisible(true);
					classDialog.getSymbol();
					if (classDialog.wasOKPressed()) {
						previewPanel.removeSymbol();
						classDialog.getSymbol().setColors(style.getLineColor(), style.getFillColor());
						previewPanel.setSymbol(classDialog.getSymbol());
						previewPanel.setOffset(classDialog.getSymbol().getOffset());
						previewPanel.setBorder(BorderFactory.createEmptyBorder());
						previewPanel.validate();
						previewPanel.repaint();
						VertexColorThemingPanel.this.getStyle();
						VertexColorThemingPanel.this.mainPanel.repaint();

						map.replace(value, 
								new DataWrapper(classDialog.getSymbol().getSymbolName(),
										classDialog.getSymbol().getSize(), 
										classDialog.getSymbol().getDistance(),
										classDialog.getSymbol().getOffset(),classDialog.getSymbol().getRotate()));
						getStyle();
						VertexColorThemingPanel.this.getParent().revalidate();
						VertexColorThemingPanel.this.getParent().repaint();
					} else {
						previewPanel.setBorder(BorderFactory.createEmptyBorder());
						//previewPanel.repaint();
						VertexColorThemingPanel.this.getParent().revalidate();
						VertexColorThemingPanel.this.getParent().repaint();
					}


				}

			}
		});
		return previewPanel;
	}

	JPanel mPanel;
	private JScrollPane classificationScrollPanel(Map<String, DataWrapper> map) throws IOException {
		mPanel = this.classificationPanel(VertexParams.selectedLayer, map);
		this.scrollPane0.setBackground(Color.WHITE);
		this.scrollPane0 = new JScrollPane(mPanel, 20, 30);
		this.scrollPane0.getViewport().getView().setBackground(Color.WHITE);
		this.scrollPane0.getViewport().getView().setForeground(Color.WHITE);
		this.scrollPane0.setPreferredSize(new Dimension(540, 400));
		this.scrollPane0.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane0.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane0.getViewport().setViewPosition(new Point(0, 0));
		return this.scrollPane0;
	}

	@Override
	public String getTitle() {
		return I18N.get("ui.renderer.style.ColorThemingPanel.colour-theming");
	}

	@Override
	public void updateStyles() {
	}

	@Override
	public String validateInput() {
		return null;
	}

	private abstract class ToolbarPlugIn extends AbstractPlugIn {
		private ToolbarPlugIn() {
		}

		public abstract Icon getIcon();
	}

	private static WorkbenchToolBar toolBar = new WorkbenchToolBar((LayerViewPanelProxy)null) {
		private static final long serialVersionUID = 1L;

		@Override
		public JButton addPlugIn(Icon icon, PlugIn plugIn, EnableCheck enableCheck, WorkbenchContext workbenchContext) {
			return super.addPlugIn(icon, plugIn, enableCheck, workbenchContext);
		}
	};
	final public ToolbarPlugIn saveStylePlugIn = new ToolbarPlugIn() {
		@Override
		public String getName() {
			return  I18N
					.get("org.openjump.core.ui.plugin.style.StylePlugIns.export-style");
		}
		@Override
		public Icon getIcon() {
			return GUIUtil.toSmallIcon(IconLoader.icon("disk.png"));
		}
		JFCWithEnterAction fc = new GUIUtil.FileChooserWithOverwritePrompting();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"JUMP layer symbology", "style.xml");
		FileNameExtensionFilter filter3 = new FileNameExtensionFilter(
				"Scalable Vector Graphic", "svg");
		FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
				"Portable Network Graphics", "png");
		@Override
		public boolean execute(PlugInContext context)
				throws Exception {
			fc.setDialogTitle(getName() );
			fc.setDialogType(JFileChooser.SAVE_DIALOG);
			fc.setFileFilter(filter);
			fc.setFileFilter(filter2);
			fc.setFileFilter(filter3);
			fc.addChoosableFileFilter(filter2);
			if (JFileChooser.APPROVE_OPTION != fc.showSaveDialog(context
					.getWorkbenchFrame())) {
				return true;
			}
			String filePath = "";
			if (fc.getFileFilter().equals(filter)) {
				File file = fc.getSelectedFile();
				file = FileUtil.addExtensionIfNone(file, "style.xml");
				IOTools.saveSimbology_Jump(file, context.getSelectedLayer(0));
				filePath = file.getAbsolutePath();
			} else if (fc.getFileFilter().equals(filter3)) {
				File file = fc.getSelectedFile();
				file = FileUtil.addExtensionIfNone(file, "svg");
				DOMImplementation domImpl = GenericDOMImplementation
						.getDOMImplementation();
				// Create an instance of org.w3c.dom.Document
				Document document = domImpl.createDocument(null, "svg", null);
				SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
				mainPanel.paintComponents(svgGenerator);
				try {
					FileOutputStream fos = new FileOutputStream(file, false);
					OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
					svgGenerator.stream(out, true);
					out.close();
					filePath = file.getAbsolutePath();
				}
				catch (Exception e) {
					context.getWorkbenchFrame().handleThrowable(e);
				}
			} else if(fc.getFileFilter().equals(filter2)) {
				final int w = mainPanel.getWidth();
				final int h = mainPanel.getHeight();
				final BufferedImage bi = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_RGB);
				final Graphics2D g = bi.createGraphics();
				mainPanel.paint(g);
				try {
					File file = new File(fc.getSelectedFile() + ".png");
					filePath = file.getAbsolutePath();
					ImageIO.write(bi, "png", file);

				} catch (final Exception ex) {
				}
			}

			JOptionPane
			.showMessageDialog(
					JUMPWorkbench.getInstance().getFrame(),
					I18N.get("org.openjump.core.ui.plugin.raster.RasterImageLayerPropertiesPlugIn.file.saved")
					+ ": " + filePath, getName(),
					JOptionPane.PLAIN_MESSAGE);	
			return true;
		}
	};


	final public ToolbarPlugIn saveAsResultPlugIn = new ToolbarPlugIn() {
		@Override
		public String getName() {
			return  I18NPlug.getI18N("VertexSymbols.Dialog.save-panel-as-result-view");
		}
		@Override
		public Icon getIcon() {
			return GUIUtil.toSmallIcon(com.cadplan.jump.icon.IconLoader.icon("application_view.png"));
		}

		@Override
		public boolean execute(PlugInContext context)
				throws Exception {
			String TITLE =I18NPlug.getI18N("VertexSymbols.Dialog.legend")+" - "+VertexParams.selectedLayer.getName();
			AdditionalResults.addAdditionalResult(TITLE, scrollPane0);

			JOptionPane
			.showMessageDialog(
					JUMPWorkbench.getInstance().getFrame(),
					I18NPlug.getI18N("VertexSymbols.Dialog.panel-saved")
					+ "("+TITLE+")", getName(),
					JOptionPane.PLAIN_MESSAGE);	


			return true;
		}




	};


}
