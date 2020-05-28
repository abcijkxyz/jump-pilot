#!/bin/sh

set -e

#run at top level of source directory 

TMPDIR=/tmp/eraseme_jumpplugin
VERSION=1.0.0
gradle clean packageSrcZip
rm -rf $TMPDIR/ojmapcoloring-src-$VERSION.zip
rm -rf $TMPDIR/ojmapcoloring-src-$VERSION
mkdir -p $TMPDIR
cp build/distributions/ojmapcoloring-src-$VERSION.zip $TMPDIR/
cd $TMPDIR/
unzip ojmapcoloring-src-$VERSION.zip
mkdir $TMPDIR/ojmapcoloring-src-$VERSION/lib/
cp ~/dev/java/jumpdbqplugin-code/lib/ojdbc6.jar $TMPDIR/ojmapcoloring-src-$VERSION/lib/
cd ojmapcoloring-src-$VERSION
gradle packageSrcZip
