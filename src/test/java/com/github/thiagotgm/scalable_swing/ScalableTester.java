/* ScalableTester.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.junit.Test;

/**
 * JUnit tester for the static helper methods in the {@link Scalable} interface.
 *
 * @version 1.0
 * @author ThiagoTGM
 * @since 2017-06-26
 */
public class ScalableTester {
    
    @Test
    public void testgetDimension() {
        
        /* Test normal case */
        Dimension dim = Scalable.getDimension( 2.56, 63.99 );
        assertEquals( "Incorrect dimension width.", 3.0, dim.getWidth(), 0.000001 );
        assertEquals( "Incorrect dimension height.", 64.0, dim.getHeight(), 0.000001 );
        
        /* Test values outside integer range */
        dim = Scalable.getDimension( Integer.MIN_VALUE - 10.0, Integer.MAX_VALUE + 10.0 );
        assertEquals( "Incorrect dimension width.", (double) Integer.MIN_VALUE, dim.getWidth(), 0.000001 );
        assertEquals( "Incorrect dimension height.", (double) Integer.MAX_VALUE, dim.getHeight(), 0.000001 );
        
        /* Test limit values in the integer range */
        dim = Scalable.getDimension( 0.0, (double) Integer.MAX_VALUE );
        assertEquals( "Incorrect dimension width.", 0.0, dim.getWidth(), 0.000001 );
        assertEquals( "Incorrect dimension height.", (double) Integer.MAX_VALUE, dim.getHeight(), 0.000001 );
        
        dim = Scalable.getDimension( 0.0, (double) Integer.MIN_VALUE );
        assertEquals( "Incorrect dimension width.", 0.0, dim.getWidth(), 0.000001 );
        assertEquals( "Incorrect dimension height.", (double) Integer.MIN_VALUE, dim.getHeight(), 0.000001 );
        
    }

}
