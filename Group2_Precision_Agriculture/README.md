# CSYE7374_Final

## database (use mysql driver):
ec2-52-20-252-81.compute-1.amazonaws.com:3306

## db monitor:
http://ec2-52-20-252-81.compute-1.amazonaws.com:9000

## db python notebook:
http://ec2-52-20-252-81.compute-1.amazonaws.com:8888

## spark cluster master:
ec2-52-21-30-155.compute-1.amazonaws.com:4040

## spark zeppelin scala notebook:
http://ec2-52-21-30-155.compute-1.amazonaws.com:8080/

## find the nearest point from db e.g. on longitude=-96.855 and latitude=39.74:
```sql
select m.station, m.name, m.state, m.latitude, m.longitude, m.elevation, c.records from stationMeta m inner join stationRecordCount c on m.station = c.station order by geography_distance(location, 'point(-96.855 39.74)') limit 1;
```
result:
```
+-------------+-------------+-------+----------+-----------+-----------+---------+
| station     | name        | state | latitude | longitude | elevation | records |
+-------------+-------------+-------+----------+-----------+-----------+---------+
| USC00252820 | FAIRBURY 5S | NE    |  40.0739 |  -97.1669 |     411.5 |   12569 |
+-------------+-------------+-------+----------+-----------+-----------+---------+
```
