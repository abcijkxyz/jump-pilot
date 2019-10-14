OpenJump WorldWind plugin

Installation:
    • copy ojworldwind-<version>.jar to the lib/ext folder
    • download WorldWind library (https://github.com/NASAWorldWind/WorldWindJava/releases) and extract zip
      in the OJ lib/ext folder. It should create the worldwind-<version> folder (ex: lib/ext/worldwind-v2.1.0)
    • copy gluegen*.jar and jogl*.jar from lib/ext/worldwind-v2.1.0 to lib/ folder
    • Run OJ: worldwind plugin is available in the view menu or in a layer's context menu.

Usage:
    • Right-click on a WGS-84 vector layer and choose "Display in WorldWind": a WW globe is displayed with the choosen
      layer.
    • To remove a layer from WorldWind, right-click on the layer and choose "Remove from WorldWind"
    • Choose view->WorldWind plugin to open a WorldWind globe

Features:
    • 3D geometries with an attribute named "height" will be extruded based on height value (name of height attribute
      can be configured in Options panel)
    • Features conversion can be done in JSON or Shapefile (some rendering difference between the 2 formats)

Limitations:
    • Multipolygons are not yet supported with GeoJson conversion format. Shapefile must be used.
