package com.cadplan.vertices.renderer.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.RectangularShape;
import java.awt.image.ImageObserver;

import com.cadplan.jump.ui.WKTshape;
import com.cadplan.jump.utils.VertexParams;

public class ExternalSymbolsRenderer {
	public static final int POLYS = 0;
	public static final int STARS = 1;
	public static final int ANYS = 2;
	public static final int WKTS = 3;
	public static final int IMAGES = 4;
	private int type;
	public int width;
	public int height;
	public int size;
	public int scale = 1;
	public float x0;
	public float y0;

	public GeneralPath buildShape(int type, Shape shape, boolean byValue, double orientation, int bsize, int numSides) {
		GeneralPath path = new GeneralPath();
		this.size = bsize;
		float x;
		float y;
		int n;
		double s;
		double angle;
		switch(type) {
		case 0:
			double absAngle = orientation;
			if (!byValue) {
				absAngle = 0.0D;
			}

			s = bsize / 2.0D;
			n = numSides;
			angle = Math.toRadians(absAngle) - 1.5707963267948966D + 3.141592653589793D / numSides;
			this.x0 = (float)shape.getBounds2D().getX() + (float)s;
			this.y0 = (float)shape.getBounds2D().getY() + (float)s;

			for(int i = 0; i < n; ++i) {
				x = (float)(s * Math.cos(angle));
				y = (float)(s * Math.sin(angle));
				if (i == 0) {
					path.moveTo(this.x0 + x, this.y0 - y);
				} else {
					path.lineTo(this.x0 + x, this.y0 - y);
				}

				angle += 6.283185307179586D / n;
			}

			if (n == 2) {
				angle = Math.toRadians(absAngle) + 3.141592653589793D / n;
				x = (float)(s * Math.cos(angle));
				y = (float)(s * Math.sin(angle));
				path.moveTo(this.x0 + x, this.y0 - y);
				angle += 3.141592653589793D;
				x = (float)(s * Math.cos(angle));
				y = (float)(s * Math.sin(angle));
				path.lineTo(this.x0 + x, this.y0 - y);
			} else {
				angle = Math.toRadians(absAngle) - 1.5707963267948966D + 3.141592653589793D / n;
				x = (float)(s * Math.cos(angle));
				y = (float)(s * Math.sin(angle));
				path.lineTo(this.x0 + x, this.y0 - y);
			}

			return path;
		case 1:
			double baseAngle = orientation;
			if (!byValue) {
				baseAngle = 0.0D;
			}

			s = bsize / 2.0D;
			n = numSides;
			angle = Math.toRadians(baseAngle) - 1.5707963267948966D + 3.141592653589793D / numSides;
			double halfAngle = 3.141592653589793D / numSides;
			this.x0 = (float)shape.getBounds2D().getX() + (float)s;
			this.y0 = (float)shape.getBounds2D().getY() + (float)s;

			float xm;
			float ym;
			for(int i = 0; i < n; ++i) {
				x = (float)(s * Math.cos(angle));
				y = (float)(s * Math.sin(angle));
				xm = (float)(s / 3.0D * Math.cos(angle - halfAngle));
				ym = (float)(s / 3.0D * Math.sin(angle - halfAngle));
				if (i == 0) {
					path.moveTo(this.x0 + x, this.y0 - y);
				} else {
					path.lineTo(this.x0 + xm, this.y0 - ym);
					path.lineTo(this.x0 + x, this.y0 - y);
				}

				angle += 6.283185307179586D / n;
			}

			angle = Math.toRadians(baseAngle) - 1.5707963267948966D + 3.141592653589793D / n;
			xm = (float)(s / 3.0D * Math.cos(angle - halfAngle));
			ym = (float)(s / 3.0D * Math.sin(angle - halfAngle));
			x = (float)(s * Math.cos(angle));
			y = (float)(s * Math.sin(angle));
			path.lineTo(this.x0 + xm, this.y0 - ym);
			path.lineTo(this.x0 + x, this.y0 - y);
			return path;
		case 2:
			float sf = (float)(bsize / 2.0D);
			float s2 = sf;
			this.x0 = (float)shape.getBounds2D().getX() + sf;
			this.y0 = (float)shape.getBounds2D().getY() + sf;
			int i;
			switch(numSides) {
			case 0:
				path.moveTo(this.x0 - sf, this.y0);
				path.lineTo(this.x0 + sf, this.y0);
				path.moveTo(this.x0, this.y0);
				path.lineTo(this.x0 - sf / 2.0F, this.y0 - sf / 2.0F);
				path.moveTo(this.x0, this.y0);
				path.lineTo(this.x0, this.y0 - sf);
				path.moveTo(this.x0, this.y0);
				path.lineTo(this.x0 + sf / 2.0F, this.y0 - sf / 2.0F);
				break;
			case 1:
				path.moveTo(this.x0 - sf, this.y0);
				path.lineTo(this.x0 + sf, this.y0);
				path.moveTo(this.x0 + sf / 2.0F, this.y0 + sf / 2.0F);
				path.lineTo(this.x0 - sf / 2.0F, this.y0 - sf / 2.0F);
				path.moveTo(this.x0, this.y0 + sf);
				path.lineTo(this.x0, this.y0 - sf);
				path.moveTo(this.x0 - sf / 2.0F, this.y0 + sf / 2.0F);
				path.lineTo(this.x0 + sf / 2.0F, this.y0 - sf / 2.0F);
				break;
			case 2:
				path.moveTo(this.x0 - sf, this.y0);
				path.lineTo(this.x0 + sf, this.y0);
				path.moveTo(this.x0, this.y0 - sf);
				path.lineTo(this.x0, this.y0 + sf);
				path.moveTo(this.x0 + sf / 2.0F, this.y0);

				for(i = 0; i <= 16; ++i) {
					angle = i * 2.0D * 3.141592653589793D / 16.0D;
					path.lineTo((float)(this.x0 + sf / 2.0F * Math.cos(angle)), (float)(this.y0 - sf / 2.0F * Math.sin(angle)));
				}

				return path;
			case 3:
				path.moveTo(this.x0 - sf, this.y0);
				path.lineTo(this.x0 + sf, this.y0);
				path.moveTo(this.x0, this.y0 - sf);
				path.lineTo(this.x0, this.y0 + sf);
				path.moveTo(this.x0 - sf / 2.0F, this.y0 - sf / 2.0F);
				path.lineTo(this.x0 + sf / 2.0F, this.y0 - sf / 2.0F);
				path.lineTo(this.x0 + sf / 2.0F, this.y0 + sf / 2.0F);
				path.lineTo(this.x0 - sf / 2.0F, this.y0 + sf / 2.0F);
				path.lineTo(this.x0 - sf / 2.0F, this.y0 - sf / 2.0F);
				break;
			case 4:
				path.moveTo(this.x0, this.y0);
				path.lineTo(this.x0, this.y0 - sf / 2.0F);
				path.moveTo(this.x0 + sf / 4.0F, this.y0 - 3.0F * sf / 4.0F);

				for(i = 0; i <= 16; ++i) {
					angle = i * 2.0D * 3.141592653589793D / 16.0D;
					path.lineTo((float)(this.x0 + s2 / 4.0F * Math.cos(angle)), (float)(this.y0 - 3.0F * s2 / 4.0F - s2 / 4.0F * Math.sin(angle)));
				}

				return path;
			case 5:
				path.moveTo(this.x0, this.y0);
				path.lineTo(this.x0, this.y0 - sf / 2.0F);
				path.lineTo(this.x0, this.y0 - sf);
				path.lineTo(this.x0 + sf / 2.0F, this.y0 - 3.0F * sf / 4.0F);
				path.lineTo(this.x0, this.y0 - sf / 2.0F);
				break;
			case 6:
				path.moveTo(this.x0, this.y0);
				path.lineTo(this.x0, this.y0 - sf);
				path.moveTo(this.x0 - sf / 2.0F, this.y0);
				path.lineTo(this.x0, this.y0 - sf / 2.0F);
				path.lineTo(this.x0 + sf / 2.0F, this.y0);
				path.lineTo(this.x0 - sf / 2.0F, this.y0);
			}

			return path;
		case 3:
		default:
			return null;
		}
	}


	public Image getImage(int type, String name, int bsize, Shape shape)
	{
		Image image = null;
		int x =0, y=0;
		if(type == IMAGES)
		{
			boolean found = false;
			size = bsize;
			x = (int) shape.getBounds2D().getX();
			y  = (int) shape.getBounds2D().getY();
			for (int i=0; i < VertexParams.images.length; i++)
			{
				if(VertexParams.imageNames[i].equals(name))
				{
					found = true;
					image = VertexParams.images[i];
					if(image != null)
					{
						//size = image.getWidth(null);
						width = image.getWidth(null);
						height = image.getHeight(null);
						int k = name.lastIndexOf("_x");
						int j = name.lastIndexOf(".");
						scale = 1;
						if(k > 0)
						{
							String scaleS = name.substring(k+2,j);
							int n = scaleS.lastIndexOf("x");
							if(n > 0) scaleS = scaleS.substring(0,n);
							try
							{
								scale = Integer.parseInt(scaleS);
							}
							catch (NumberFormatException ex)
							{
								scale = 1;
							}
							//System.out.println("Name:"+name+"  image scale factor="+scale);
						}
					}
					//size = (int) (size/scale);
					//System.out.println("Image name="+name+ "  After scaling: size="+size+ " width="+width+" height="+height+" scale="+scale);
				}

			}
			if(!found)    // missing image
			{
				//Icon icon = (Icon) UIManager.get("JOptionPane.questionIcon");
				image = null;
			}

		}
		int newWidth;
		int newHeight;
		if(width > height)
		{
			newWidth = size/scale;
			if(width > 0) newHeight =size*height/width/scale;
			else newHeight = height;
		}
		else
		{
			newHeight = size/scale;
			if(height > 0) newWidth = size*width/height/scale;
			else newWidth = width;
		}
		//width = newWidth;
		//height = newHeight;
		x0 = (float) (x + size/2.0);
		y0 = (float) (y + size/2.0);
		//	        x0 = x;
		//	        y0 = y;

		return image;
	}



	public Image getImage2(int type, String name, int bsize, Shape shape) {
		Image image = null;
		int x = 0;
		int y = 0;
		int i;
		if (type == 4) {
			boolean found = false;
			this.size = bsize;
			x = (int)shape.getBounds2D().getX();
			y = (int)shape.getBounds2D().getY();

			for(i = 0; i < VertexParams.images.length; ++i) {
				if (VertexParams.imageNames[i].equals(name)) {
					found = true;
					image = VertexParams.images[i];
					if (image != null) {
						this.width = image.getWidth((ImageObserver)null);
						this.height = image.getHeight((ImageObserver)null);
						int k = name.lastIndexOf("_x");
						int j = name.lastIndexOf(".");
						this.scale = 1;
						if (k > 0) {
							String scaleS = name.substring(k + 2, j);
							int n = scaleS.lastIndexOf("x");
							if (n > 0) {
								scaleS = scaleS.substring(0, n);
							}

							try {
								this.scale = Integer.parseInt(scaleS);
							} catch (NumberFormatException var15) {
								this.scale = 1;
							}
						}
					}
				}
			}

			if (!found) {
				image = null;
			}
		}

		int var10000;
		if (this.width > this.height) {
			var10000 = this.size / this.scale;
			if (this.width > 0) {
				i = this.size * this.height / this.width / this.scale;
			} else {
				i = this.height;
			}
		} else {
			var10000 = this.size / this.scale;
			int var16;
			if (this.height > 0) {
				var16 = this.size * this.width / this.height / this.scale;
			} else {
				var16 = this.width;
			}
		}

		this.x0 = (float)(x + this.size / 2.0D);
		this.y0 = (float)(y + this.size / 2.0D);
		return image;
	}

	public WKTshape getWKTShape(int type, String name, int bsize, Shape shape) {
		WKTshape wktShape = null;
		boolean found = false;
		this.size = bsize;
		this.scale = 1;
		int x = (int)shape.getBounds2D().getX();
		int y = (int)shape.getBounds2D().getY();
		int newWidth;
		int k;
		if (type == 3) {
			for(newWidth = 0; newWidth < VertexParams.wktShapes.length; ++newWidth) {
				if (VertexParams.wktNames[newWidth].equals(name)) {
					found = true;
					wktShape = VertexParams.wktShapes[newWidth];
					if (wktShape != null) {
						this.width = wktShape.extent;
						this.height = wktShape.extent;
						k = name.lastIndexOf("_x");
						int j = name.lastIndexOf(".");
						this.scale = 1;
						if (k > 0) {
							String scaleS = name.substring(k + 2, j);
							int n = scaleS.lastIndexOf("x");
							if (n > 0) {
								scaleS = scaleS.substring(0, n);
							}

							try {
								this.scale = Integer.parseInt(scaleS);
							} catch (NumberFormatException var15) {
								this.scale = 1;
							}
						}
					}

					this.size /= this.scale;
				}
			}
		}

		if (!found) {
			wktShape = null;
		}

		if (this.width > this.height) {
			newWidth = this.size;
			k = this.size * this.height / this.width;
		} else {
			k = this.size;
			newWidth = this.size * this.width / this.height;
		}

		this.width = newWidth;
		this.height = k;
		this.x0 = (float)(x + this.width / this.scale / 2.0D);
		this.y0 = (float)(y + this.height / this.scale / 2.0D);
		((RectangularShape)shape).setFrame(0.0D, 0.0D, this.width, this.height);
		return wktShape;
	}



	public void paint(Graphics2D g, int type, WKTshape wktShape, int size, boolean showLine, Color lineColor, boolean showFill, Color fillColor, boolean dotted) {
		if (type == 3) {
			if (wktShape == null) {
				g.setColor(Color.BLACK);
				g.drawString("No WKT", 0, 10);
			} else {
				wktShape.paint(g, size, showLine, lineColor, showFill, fillColor, dotted);
			}

			if (dotted) {
				g.setColor(Color.BLACK);
				g.fillRect(-1, -1, 2, 2);
			}
		}

	}

	public void paint(Graphics2D g, int type, Image image) {
		if (type == 4) {
			if (image == null) {
				g.setColor(Color.BLACK);
				g.drawString("No Image", 0, 10);
			} else {
				int sizex = this.size;
				int sizey = this.size * this.height / this.width;
				g.drawImage(image, (int)(this.x0 - sizex / 2), (int)(this.y0 - sizey / 2), sizex, sizey, (ImageObserver)null);
			}
		}

	}

	public void paint(Graphics2D g, int type, GeneralPath path, boolean showLine, Color lineColor, boolean showFill, Color fillColor, boolean dotted) {
		if (path != null) {
			switch(type) {
			case 0:
			case 1:
			case 2:
				g.setColor(fillColor);
				if (showFill) {
					g.fill(path);
				}

				g.setColor(lineColor);
				if (showLine) {
					g.draw(path);
				}

				if (dotted) {
					g.setColor(Color.BLACK);
					Rectangle bounds = path.getBounds();
					g.fillRect(bounds.x + bounds.width / 2 - 1, bounds.y + bounds.height / 2 - 1, 2, 2);
				}
			default:
			}
		}
	}
}
