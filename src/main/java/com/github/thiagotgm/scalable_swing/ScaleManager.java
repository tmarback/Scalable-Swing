/* ScaleManager.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Manages the scaling properties of program components.
 *
 * @version 3.0.0
 * @author ThiagoTGM
 * @since 2017-06-25
 */
public abstract class ScaleManager {
    
    private static final float DEFAULT_SCALE = 1;
    private static final float DEFAULT_TEXT_SCALE = 1;
    private static final int FONT_FACTOR = 72; // Typographic point size.
    
    private static float scale;
    private static float textScale;
    private static int resolution;
    
    private static double scaleMultiplier;
    private static float fontMultiplier;
            
    static {
        
        scale = DEFAULT_SCALE;
        textScale = DEFAULT_TEXT_SCALE;
        resolution = Toolkit.getDefaultToolkit().getScreenResolution(); // Get initial resolution.
        calculateMultipliers(); // Calculate the multipliers for the initial settings.
    
    }
    
    /**
     * Calculates the scale multiplier for components with the current resolution and scaling values.
     */
    private static void calculateMultipliers() {
        
        scaleMultiplier = resolution * scale; // Calculate based on resolution and scale value.
        calculateFontMultiplier(); // Calculate font multiplier as well.
        
    }
    
    /**
     * Calculates the scale multiplier for fonts with the current resolution and scaling values.
     */
    private static void calculateFontMultiplier() {

        fontMultiplier = (float) scaleMultiplier * textScale; // Calculate based on resolution and scale value.
        fontMultiplier /= FONT_FACTOR; // Convert to typographic point size.
                
    }
    
    /**
     * Scales a dimension to the current resolution and the current scaling values.<br>
     * eg, given the actual on-screen dimensions, calculates the equivalent pixel dimensions.
     *
     * @param dim Dimension to be scaled (in inches).
     * @return The scaled dimension, in pixels, accounting for the current scaling multipliers.
     *         If the dimension passed in is null, null is returned.
     */
    public static Dimension scale( RealDimension dim ) {
        
        if ( dim == null ) {
            return null;
        }
        double width = dim.getWidth() * scaleMultiplier; // Calculate width and height.
        double height = dim.getHeight() * scaleMultiplier;
        return Scalable.getDimension( width, height ); // Package into dimension.
        
    }
    
    /**
     * Unscales a dimension with the current resolution and the current scaling values.<br>
     * eg, given the pixel dimensions, calculates the actual on-screen dimensions.
     *
     * @param dim Dimension to be scaled (in pixels).
     * @return The real dimension, in inches, accounting for the current scaling multipliers.
     *         If the dimension passed in is null, null is returned.
     */
    public static RealDimension unscale( Dimension dim ) {
        
        if ( dim == null ) {
            return null;
        }
        double width = dim.getWidth() / scaleMultiplier; // Calculate width and height.
        double height = dim.getHeight() / scaleMultiplier;
        return new RealDimension( width, height ); // Package into dimension.
        
    }
    
    /**
     * Scales a font size to the current resolution and the current scaling values.
     *
     * @param fontSize Font size (2D) to be scaled.
     * @return The scaled font size, accounting for the current scaling multipliers.
     */
    public static float scaleFontSize( float fontSize ) {
        
        return fontSize * fontMultiplier; // Calculate scaled font size.
        
    }
    
    /**
     * Sets the scale for all components.
     *
     * @param scale The scaling value to be used (1 is 100% scale).
     */
    public static void setScale( float scale ) {
        
        ScaleManager.scale = scale;
        calculateMultipliers(); // Update size multipliers.
        // TODO Trigger an event
        
    }
    
    /**
     * Sets the scale for all text.
     *
     * @param textScale The scaling value to be used (1 is 100% scale).
     */
    public static void setTextScale( float textScale ) {
        
        ScaleManager.textScale = textScale;
        calculateFontMultiplier(); // Update text size multiplier.
        // TODO Trigger an event
        
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
    
    /**
     * Retrieves the screen resolution used for scaling.
     *
     * @return The currently used screen resolution, in pixels per inch.
     * @see Toolkit#getScreenResolution()
     */
    public static int getResolution() {
        
        return resolution;
        
    }
    
    /**
     * Checks for resolution changes.
     */
    public static void updateResolution() {
        
        int curResolution = Toolkit.getDefaultToolkit().getScreenResolution(); // Get current resolution.
        if ( resolution != curResolution ) { // If the resolution changed.
            resolution = curResolution; // Update resolution.
            calculateMultipliers(); // Update size multipliers.
            // TODO Trigger an event
        }
        
    }

}
