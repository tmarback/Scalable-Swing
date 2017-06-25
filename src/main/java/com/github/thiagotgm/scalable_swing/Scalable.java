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
 * Interface that defines a component capable of scaling with monitor resolution.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-06-25
 */
public interface Scalable {
    
    /**
     * Helper to obtain a Dimension object initialized to given width and height
     * in double precision.
     *
     * @param width Width of the dimension.
     * @param height Height of the dimension.
     * @return Dimension with the given width and height.
     */
    public static Dimension getDimension( double width, double height ) {
        
        Dimension dim = new Dimension();
        dim.setSize( width, height );
        return dim;
        
    }
    
    /**
     * Retrieves the minimum size of the component, scaled to the monitor's resolution.
     *
     * @return The minimum size of the component, in inches.
     * @see javax.swing.JComponent#getMinimumSize()
     */
    Dimension getScaledMinimumSize();
    
    /**
     * Retrieves the maximum size of the component, scaled to the monitor's resolution.
     *
     * @return The maximum size of the component, in inches.
     * @see javax.swing.JComponent#getMaximumSize()
     */
    Dimension getScaledMaximumSize();
    
    /**
     * Retrieves the preferred size of the component, scaled to the monitor's resolution.
     *
     * @return The preferred size of the component, in inches.
     * @see javax.swing.JComponent#getPrefferedSize()
     */
    Dimension getScaledPreferredSize();
    
    /**
     * Sets the minimum size of the component to a scaled value.
     *
     * @param d The minimum dimensions of the component, in inches.
     * @see javax.swing.JComponent#setMinimumSize(Dimension)
     */
    void setScaledMinimumSize( Dimension d );
    
    /**
     * Sets the maximum size of the component to a scaled value.
     *
     * @param d The maximum dimensions of the component, in inches.
     * @see javax.swing.JComponent#setMaximumSize(Dimension)
     */
    void setScaledMaximumSize( Dimension d );
    
    /**
     * Sets the preferred size of the component to a scaled value.
     *
     * @param d The preferred dimensions of the component, in inches.
     * @see javax.swing.JComponent#setPreferredSize(Dimension)
     */
    void setScaledPreferredSize( Dimension d );
    
    /**
     * Recalculates all dimensions to scale on the current monitor resolution.
     */
    void rescale();

}
