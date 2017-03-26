package com.cloudera.datascience.cdsw.acme

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.nio.file.StandardOpenOption._
import scala.collection.JavaConverters._

import org.apache.commons.csv.CSVFormat
import org.dmg.pmml.FieldName
import org.jpmml.evaluator.{ModelEvaluatorFactory, ProbabilityDistribution}
import org.jpmml.model.PMMLUtil
import org.scalatest.{FlatSpec, Matchers}

class PMMLModelSuite extends FlatSpec with Matchers {
  
  "model" should "be 90+% accurate" in {
    val modelPath = Paths.get("src", "main", "resources", "model.pmml")
    val stream = Files.newInputStream(modelPath, READ)
    val pmml = 
      try {
        PMMLUtil.unmarshal(stream)
      } finally {
        stream.close()
      }
    
    val evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml)
    evaluator.verify()
    
    var correct = 0
    var total = 0
    
    val testPath = Paths.get("src", "test", "resources", "datatest.csv")
    val testReader = Files.newBufferedReader(testPath, StandardCharsets.UTF_8)
    try {
      CSVFormat.RFC4180.withFirstRecordAsHeader().parse(testReader).asScala.foreach { record =>
        val inputMap = record.toMap.asScala.
          filterKeys(_ != "Occupancy").
          map { case (field, fieldValue) => (new FieldName(field), fieldValue) }.asJava
        val outputMap = evaluator.evaluate(inputMap)
        
        val expected = record.get("Occupancy").toInt
        val actual = outputMap.get(new FieldName("Occupancy")).
          asInstanceOf[ProbabilityDistribution].getResult.toString.toInt
        
        if (expected == actual) {
          correct += 1
        }
        total += 1
      }
    } finally {
      testReader.close()
    }
    
    val accuracy = correct.toDouble / total
    println(s"Accuracy: $accuracy")
    assert(accuracy >= 0.9)
  }

}
