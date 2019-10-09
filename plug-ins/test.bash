#!/usr/bin/env bash
# 12 octobre 2018:
# converts tissot indicatrix from GDB to shapefile for OJ test:
ogr2ogr tissotellipses.shp /Users/nicolas/Downloads/TissotsIndicatrix/TissotsIndicatrix.gdb  TissotEllipses

# loads into postgis to test
shp2pgsql -ID -g geom -s 4326  tissotellipses.shp tissotellipses | psql


#batparis from serveur qv
pg_dump -p 5435 -h localhost -t batparis quelleville | psql