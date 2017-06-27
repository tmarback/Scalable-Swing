# Scalable-Swing
Container for Java Swing components that allow them to scale according to screen resolution.
Also allows setting scaling values through ScaleManager to make all scaled components larger or smaller.

Currently allows two ways to make components scalable:

- Create a ComponentScaler that scales the component. This is in case a separate size controller might be desireable.
- Create a ScalableJComponent that wraps the component. The wrapper can (and should) be used in place of the wrapped component itself, except for functionality that is specific to a subtype of JComponent.

Planned features:

- Making specific subtype wrappers for common JComponent subtypes, allowing full replacement of common components;
- Scaling text automatically;
- Allowing components to register themselves to rescale whenever the resolution or scaling values change;
- Wrapper for AWT components, for some classes used on layout management;
- Wrappers for non-component but commonly used types (such as JFrame and Insets).
