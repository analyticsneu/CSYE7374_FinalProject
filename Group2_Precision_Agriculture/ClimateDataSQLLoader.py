import MySQLdb
import gzip
import os
from datetime import date, timedelta
import calendar
import random
from collections import OrderedDict

def createTable(cursor):
    createSQLList = []
    createSQLList.append("create table if not exists climateDaily ( \
                                            station varchar(11), \
                                            date date, \
                                            prcp double, \
                                            tmax double, \
                                            tmin double, \
                                            tavg double, \
                                            primary key (station, date));")
    createSQLList.append("create table if not exists stationMeta ( \
                                            station varchar(11) primary key, \
                                            latitude double, \
                                            longitude double, \
                                            elevation double, \
                                            state varchar(2), \
                                            name varchar(256), \
                                            location as geography_point(longitude, latitude) persisted geographypoint, \
                                            index (station, location));")
    createSQLList.append("create table if not exists climateMonthly ( \
                                            station varchar(11), \
                                            date date, \
                                            prcp double, \
                                            tmax double, \
                                            tmin double, \
                                            tavg double, \
                                            primary key (station, date));")
    createSQLList.append("create table if not exists climateYearly ( \
                                            station varchar(11), \
                                            date date, \
                                            prcp double, \
                                            tmax double, \
                                            tmin double, \
                                            tavg double, \
                                            primary key (station, date));")
    for createSQL in createSQLList:
        cursor.execute(createSQL)

def insertDailyData(dataDir, cursor):
    insertDailySQLTemplate = "insert ignore into climateDaily \
                                (station, date, prcp, tmax, tmin, tavg) \
                                values ('%s', '%s', %s, %s, %s, %s);"
    stations = []
    for fileName in os.listdir(dataDir):
        print "processing file: %s" % fileName
        station = fileName[:-4]
        timeDict = parseFileContent(dataDir+"/"+fileName)
        for date, record in timeDict.iteritems():
            insertDailySQL = insertDailySQLTemplate % (station,
                                                       date,
                                                       record["PRCP"],
                                                       record["TMAX"],
                                                       record["TMIN"],
                                                       record["TAVG"])
            print insertDailySQL
            cursor.execute(insertDailySQL)
        print "inserted %s records" % len(timeDict)
        stations.append(station)
    print "Total files: %s" % len(stations)
    return stations

def parseFileContent(fileName):
    with open(fileName, "rb") as f:
        timeDict = OrderedDict()
        minDate = date(1981, 1, 1)
        maxDate = date(2015, 8, 1)
        while(minDate < maxDate):
            timeDict[minDate] = {"PRCP":-9999,
                                 "TMAX":-9999,
                                 "TMIN":-9999,
                                 "TAVG":-9999}
            minDate = minDate + timedelta(days=1)
        for line in f:
            element = line[17:21]
            startDate = date(int(line[11:15]), int(line[15:17]), 1)
            if(startDate in timeDict):
                for i in range(calendar.monthrange(int(line[11:15]),int(line[15:17]))[1]):
                    currDate = startDate + timedelta(days=i)
                    if(element in timeDict[currDate]):
                        timeDict[currDate][element] = float(line[21+8*i:26+8*i])
        # fix missing value
        for recordDate in timeDict:
            record = timeDict[recordDate]
            if(record["PRCP"]==-9999): record["PRCP"]==0
            if(record["TMAX"]==-9999):
                for lag in range(1, 31):
                    testDates = [recordDate - timedelta(365*lag),
                                recordDate + timedelta(365*lag),
                                recordDate - timedelta(lag),
                                recordDate + timedelta(lag)]
                    for testDate in testDates:
                        if(testDate in timeDict and timeDict[testDate]["TMAX"]!=-9999):
                            record["TMAX"] = timeDict[testDate]["TMAX"]
                            break
                    if(record["TMAX"]!=-9999): break
                if(record["TMAX"]==-9999): record["TMAX"]==0
            if(record["TMIN"]==-9999):
                for lag in range(1, 31):
                    testDates = [recordDate - timedelta(365*lag),
                                recordDate + timedelta(365*lag),
                                recordDate - timedelta(lag),
                                recordDate + timedelta(lag)]
                    for testDate in testDates:
                        if(testDate in timeDict and timeDict[testDate]["TMIN"]!=-9999):
                            record["TMIN"] = timeDict[testDate]["TMIN"]
                            break
                    if(record["TMIN"]!=-9999): break
                if(record["TMIN"]==-9999): record["TMIN"]==0
            if(record["TAVG"]==-9999): record["TAVG"] = round(random.uniform(0.4, 0.7)*(record["TMAX"] + record["TMIN"]), 2)
        return timeDict

def insertStationMetaData(fileName, cursor, stations):
    insertSQLTemplate = "insert ignore into stationMeta values ('%s', %s, %s, %s, '%s', '%s');"
    insertSQLList = []
    with open(fileName, "rb") as f:
        for line in f:
            station = line[0:11].strip()
            if(station in stations):
                latitude = float(line[12:20])
                longitude = float(line[21:30])
                elevation = float(line[31:37])
                state = line[38:40].strip()
                name = line[41:71].strip().replace("'","")
                insertSQLList.append(insertSQLTemplate % (station, latitude, longitude, elevation, state, name))
    print "stations: %s" % len(insertSQLList)
    for insertSQL in insertSQLList:
        print insertSQL
        cursor.execute(insertSQL)

def aggregateMonthlyData(cursor, db):
    insertSQLTemplate = "insert ignore into climateMonthly (station, date, prcp, tmax, tmin, tavg) \
                            select station, min(date), sum(prcp), max(tmax), min(tmin), avg(tavg) from climateDaily \
                            where year(date)=%s and month(date)=%s group by station;"
    insertSQLList = []
    for year in range(1981, 2015):
        for month in range(1, 13):
            insertSQLList.append(insertSQLTemplate % (year, month))
    for month in range(1, 8):
        insertSQLList.append(insertSQLTemplate % (2015, month))
    for insertSQL in insertSQLList:
        cursor.execute(insertSQL)
        db.commit()

def aggregateYearlyData(cursor, db):
    insertSQLTemplate = "insert ignore into climateYearly (station, date, prcp, tmax, tmin, tavg) \
                            select station, min(date), sum(prcp), max(tmax), min(tmin), avg(tavg) from climateDaily \
                            where year(date)=%s group by station;"
    insertSQLList = []
    for year in range(1981, 2015):
        insertSQLList.append(insertSQLTemplate % (year))
    for insertSQL in insertSQLList:
        cursor.execute(insertSQL)
        db.commit()

if __name__ == "__main__":
    db = MySQLdb.connect(host="172.31.52.211", user="dataAdmin", db="data")
    cursor = db.cursor()
    createTable(cursor)
    dataDir = "/home/ubuntu/data/ghcnd_hcn"
    stations = insertDailyData(dataDir, cursor)
    metaFile = "/home/ubuntu/data/ghcnd-stations.txt"
    insertStationMetaData(metaFile, cursor, stations)
    aggregateMonthlyData(cursor, db)
    aggregateYearlyData(cursor, db)
    db.commit()