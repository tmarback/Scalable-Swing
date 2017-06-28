# Scalable-Swing
Container for Java Swing components that allow them to scale according to screen resolution (more specifically, DPI).
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

To use the library with Maven, add this as part of your `pom.xml` (where `@VERSION@` should be replaced by the desired release version):
```
<repositories>
  ...
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
...
<dependencies>
  ...
  <dependency>
    <groupId>com.github.ThiagoTGM</groupId>
    <artifactId>Scalable-Swing</artifactId>
    <version>@VERSION@</version>
  </dependency>
</dependencies>
```

OBS: This implementation is dependent on `Toolkit#getScreenResolution()`, which does not always return the accurate DPI value for a screen, and so the real screen dimensions might not reflect what was set through this API with perfect accuracy (it might be smaller than intended in some monitors).
