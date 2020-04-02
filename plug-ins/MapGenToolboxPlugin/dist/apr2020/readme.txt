MapGen Toolbox Version 1.2 - 2020/04/02 (aligned with java8 and OpenJUMP 1.15)

MapGen Toolbox Installation instructions
----------------------------------------

copy the following files into OpenJUMPs "ext" folder:
- mapgen-toolbox-1.2.jar
- jmat_5.0.jar

restart OpenJUMP.

You should find a new set of functions under the main menu item 
Plugins > Generalization > ...

For the license terms under which the software/toolbox can be used see the license.txt file.

Changes from version 1.1 from 2013
----------------------------------
Small update to make the extension compatible with current OpenJUMP version (1.15)

Changes to Version 1.0 from 2006
--------------------------------
- minor updates: placed in correct OpenJUMP menu "Plugins" and improved spelling of Function names.
- updated the function "Square Selected Buildings", to check the outputs of the squaring algorithms. For that a new value (allowed change in area) needs to be provided by the user. Produced invalid geometries and squaring results with a large deviation in area from input building are now returned in a separate layer. These problematic buildings should then be generalized manually by the user.
- added Bezier curve based Smoothing Plugin by Michael Michaud and Michael Bedward 
- added Orthogonalize Plugin by Larry Becker 
