/* RealDimension.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import java.awt.geom.Dimension2D;
import java.io.Serializable;

/**
 * Encapsulates a real-world dimension to be used by UI before scaling to resolution.<p>
 * Essentially an alternate to {@link java.awt.Dimension} that keeps double-precision dimensions.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-06-26
 */
public class RealDimension extends Dimension2D implements Serializable {
    
    /** Serial UID that represents this class. */
    private static final long serialVersionUID = -4707181191293074897L;
    
    public double width;
    public double height;
    
    /**
     * Creates an instance of RealDimension that has width and height zero.
     */
    public RealDimension() {}
    
    /**
     * Creates an instance of RealDimension with width and height equal to the
     * passed in RealDimension.
     *
     * @param d The dimension for the width and height values.
     */
    public RealDimension( RealDimension d ) {
        
        this.width = d.width;
        this.height = d.height;
        
    }
    
    /**
     * Creates an instance of RealDimension with the specified width and height.
     *
     * @param width Width of the RealDimension.
     * @param heigth Height of the RealDimension.
     */
    public RealDimension( double width, double heigth ) {
        
        this.width = width;
        this.height = heigth;
        
    }

    @Override
    public double getWidth() {

        return this.width;
        
    }

    @Override
    public double getHeight() {

        return this.height;
                
    }

    @Override
    public void setSize( double width, double height ) {

        this.width = width;
        this.height = height;

    }
    
    /**
     * Sets the width and height of this RealDimension to be equal to that of the
     * given RealDimension instance.
     *
     * @param d Dimensions to set on the calling instance.
     */
    public void setSize( RealDimension d ) {
        
        this.width = d.width;
        this.height = d.height;
        
    }
    
    /**
     * Determines if a given Object is a RealDimension with the same values.
     *
     * @param obj Object reference to compare to.
     * @return True if obj and the calling instance are RealDimensions with the same
     *         width and height. False otherwise.
     */
    @Override
    public boolean equals( Object obj ) {
        
        if ( !( obj instanceof RealDimension ) ) {
            return false; // Given Object is null or not a RealDimension.
        }
        RealDimension d = (RealDimension) obj;
        return ( this.width == d.width ) && ( this.height == d.height );
        
    }
    
    /**
     * Retrieves the String representation of this RealDimension.
     *
     * @return The String that represents this dimension.
     */
    @Override
    public String toString() {
        
        return String.format( "RealDimension-[ width=%f, height=%f ]", width, height );
        
    }

}
