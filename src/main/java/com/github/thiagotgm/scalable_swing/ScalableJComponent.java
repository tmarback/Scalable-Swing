/* ScalableJComponent.java
 *
 * Copyright (C) 2017 Thiago Marback
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.github.thiagotgm.scalable_swing;

import java.awt.AWTException;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EventListener;
import java.util.Locale;
import java.util.Set;

import javax.accessibility.AccessibleContext;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;

/**
 * Wrapper for a JComponent object that makes it scalable to monitor resolution.
 * <p>
 * All standard JComponent method calls made to the wrapper are simply redirected to
 * the wrapped component unless specified otherwise.<br>
 * For any methods that are not simply redirected, calling them directly through the
 * wrapped component instead of the wrapper may cause the wrapper to be desynched, and
 * any wrapper behavior after that is undefined.
 * <p>
 * The wrapped component can be retrieved at any point using {@link #getTarget()}.
 *
 * @version 1.1.0
 * @author ThiagoTGM
 * @since 2017-06-26
 * @param <T> The specific component type being wrapped.
 */
public class ScalableJComponent<T extends JComponent> extends JComponent implements ScalableComponent {
    
    /** Serial UID that represents this class. */
    private static final long serialVersionUID = 8280086683363343596L;
    
    private final T target;
    private final ComponentScaler scaler;
    
    private boolean scalePixelSize;
    private Dimension pixelMinSize;
    private Dimension pixelMaxSize;
    private Dimension pixelPreferredSize;
    
    /**
     * Instantiates a new scalable wrapper for the given component.
     *
     * @param target Component to be wrapped.
     */
    public ScalableJComponent( T target ) {
        
        this.target = target;
        this.scaler = new ComponentScaler( target );
        
    }

    @Override
    public RealDimension getScaledMinimumSize() {

        return scaler.getScaledMinimumSize();
        
    }

    @Override
    public RealDimension getScaledMaximumSize() {

        return scaler.getScaledMaximumSize();
        
    }

    @Override
    public RealDimension getScaledPreferredSize() {

        return scaler.getScaledPreferredSize();
        
    }

    @Override
    public void setScaledMinimumSize( RealDimension minimumSize ) {

        pixelMinSize = null;
        scaler.setScaledMinimumSize( minimumSize );
        
    }

    @Override
    public void setScaledMaximumSize( RealDimension maximumSize ) {

        pixelMaxSize = null;
        scaler.setScaledMaximumSize( maximumSize );
        
    }

    @Override
    public void setScaledPreferredSize( RealDimension preferredSize ) {

        pixelPreferredSize = null;
        scaler.setScaledPreferredSize( preferredSize );
        
    }

    /**
     * Recalculates all dimensions to scale on the current monitor resolution.<br>
     * If a certain dimension was not set, or was set as a pixel size and pixel sizes
     * are set to not be rescaled, it is not recalculated.<br>
     * If the component's Font is Scalable, it is also rescaled.
     */
    @Override
    public void rescale() {

        scaler.rescale();
        if ( !scalePixelSize ) { // Pixel sizes should not be scaled.
            if ( pixelMinSize != null ) { // Reset minimum pixel size if it was set.
                setMinimumSize( pixelMinSize );
            }
            if ( pixelMaxSize != null ) { // Reset maximum pixel size if it was set.
                setMaximumSize( pixelMaxSize );
            }
            if ( pixelPreferredSize != null ) { // Reset preferred pixel size if it was set.
                setPreferredSize( pixelPreferredSize );
            }
        }
        
    }
    
    /**
     * Retrieves the component being wrapped by the calling instance of this
     * class.
     *
     * @return The wrapped component.
     */
    public T getTarget() {
        
        return target;
        
    }
    
    /**
     * Sets whether pixel sizes set through {@link #setMinimumSize(Dimension)},
     * {@link #setMaximumSize(Dimension)}, or {@link #setPreferredSize(Dimension)}
     * should be rescaled to maintain the same on-screen dimensions when the resolution
     * or scaling values change.
     *
     * @param scale If true, any pixel sizes set will be rescaled to maintain the same
     *              on-screen size if {@link #rescale()} is called after the resolution
     *              or scaling values changed. If false, any pixel sizes set will be
     *              maintained constant when rescaled, until they are overwritten by
     *              setting the corresponding real sizes.
     */
    public void scalePixelSizes( boolean scale ) {
        
        scalePixelSize = scale;
        
    }
    
    /* Intercepts direct size changes to maintain consistency */

    /**
     * Sets the minimum size of this component to be the dimensions given, in pixels.
     * <p>
     * If the scaling properties or resolution change, the pixel dimensions
     * set will be changed to maintain the same real size.<br>
     * This behavior can be stopped by setting {@link #scalePixelSizes(boolean)} to
     * false. In that case, the specific pixel size set here will be maintained until
     * a real size is set through {@link #setScaledMinimumSize(RealDimension)}.
     *
     * @param minimumSize The minimum size, in pixels, for this component.
     */
    @Override
    public void setMinimumSize( Dimension minimumSize ) {

        pixelMinSize = minimumSize;
        scaler.setScaledMinimumSize( ScaleManager.unscale( minimumSize ) );
    
    }
    
    /**
     * Sets the maximum size of this component to be the dimensions given, in pixels.
     * <p>
     * If the scaling properties or resolution change, the pixel dimensions
     * set will be changed to maintain the same real size.<br>
     * This behavior can be stopped by setting {@link #scalePixelSizes(boolean)} to
     * false. In that case, the specific pixel size set here will be maintained until
     * a real size is set through {@link #setScaledMaximumSize(RealDimension)}.
     *
     * @param maximumSize The maximum size, in pixels, for this component.
     */
    @Override
    public void setMaximumSize( Dimension maximumSize ) {

        pixelMaxSize = maximumSize;
        scaler.setScaledMaximumSize( ScaleManager.unscale( maximumSize ) );
        
    }

    /**
     * Sets the preferred size of this component to be the dimensions given, in pixels.
     * <p>
     * If the scaling properties or resolution change, the pixel dimensions
     * set will be changed to maintain the same real size.<br>
     * This behavior can be stopped by setting {@link #scalePixelSizes(boolean)} to
     * false. In that case, the specific pixel size set here will be maintained until
     * a real size is set through {@link #setScaledPreferredSize(RealDimension)}.
     *
     * @param preferredSize The minimum size, in pixels, for this component.
     */
    @Override
    public void setPreferredSize( Dimension preferredSize ) {

        pixelPreferredSize = preferredSize;
        scaler.setScaledPreferredSize( ScaleManager.unscale( preferredSize ) );
        
    }
    
    /* ###### Delegates to the wrapped JComponent ###### */

    @Override
    public int hashCode() {

        return target.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {

        return target.equals( obj );
    }

    @Override
    public int getComponentCount() {

        return target.getComponentCount();
    }

    @Override
    @Deprecated
    public int countComponents() {

        return target.countComponents();
    }

    @Override
    public Component getComponent( int n ) {

        return target.getComponent( n );
    }

    @Override
    public Component[] getComponents() {

        return target.getComponents();
    }

    @Override
    @Deprecated
    public Insets insets() {

        return target.insets();
    }

    @Override
    public Component add( Component comp ) {

        return target.add( comp );
    }

    @Override
    public Component add( String name, Component comp ) {

        return target.add( name, comp );
    }

    @Override
    public Component add( Component comp, int index ) {

        return target.add( comp, index );
    }

    @Override
    public void setInheritsPopupMenu( boolean value ) {

        target.setInheritsPopupMenu( value );
    }

    @Override
    public boolean getInheritsPopupMenu() {

        return target.getInheritsPopupMenu();
    }

    @Override
    public void setComponentPopupMenu( JPopupMenu popup ) {

        target.setComponentPopupMenu( popup );
    }

    @Override
    public JPopupMenu getComponentPopupMenu() {

        return target.getComponentPopupMenu();
    }

    @Override
    public void updateUI() {

        target.updateUI();
    }

    @Override
    public String getUIClassID() {

        return target.getUIClassID();
    }

    @Override
    public void setComponentZOrder( Component comp, int index ) {

        target.setComponentZOrder( comp, index );
    }

    @Override
    public String getName() {

        return target.getName();
    }

    @Override
    public void setName( String name ) {

        target.setName( name );
    }

    @Override
    public Container getParent() {

        return target.getParent();
    }

    @Override
    @Deprecated
    public ComponentPeer getPeer() {

        return target.getPeer();
    }

    @Override
    public int getComponentZOrder( Component comp ) {

        return target.getComponentZOrder( comp );
    }

    @Override
    public void setDropTarget( DropTarget dt ) {

        target.setDropTarget( dt );
    }

    @Override
    public void update( Graphics g ) {

        target.update( g );
    }

    @Override
    public void paint( Graphics g ) {

        target.paint( g );
    }

    @Override
    public void add( Component comp, Object constraints ) {

        target.add( comp, constraints );
    }

    @Override
    public DropTarget getDropTarget() {

        return target.getDropTarget();
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {

        return target.getGraphicsConfiguration();
    }

    @Override
    public void add( Component comp, Object constraints, int index ) {

        target.add( comp, constraints, index );
    }

    @Override
    public Toolkit getToolkit() {

        return target.getToolkit();
    }

    @Override
    public boolean isValid() {

        return target.isValid();
    }

    @Override
    public boolean isDisplayable() {

        return target.isDisplayable();
    }

    @Override
    public boolean isVisible() {

        return target.isVisible();
    }

    @Override
    public void printAll( Graphics g ) {

        target.printAll( g );
    }

    @Override
    public void print( Graphics g ) {

        target.print( g );
    }

    @Override
    public Point getMousePosition() throws HeadlessException {

        return target.getMousePosition();
    }

    @Override
    public void remove( int index ) {

        target.remove( index );
    }

    @Override
    public boolean isPaintingTile() {

        return target.isPaintingTile();
    }

    @Override
    public boolean isShowing() {

        return target.isShowing();
    }

    @Override
    @Deprecated
    public boolean isManagingFocus() {

        return target.isManagingFocus();
    }

    @Override
    public void remove( Component comp ) {

        target.remove( comp );
    }

    @Override
    public boolean isEnabled() {

        return target.isEnabled();
    }

    @Override
    public void removeAll() {

        target.removeAll();
    }

    @Override
    @Deprecated
    public void setNextFocusableComponent( Component aComponent ) {

        target.setNextFocusableComponent( aComponent );
    }

    @Override
    @Deprecated
    public void enable( boolean b ) {

        target.enable( b );
    }

    @Override
    @Deprecated
    public Component getNextFocusableComponent() {

        return target.getNextFocusableComponent();
    }

    @Override
    public void setRequestFocusEnabled( boolean requestFocusEnabled ) {

        target.setRequestFocusEnabled( requestFocusEnabled );
    }

    @Override
    public void enableInputMethods( boolean enable ) {

        target.enableInputMethods( enable );
    }

    @Override
    public boolean isRequestFocusEnabled() {

        return target.isRequestFocusEnabled();
    }

    @Override
    public void requestFocus() {

        target.requestFocus();
    }

    @Override
    @Deprecated
    public void show() {

        target.show();
    }

    @Override
    public boolean requestFocus( boolean temporary ) {

        return target.requestFocus( temporary );
    }

    @Override
    public LayoutManager getLayout() {

        return target.getLayout();
    }

    @Override
    public void setLayout( LayoutManager mgr ) {

        target.setLayout( mgr );
    }

    @Override
    public boolean requestFocusInWindow() {

        return target.requestFocusInWindow();
    }

    @Override
    @Deprecated
    public void show( boolean b ) {

        target.show( b );
    }

    @Override
    public void doLayout() {

        target.doLayout();
    }

    @Override
    @Deprecated
    public void layout() {

        target.layout();
    }

    @Override
    public void grabFocus() {

        target.grabFocus();
    }

    @Override
    public void setVerifyInputWhenFocusTarget(
            boolean verifyInputWhenFocusTarget ) {

        target.setVerifyInputWhenFocusTarget( verifyInputWhenFocusTarget );
    }

    @Override
    public Color getForeground() {

        return target.getForeground();
    }

    @Override
    public void invalidate() {

        target.invalidate();
    }

    @Override
    public boolean getVerifyInputWhenFocusTarget() {

        return target.getVerifyInputWhenFocusTarget();
    }

    @Override
    public boolean isForegroundSet() {

        return target.isForegroundSet();
    }

    @Override
    public void validate() {

        target.validate();
    }

    @Override
    public FontMetrics getFontMetrics( Font font ) {

        return target.getFontMetrics( font );
    }

    @Override
    public Color getBackground() {

        return target.getBackground();
    }

    @Override
    public Dimension getPreferredSize() {

        return target.getPreferredSize();
    }

    @Override
    public boolean isBackgroundSet() {

        return target.isBackgroundSet();
    }

    @Override
    public Font getFont() {

        return target.getFont();
    }

    @Override
    public Dimension getMaximumSize() {

        return target.getMaximumSize();
    }

    @Override
    public Dimension getMinimumSize() {

        return target.getMinimumSize();
    }

    @Override
    public boolean contains( int x, int y ) {

        return target.contains( x, y );
    }

    @Override
    public boolean isFontSet() {

        return target.isFontSet();
    }

    @Override
    public void setBorder( Border border ) {

        target.setBorder( border );
    }

    @Override
    public Locale getLocale() {

        return target.getLocale();
    }

    @Override
    public void setLocale( Locale l ) {

        target.setLocale( l );
    }

    @Override
    public ColorModel getColorModel() {

        return target.getColorModel();
    }

    @Override
    @Deprecated
    public Dimension preferredSize() {

        return target.preferredSize();
    }

    @Override
    public Border getBorder() {

        return target.getBorder();
    }

    @Override
    public Insets getInsets() {

        return target.getInsets();
    }

    @Override
    public Point getLocation() {

        return target.getLocation();
    }

    @Override
    public Insets getInsets( Insets insets ) {

        return target.getInsets( insets );
    }

    @Override
    public Point getLocationOnScreen() {

        return target.getLocationOnScreen();
    }

    @Override
    @Deprecated
    public Dimension minimumSize() {

        return target.minimumSize();
    }

    @Override
    public float getAlignmentY() {

        return target.getAlignmentY();
    }

    @Override
    public void setAlignmentY( float alignmentY ) {

        target.setAlignmentY( alignmentY );
    }

    @Override
    public float getAlignmentX() {

        return target.getAlignmentX();
    }

    @Override
    public void setAlignmentX( float alignmentX ) {

        target.setAlignmentX( alignmentX );
    }

    @Override
    @Deprecated
    public Point location() {

        return target.location();
    }

    @Override
    public void setLocation( int x, int y ) {

        target.setLocation( x, y );
    }

    @Override
    public void setInputVerifier( InputVerifier inputVerifier ) {

        target.setInputVerifier( inputVerifier );
    }

    @Override
    public InputVerifier getInputVerifier() {

        return target.getInputVerifier();
    }

    @Override
    @Deprecated
    public void move( int x, int y ) {

        target.move( x, y );
    }

    @Override
    public Graphics getGraphics() {

        return target.getGraphics();
    }

    @Override
    public void setLocation( Point p ) {

        target.setLocation( p );
    }

    @Override
    public void setDebugGraphicsOptions( int debugOptions ) {

        target.setDebugGraphicsOptions( debugOptions );
    }

    @Override
    public Dimension getSize() {

        return target.getSize();
    }

    @Override
    @Deprecated
    public Dimension size() {

        return target.size();
    }

    @Override
    public void setSize( int width, int height ) {

        target.setSize( width, height );
    }

    @Override
    public int getDebugGraphicsOptions() {

        return target.getDebugGraphicsOptions();
    }

    @Override
    @Deprecated
    public void resize( int width, int height ) {

        target.resize( width, height );
    }

    @Override
    public void setSize( Dimension d ) {

        target.setSize( d );
    }

    @Override
    @Deprecated
    public void resize( Dimension d ) {

        target.resize( d );
    }

    @Override
    public void registerKeyboardAction( ActionListener anAction,
            String aCommand, KeyStroke aKeyStroke, int aCondition ) {

        target.registerKeyboardAction( anAction, aCommand, aKeyStroke,
                aCondition );
    }

    @Override
    public Rectangle getBounds() {

        return target.getBounds();
    }

    @Override
    @Deprecated
    public Rectangle bounds() {

        return target.bounds();
    }

    @Override
    public void setBounds( int x, int y, int width, int height ) {

        target.setBounds( x, y, width, height );
    }

    @Override
    public void paintComponents( Graphics g ) {

        target.paintComponents( g );
    }

    @Override
    public void printComponents( Graphics g ) {

        target.printComponents( g );
    }

    @Override
    public void addContainerListener( ContainerListener l ) {

        target.addContainerListener( l );
    }

    @Override
    public void removeContainerListener( ContainerListener l ) {

        target.removeContainerListener( l );
    }

    @Override
    public ContainerListener[] getContainerListeners() {

        return target.getContainerListeners();
    }

    @Override
    public void setBounds( Rectangle r ) {

        target.setBounds( r );
    }

    @Override
    public void registerKeyboardAction( ActionListener anAction,
            KeyStroke aKeyStroke, int aCondition ) {

        target.registerKeyboardAction( anAction, aKeyStroke, aCondition );
    }

    @Override
    public void unregisterKeyboardAction( KeyStroke aKeyStroke ) {

        target.unregisterKeyboardAction( aKeyStroke );
    }

    @Override
    public KeyStroke[] getRegisteredKeyStrokes() {

        return target.getRegisteredKeyStrokes();
    }

    @Override
    public int getConditionForKeyStroke( KeyStroke aKeyStroke ) {

        return target.getConditionForKeyStroke( aKeyStroke );
    }

    @Override
    public ActionListener getActionForKeyStroke( KeyStroke aKeyStroke ) {

        return target.getActionForKeyStroke( aKeyStroke );
    }

    @Override
    public boolean isLightweight() {

        return target.isLightweight();
    }

    @Override
    public void resetKeyboardActions() {

        target.resetKeyboardActions();
    }

    @Override
    public boolean isPreferredSizeSet() {

        return target.isPreferredSizeSet();
    }

    @Override
    public boolean isMinimumSizeSet() {

        return target.isMinimumSizeSet();
    }

    @Override
    public boolean isMaximumSizeSet() {

        return target.isMaximumSizeSet();
    }

    @Override
    @Deprecated
    public void deliverEvent( Event e ) {

        target.deliverEvent( e );
    }

    @Override
    public Component getComponentAt( int x, int y ) {

        return target.getComponentAt( x, y );
    }

    @Override
    public int getBaseline( int width, int height ) {

        return target.getBaseline( width, height );
    }

    @Override
    @Deprecated
    public Component locate( int x, int y ) {

        return target.locate( x, y );
    }

    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior() {

        return target.getBaselineResizeBehavior();
    }

    @Override
    public Component getComponentAt( Point p ) {

        return target.getComponentAt( p );
    }

    @Override
    public Point getMousePosition( boolean allowChildren )
            throws HeadlessException {

        return target.getMousePosition( allowChildren );
    }

    @Override
    @Deprecated
    public boolean requestDefaultFocus() {

        return target.requestDefaultFocus();
    }

    @Override
    public void setVisible( boolean aFlag ) {

        target.setVisible( aFlag );
    }

    @Override
    public Component findComponentAt( int x, int y ) {

        return target.findComponentAt( x, y );
    }

    @Override
    public void setEnabled( boolean enabled ) {

        target.setEnabled( enabled );
    }

    @Override
    public void setForeground( Color fg ) {

        target.setForeground( fg );
    }

    @Override
    public void setBackground( Color bg ) {

        target.setBackground( bg );
    }

    @Override
    public void setFont( Font font ) {

        target.setFont( font );
    }

    @Override
    public Component findComponentAt( Point p ) {

        return target.findComponentAt( p );
    }

    @Override
    public void setCursor( Cursor cursor ) {

        target.setCursor( cursor );
    }

    @Override
    public Cursor getCursor() {

        return target.getCursor();
    }

    @Override
    public boolean isAncestorOf( Component c ) {

        return target.isAncestorOf( c );
    }

    @Override
    public boolean isCursorSet() {

        return target.isCursorSet();
    }

    @Override
    public void paintAll( Graphics g ) {

        target.paintAll( g );
    }

    @Override
    public void setToolTipText( String text ) {

        target.setToolTipText( text );
    }

    @Override
    public void repaint() {

        target.repaint();
    }

    @Override
    public String getToolTipText() {

        return target.getToolTipText();
    }

    @Override
    public String getToolTipText( MouseEvent event ) {

        return target.getToolTipText( event );
    }

    @Override
    public void repaint( long tm ) {

        target.repaint( tm );
    }

    @Override
    public Point getToolTipLocation( MouseEvent event ) {

        return target.getToolTipLocation( event );
    }

    @Override
    public void repaint( int x, int y, int width, int height ) {

        target.repaint( x, y, width, height );
    }

    @Override
    public Point getPopupLocation( MouseEvent event ) {

        return target.getPopupLocation( event );
    }

    @Override
    public void list( PrintStream out, int indent ) {

        target.list( out, indent );
    }

    @Override
    public JToolTip createToolTip() {

        return target.createToolTip();
    }

    @Override
    public void scrollRectToVisible( Rectangle aRect ) {

        target.scrollRectToVisible( aRect );
    }

    @Override
    public void list( PrintWriter out, int indent ) {

        target.list( out, indent );
    }

    @Override
    public void setAutoscrolls( boolean autoscrolls ) {

        target.setAutoscrolls( autoscrolls );
    }

    @Override
    public boolean getAutoscrolls() {

        return target.getAutoscrolls();
    }

    @Override
    public void setTransferHandler( TransferHandler newHandler ) {

        target.setTransferHandler( newHandler );
    }

    @Override
    public boolean imageUpdate( Image img, int infoflags, int x, int y, int w,
            int h ) {

        return target.imageUpdate( img, infoflags, x, y, w, h );
    }

    @Override
    public Set<AWTKeyStroke> getFocusTraversalKeys( int id ) {

        return target.getFocusTraversalKeys( id );
    }

    @Override
    public TransferHandler getTransferHandler() {

        return target.getTransferHandler();
    }

    @Override
    public boolean areFocusTraversalKeysSet( int id ) {

        return target.areFocusTraversalKeysSet( id );
    }

    @Override
    public Image createImage( ImageProducer producer ) {

        return target.createImage( producer );
    }

    @Override
    public Image createImage( int width, int height ) {

        return target.createImage( width, height );
    }

    @Override
    public boolean isFocusCycleRoot( Container container ) {

        return target.isFocusCycleRoot( container );
    }

    @Override
    public VolatileImage createVolatileImage( int width, int height ) {

        return target.createVolatileImage( width, height );
    }

    @Override
    public VolatileImage createVolatileImage( int width, int height,
            ImageCapabilities caps ) throws AWTException {

        return target.createVolatileImage( width, height, caps );
    }

    @Override
    public boolean prepareImage( Image image, ImageObserver observer ) {

        return target.prepareImage( image, observer );
    }

    @Override
    public boolean prepareImage( Image image, int width, int height,
            ImageObserver observer ) {

        return target.prepareImage( image, width, height, observer );
    }

    @Override
    public void setFocusTraversalPolicy( FocusTraversalPolicy policy ) {

        target.setFocusTraversalPolicy( policy );
    }

    @Override
    public int checkImage( Image image, ImageObserver observer ) {

        return target.checkImage( image, observer );
    }

    @Override
    public FocusTraversalPolicy getFocusTraversalPolicy() {

        return target.getFocusTraversalPolicy();
    }

    @Override
    public int checkImage( Image image, int width, int height,
            ImageObserver observer ) {

        return target.checkImage( image, width, height, observer );
    }

    @Override
    public boolean isFocusTraversalPolicySet() {

        return target.isFocusTraversalPolicySet();
    }

    @Override
    public void setFocusCycleRoot( boolean focusCycleRoot ) {

        target.setFocusCycleRoot( focusCycleRoot );
    }

    @Override
    public boolean isFocusCycleRoot() {

        return target.isFocusCycleRoot();
    }

    @Override
    @Deprecated
    public void enable() {

        target.enable();
    }

    @Override
    public void transferFocusDownCycle() {

        target.transferFocusDownCycle();
    }

    @Override
    @Deprecated
    public void disable() {

        target.disable();
    }

    @Override
    public void applyComponentOrientation( ComponentOrientation o ) {

        target.applyComponentOrientation( o );
    }

    @Override
    public void addPropertyChangeListener( PropertyChangeListener listener ) {

        target.addPropertyChangeListener( listener );
    }

    @Override
    public void addPropertyChangeListener( String propertyName,
            PropertyChangeListener listener ) {

        target.addPropertyChangeListener( propertyName, listener );
    }

    @Override
    public void setFocusTraversalKeys( int id,
            Set<? extends AWTKeyStroke> keystrokes ) {

        target.setFocusTraversalKeys( id, keystrokes );
    }

    @Override
    @Deprecated
    public void reshape( int x, int y, int w, int h ) {

        target.reshape( x, y, w, h );
    }

    @Override
    public Rectangle getBounds( Rectangle rv ) {

        return target.getBounds( rv );
    }

    @Override
    public Dimension getSize( Dimension rv ) {

        return target.getSize( rv );
    }

    @Override
    public Point getLocation( Point rv ) {

        return target.getLocation( rv );
    }

    @Override
    public int getX() {

        return target.getX();
    }

    @Override
    public int getY() {

        return target.getY();
    }

    @Override
    public int getWidth() {

        return target.getWidth();
    }

    @Override
    public int getHeight() {

        return target.getHeight();
    }

    @Override
    public boolean isOpaque() {

        return target.isOpaque();
    }

    @Override
    public void setIgnoreRepaint( boolean ignoreRepaint ) {

        target.setIgnoreRepaint( ignoreRepaint );
    }

    @Override
    public void setOpaque( boolean isOpaque ) {

        target.setOpaque( isOpaque );
    }

    @Override
    public boolean getIgnoreRepaint() {

        return target.getIgnoreRepaint();
    }

    @Override
    @Deprecated
    public boolean inside( int x, int y ) {

        return target.inside( x, y );
    }

    @Override
    public boolean contains( Point p ) {

        return target.contains( p );
    }

    @Override
    public void computeVisibleRect( Rectangle visibleRect ) {

        target.computeVisibleRect( visibleRect );
    }

    @Override
    public Rectangle getVisibleRect() {

        return target.getVisibleRect();
    }

    @Override
    public void firePropertyChange( String propertyName, boolean oldValue,
            boolean newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void firePropertyChange( String propertyName, int oldValue,
            int newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void firePropertyChange( String propertyName, char oldValue,
            char newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void addVetoableChangeListener( VetoableChangeListener listener ) {

        target.addVetoableChangeListener( listener );
    }

    @Override
    public void removeVetoableChangeListener(
            VetoableChangeListener listener ) {

        target.removeVetoableChangeListener( listener );
    }

    @Override
    public VetoableChangeListener[] getVetoableChangeListeners() {

        return target.getVetoableChangeListeners();
    }

    @Override
    public Container getTopLevelAncestor() {

        return target.getTopLevelAncestor();
    }

    @Override
    public void addAncestorListener( AncestorListener listener ) {

        target.addAncestorListener( listener );
    }

    @Override
    public void removeAncestorListener( AncestorListener listener ) {

        target.removeAncestorListener( listener );
    }

    @Override
    public AncestorListener[] getAncestorListeners() {

        return target.getAncestorListeners();
    }

    @SuppressWarnings( "hiding" )
    @Override
    public <T extends EventListener> T[] getListeners( Class<T> listenerType ) {

        return target.getListeners( listenerType );
    }

    @Override
    public void addNotify() {

        target.addNotify();
    }

    @Override
    public void removeNotify() {

        target.removeNotify();
    }

    @Override
    public void repaint( long tm, int x, int y, int width, int height ) {

        target.repaint( tm, x, y, width, height );
    }

    @Override
    public void repaint( Rectangle r ) {

        target.repaint( r );
    }

    @Override
    public void revalidate() {

        target.revalidate();
    }

    @Override
    public boolean isValidateRoot() {

        return target.isValidateRoot();
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {

        return target.isOptimizedDrawingEnabled();
    }

    @Override
    public void paintImmediately( int x, int y, int w, int h ) {

        target.paintImmediately( x, y, w, h );
    }

    @Override
    @Deprecated
    public boolean postEvent( Event e ) {

        return target.postEvent( e );
    }

    @Override
    public void addComponentListener( ComponentListener l ) {

        target.addComponentListener( l );
    }

    @Override
    public void removeComponentListener( ComponentListener l ) {

        target.removeComponentListener( l );
    }

    @Override
    public void paintImmediately( Rectangle r ) {

        target.paintImmediately( r );
    }

    @Override
    public ComponentListener[] getComponentListeners() {

        return target.getComponentListeners();
    }

    @Override
    public void addFocusListener( FocusListener l ) {

        target.addFocusListener( l );
    }

    @Override
    public void removeFocusListener( FocusListener l ) {

        target.removeFocusListener( l );
    }

    @Override
    public FocusListener[] getFocusListeners() {

        return target.getFocusListeners();
    }

    @Override
    public void addHierarchyListener( HierarchyListener l ) {

        target.addHierarchyListener( l );
    }

    @Override
    public void removeHierarchyListener( HierarchyListener l ) {

        target.removeHierarchyListener( l );
    }

    @Override
    public HierarchyListener[] getHierarchyListeners() {

        return target.getHierarchyListeners();
    }

    @Override
    public void addHierarchyBoundsListener( HierarchyBoundsListener l ) {

        target.addHierarchyBoundsListener( l );
    }

    @Override
    public void removeHierarchyBoundsListener( HierarchyBoundsListener l ) {

        target.removeHierarchyBoundsListener( l );
    }

    @Override
    public void setDoubleBuffered( boolean aFlag ) {

        target.setDoubleBuffered( aFlag );
    }

    @Override
    public HierarchyBoundsListener[] getHierarchyBoundsListeners() {

        return target.getHierarchyBoundsListeners();
    }

    @Override
    public boolean isDoubleBuffered() {

        return target.isDoubleBuffered();
    }

    @Override
    public JRootPane getRootPane() {

        return target.getRootPane();
    }

    @Override
    public void addKeyListener( KeyListener l ) {

        target.addKeyListener( l );
    }

    @Override
    public void removeKeyListener( KeyListener l ) {

        target.removeKeyListener( l );
    }

    @Override
    public KeyListener[] getKeyListeners() {

        return target.getKeyListeners();
    }

    @Override
    public void addMouseListener( MouseListener l ) {

        target.addMouseListener( l );
    }

    @Override
    public void removeMouseListener( MouseListener l ) {

        target.removeMouseListener( l );
    }

    @Override
    public MouseListener[] getMouseListeners() {

        return target.getMouseListeners();
    }

    @Override
    public void addMouseMotionListener( MouseMotionListener l ) {

        target.addMouseMotionListener( l );
    }

    @Override
    public void removeMouseMotionListener( MouseMotionListener l ) {

        target.removeMouseMotionListener( l );
    }

    @Override
    public MouseMotionListener[] getMouseMotionListeners() {

        return target.getMouseMotionListeners();
    }

    @Override
    public void addMouseWheelListener( MouseWheelListener l ) {

        target.addMouseWheelListener( l );
    }

    @Override
    @Deprecated
    public void hide() {

        target.hide();
    }

    @Override
    public void removeMouseWheelListener( MouseWheelListener l ) {

        target.removeMouseWheelListener( l );
    }

    @Override
    public MouseWheelListener[] getMouseWheelListeners() {

        return target.getMouseWheelListeners();
    }

    @Override
    public void addInputMethodListener( InputMethodListener l ) {

        target.addInputMethodListener( l );
    }

    @Override
    public void removeInputMethodListener( InputMethodListener l ) {

        target.removeInputMethodListener( l );
    }

    @Override
    public InputMethodListener[] getInputMethodListeners() {

        return target.getInputMethodListeners();
    }

    @Override
    public InputMethodRequests getInputMethodRequests() {

        return target.getInputMethodRequests();
    }

    @Override
    public InputContext getInputContext() {

        return target.getInputContext();
    }

    @Override
    @Deprecated
    public boolean handleEvent( Event evt ) {

        return target.handleEvent( evt );
    }

    @Override
    @Deprecated
    public boolean mouseDown( Event evt, int x, int y ) {

        return target.mouseDown( evt, x, y );
    }

    @Override
    @Deprecated
    public boolean mouseDrag( Event evt, int x, int y ) {

        return target.mouseDrag( evt, x, y );
    }

    @Override
    @Deprecated
    public boolean mouseUp( Event evt, int x, int y ) {

        return target.mouseUp( evt, x, y );
    }

    @Override
    @Deprecated
    public boolean mouseMove( Event evt, int x, int y ) {

        return target.mouseMove( evt, x, y );
    }

    @Override
    @Deprecated
    public boolean mouseEnter( Event evt, int x, int y ) {

        return target.mouseEnter( evt, x, y );
    }

    @Override
    @Deprecated
    public boolean mouseExit( Event evt, int x, int y ) {

        return target.mouseExit( evt, x, y );
    }

    @Override
    @Deprecated
    public boolean keyDown( Event evt, int key ) {

        return target.keyDown( evt, key );
    }

    @Override
    @Deprecated
    public boolean keyUp( Event evt, int key ) {

        return target.keyUp( evt, key );
    }

    @Override
    @Deprecated
    public boolean action( Event evt, Object what ) {

        return target.action( evt, what );
    }

    @Override
    @Deprecated
    public boolean gotFocus( Event evt, Object what ) {

        return target.gotFocus( evt, what );
    }

    @Override
    @Deprecated
    public boolean lostFocus( Event evt, Object what ) {

        return target.lostFocus( evt, what );
    }

    @Override
    @Deprecated
    public boolean isFocusTraversable() {

        return target.isFocusTraversable();
    }

    @Override
    public boolean isFocusable() {

        return target.isFocusable();
    }

    @Override
    public void setFocusable( boolean focusable ) {

        target.setFocusable( focusable );
    }

    @Override
    public void setFocusTraversalKeysEnabled(
            boolean focusTraversalKeysEnabled ) {

        target.setFocusTraversalKeysEnabled( focusTraversalKeysEnabled );
    }

    @Override
    public boolean getFocusTraversalKeysEnabled() {

        return target.getFocusTraversalKeysEnabled();
    }

    @Override
    public Container getFocusCycleRootAncestor() {

        return target.getFocusCycleRootAncestor();
    }

    @Override
    public void transferFocus() {

        target.transferFocus();
    }

    @Override
    @Deprecated
    public void nextFocus() {

        target.nextFocus();
    }

    @Override
    public void transferFocusBackward() {

        target.transferFocusBackward();
    }

    @Override
    public void transferFocusUpCycle() {

        target.transferFocusUpCycle();
    }

    @Override
    public boolean hasFocus() {

        return target.hasFocus();
    }

    @Override
    public boolean isFocusOwner() {

        return target.isFocusOwner();
    }

    @Override
    public void add( PopupMenu popup ) {

        target.add( popup );
    }

    @Override
    public void remove( MenuComponent popup ) {

        target.remove( popup );
    }

    @Override
    public String toString() {

        return target.toString();
    }

    @Override
    public void list() {

        target.list();
    }

    @Override
    public void list( PrintStream out ) {

        target.list( out );
    }

    @Override
    public void list( PrintWriter out ) {

        target.list( out );
    }

    @Override
    public void removePropertyChangeListener(
            PropertyChangeListener listener ) {

        target.removePropertyChangeListener( listener );
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {

        return target.getPropertyChangeListeners();
    }

    @Override
    public void removePropertyChangeListener( String propertyName,
            PropertyChangeListener listener ) {

        target.removePropertyChangeListener( propertyName, listener );
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName ) {

        return target.getPropertyChangeListeners( propertyName );
    }

    @Override
    public void firePropertyChange( String propertyName, byte oldValue,
            byte newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void firePropertyChange( String propertyName, short oldValue,
            short newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void firePropertyChange( String propertyName, long oldValue,
            long newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void firePropertyChange( String propertyName, float oldValue,
            float newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void firePropertyChange( String propertyName, double oldValue,
            double newValue ) {

        target.firePropertyChange( propertyName, oldValue, newValue );
    }

    @Override
    public void setComponentOrientation( ComponentOrientation o ) {

        target.setComponentOrientation( o );
    }

    @Override
    public ComponentOrientation getComponentOrientation() {

        return target.getComponentOrientation();
    }

    @Override
    public AccessibleContext getAccessibleContext() {

        return target.getAccessibleContext();
    }
    
}
