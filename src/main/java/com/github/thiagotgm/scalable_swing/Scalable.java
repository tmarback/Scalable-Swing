/* Scalable.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import java.awt.Dimension;

/**
 * Defines a GUI element that can be rescaled to the current monitor resolution.
 *
 * @version 1.0.0
 * @author ThiagoTGM
 * @since 2017-06-26
 */
public interface Scalable {
    
    /**
     * Helper to obtain a Dimension object initialized to given width and height
     * in double precision.<br>
     * The actual width and height of the Dimension returned will be rounded to
     * the nearest integer value, and are kept within valid integer range.
     *
     * @param width Width of the dimension.
     * @param height Height of the dimension.
     * @return Dimension with the given width and height.
     * @see Dimension#setSize(double, double)
     */
    public static Dimension getDimension( double width, double height ) {
        
        Dimension dim = new Dimension();
        dim.setSize( width, height );
        return dim;
        
    }

    /**
     * Recalculates all sizes to scale on the current monitor resolution and scaling settings.
     */
    void rescale();

}