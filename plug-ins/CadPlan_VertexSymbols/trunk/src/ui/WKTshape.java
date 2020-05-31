package com.cadplan.jump.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class WKTshape implements Shape{
	public int lineWidth;
	public int extent;
	public String wktText;

	public WKTshape(int width, int extent, String text) {
		this.lineWidth = width;
		this.extent = extent;
		this.wktText = text;
	}

	public void paint(Graphics2D g, int size, boolean showLine, Color lineColor, boolean showFill, Color fillColor, boolean dotted) {
		Geometry geometry = null;
		double scale = (double)size / (double)this.extent;
		Stroke stroke = new BasicStroke(this.lineWidth);
		g.setStroke(stroke);
		WKTReader testReader = new WKTReader();

		try {
			geometry = testReader.read(this.wktText);
		} catch (ParseException var19) {
		}

		if (geometry instanceof LineString) {
			this.paintLineString(g, (LineString)geometry, size, showLine, lineColor, showFill, fillColor, dotted, scale);
		} else {
			int numGeometries;
			int i;
			if (geometry instanceof MultiLineString) {
				numGeometries = ((GeometryCollection)geometry).getNumGeometries();

				for(i = 0; i < numGeometries; ++i) {
					LineString ls = (LineString)((GeometryCollection)geometry).getGeometryN(i);
					this.paintLineString(g, ls, size, showLine, lineColor, showFill, fillColor, dotted, scale);
				}
			} else if (geometry instanceof Polygon) {
				this.paintPolygon(g, (Polygon)geometry, size, showLine, lineColor, showFill, fillColor, dotted, scale);
			} else if (geometry instanceof MultiPolygon) {
				numGeometries = ((MultiPolygon)geometry).getNumGeometries();

				for(i = 0; i < numGeometries; ++i) {
					Polygon p = (Polygon)((MultiPolygon)geometry).getGeometryN(i);
					this.paintPolygon(g, p, size, showLine, lineColor, showFill, fillColor, dotted, scale);
				}
			} else if (geometry instanceof GeometryCollection) {
				numGeometries = ((GeometryCollection)geometry).getNumGeometries();

				for(i = 0; i < numGeometries; ++i) {
					Geometry pg = ((GeometryCollection)geometry).getGeometryN(i);
					if (pg instanceof LineString) {
						this.paintLineString(g, (LineString)pg, size, showLine, lineColor, showFill, fillColor, dotted, scale);
					} else {
						int numPolygons;
						int k;
						if (pg instanceof MultiLineString) {
							numPolygons = ((GeometryCollection)pg).getNumGeometries();

							for(k = 0; k < numPolygons; ++k) {
								LineString ls = (LineString)((GeometryCollection)pg).getGeometryN(k);
								this.paintLineString(g, ls, size, showLine, lineColor, showFill, fillColor, dotted, scale);
							}
						} else if (pg instanceof Polygon) {
							this.paintPolygon(g, (Polygon)pg, size, showLine, lineColor, showFill, fillColor, dotted, scale);
						} else if (pg instanceof MultiPolygon) {
							numPolygons = ((MultiPolygon)pg).getNumGeometries();

							for(k = 0; k < numPolygons; ++k) {
								Polygon p = (Polygon)((MultiPolygon)pg).getGeometryN(k);
								this.paintPolygon(g, p, size, showLine, lineColor, showFill, fillColor, dotted, scale);
							}
						}
					}
				}
			}
		}

	}

	public GeneralPath getPath() {
		return this.path;
	}


	GeneralPath path;
	private void paintLineString(Graphics2D g, LineString geometry, int size, boolean showLine, Color lineColor, boolean showFill, Color fillColor, boolean dotted, double scale) {
		path = new GeneralPath();
		Coordinate[] coords = geometry.getCoordinates();
		float x = (float)(coords[0].x * scale);
		float y = -((float)(coords[0].y * scale));
		path.moveTo(x, y);

		for(int i = 1; i < coords.length; ++i) {
			x = (float)(coords[i].x * scale);
			y = -((float)(coords[i].y * scale));
			path.lineTo(x, y);
		}

		if (showLine) {
			g.setColor(lineColor);
			g.draw(path);
		}

	}

	private void paintPolygon(Graphics2D g, Polygon geometry, int size, boolean showLine, Color lineColor, boolean showFill, Color fillColor, boolean dotted, double scale) {
		Area outer = null;
		Area hole = null;
		new Area();
		Polygon pgeometry = geometry;
		int numRings = geometry.getNumInteriorRing();

		for(int j = 0; j < numRings + 1; ++j) {
			path = new GeneralPath();
			LineString lineString;
			if (j == 0) {
				lineString = pgeometry.getExteriorRing();
			} else {
				lineString = pgeometry.getInteriorRingN(j - 1);
			}

			Coordinate[] coords = lineString.getCoordinates();
			float x = (float)(coords[0].x * scale);
			float y = -((float)(coords[0].y * scale));
			path.moveTo(x, y);

			for(int i = 1; i < coords.length; ++i) {
				x = (float)(coords[i].x * scale);
				y = -((float)(coords[i].y * scale));
				path.lineTo(x, y);
			}

			if (j == 0) {
				outer = new Area(path);
			} else {
				hole = new Area(path);
				outer.subtract(hole);
			}
		}

		if (showFill) {
			g.setColor(fillColor);
			g.fill(outer);
		}

		if (showLine) {
			g.setColor(lineColor);
			g.draw(outer);
		}

	}

	@Override
	public boolean contains(Point2D arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Rectangle2D arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(Rectangle2D arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
