import java.sql.ResultSet

import com.memsql.spark.connector.rdd.MemSQLRDD
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.alitouka.spark.dbscan._
import org.alitouka.spark.dbscan.spatial.Point
import com.memsql.spark.connector._

/**
 * Created by leonli0326 on 8/18/2015.
 */
object ClimateClustering {

  class ClimateMonthly (
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

    override def toString = s"MonthlyData($station, $date, $prcp, $tmax, $tmin, $tavg)"
  }

  def getMonthlyDataByYear(sc: SparkContext, year: Int, host:String): RDD[ClimateMonthly] = {
    val port = 3306
    val user = "dataAdmin"
    val password = ""
    val dbName = "data"
    def getClimateMonthly(rs: ResultSet): ClimateMonthly = {
      return new ClimateMonthly(rs.getString("station"),
        rs.getString("date"),
        rs.getDouble("prcp"),
        rs.getDouble("tmax"),
        rs.getDouble("tmin"),
        rs.getDouble("tavg"))
    }
    val sql = s"select * from climateMonthly where date >= subdate(now(), interval $year year) limit 100000;"
    return new MemSQLRDD(sc, host, port, user, password, dbName, sql, getClimateMonthly).sortBy(c => c.date)
  }

  def transformClimateMonthlyToPoint(rdd: RDD[ClimateMonthly]): RDD[(String, Point)] = {
    def transformMonthlyDataPoint(iterable: Iterable[ClimateMonthly]): Point = {
      val list = iterable.toList.sortBy(_.date)
      var result = new Array[Double](list.size*4)
      var i = 0
      for( monthlyData <- list){
        result(i) = monthlyData.prcp
        result(i+1) = monthlyData.tmax
        result(i+2) = monthlyData.tmin
        result(i+3) = monthlyData.tavg
        i += 4
      }
      return new Point(result)
    }
    val rddPoints = rdd.groupBy(monthlyData => monthlyData.station)
      .mapValues(transformMonthlyDataPoint)
    return rddPoints
  }

  def transfromClimateMonthlyToArray(rdd: RDD[ClimateMonthly]): RDD[(String, Array[Double])] = {
    def transformMonthlyDataArray(iterable: Iterable[ClimateMonthly]): Array[Double] = {
      val list = iterable.toList.sortBy(_.date)
      var result = new Array[Double](list.size*4)
      var i = 0
      for( monthlyData <- list){
        result(i) = monthlyData.prcp
        result(i+1) = monthlyData.tmax
        result(i+2) = monthlyData.tmin
        result(i+3) = monthlyData.tavg
        i += 4
      }
      return result
    }
    val rddPoints = rdd.groupBy(monthlyData => monthlyData.station)
      .mapValues(transformMonthlyDataArray)
    return rddPoints
  }

  def fitModelDBSCAN(rddPoints: RDD[(String, Point)]): RDD[(String, Int)] = {
    val clusteringSettings = new DbscanSettings().withEpsilon(1000).withNumberOfPoints(5)
    val modelDbscan = Dbscan.train(rddPoints.map(tup => tup._2), clusteringSettings)
    return rddPoints.map(t => t._1).zip(modelDbscan.allPoints.map(p => p.clusterId.toInt))
  }

  def fitModelKmeans(rddArray: RDD[(String, Array[Double])]): RDD[(String, Int)] = {
    val trainingData = rddArray.map(kv => Vectors.dense(kv._2)).cache()
    val modelKmean = KMeans.train(trainingData, 5, 50)
    val prediction = rddArray.mapValues(array => modelKmean.predict(Vectors.dense(array)))
    return prediction
  }

  def persistClustering(rdd: RDD[(String, Int)], host:String): Unit = {
    val port = 3306
    val user = "dataAdmin"
    val password = ""
    val dbName = "data"
    rdd
      .map(tup => Array(tup._1, tup._2.toString()))
      .saveToMemsql(host, port, user, password, dbName, "climateClustering", "climateGroup=values(climateGroup)")
  }

  def completeDBSCANForStation(sc: SparkContext, year: Int, host:String): RDD[(String, Int)] = {
    val rdd = getMonthlyDataByYear(sc, year, host)
    val rddPoints = transformClimateMonthlyToPoint(rdd)
    val rddPrediction = fitModelDBSCAN(rddPoints)
    persistClustering(rddPrediction, host)
    for(x <- rddPrediction.collect()){
      println(x.toString())
    }
    return rddPrediction
  }

  def completeKMeanForStation(sc: SparkContext, year: Int, host:String): RDD[(String, Int)] = {
    val rdd = getMonthlyDataByYear(sc, year, host)
    val rddArray = transfromClimateMonthlyToArray(rdd)
    val rddPrediction = fitModelKmeans(rddArray)
    persistClustering(rddPrediction, host)
    for(x <- rddPrediction.collect()){
      println(x.toString())
    }
    return rddPrediction
  }

  def main(args: Array[String]) {
    if(args.length<1){
      System.err.println("Must specify Database entry point!")
      return
    }
    val host = args(0)
    val conf = new SparkConf().setMaster("local[8]").setAppName("app").set("spark.executor.memory", "2g")
    val sc = new SparkContext(conf)
    try {
      val rddKmean = completeKMeanForStation(sc, 1, host)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
