package com.neu.css.perdict.clean

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat
import com.neu.css.perdict.model.RecordDataFrame

/**
 * lakshl
 */
class DataCleanerUtil {
  
  /**
   * remove the missing values from the input txt file 
   */
  def removeMissingValues(inputRDD:RDD[String]):RDD[String]={
      inputRDD.filter(line=> !(line.contains("?") || (line.contains("Global_active_power;Global_reactive_power"))))
   }

   /**
    * format the input file in to RecordDataFrame
    */
   def convertToFormat(inputRDD:RDD[String]):RDD[RecordDataFrame] ={
     val fullDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
     val simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy")
     inputRDD.map(line => {
       val values = line.split(";")
       val recordValue = new RecordDataFrame()
       val date = fullDateFormat.parse(values{0}+" "+values{1})
       recordValue.id=date.getTime
       val dateValue = simpleDateFormat.parse(values{0})
       val dateString=simpleDateFormat.format(dateValue)
       recordValue.date = dateString
       recordValue.time = values{1}
       recordValue.day = date.getDay
       recordValue.month = date.getMonth
       recordValue.year = date.getYear
       recordValue.hourofDay= date.getHours
       recordValue.activePower = values{2}.toDouble
       recordValue.reactivePower = values{3}.toDouble
       recordValue.voltage = values{4}.toDouble
       recordValue.globalIntensity= values{5}.toDouble
       recordValue.subMetering1=values{6}.toDouble
       recordValue.subMetering2= values{7}.toDouble
       recordValue.subMetering3 = values{8}.toDouble
       recordValue
     })
   }
}





