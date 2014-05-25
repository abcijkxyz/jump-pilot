/*
 * The JCS Conflation Suite (JCS) is a library of Java classes that
 * can be used to build automated or semi-automated conflation solutions.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jcs.conflate.coverage;

import java.util.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.Debug;
import com.vividsolutions.jcs.conflate.boundarymatch.SegmentMatcher;

/**
 * Contains information about a Feature which has or participates
 * in one or more coverage gaps.
 * (E.g. has one or more segments along its edge which form a gap or overlap
 * with another feature).
 */
public class CoverageFeature {

    private Feature feature;
    private boolean isProcessed = false;
    private boolean isAdjusted = false;
    private Shell shell;
    // add hole/shell adjustment capability [mmichaud 2010-01-03]
    private Shell[] holes;

    public CoverageFeature(Feature feature, VertexMap vmap, Set<Coordinate> adjustableCoords) {
        this.feature = feature;
        Polygon poly = (Polygon) feature.getGeometry();
        shell = new Shell(vmap, feature.getID(), 0);
        shell.initialize((LinearRing) poly.getExteriorRing(), adjustableCoords);
        // holes initialization [2010-01-03]
        holes = new Shell[poly.getNumInteriorRing()];
        for (int i = 0 ; i < holes.length ; i++) {
            holes[i] = new Shell(vmap, feature.getID(), i+1);
            holes[i].initialize((LinearRing) poly.getInteriorRingN(i), adjustableCoords);
        }
    }

    public Feature getFeature() { return feature; }
    
    // added by michaud on 2010-01-17
    public Shell[] getHoles() {
         return holes;
    }

    public boolean isAdjusted(double distanceTolerance) {
        if (!isProcessed) return false;
        isAdjusted |= shell.isAdjusted(distanceTolerance);
        for (Shell hole : holes) {
            isAdjusted |= hole.isAdjusted(distanceTolerance);
        }
        return isAdjusted;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * Creates a new geometry incorporating the adjusted shell.  Any holes in
     * the original geometry are cloned and added to the new geometry.
     *
     * @return an adjusted version of the geometry for this Feature
     * @return null if the adjusted geometry is invalid
     */
    public Geometry getAdjustedGeometry(double distanceTolerance) {
        Debug.println("      adjust shell");
        Coordinate[] coord = shell.getAdjusted(distanceTolerance);
        // check for a valid ring
        if (coord.length <= 3) return null;
        
        Polygon g = (Polygon) feature.getGeometry();
        GeometryFactory fact = new GeometryFactory(g.getPrecisionModel(), g.getSRID());
        
        // Holes processing
        List<LinearRing> rings = new ArrayList<LinearRing>();
        Coordinate[][] coords = new Coordinate[holes.length][];
        for (int i = 0 ; i < holes.length ; i++) {
            Debug.println("      adjust hole " + (i+1));
            coords[i] = holes[i].getAdjusted(distanceTolerance);
            if (coords[i].length <= 3) continue;
            rings.add(fact.createLinearRing(coords[i]));
        }
        Geometry adjGeom = fact.createPolygon(fact.createLinearRing(coord), rings.toArray(new LinearRing[0]));
        return adjGeom;
    }

    /**
     * Computes the adjustments to this feature.
     *
     * @param nearFeatures a list of CoverageGapFeatures that are close to this feature
     * @param segMatcher the SegmentMatcher to use, initialized with the distance tolerance
     */
    public void computeAdjustment(List nearFeatures,
                                  SegmentMatcher segMatcher,
                                  SegmentIndex matchedSegmentIndex) {
        computeAdjustmentSingle(nearFeatures, segMatcher, matchedSegmentIndex);
    }

    public boolean computeAdjustmentSingle(List nearFeatures,
                                SegmentMatcher segMatcher,
                                SegmentIndex matchedSegmentIndex) {
        isProcessed = true;
        boolean isModified = false;
        // Compare this feature with all near feature candidates
        for (Iterator i = nearFeatures.iterator(); i.hasNext(); ) {
            CoverageFeature cgf = (CoverageFeature) i.next();
            if (cgf == this) continue;
            //if (cgf.getFeature().getID() < feature.getID()) continue;
            Debug.println("      Try to match " + feature.getID() + "/0 with feature " + cgf.getFeature().getID() + "...");
            isModified |= shell.match(cgf.shell, segMatcher, matchedSegmentIndex);
            
            // proessing holes added by michaud on 2010-01-17
            int count = 0;
            for (Shell hole : holes) {
                Debug.println("      Try to match " + feature.getID() + "/" + (++count) + " with feature " + cgf.getFeature().getID() + "...");
                isModified |= hole.match(cgf.shell, segMatcher, matchedSegmentIndex);
            }
        }
        Debug.println("      modified = " + isModified);
        return isModified;
    }
}
