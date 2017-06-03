/**
  * Created by c on 2017/6/2.
  */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.classification.{ClassificationModel, LogisticRegressionWithSGD, NaiveBayes, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.optimization.{SquaredL2Updater, Updater}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.Entropy
import org.apache.spark.rdd.RDD

object ScalaApp {

  def main(args: Array[String]) {
    // Prepare data
    val sc: SparkContext = new SparkContext("local[2]", "First Spark App")
    sc.setLogLevel("ERROR")
    val rawData = sc.textFile("data/train_noheader.tsv")
    val records = rawData.map(line => line.split("\t"))
    val data = records.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val features = trimmed.slice(4, r.size - 1).map(d => if (d == "?") 0.0 else d.toDouble)
      LabeledPoint(label, Vectors.dense(features))
    }
    data.cache()
    val numData = data.count
    val nbData = records.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val features = trimmed.slice(4, r.size - 1).map(d => if (d == "?") 0.0 else d.toDouble)
        .map(d => if (d < 0) 0.0 else d)
      LabeledPoint(label, Vectors.dense(features))
    }
    // Train 4 classification models
    val numIterations = 10
    val maxTreeDepth = 5
    val lrModel = LogisticRegressionWithSGD.train(data, numIterations)
    val svmModel = SVMWithSGD.train(data, numIterations)
    val nbModel = NaiveBayes.train(nbData)
    val dtModel = DecisionTree.train(data, Algo.Classification, Entropy, maxTreeDepth)
    // Calculate accuracy of models
    val lrTotalCorrect = data.map { lp =>
      if (lrModel.predict(lp.features) == lp.label) 1 else 0}.sum()
    val lrAccuracy = lrTotalCorrect / data.count
    println("lrAccuracy:" + lrAccuracy)
    val svmTotalCorrect = data.map { lp =>
      if (svmModel.predict(lp.features) == lp.label) 1 else 0}.sum()
    val svmAccuracy = svmTotalCorrect / data.count
    println("svmAccuracy:" + svmAccuracy)
    val nbTotalCorrect = nbData.map { lp =>
      if (nbModel.predict(lp.features) == lp.label) 1 else 0}.sum()
    val nbAccuracy = nbTotalCorrect / data.count
    println("nbAccuracy:" + nbAccuracy)
    val dtTotalCorrect = data.map { lp =>
      val score = dtModel.predict(lp.features)
      val predicted = if (score > 0.5) 1 else 0
      if (predicted == lp.label) 1 else 0}.sum()
    val dtAccuracy = dtTotalCorrect / data.count
    println("dtAccuracy:" + dtAccuracy)
    // Calculate area of PR curve and ROC curve
    val metrics = Seq(lrModel, svmModel).map {model =>
      val scoreAndLabels = data.map {lp =>
        (model.predict(lp.features), lp.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (model.getClass.getSimpleName(), metrics.areaUnderPR(), metrics.areaUnderROC())
    }
    val nbMetrics = Seq(nbModel).map {model =>
      val scoreAndLabels = nbData.map {lp =>
        (model.predict(lp.features), lp.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (model.getClass.getSimpleName(), metrics.areaUnderPR(), metrics.areaUnderROC())
    }
    val dtMetrics = Seq(dtModel).map {model =>
      val scoreAndLabels = data.map {lp =>
        val score = model.predict(lp.features)
        (if (score > 0.5) 1.0 else 0.0, lp.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (model.getClass.getSimpleName(), metrics.areaUnderPR(), metrics.areaUnderROC())
    }
    val allMetrics = metrics ++ nbMetrics ++ dtMetrics
    allMetrics.foreach {case (m, pr, roc) =>
      println(f"$m, Area under PR: ${pr * 100}%2.4f%%, Area under ROC: ${roc * 100}%2.4f%%")}
    // Add normalization to the training data
    val vectors = data.map(lp => lp.features)
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(vectors)
    val scaledData = data.map(lp => LabeledPoint(lp.label, scaler.transform(lp.features)))
    // Train linear regression model and check result
    val lrModelScaled = LogisticRegressionWithSGD.train(scaledData, numIterations)
    val lrTotalCorrectScaled = scaledData.map { point =>
      if (lrModelScaled.predict(point.features) == point.label) 1 else 0
    }.sum()
    val lrAccuracyScaled = lrTotalCorrectScaled / numData
    val lrPredictionsVsTrue = scaledData.map { point =>
      (lrModelScaled.predict(point.features), point.label)
    }
    val lrMetricsScaled = new BinaryClassificationMetrics(lrPredictionsVsTrue)
    val lrPr = lrMetricsScaled.areaUnderPR
    val lrRoc = lrMetricsScaled.areaUnderROC
    println("Normalize the training data:")
    println(f"${lrModelScaled.getClass.getSimpleName}\n" +
      f"Accuracy: ${lrAccuracyScaled * 100}%2.4f%%\nArea under PR: " +
      f"${lrPr * 100.0}%2.4f%%\nArea under ROC: ${lrRoc * 100.0}%2.4f%%")
    // Add category features and calculate
    val categories = records.map(r => r(3)).distinct.collect.zipWithIndex.toMap
    val numCategories = categories.size
    val dataCategories = records.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val categoryIdx = categories(r(3))
      val categoryFeatures = Array.ofDim[Double](numCategories)
      categoryFeatures(categoryIdx) = 1.0
      val otherFeatures = trimmed.slice(4, r.size - 1).map(d => if (d == "?") 0.0 else d.toDouble)
      val features = categoryFeatures ++ otherFeatures
      LabeledPoint(label, Vectors.dense(features))
    }
    val scalerCats = new StandardScaler(withMean = true, withStd = true).fit(dataCategories.map(lp => lp.features))
    val scaledDataCats = dataCategories.map(lp => LabeledPoint(lp.label, scalerCats.transform(lp.features)))
    val lrModelScaledCats = LogisticRegressionWithSGD.train(scaledDataCats, numIterations)
    val lrTotalCorrectScaledCats = scaledDataCats.map { point =>
      if (lrModelScaledCats.predict(point.features) == point.label) 1 else 0
    }.sum
    val lrAccuracyScaledCats = lrTotalCorrectScaledCats / numData
    val lrPredictionsVsTrueCats = scaledDataCats.map { point =>
      (lrModelScaledCats.predict(point.features), point.label)
    }
    val lrMetricsScaledCats = new BinaryClassificationMetrics(lrPredictionsVsTrueCats)
    val lrPrCats = lrMetricsScaledCats.areaUnderPR
    val lrRocCats = lrMetricsScaledCats.areaUnderROC
    println("Add category feature:")
    println(f"${lrModelScaledCats.getClass.getSimpleName}\nAccuracy: " +
      f"${lrAccuracyScaledCats * 100}%2.4f%%\nArea under PR: " +
      f"${lrPrCats * 100.0}%2.4f%%\nArea under ROC: ${lrRocCats * 100.0}%2.4f%%")
    // Use only category features for naive bayes
    val dataNB = records.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val categoryIdx = categories(r(3))
      val categoryFeatures = Array.ofDim[Double](numCategories)
      categoryFeatures(categoryIdx) = 1.0
      LabeledPoint(label, Vectors.dense(categoryFeatures))
    }
    val nbModelCats = NaiveBayes.train(dataNB)
    val nbTotalCorrectCats = dataNB.map { point =>
      if (nbModelCats.predict(point.features) == point.label) 1 else 0
    }.sum
    val nbAccuracyCats = nbTotalCorrectCats / numData
    val nbPredictionsVsTrueCats = dataNB.map { point =>
      (nbModelCats.predict(point.features), point.label)
    }
    val nbMetricsCats = new BinaryClassificationMetrics(nbPredictionsVsTrueCats)
    val nbPrCats = nbMetricsCats.areaUnderPR
    val nbRocCats = nbMetricsCats.areaUnderROC
    println(f"${nbModelCats.getClass.getSimpleName}\nAccuracy: " +
      f"${nbAccuracyCats * 100}%2.4f%%\nArea under PR: " +
      f"${nbPrCats * 100.0}%2.4f%%\nArea under ROC: ${nbRocCats * 100.0}%2.4f%%")
    // Parameter tuning
    def trainWithParams(input: RDD[LabeledPoint], regParam: Double,
                            numIterations: Int, updater: Updater, stepSize: Double) = {
      val lr = new LogisticRegressionWithSGD()
      lr.optimizer.setRegParam(regParam).setUpdater(updater).setStepSize(stepSize)
      lr.run(input)
    }
    def createMetrics(label: String, data: RDD[LabeledPoint], model: ClassificationModel) = {
      val scoreAndLabels = data.map {point =>
        (model.predict(point.features), point.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (label, metrics.areaUnderROC)
    }
    scaledDataCats.cache
    val trainTestSplit = scaledDataCats.randomSplit(Array(0.6, 0.4), seed = 123)
    val train = trainTestSplit(0)
    val test = trainTestSplit(1)
    val regResultsTest = Seq(0.0, 0.001, 0.0025, 0.005, 0.01).map {param =>
      val model = trainWithParams(train, param, numIterations, new SquaredL2Updater, 1.0)
      createMetrics(s"$param L2 regularization parameter", train, model)
    }
    regResultsTest.foreach { case (param, auc) => println(f"$param, AUC = ${auc * 100}%2.6f%%") }
  }
}
