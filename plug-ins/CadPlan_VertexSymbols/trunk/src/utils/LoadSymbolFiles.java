package com.cadplan.jump.utils;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.batik.transcoder.TranscoderException;

import com.cadplan.fileio.TextFile;
import com.cadplan.jump.ui.SVGRasterizer;
import com.cadplan.jump.ui.WKTshape;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.renderer.java2D.Java2DConverter;
import com.vividsolutions.jump.workbench.ui.renderer.java2D.Java2DConverter.PointConverter;

public class LoadSymbolFiles extends Component implements FilenameFilter, Runnable {
	private static final long serialVersionUID = 1L;
	boolean debug = false;
	String[] imageNames = null;
	Image[] images = null;
	String[] wktNames = null;
	WKTshape[] wktShapes = null;
	int numImages = 0;
	int numWKT = 0;
	MediaTracker tracker;
	String wd;
	Thread thread;
	boolean stopped = false;
	PlugInContext context;
	int numberSymbols = 0;

	public LoadSymbolFiles(PlugInContext context) {
		this.context = context;
	}

	private void loadImagesData() {
		File pluginDir = this.context.getWorkbenchContext().getWorkbench().getPlugInManager().getPlugInDirectory();
		this.tracker = new MediaTracker(this);
		this.wd = pluginDir.getAbsolutePath();
		this.loadNames();
		VertexParams.imageNames = this.imageNames;
		VertexParams.images = this.images;
		VertexParams.wktNames = this.wktNames;
		VertexParams.wktShapes = this.wktShapes;
	}

	@Override
	public boolean accept(File dir, String name) {
		boolean match = false;
		if (name.toLowerCase().endsWith(".gif") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".wkt") || name.toLowerCase().endsWith(".svg")) {
			match = true;
		}

		return match;
	}

	private void loadNames() {
		File file = new File(this.wd + File.separator + "VertexImages");
		if (!file.exists()) {
			file.mkdirs();
		}

		if (this.debug) {
			System.out.println("Location: " + file);
		}

		String[] fileNames = file.list(this);
		Arrays.sort(fileNames);
		if (fileNames != null && fileNames.length != 0) {
			this.numberSymbols = fileNames.length;

			int j;
			for(j = 0; j < fileNames.length; ++j) {
				if (fileNames[j].toLowerCase().endsWith(".wkt")) {
					++this.numWKT;
				} else {
					++this.numImages;
				}
			}

			this.images = new Image[this.numImages];
			this.imageNames = new String[this.numImages];
			this.wktShapes = new WKTshape[this.numWKT];
			this.wktNames = new String[this.numWKT];
			j = 0;

			int i;
			for(i = 0; i < fileNames.length; ++i) {
				if (!fileNames[i].toLowerCase().endsWith(".wkt")) {
					if (this.debug) {
						System.out.println("Loading image: " + fileNames[i]);
					}

					this.imageNames[j] = fileNames[i];
					this.images[j] = this.loadImage(this.wd + File.separator + "VertexImages" + File.separator + fileNames[i]);
					++j;
				}
			}

			j = 0;

			for(i = 0; i < fileNames.length; ++i) {
				if (fileNames[i].toLowerCase().endsWith(".wkt")) {
					if (this.debug) {
						System.out.println("Loading image: " + fileNames[i]);
					}

					this.wktNames[j] = fileNames[i];
					this.wktShapes[j] = this.loadWKT(this.wd + File.separator + "VertexImages" + File.separator + fileNames[i]);
					++j;
				}
			}

		}
	}

	public Image loadImage(String name) {
		URL url = null;
		Object image = null;

		try {
			url = new URL("file", "localhost", name);
			// url = new URL("file:///" + name);
		} catch (MalformedURLException var13) {
			JOptionPane.showMessageDialog((Component)null, "Error: " + var13, "Error...", 0);
		}

		if (name.toLowerCase().endsWith(".svg")) {
			if (this.debug) {
				System.out.println("Loading SVG image: " + name);
			}

			SVGRasterizer r = new SVGRasterizer(url);
			int size = 64;
			int k = name.lastIndexOf("_x");
			if (k > 0) {
				int j = name.lastIndexOf(".");
				String ss = name.substring(k + 2, j);
				j = ss.indexOf("x");
				if (j > 0) {
					ss = ss.substring(j + 1, ss.length());

					try {
						size = Integer.parseInt(ss);
					} catch (NumberFormatException ex) {
						size = 64;
					}
				}
			}

			if (this.debug) {
				System.out.println("SVG Image:" + name + "   size=" + size);
			}

			r.setImageWidth(size);
			r.setImageHeight(size);

			try {
				image = r.createBufferedImage();
			} catch (TranscoderException ex) {
				if (this.debug) {
					System.out.println("ERROR:" + ex);
				}
			}

			try {
				this.tracker.addImage((Image)image, 1);
				this.tracker.waitForID(1);
			} catch (InterruptedException var11) {
			}

			if (this.debug) {
				System.out.println("Image size: " + ((Image)image).getWidth(this) + ", " + ((Image)image).getHeight(this));
			}
		} else {
			image = Toolkit.getDefaultToolkit().getImage(url);

			try {
				this.tracker.addImage((Image)image, 1);
				this.tracker.waitForID(1);
			} catch (InterruptedException var10) {
			}

			if (this.debug) {
				System.out.println("Image size: " + ((Image)image).getWidth(this) + ", " + ((Image)image).getHeight(this));
			}

			if (((Image)image).getWidth(this) < 0) {
				image = null;
			}
		}

		return (Image)image;
	}

	private WKTshape loadWKT(String name) {
		Geometry geometry = null;
		int k = name.lastIndexOf(File.separator);
		String dirName = name.substring(0, k);
		String fileName = name.substring(k + 1, name.length());
		if (this.debug) {
			System.out.println("dir:" + dirName + "  file:" + fileName);
		}

		TextFile tfile = new TextFile(dirName, fileName);
		tfile.openRead();
		String text = tfile.readAll();
		tfile.close();
		int width = 1;
		int extent = 10;
		String wkt = "";
		if (text.contains(":")) {
			StringTokenizer st = new StringTokenizer(text, ":");

			try {
				width = Integer.parseInt(st.nextToken());
				extent = Integer.parseInt(st.nextToken());
				wkt = st.nextToken();
				WKTReader testReader = new WKTReader();

				try {
					testReader.read(wkt);
				} catch (ParseException var16) {
					JOptionPane.showMessageDialog((Component)null, "Error parsing WKT in file: " + name + "\n" + wkt, "Error...", 0);
					System.out.println("Error parsing WKT file: " + name + "\n" + wkt);
				}
			} catch (Exception var17) {
				JOptionPane.showMessageDialog((Component)null, "Error parsing WKT file: " + name + "\n" + text, "Error...", 0);
			}
		} else {
			try {
				wkt = text;
				WKTReader testReader = new WKTReader();

				try {
					geometry = testReader.read(wkt);
				} catch (ParseException var14) {
					JOptionPane.showMessageDialog((Component)null, "Error parsing WKT in file: " + name + "\n" + text, "Error...", 0);
					System.out.println("Error parsing WKT file: " + name + "\n" + text);
				}

				scaleGeometry(geometry);
				width = 1;
				extent = (int)geometry.getEnvelopeInternal().getWidth();
			} catch (Exception var15) {
				JOptionPane.showMessageDialog((Component)null, "Error parsing WKT file: " + name + "\n" + text, "Error...", 0);
			}
		}

		if (this.debug) {
			System.out.println("WKT:" + text);
		}

		return new WKTshape(width, extent, wkt);
	}

	public static Shape getShape(Geometry geometry) throws NoninvertibleTransformException {
		final Coordinate c = geometry.getCentroid().getCoordinate();
		Java2DConverter java2DConverter = new Java2DConverter(new PointConverter() {
			@Override
			public Point2D toViewPoint(Coordinate modelCoordinate) {
				return new Double(modelCoordinate.x - c.x, modelCoordinate.y - c.y);
			}

			@Override
			public double getScale() {
				return 1.0D;
			}

			@Override
			public Envelope getEnvelopeInModelCoordinates() {
				return new Envelope(0.0D, 16.0D, 0.0D, 16.0D);
			}
		});
		return java2DConverter.toShape(geometry);
	}

	public static void scaleGeometry(Geometry geometry) {
		Envelope enve = new Envelope(0.0D, 20.0D, 0.0D, 20.0D);
		Envelope gemEnve = geometry.getEnvelopeInternal();
		final double scale = Math.sqrt(Math.pow(gemEnve.getHeight(), 2.0D) + Math.pow(gemEnve.getWidth(), 2.0D)) / Math.sqrt(Math.pow(enve.getHeight(), 2.0D) + Math.pow(enve.getWidth(), 2.0D));
		final Coordinate center = geometry.getCentroid().getCoordinate();
		geometry.apply(new CoordinateFilter() {
			@Override
			public void filter(Coordinate coordinate) {
				coordinate.x = center.x + scale * (coordinate.x - center.x);
				coordinate.y = center.y + scale * (coordinate.y - center.y);
			}
		});
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void stop() {
		this.stopped = true;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		this.loadImagesData();
		double timeMillis = (System.currentTimeMillis() - start) / 1000.0D;
	}
}
