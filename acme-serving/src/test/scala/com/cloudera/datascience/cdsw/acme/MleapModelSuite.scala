package com.cloudera.datascience.cdsw.acme

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import scala.collection.JavaConverters._

import ml.combust.bundle.BundleFile
import ml.combust.mleap.runtime.MleapContext.defaultContext
import ml.combust.mleap.runtime.MleapSupport._
import ml.combust.mleap.runtime.types._
import ml.combust.mleap.runtime.{ArrayRow, LeapFrame, LocalDataset}
import org.apache.commons.csv.CSVFormat
import org.scalatest.{FlatSpec, Matchers}
import resource.managed

class MleapModelSuite extends FlatSpec with Matchers {
  
  "model" should "be 90+% accurate" in {
    val bundleFile = new File(Paths.get("src", "main", "resources", "model.zip").toString)
    val mleapTransformer = (for(bf <- managed(BundleFile(bundleFile))) yield {
      bf.loadMleapBundle().get.root
    }).tried.get

    val schema: StructType = StructType(
      StructField("Temperature", DoubleType()),
      StructField("Humidity", DoubleType()),
      StructField("Light", DoubleType()),
      StructField("CO2", DoubleType()),
      StructField("HumidityRatio", DoubleType()),
      StructField("Occupancy", DoubleType())).get

    val testPath = Paths.get("src", "test", "resources", "datatest.csv")

    var correct = 0
    var total = 0

    val testReader = Files.newBufferedReader(testPath, StandardCharsets.UTF_8)
    try {
      val rows = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(testReader).asScala.map { rec =>
        val r = schema.fields.map(f => rec.get(f.name).toDouble).toArray
        ArrayRow(r)
      }.toArray
      val dataset = LocalDataset(rows)
      val leapFrame = LeapFrame(schema, dataset)
      val transformed = mleapTransformer.transform(leapFrame)
      for (df <- transformed; subdf <- df.select("Occupancy", "prediction")) {
        subdf.dataset.toLocal.foreach { r =>
          val actual = r.getAs[Double](0)
          val expected = r.getAs[Double](1)
          if (actual == expected) {
            correct += 1
          }
          total += 1
        }
      }
    } finally {
      testReader.close()
    }

    val accuracy = correct.toDouble / total
    println(s"Accuracy: $accuracy")
    assert(accuracy >= 0.9)
  }

}
