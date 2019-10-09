-- 12 octobre 2018:
-- test for 4326 objects : 3D, etc

select st_summary(geom), st_asewkt(geom)
from tissotellipses;

-- adds 3D:

alter table tissotellipses alter column geom type geometry(polygon, 4326) using st_geometryN(geom, 1);

select st_asewkt(geom)
from fdr;

drop table fdr_simple;
create table fdr_simple as
  select id, geom
  from fdr
  tablesample system(10)
  ;

-- sample
create table fdr_simple2 as select * from ta;

drop table fdr_fir_simple;
create table fdr_fir_simple as
select flight_id, st_simplifypreservetopology(geom, 0.0025)
from fdr_fir;

-- batiments 3D paris:
alter table batparis alter column geom type geometry(multipolygon, 4326) using st_transform(st_setsrid(geom, 2154), 4326);
create index batparis_geom_gist on batparis using gist (geom);
analyse batparis;

-- transform to 3D
select gid, altitude, height, hauteur_ign, hauteur_osm, st_asewkt(geom)
from batparis;

-- by boundary + set point= pas terrible
with tmp as (
    select gid, height, st_dump(st_boundary(geom)) as d
    from batparis
) select gid, height, st_asewkt(st_setpoint(
                st_force3d((d).geom),
                g-1,
                st_makePoint(
                  st_X(st_pointN((d).geom, g)),
                  st_Y(st_pointN((d).geom, g)),
                  height))) as geom
from tmp t cross join generate_series(1, st_numpoints((d).geom)) as g;

with tmp as (
    select gid, height, st_dump(st_boundary(geom)) as d
    from batparis
     limit 10
    ) select gid, height, g, st_numpoints((d).geom), (d).path
from tmp t cross join generate_series(1, st_numpoints((d).geom)) as g;

-- par dumppoints:
drop table batparis3d;
create table batparis3d as
  with tmp as (
      select gid, height, altitude, st_dumpPoints(st_boundary(geom)) as d
      from batparis
      where st_numgeometries(geom) = 1
  --     where gid = 2885
  ), tmp1 as (
      select gid,height, altitude,(d).path,
             st_makePoint(st_X((d).geom), st_Y((d).geom), altitude + 50) as newgeom
      from tmp t
  ), tmp2 as (
      select gid, path [ 1 ] as path, height, st_makeline(newgeom order by path [2]) as geom
      from tmp1
      group by gid, path [ 1 ], height
  ), tmp3 as (
      select gid, height,  array_agg(geom order by path) as geomarr
      from tmp2
      group by gid, height
  ) select gid, height,
           st_setSRID(st_makepolygon(geomarr[1], geomarr[2:]), 4326)::geometry(polygonz, 4326) as geom
  --          st_isclosed(geomarr[1]), geomarr[2:]
  from tmp3;

alter table batparis3d add primary key (gid);
create index batparis3d_geom_gist on batparis3d using gist(geom);
vacuum analyse batparis3d;

select st_asewkt(geom)
from batparis3d;

select st_astext(geom)
from batparis
where gid = 2885;

select st_isvalidreason(geom)
from batparis3d
where not st_isvalid(geom);

select min(height), max(height)
from batparis;

select ('{1, 2, 3}'::int[])[2:];

with tmp as (
  select gid, name, geom::box2d as e from country
) select *
from tmp
where st_xmin(e) < -180.0;

update country set geom = st_buffer(geom, -0.001)
where gid = 156;

-- 16 octobre 2018:
-- test custom height generation from countries to diplay extruded polygons:
select c.name
from country c;

-- table population
create table country_pop (
  id int primary key ,
  name text,
  continent_region text,
  stat_region text,
  pop2016 bigint,
  pop2017 bigint,
  change text
);

copy country_pop from '/Users/nicolas/tmp/country_pop.csv' with (format csv, header false, delimiter ',');

-- some cleanup
select name, regexp_replace(name, E'\\[.*\\]', '') as newname
from country_pop;

select format('|%s|', name), format('|%s|', trim(both ' ' from name))
from country_pop;
-- hou hugly space char
-- | Jordan|

update country_pop set name = ltrim(regexp_replace(name, E'\\[.*\\]', ''));
update country_pop set name = trim(both ' ' from name);

-- must have an Z coord > 0 for extrusion
drop table country_stats;
create table country_stats as
select c.gid, c.name, p.pop2016, p.pop2017, p.pop2017 * 100 * 1000/(select sum(pop2017)::float from country_pop) as height,
     st_geomfromewkt(replace(replace(st_asewkt(st_force3d(c.geom)), ' 0,', ' 10,'), ' 0)', ' 10)')) as geom
from country as c left join country_pop as p on c.name = p.name;
