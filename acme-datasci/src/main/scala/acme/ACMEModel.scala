// Don't execute these lines in the workbench -- skip to "Start workbench session"
package acme
import org.apache.spark.ml.PipelineModel

/**
 * The Scala object definition here is 'boilerplate' that makes the code
 * here compilable and usable in the software build. It is not directly used
 * in the workbench. 
 */
object ACMEModel {
  /** 
   * @return the data scientist's best model for the ACME data
   */
  def buildModel(): PipelineModel = {


// Start workbench session
// Loads code in `acme-dataeng`. Normally this would be exported as a Maven
// artifact and loaded with AddDeps, but for simplicity is a local JAR.
/*
%AddJar file:lib/acme-dataeng-1.0.0.jar
 */
import com.cloudera.datascience.cdsw.acme.ACMEData
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import scala.util.Random

// Read and cache training data prepared from acme-dataeng:
val training = ACMEData.readData()
training.cache()
training.show()

// Build a logistic regression model,
val assembler = new VectorAssembler().
  setInputCols(training.columns.filter(_ != "Occupancy")).
  setOutputCol("featureVec")

val lr = new LogisticRegression().
  setFeaturesCol("featureVec").
  setLabelCol("Occupancy").
  setRawPredictionCol("rawPrediction")

val pipeline =
  new Pipeline().setStages(Array(assembler, lr))

// and tune that model:
val paramGrid = new ParamGridBuilder().
  addGrid(lr.regParam, Seq(0.00001, 0.001, 0.1)).
  addGrid(lr.elasticNetParam, Seq(1.0)).
  build()
    
val eval = new BinaryClassificationEvaluator().
  setLabelCol("Occupancy").
  setRawPredictionCol("rawPrediction")

val validator = new TrainValidationSplit().
  setSeed(Random.nextLong()).
  setEstimator(pipeline).
  setEvaluator(eval).
  setEstimatorParamMaps(paramGrid).
  setTrainRatio(0.9)

val validatorModel = validator.fit(training)
val pipelineModel = validatorModel.bestModel.asInstanceOf[PipelineModel]
val lrModel = pipelineModel.stages.last.asInstanceOf[LogisticRegressionModel]
    
// Logistic regression model parameters:
training.columns.zip(lrModel.coefficients.toArray).foreach(println)

// Model hyperparameters:
lrModel.getElasticNetParam
lrModel.getRegParam
    
// Validation metric (accuracy):
validatorModel.validationMetrics.max
    
pipelineModel
// End workbench session

  }
}
