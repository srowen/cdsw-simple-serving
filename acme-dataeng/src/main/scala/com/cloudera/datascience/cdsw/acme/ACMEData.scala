package com.cloudera.datascience.cdsw.acme

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.{DataFrame, SparkSession}

object ACMEData {

  /**
   * Note that data should be copied to hdfs:///tmp/datatraining.txt first;
   * see README.md.
   *
   * @return DataFrame holding training data
   */
  def readData(): DataFrame = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._
    
    // Uh oh. The data actually has an extra ID column at the front! 
    // Needs to be dropped manually
    
    val rawInput = "hdfs:///tmp/datatraining.txt"
    val csvInput = "hdfs:///tmp/datatraining.csv"
    
    val csvInputPath = new Path(csvInput)
    val fs = csvInputPath.getFileSystem(spark.sparkContext.hadoopConfiguration)
    if (fs.exists(csvInputPath)) {
      fs.delete(csvInputPath, true)
    }
    
    spark.read.textFile(rawInput).
      map { line =>
        if (line.startsWith("\"date\"")) {
          line
        } else {
          line.substring(line.indexOf(',') + 1)
        }
      }.
      repartition(1).
      write.text(csvInput)
    
    spark.read.
      option("inferSchema", true).
      option("header", true).
      csv(csvInput).
      drop("date")
  }

}
