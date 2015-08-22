import java.text.SimpleDateFormat
import java.util.Calendar

import _root_.kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._
import org.apache.spark._
import org.apache.spark.streaming.kafka._
import com.memsql.spark.connector._

/**
 * Created by leonli0326 on 8/20/2015.
 */
object ClimateStreaming {

  class ClimateCurrent(
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

    def reduce(other: ClimateCurrent): ClimateCurrent = {
      return new ClimateCurrent(
        this.station,
        this.date,
        this.prcp + other.prcp,
        math.max(this.tmax, other.tmax),
        math.min(this.tmin, other.tmin),
        this.tavg + other.tavg
      )
    }

    def getAvg(ct: Int): ClimateCurrent = {
      this.tavg = this.tavg / ct
      return this
    }

    override def toString = s"ClimateCurrent($station, $date, $prcp, $tmax, $tmin, $tavg)"
  }

  def createClimateCurrentFromString(s: String): ClimateCurrent = {
    val v = s.split(",")
    return new ClimateCurrent(v(0), v(1), v(2).toDouble*100, v(3).toDouble, v(4).toDouble, v(5).toDouble)
  }

  def persistAggregation(rdd: RDD[(String, ClimateCurrent)], host:String): Unit = {
    val port = 3306
    val user = "dataAdmin"
    val password = ""
    val dbName = "data"
    val updateValue = "prcp=values(prcp), tmax=values(tmax), tmin=values(tmin), tavg=values(tavg)"
    rdd
      .map(tup => Array(tup._1, tup._2.date, tup._2.prcp.toString(), tup._2.tmax.toString(), tup._2.tmin.toString(), tup._2.tavg.toString()))
      .saveToMemsql(host, port, user, password, dbName, "climateDaily", updateValue)
  }

  def startKafkaDStream(ssc: StreamingContext, host:String): Unit = {
    val topicSet = Set[String]("weather")
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092",
      "bootstrap.servers" -> "localhost:9092")
    val dStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicSet)

    val processed = dStream
      .filter(tup => tup._2.split(",")(1)==new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()))
      .mapValues(v => (createClimateCurrentFromString(v), 1))
      .reduceByKey((tup1, tup2) => (tup1._1.reduce(tup2._1), tup1._2 + tup2._2))
      .mapValues(tup => tup._1.getAvg(tup._2))

    processed.print()
    processed.foreachRDD(r => persistAggregation(r, host))

  }

  def main(args: Array[String]) {
    if(args.length<1){
      System.err.println("Must specify Database entry point!")
      return
    }
    val host = args(0)
    val conf = new SparkConf().setAppName("SparkStreaming").setMaster("local[4]")
    val ssc = new StreamingContext(conf, Seconds(60))
    startKafkaDStream(ssc, host)

    ssc.start()
    ssc.awaitTermination()
  }
}
