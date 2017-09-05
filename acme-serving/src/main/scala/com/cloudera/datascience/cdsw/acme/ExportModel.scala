package com.cloudera.datascience.cdsw.acme

import java.nio.file.Paths

import acme.ACMEModel
import ml.combust.bundle.BundleFile
import ml.combust.bundle.serializer.SerializationFormat
import org.apache.spark.ml.bundle.SparkBundleContext
import ml.combust.mleap.spark.SparkSupport._
import resource.managed

object ExportModel {
  
  def main(args: Array[String]): Unit = {
    val training = ACMEData.readData()
    val pipeline = ACMEModel.buildModel()
    val sbc = SparkBundleContext.defaultContext.withDataset(pipeline.transform(training))
    val bundleFile = new java.io.File(Paths.get("src", "main", "resources", "model.zip").toString)
    for(bf <- managed(BundleFile(bundleFile))) {
      pipeline.writeBundle.format(SerializationFormat.Json).save(bf)(sbc).get
    }
  }

}
