/**
 * Created by leonli0326 on 8/18/2015.
 */

import java.sql.ResultSet

import breeze.linalg._

import com.cloudera.sparkts._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import com.memsql.spark.connector.rdd.MemSQLRDD
import org.joda.time.format.DateTimeFormat
import com.memsql.spark.connector._

import scala.collection.Map

object ClimateARIMA {

  class ClimateDaily(
                      _station: String,
                      _date: String,
                      _prcp: Double,
                      _tmax: Double,
                      _tmin: Double,
                      _tavg: Double
                      ) extends Serializable {
    var station: String = _station
    var date: String = _date
    var prcp: Double = _prcp
    var tmax: Double = _tmax
    var tmin: Double = _tmin
    var tavg: Double = _tavg

    override def toString = s"ClimateDaily($station, $date, $prcp, $tmax, $tmin, $tavg)"

    def removeSeasonality(model: SeasonalityModel): ClimateDaily = {
      return new ClimateDaily(
        this.station,
        this.date,
        this.prcp - model.prcp,
        this.tmax - model.tmax,
        this.tmin - model.tmin,
        this.tavg - model.tavg
      )
    }

    def restoreSeasonality(model: SeasonalityModel): ClimateDaily = {
      return new ClimateDaily(
        this.station,
        this.date,
        this.prcp + model.prcp,
        this.tmax + model.tmax,
        this.tmin + model.tmin,
        this.tavg + model.tavg
      )
    }
  }

  class SeasonalityModel(
                          _prcp: Double,
                          _tmax: Double,
                          _tmin: Double,
                          _tavg: Double
                          ) extends Serializable {
    var prcp: Double = _prcp
    var tmax: Double = _tmax
    var tmin: Double = _tmin
    var tavg: Double = _tavg

    def add(other: SeasonalityModel): SeasonalityModel = {
      return new SeasonalityModel(
        this.prcp + other.prcp,
        this.tmax + other.tmax,
        this.tmin + other.tmin,
        this.tavg + other.tavg
      )
    }

    def divide(divident: Double): SeasonalityModel = {
      return new SeasonalityModel(
        this.prcp / divident,
        this.tmax / divident,
        this.tmin / divident,
        this.tavg / divident
      )
    }

    override def toString = s"SeasonalityModel($prcp, $tmax, $tmin, $tavg)"
  }

  class ClimateARIMAModel(
                           _prcpModel: ARIMAModel,
                           _tmaxModel: ARIMAModel,
                           _tminModel: ARIMAModel,
                           _tavgModel: ARIMAModel
                           ) extends Serializable {
    var prcpModel: ARIMAModel = _prcpModel
    var tmaxModel: ARIMAModel = _tmaxModel
    var tminModel: ARIMAModel = _tminModel
    var tavgModel: ARIMAModel = _tavgModel
  }

  def getAllStation(sc: SparkContext, host:String): Array[String] = {
    val port = 3306
    val user = "dataAdmin"
    val password = ""
    val dbName = "data"
//    val sql = s"select station from stationMeta limit 1000000;"
    val sql = s"select station from stationMeta where station not in (select distinct station from climateForecast) limit 10000;"
    return new MemSQLRDD(sc, host, port, user, password, dbName, sql, (rs => rs.getString("station"))).collect()
  }

  def getDataByStation(sc: SparkContext, station: String, host:String): RDD[ClimateDaily] = {
    val port = 3306
    val user = "dataAdmin"
    val password = ""
    val dbName = "data"
    val sql = s"select * from climateDaily where station='$station' limit 1000000;"
    def getClimateDaily(rs: ResultSet): ClimateDaily = {
      return new ClimateDaily(
        rs.getString("station"),
        rs.getString("date"),
        rs.getDouble("prcp"),
        rs.getDouble("tmax"),
        rs.getDouble("tmin"),
        rs.getDouble("tavg")
      )
    }
    val rdd = new MemSQLRDD(sc, host, port, user, password, dbName, sql, getClimateDaily).sortBy(c => c.date)
    return rdd
  }

  def getSeasonality(rdd: RDD[ClimateDaily]): Map[String, SeasonalityModel] = {
    val seasonality = rdd
      .keyBy(d => d.date.substring(5))
      .mapValues(v => (1, new SeasonalityModel(v.prcp, v.tmax, v.tmin, v.tavg)))
      .reduceByKey((a, b) => (a._1 + b._1, a._2.add(b._2)))
      .mapValues(v => v._2.divide(v._1.toDouble))
      .collectAsMap()
    return seasonality
  }

  def removeSeasonality(rdd: RDD[ClimateDaily], seasonality: Map[String, SeasonalityModel]): RDD[ClimateDaily] = {
    val removedSeasonality = rdd
      .map(c => c.removeSeasonality(seasonality(c.date.substring(5))))
    return removedSeasonality
  }

  def fitModel(rdd: RDD[ClimateDaily]): ClimateARIMAModel = {
    return new ClimateARIMAModel(
      ARIMA.fitModel(2, 1, 1, Vector(rdd.map(c => c.prcp).collect()), method="css-bobyqa"),
      ARIMA.fitModel(2, 1, 1, Vector(rdd.map(c => c.tmax).collect()), method="css-bobyqa"),
      ARIMA.fitModel(2, 1, 1, Vector(rdd.map(c => c.tmin).collect()), method="css-bobyqa"),
      ARIMA.fitModel(2, 1, 1, Vector(rdd.map(c => c.tavg).collect()), method="css-bobyqa")
    )
  }

  def forecast(rdd: RDD[ClimateDaily], model: ClimateARIMAModel, seasonality: Map[String, SeasonalityModel], nFuture: Int, station: String, sc: SparkContext): RDD[ClimateDaily] = {
    val forecastPrcp = model.prcpModel.forecast(Vector(rdd.map(c => c.prcp).collect()), nFuture)
    val forecastTmax = model.tmaxModel.forecast(Vector(rdd.map(c => c.tmax).collect()), nFuture)
    val forecastTmin = model.tminModel.forecast(Vector(rdd.map(c => c.tmin).collect()), nFuture)
    val forecastTavg = model.tavgModel.forecast(Vector(rdd.map(c => c.tavg).collect()), nFuture)
    var forecastClimates = new Array[ClimateDaily](nFuture)
    val lastDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(rdd.map(c => c.date).max())
    for (i <- 0 until nFuture) {
      val newDate = lastDate.plusDays(i)
      val currRecord = new ClimateDaily(station, newDate.toString("yyyy-MM-dd"), forecastPrcp(i), forecastTmax(i), forecastTmin(i), forecastTavg(i))
      forecastClimates(i) = currRecord.restoreSeasonality(seasonality(currRecord.date.substring(5)))
    }
    return sc.parallelize(forecastClimates)
  }

  def persisitForecast(rdd: RDD[ClimateDaily], host:String): Unit = {
    val port = 3306
    val user = "dataAdmin"
    val password = ""
    val dbName = "data"
    rdd
      .map(c => Array(c.station, c.date, c.prcp.toString(), c.tmax.toString(), c.tmin.toString(), c.tavg.toString()))
      .saveToMemsql(host, port, user, password, dbName, "climateForecast")
  }

  def completeForecastForStation(sc: SparkContext, station: String, nFuture: Int, host:String): Unit = {
    val rdd = getDataByStation(sc, station, host)
    val seasonality = getSeasonality(rdd)
    val rddNoSeason = removeSeasonality(rdd, seasonality)
    val arimaModel = fitModel(rddNoSeason)
    val rddForecast = forecast(rddNoSeason, arimaModel, seasonality, 365*nFuture, station, sc)
    persisitForecast(rddForecast, host)
  }

  def main(args: Array[String]) {
    if(args.length<1){
      System.err.println("Must specify Database entry point!")
      return
    }
    val host = args(0)
    val conf = new SparkConf().setMaster("local[8]").setAppName("app")
    val sc = new SparkContext(conf)
    val stations = getAllStation(sc, host)
    for(station <- stations){
      try {
        completeForecastForStation(sc, station, 5, host)
      } catch {
        case e: Exception => e.printStackTrace()
      }
    }
  }
}
