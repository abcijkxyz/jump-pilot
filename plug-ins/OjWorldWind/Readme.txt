OpenJump WorldWind plugin

Installation:
    • download WorldWind library (https://github.com/NASAWorldWind/WorldWindJava/releases) and extract zip folder
      in the OJ lib/ext/worldwind-2.1.0 folder
    • copy ojworldwind.jar to the lib/ext folder

Usage:
    • Right-click on a WGS-84 vector layer and choose "Display in WorldWind": a WW globe is displayed with the choosen
      layer.
    • To remove a layer from WorldWind, right-click on the layer and choose "Remove from WorldWind"
    • Choose view->WorldWind plugin to open a WorldWind globe

Features:
    • 3D geometries with an attribute named "height" will be extruded based on height value (name of height attribute
      can be configured in Options panel)
    • Features conversion can be done in JSON or Shapefile (some rendering difference between the 2 formats)
