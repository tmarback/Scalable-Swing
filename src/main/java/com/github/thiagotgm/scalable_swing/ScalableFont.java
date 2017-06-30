/* ScalableFont.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

/**
 * Variant of Font that scales its size with respect to monitor resolution and scaling settings.
 * <p>
 * All deriveFont methods are applied to the base font (that represents the calling instance
 * without scaling), with the returned instances scaling the derived base font.
 *
 * @version 1.0.0
 * @author ThiagoTGM
 * @since 2017-06-30
 */
public class ScalableFont extends Font implements Scalable {
    
    /** Serial UID that represents this class. */
    private static final long serialVersionUID = 7486472961971502586L;
    protected float unscaledSize;

    /**
     * Constructs a new font with the given attributes. The size attribute is assumed
     * to be unscaled and will be scaled in the constructed font.
     *
     * @param attributes Attributes to be used in the constructed font.
     * @see Font#Font(Map)
     */
    public ScalableFont( Map<? extends Attribute, ?> attributes ) {
        
        super( attributes );
        unscaledSize = pointSize; // The size after initialization is unscaled.
        rescale(); // Scale size.
        
    }

    /**
     * Constructs a new font from the given font. If the font given is already a ScalableFont,
     * all the sizes are maintained. Else, the size of the given font is assumed as being
     * unscaled and will be scaled by the constructed font.
     *
     * @param font Font to base the new font of.
     */
    public ScalableFont( Font font ) {
        
        super( font );
        if ( font instanceof ScalableFont ) { // Font given is already scalable.
            this.unscaledSize = ( (ScalableFont) font ).unscaledSize; // Use same unscaled size.
        } else { // Font given is not a ScalableFont.
            unscaledSize = pointSize; // The size of the given font is unscaled.
            rescale(); // Scale size.
        }
        
    }

    /**
     * Creates a font with the given characteristics.
     * The size is scaled to the current resolution and scaling values.
     *
     * @param name Name of the font.
     * @param style Style constant for the font.
     * @param size Unscaled point size for the font.
     * @see Font#Font(String, int, int)
     */
    public ScalableFont( String name, int style, int size ) {
        
        super( name, style, size );
        unscaledSize = size;
        rescale(); // Scale size.
        

    }
    
    /**
     * Retrieves the Font that describes this ScaledFont without
     * scaling.
     *
     * @return The unscaled Font.
     */
    public Font unscaledFont() {
        
        return super.deriveFont( unscaledSize ); // Get font with unscaled size.
        
    }

    @Override
    public void rescale() {

        pointSize = ScaleManager.scaleFontSize( unscaledSize ); // Scale font size.
        size = Math.round( pointSize ); // Round size to integer.

    }

    @Override
    public ScalableFont deriveFont( int style, float size ) {

        return new ScalableFont( unscaledFont().deriveFont( style, size ) );

    }

    @Override
    public ScalableFont deriveFont( int style, AffineTransform trans ) {

        return new ScalableFont( unscaledFont().deriveFont( style, trans ) );
        
    }

    @Override
    public ScalableFont deriveFont( float size ) {

        return new ScalableFont( unscaledFont().deriveFont( size ) );
        
    }

    @Override
    public ScalableFont deriveFont( AffineTransform trans ) {

        return new ScalableFont( unscaledFont().deriveFont( trans ) );
        
    }

    @Override
    public ScalableFont deriveFont( int style ) {

        return new ScalableFont( unscaledFont().deriveFont( style ) );
        
    }

    @Override
    public ScalableFont deriveFont( Map<? extends Attribute, ?> attributes ) {

        return new ScalableFont( unscaledFont().deriveFont( attributes ) );
        
    }

}
