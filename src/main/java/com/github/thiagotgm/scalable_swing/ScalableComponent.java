/* ScalableComponent.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

/**
 * Interface that defines a component capable of scaling with monitor resolution.<br>
 * Behavior of getters and {@link #rescale()} in case a certain scaled size was not specified
 * (the corresponding size might have been set directly using JComponent size setters) is
 * dependent on implementation.
 *
 * @version 2.0
 * @author ThiagoTGM
 * @since 2017-06-25
 */
public interface ScalableComponent extends Scalable {
    
    /**
     * Retrieves the minimum size of the component, scaled to the monitor's resolution.
     *
     * @return The minimum size of the component, in inches.
     * @see javax.swing.JComponent#getMinimumSize()
     */
    RealDimension getScaledMinimumSize();
    
    /**
     * Retrieves the maximum size of the component, scaled to the monitor's resolution.
     *
     * @return The maximum size of the component, in inches.
     * @see javax.swing.JComponent#getMaximumSize()
     */
    RealDimension getScaledMaximumSize();
    
    /**
     * Retrieves the preferred size of the component, scaled to the monitor's resolution.
     *
     * @return The preferred size of the component, in inches.
     * @see javax.swing.JComponent#getPreferredSize()
     */
    RealDimension getScaledPreferredSize();
    
    /**
     * Sets the minimum size of the component to a scaled value.
     *
     * @param d The minimum dimensions of the component, in inches.
     * @see javax.swing.JComponent#setMinimumSize(Dimension)
     */
    void setScaledMinimumSize( RealDimension d );
    
    /**
     * Sets the maximum size of the component to a scaled value.
     *
     * @param d The maximum dimensions of the component, in inches.
     * @see javax.swing.JComponent#setMaximumSize(Dimension)
     */
    void setScaledMaximumSize( RealDimension d );
    
    /**
     * Sets the preferred size of the component to a scaled value.
     *
     * @param d The preferred dimensions of the component, in inches.
     * @see javax.swing.JComponent#setPreferredSize(Dimension)
     */
    void setScaledPreferredSize( RealDimension d );
    
    /**
     * Recalculates all sizes to scale on the current monitor resolution.<br>
     * If a certain size was not set it is not recalculated.
     */
    @Override
    void rescale();

}
