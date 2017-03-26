package com.cloudera.datascience.cdsw.acme

import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption._
import java.nio.file.{Files, Paths}
import javax.xml.transform.stream.StreamResult

import org.dmg.pmml.Application
import org.jpmml.model.JAXBUtil
import org.jpmml.sparkml.ConverterUtil

import acme.ACMEModel

object ExportModel {
  
  def main(args: Array[String]): Unit = {
    val training = ACMEData.readData()
    val pipeline = ACMEModel.buildModel()
    
    val pmml = ConverterUtil.toPMML(training.schema, pipeline)
    pmml.getHeader.setApplication(new Application("ACME Occupancy Detection"))
    
    val modelPath = Paths.get("src", "main", "resources")
    if (!Files.exists(modelPath)) {
      Files.createDirectory(modelPath)
    }
    val pmmlFile = modelPath.resolve("model.pmml")
    val writer = Files.newBufferedWriter(pmmlFile, StandardCharsets.UTF_8, WRITE, CREATE, TRUNCATE_EXISTING)
    try {
      JAXBUtil.marshalPMML(pmml, new StreamResult(writer))
    } finally {
      writer.close()
    }
  }

}
