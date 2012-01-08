package com.vividsolutions.jump.workbench.ui.renderer.style;

import java.awt.Paint;

import org.openjump.util.CustomTexturePaint;

import com.vividsolutions.jump.workbench.ui.images.IconLoader;

/**
 * <code>FillPatternFactory</code> creates a list of all known patterns.
 * 
 * @author unknown
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class FillPatternFactory {
    /**
     * You can add your own fill patterns to the Collection bearing this key on
     * the Workbench Blackboard (create the Collection if necessary).
     */
    public static String CUSTOM_FILL_PATTERNS_KEY = FillPatternFactory.class.getName() + " - CUSTOM FILL PATTERNS";

    /**
     * @return an array of all fill patterns
     */
    public Paint[] createFillPatterns() {
        return new Paint[] { 
                WKTFillPattern.createDiagonalStripePattern(2, 2, false, true),
                WKTFillPattern.createDiagonalStripePattern(2, 4, false, true),
                WKTFillPattern.createDiagonalStripePattern(4, 2, false, true),
                WKTFillPattern.createDiagonalStripePattern(4, 4, false, true),
                WKTFillPattern.createDiagonalStripePattern(6, 2, false, true),
                WKTFillPattern.createDiagonalStripePattern(6, 4, false, true),
                WKTFillPattern.createDiagonalStripePattern(2, 2, true, false),
                WKTFillPattern.createDiagonalStripePattern(2, 4, true, false),
                WKTFillPattern.createDiagonalStripePattern(4, 2, true, false),
                WKTFillPattern.createDiagonalStripePattern(4, 4, true, false),
                WKTFillPattern.createDiagonalStripePattern(6, 2, true, false),
                WKTFillPattern.createDiagonalStripePattern(6, 4, true, false),
                WKTFillPattern.createDiagonalStripePattern(2, 2, true, true),
                WKTFillPattern.createDiagonalStripePattern(2, 4, true, true),
                WKTFillPattern.createDiagonalStripePattern(4, 2, true, true),
                WKTFillPattern.createDiagonalStripePattern(4, 4, true, true),
                WKTFillPattern.createDiagonalStripePattern(6, 2, true, true),
                WKTFillPattern.createDiagonalStripePattern(6, 4, true, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(2, 2, false, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(2, 4, false, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(4, 2, false, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(4, 4, false, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(6, 2, false, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(6, 4, false, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(2, 2, true, false),
                WKTFillPattern.createVerticalHorizontalStripePattern(2, 4, true, false),
                WKTFillPattern.createVerticalHorizontalStripePattern(4, 2, true, false),
                WKTFillPattern.createVerticalHorizontalStripePattern(4, 4, true, false),
                WKTFillPattern.createVerticalHorizontalStripePattern(6, 2, true, false),
                WKTFillPattern.createVerticalHorizontalStripePattern(6, 4, true, false),
                WKTFillPattern.createVerticalHorizontalStripePattern(2, 2, true, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(2, 4, true, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(4, 2, true, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(4, 4, true, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(6, 2, true, true),
                WKTFillPattern.createVerticalHorizontalStripePattern(6, 4, true, true),
                //new ImageFillPattern(IconLoader.class, "40S-TWEED_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "40S-TWEED_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ALLOY-TEARS_C3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ASPHALT_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ASPHALT_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BACTERIA_E1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BACTERIA_E1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BARK_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BARK_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BASKET_A2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BASKET_A2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BATHROOM-TILES_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLOWN-CEMENT_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLOWN-CEMENT_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-CORN_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-CORN_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-PYRAMIDS_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-PYRAMIDS_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-RIVET_C3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-RIVET_C3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-WEAVE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BLUE-WEAVE_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BOBBLE-WEAVE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BOBBLE-WEAVE_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BOILERPLATE_C3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BOILERPLATE_C3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BROCADE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BROCADE_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BROWN-CELLS_E1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BUBBLOID_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "BUBBLOID_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CABLE-KNIT_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CABLE-KNIT_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CANTEEN-FLOOR_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CANTEEN-FLOOR_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CEILING-TILE_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CHECK-A-BLOCK_C6_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CHINESE-SILK_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CHINESE-SILK_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "COCONUT-HUSK_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "COCONUT-HUSK_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "COILS_C3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "COLLAGE_C4_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "COLOR-HEXAGONS_A2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "COLOR-HEXAGONS_A2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CRATOID_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CRATOID_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CRIMPLENE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CRIMPLENE_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "CUNEIFORM_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "DECKING_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "DECKING_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "DIRT-ROAD_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "DIRT-ROAD_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "DISH-CLOTH_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "DISH-CLOTH_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ELEPHANT-HIDE_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ELEPHANT-HIDE_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "EMBOSSED-PAPER_C4_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "EMBOSSED-PAPER_C4_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "FIBER-MATTING_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "FIBER-MATTING_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "FOIL-PACK_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GLASS-BEADS_C2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GLASS-BEADS_C2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GLASS-DRIPS_C2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GLASS-DRIPS_C2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GRAY-RENDER_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GRAY-RENDER_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GREEN-RENDER_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GREEN-RENDER_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GREEN-STUCCO_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GREEN-STUCCO_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GREEN-ZIGZAG_A2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "GREEN-ZIGZAG_A2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "HERRINGBONE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "HERRINGBONE_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "JACK-FROST_C2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "JACK-FROST_C2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "JELLY-BEANS_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "JELLY-BEANS_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LAVA-FLOW_C5_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LAVA-FLOW_C5_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LEATHER_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LEATHER_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LED-MATRIX_A2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LINOLEUM_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "LINOLEUM_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "METAL-MESH_C3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "METAL-MESH_C3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "MICA_C5_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "MICA_C5_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "MOLD_E1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "MOLD_E1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "MUSLIN_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "MUSLIN_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "OP-ART_A2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "OP-ART_A2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PAINT-SQUABS_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PEA-PODS_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PEPPERMINTS_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PEPPERMINTS_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PINK-CELLS_E1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PINK-CELLS_E1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PINK-STUCCO_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PINK-STUCCO_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PLASTER-SWIRLS_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PLASTER-SWIRLS_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PORCELAIN_C2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PORCELAIN_C2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PRESSED-ALLOY_C3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PURPLE-HIDE_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PURPLE-HIDE_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PURPLE-RENDER_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "PURPLE-RENDER_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RAFFIA-WEAVE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RED-STUCCO_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RED-STUCCO_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RED-ZIGZAG_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RED-ZIGZAG_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ROCK-STRATA_C5_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ROCK-STRATA_C5_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "ROMAN-ARMOR_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RUBBER-MATTING_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RUBBER-MATTING_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RUBBER-PEAKS_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RUBBER-PEAKS_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RUSH-MATTING_F1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "RUSH-MATTING_F1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "SECURITY-GLASS_C2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "SECURITY-GLASS_C2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "SMOCKING_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "SMOCKING_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "SPECTRAL-CHECK_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "SPECTRAL-CHECK_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "STARLIGHT_D1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "STARLIGHT_D1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "THREAD-BEAR_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "THREAD-BEAR_C1_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "TIMBER-YARD_C6_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "TIMBER-YARD_C6_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "TORTOISE-SHELL_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "TORTOISE-SHELL_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "VELVET-BODICE_C1_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "VINE-LEAF_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "VINE-LEAF_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "WOOD-TILE_C6_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "WOOD-VENEER_F2_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "WOOD-VENEER_F2_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "WORM-CASTS_E3_064_A_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "WORM-CASTS_E3_064_B_RGB.JPG"),
                //new ImageFillPattern(IconLoader.class, "warp-&-weft_C1_064_A_RGB.JPG"),
                new CustomTexturePaint() };
    }
}
