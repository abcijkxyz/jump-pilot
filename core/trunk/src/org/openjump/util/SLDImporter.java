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

package org.openjump.util;

import static java.awt.Color.decode;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vividsolutions.jump.util.Range;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.SquareVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

import de.latlon.deejump.plugin.style.BitmapVertexStyle;
import de.latlon.deejump.plugin.style.CircleVertexStyle;
import de.latlon.deejump.plugin.style.CrossVertexStyle;
import de.latlon.deejump.plugin.style.StarVertexStyle;
import de.latlon.deejump.plugin.style.TriangleVertexStyle;

/**
 * <code>SLDImporter</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 */
public class SLDImporter {

    /**
     * The SLD namespace URI.
     */
    public static final String SLDNS = "http://www.opengis.net/sld";

    /**
     * 
     */
    public static final String OGCNS = "http://www.opengis.net/ogc";

    private static int getInt(String name, Element e) {
        NodeList nl = e.getElementsByTagNameNS(SLDNS, name);
        if (nl.getLength() == 0) {
            return 0;
        }
        return parseInt(nl.item(0).getTextContent());
    }

    private static LinkedList<Element> getElements(String name, Element e) {
        NodeList nl = e.getElementsByTagNameNS(SLDNS, name);
        if (nl.getLength() == 0) {
            return new LinkedList<Element>();
        }
        LinkedList<Element> elems = new LinkedList<Element>();
        for (int i = 0; i < nl.getLength(); ++i) {
            elems.add((Element) nl.item(i));
        }
        return elems;
    }

    private static Element getElement(String name, Element e) {
        return getElement(name, SLDNS, e);
    }

    private static Element getElement(String name, String ns, Element e) {
        NodeList nl = e.getElementsByTagNameNS(ns, name);

        if (nl.getLength() == 0) {
            return null;
        }

        return (Element) nl.item(0);
    }

    private static void applyFill(Element fill, StrokeFillStyle style) {
        if (fill == null) {
            return;
        }

        LinkedList<Element> params = getElements("CssParameter", fill);

        for (Element p : params) {
            String type = p.getAttribute("name");
            String a = p.getTextContent();
            if (a == null || a.trim().length() == 0) {
                continue;
            }

            if (type.equals("fill")) {
                style.setFillColor(decode(a));
            }

            if (type.equals("fill-opacity")) {
                style.setAlpha((int) (255 * parseDouble(a)));
            }
        }
    }

    private static void applyStroke(Element stroke, StrokeFillStyle style) {
        if (stroke == null) {
            return;
        }

        LinkedList<Element> params = getElements("CssParameter", stroke);

        for (Element p : params) {
            String type = p.getAttribute("name");
            String a = p.getTextContent();
            if (a == null || a.trim().length() == 0) {
                continue;
            }

            if (type.equals("stroke")) {
                style.setLineColor(decode(a));
            }

            if (type.equals("stroke-width")) {
                style.setLineWidth(parseInt(a));
            }

            if (type.equals("stroke-opacity")) {
                style.setAlpha((int) (255 * parseDouble(a)));
            }

            if (type.equals("stroke-dasharray")) {
                style.setLinePattern(a.replace(' ', ','));
                style.setRenderingLinePattern(true);
            }
        }
    }

    private static URL parseGraphicURL(Element e) {
        e = getElement("OnlineResource", e);

        if (e == null) {
            return null;
        }

        // assume, it's an external graphic
        String s = e.getAttributeNS("http://www.w3.org/1999/xlink", "href");
        URL u = null;
        try {
            u = new URL(s);
        } catch (MalformedURLException ex) {
            try {
                u = new File(s).toURL();
            } catch (MalformedURLException e1) {
                // ignore it
            }
        }

        return u;
    }

    private static SizedStrokeFillStyle parseGraphic(Element e) {
        URL u = parseGraphicURL(e);
        if (u != null) {
            return new BitmapVertexStyle(u.getFile());
        }

        return null;
    }

    private static LinkedList<StrokeFillStyle> parsePointSymbolizer(
            Element symbolizer) {
        Element e = getElement("WellKnownName", symbolizer);
        StrokeFillStyle style = new BasicStyle();
        SizedStrokeFillStyle extra = null;

        if (e != null) {
            String n = e.getTextContent();
            if (n != null) {
                n = n.trim();
            }

            if (n != null) {
                if (n.equalsIgnoreCase("circle")) {
                    extra = new CircleVertexStyle();
                }
                if (n.equalsIgnoreCase("cross")) {
                    extra = new CrossVertexStyle();
                }
                if (n.equalsIgnoreCase("square")) {
                    extra = new SquareVertexStyle();
                }
                if (n.equalsIgnoreCase("star")) {
                    extra = new StarVertexStyle();
                }
                if (n.equalsIgnoreCase("triangle")) {
                    extra = new TriangleVertexStyle();
                }
            }
        }

        if (extra == null) {
            extra = parseGraphic(symbolizer);
        }

        int size = getInt("Size", symbolizer);
        if (size != 0) {
            extra.setSize(size / 2);
        }

        Element fill = getElement("Fill", symbolizer);
        Element stroke = getElement("Stroke", symbolizer);

        applyFill(fill, style);
        applyStroke(stroke, style);
        if (extra != null) {
            applyFill(fill, extra);
            applyStroke(stroke, extra);
        }

        LinkedList<StrokeFillStyle> styles = new LinkedList<StrokeFillStyle>();
        styles.add(style);
        if (extra != null) {
            style.setEnabled(false);
            extra.setEnabled(true);
            styles.add(extra);
        }

        return styles;
    }

    private static LinkedList<StrokeFillStyle> parseLineSymbolizer(
            Element symbolizer) {
        StrokeFillStyle style = new BasicStyle();

        Element fill = getElement("Fill", symbolizer);
        Element stroke = getElement("Stroke", symbolizer);

        applyFill(fill, style);
        applyStroke(stroke, style);

        LinkedList<StrokeFillStyle> list = new LinkedList<StrokeFillStyle>();
        list.add(style);

        return list;
    }

    private static LinkedList<StrokeFillStyle> parsePolygonSymbolizer(
            Element symbolizer) {
        BasicStyle style = new BasicStyle();

        Element fill = getElement("Fill", symbolizer);
        Element stroke = getElement("Stroke", symbolizer);

        URL u = parseGraphicURL(symbolizer);
        if (u != null) {
            Paint p = new CustomTexturePaint(u);
            style.setFillPattern(p);
            style.setRenderingFillPattern(true);
        }

        applyFill(fill, style);
        applyStroke(stroke, style);

        LinkedList<StrokeFillStyle> list = new LinkedList<StrokeFillStyle>();
        list.add(style);
        style.setEnabled(true);

        return list;
    }

    private static Object parseValues(Element filter) {
        Element lower = getElement("LowerBoundary", OGCNS, filter);
        Element upper = getElement("UpperBoundary", OGCNS, filter);
        if (lower != null && upper != null) {
            String s1 = getElement("Literal", OGCNS, lower).getTextContent()
                    .trim();
            String s2 = getElement("Literal", OGCNS, upper).getTextContent()
                    .trim();
            return new Range(s1, true, s2, false);
        }

        return getElement("Literal", OGCNS, filter).getTextContent().trim();
    }

    // note that not at all are all filters supported, they're (informally)
    // expected to be in the format of the SLD exporter
    private static ColorThemingStyle parseColorThemingStyle(NodeList rules,
            NodeList filters) {
        ColorThemingStyle style = new ColorThemingStyle();

        String att = (((Element) rules.item(0)).getElementsByTagNameNS(OGCNS,
                "PropertyName")).item(0).getTextContent();
        att = att.substring(att.indexOf(':') + 1);

        style.setAttributeName(att);
        HashMap<Object, StrokeFillStyle> map = new HashMap<Object, StrokeFillStyle>();
        HashMap<Object, String> labelMap = new HashMap<Object, String>();

        for (int i = 0; i < rules.getLength(); ++i) {
            Element symbolizer = getElement("PointSymbolizer", (Element) rules
                    .item(i));

            if (symbolizer != null) {
                StrokeFillStyle s = parsePointSymbolizer(symbolizer).getFirst();
                s.setEnabled(true);
                Object val = parseValues((Element) filters.item(i));
                map.put(val, s);
                labelMap.put(val, val.toString());
                if (style.getDefaultStyle() == null) {
                    style.setDefaultStyle((BasicStyle) s);
                }
            }

            symbolizer = getElement("LineSymbolizer", (Element) rules.item(i));

            if (symbolizer != null) {
                StrokeFillStyle s = parseLineSymbolizer(symbolizer).getFirst();
                s.setEnabled(true);
                Object val = parseValues((Element) filters.item(i));
                map.put(val, s);
                labelMap.put(val, val.toString());
                if (style.getDefaultStyle() == null) {
                    style.setDefaultStyle((BasicStyle) s);
                }
            }

            symbolizer = getElement("PolygonSymbolizer", (Element) rules
                    .item(i));

            if (symbolizer != null) {
                StrokeFillStyle s = parsePolygonSymbolizer(symbolizer)
                        .getFirst();
                s.setEnabled(true);
                Object val = parseValues((Element) filters.item(i));
                map.put(val, s);
                labelMap.put(val, val.toString());
                if (style.getDefaultStyle() == null) {
                    style.setDefaultStyle((BasicStyle) s);
                }
            }

        }

        style.setAttributeValueToBasicStyleMap(map);
        style.setEnabled(true);
        style.setAttributeValueToLabelMap(labelMap);

        return style;
    }

    /**
     * @param doc
     * @return a list of corresponding JUMP styles
     */
    public static LinkedList<Style> importSLD(Document doc) {
        LinkedList<Style> styles = new LinkedList<Style>();

        // maybe ask which feature type style to use?

        NodeList nl = doc.getElementsByTagNameNS(SLDNS, "Rule");
        NodeList filters = doc.getElementsByTagNameNS(OGCNS, "Filter");

        if (nl.getLength() == filters.getLength()) {
            styles.add(parseColorThemingStyle(nl, filters));
        } else {
            for (int i = 0; i < nl.getLength(); ++i) {
                Element rule = (Element) nl.item(i);
                LinkedList<Element> symbolizers = getElements(
                        "PointSymbolizer", rule);
                for (Element s : symbolizers) {
                    styles.addAll(parsePointSymbolizer(s));
                }
                symbolizers = getElements("LineSymbolizer", rule);
                for (Element s : symbolizers) {
                    styles.addAll(parseLineSymbolizer(s));
                }
                symbolizers = getElements("PolygonSymbolizer", rule);
                for (Element s : symbolizers) {
                    styles.addAll(parsePolygonSymbolizer(s));
                }
            }
        }

        return styles;
    }

    /**
     * <code>FillStyle</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     */
    public static interface FillStyle {

        /**
         * @param c
         */
        public void setFillColor(Color c);

        /**
         * @param a
         */
        public void setAlpha(int a);

    }

    /**
     * <code>StrokeStyle</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     */
    public static interface StrokeStyle {

        /**
         * @param c
         */
        public void setLineColor(Color c);

        /**
         * @param w
         */
        public void setLineWidth(int w);

        /**
         * @param a
         */
        public void setAlpha(int a);

        /**
         * @param b
         * @return a basic style
         */
        public BasicStyle setRenderingLinePattern(boolean b);

        /**
         * @param p
         * @return a basic style
         */
        public BasicStyle setLinePattern(String p);

    }

    /**
     * <code>SizedStyle</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     */
    public static interface SizedStyle {

        /**
         * @param s
         */
        public void setSize(int s);

    }

    /**
     * <code>StrokeFillStyle</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     */
    public static interface StrokeFillStyle extends StrokeStyle, FillStyle,
            Style {

        // no methods, they're combined by the stroke and fill interfaces

    }

    /**
     * <code>SizedStrokeFillStyle</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     */
    public static interface SizedStrokeFillStyle extends StrokeFillStyle,
            SizedStyle {
        // no methods, they're combined
    }

}
