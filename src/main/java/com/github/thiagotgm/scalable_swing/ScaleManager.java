/* ScaleManager.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import java.awt.Dimension;
import java.awt.Font;

/**
 * Manages the scaling properties of program components.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-06-25
 */
public abstract class ScaleManager {
    
    private static final float DEFAULT_SCALE = 1;
    private static final float DEFAULT_TEXT_SCALE = 1;
    private static final int FONT_DIVISOR = 70;
    
    private static float scale = DEFAULT_SCALE;
    private static float textScale = DEFAULT_TEXT_SCALE;
    
    /**
     * Scales a dimension to the given resolution and the current scaling values.<br>
     * eg, given the actual on-screen dimensions, calculates the equivalent pixel dimensions.
     *
     * @param dim Dimension to be scaled (in inches).
     * @param resolution Resolution to scale to.
     * @return The scaled dimension, in pixels, accounting for the current scaling multipliers.
     */
    public static Dimension scale( RealDimension dim, int resolution ) {
        
        resolution *= scale; // Accounts for current scale.
        double width = dim.getWidth() * resolution; // Calculate width and height.
        double height = dim.getHeight() * resolution;
        return Scalable.getDimension( width, height ); // Package into dimension.
        
    }
    
    /**
     * Unscales a dimension with the given resolution and the current scaling values.<br>
     * eg, given the pixel dimensions, calculates the actual on-screen dimensions.
     *
     * @param dim Dimension to be scaled (in pixels).
     * @param resolution Resolution the dimension was scaled to.
     * @return The real dimension, in inches, accounting for the current scaling multipliers.
     */
    public static RealDimension unscale( Dimension dim, int resolution ) {
        
        resolution *= scale; // Accounts for current scale.
        double width = dim.getWidth() / resolution; // Calculate width and height.
        double height = dim.getHeight() / resolution;
        return new RealDimension( width, height ); // Package into dimension.
        
    }
    
    /**
     * Scales a dimension to the given resolution and the current scaling values.
     *
     * @param font Font to be scaled. Assumed to not be scaled to any resolution.
     * @param resolution Resolution to scale to.
     * @return The scaled resolution, accounting for the current scaling multipliers.
     */
    public static Font scale( Font font, int resolution ) {
        
        float multiplier = resolution / FONT_DIVISOR; // Calculates font multiplier.
        multiplier *= scale * textScale; // Accounts for scaling values.
        return font.deriveFont( font.getSize2D() * multiplier ); // Create new font.
        
    }
    
    /**
     * Sets the scaling multiplier for all components.
     *
     * @param scale The scaling value to be used (1 is 100% scale).
     */
    public static void setScale( float scale ) {
        
        ScaleManager.scale = scale;
        
    }
    
    /**
     * Sets the scaling multiplier for all text.
     *
     * @param scale The scaling value to be used (1 is 100% scale).
     */
    public static void setTextScale( float textScale ) {
        
        ScaleManager.textScale = textScale;
        
    }
    
    /**
     * Retrieves the current scaling multiplier for all components.
     *
     * @return The scaling value currently being used (1 is 100% scale).
     */
    public static float getScale() {
        
        return scale;
        
    }
    
    /**
     * Retrieves the current scaling multiplier for all text.
     *
     * @return The scaling value currently being used (1 is 100% scale).
     */
    public static float getTextScale() {
        
        return textScale;
        
    }

}
