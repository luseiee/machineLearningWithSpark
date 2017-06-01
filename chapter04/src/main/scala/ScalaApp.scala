/**
  * Created by c on 2017/5/31.
  */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.jblas.DoubleMatrix
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.evaluation.RankingMetrics

object ScalaApp {

  def main(args: Array[String]) {
    val sc = new SparkContext("local[2]", "First Spark App")
    sc.setLogLevel("ERROR")
    val rawData = sc.textFile("data/u.data")
    val rawRatings = rawData.map(_.split("\t").take(3))
    val ratings = rawRatings.map {case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble)}
    val model = ALS.train(ratings, 50, 10, 0.01)
    println(model.userFeatures.count)
    println(model.userFeatures.take(1))
    // Get rating for specific user and movie
    println(model.predict(196, 242))
    // Make recommendations to user
    val userId = 789
    val K = 5
    val topKRecs = model.recommendProducts(userId, K)
    println(topKRecs.mkString("\n"))
    // Check the recommendation movies' name
    val movies = sc.textFile("data/u.item")
    val titles = movies.map(line => line.split("\\|")).map(fields => (fields(0).toInt, fields(1))).collectAsMap()
    val moviesForUser = ratings.keyBy(_.user).lookup(789)
    println("User " + userId +"'s favorite movies:")
    moviesForUser.sortBy(-_.rating).take(5).map(rating => (titles(rating.product), rating.rating)).foreach(println)
    println("Movies recommended to user " + userId)
    topKRecs.map(rating => (titles(rating.product), rating.rating)).foreach(println)
    // Similar item recommendation
    def cosineSimilarity(vec1: DoubleMatrix, vec2: DoubleMatrix): Double = {
      vec1.dot(vec2) / (vec1.norm2() * vec2.norm2())
    }
    val itemId = 567
    val itemFactor = model.productFeatures.lookup(itemId).head
    val itemVector = new DoubleMatrix(itemFactor)
    println(cosineSimilarity(itemVector, itemVector))
    val sims = model.productFeatures.map {case (id, factor) =>
      val factorVector = new DoubleMatrix(factor)
      val sim = cosineSimilarity(factorVector, itemVector)
      (id, sim)
    }
    val sortedSims = sims.top(K)(Ordering.by[(Int, Double), Double] {case (id, similarity) => similarity})
    println(sortedSims.mkString("\n"))
    // Check the similar movies' name
    println("Item number " + itemId + "'s name:")
    println(titles(itemId))
    println("Names of similar movies:")
    println(sortedSims.map {case (id, similarity) => (titles(id), similarity)}.mkString("\n"))
    // Calculate MSE
    val usersProducts = ratings.map {case Rating(user, product, rating) => (user, product)}
    val predictions = model.predict(usersProducts).map {case Rating(user, product, rating) => ((user, product), rating)}
    val ratingsAndPredictions = ratings.map {case Rating(user, product, rating) =>
      ((user, product), rating)}.join(predictions)
    val MSE = ratingsAndPredictions.map {
      case ((user, product), (actual, predicted)) => math.pow((actual - predicted), 2)
      }.reduce((x, y) => x + y) / ratingsAndPredictions.count
    println("MSE = " + MSE)
    println("RMSE = " + math.sqrt(MSE))
    // Calculate MSE with MLlib
    val predictedAndTrue = ratingsAndPredictions.map {
      case ((user, product), (actual, predicted)) => (actual, predicted)}
    val regressionMetrics = new RegressionMetrics(predictedAndTrue)
    println("MLlib MSE = " + regressionMetrics.meanSquaredError)
    println("MLlib RMSE = " + regressionMetrics.rootMeanSquaredError)
    // Calculate MAP with MLlib
    val userMovies = ratings.map{ case Rating(user, product, rating) => (user, product) }.groupBy(_._1)
    val itemFactors = model.productFeatures.map { case (id, factor) => factor }.collect()
    val itemMatrix = new DoubleMatrix(itemFactors)
    val imBroadcast = sc.broadcast(itemMatrix)
    val allRecs = model.userFeatures.map{ case (userId, array) =>
      val userVector = new DoubleMatrix(array)
      val scores = imBroadcast.value.mmul(userVector)
      val sortedWithId = scores.data.zipWithIndex.sortBy(-_._1)
      val recommendedIds = sortedWithId.map(_._2 + 1).toSeq
      (userId, recommendedIds)
    }
    val predictedAndTrueForRanking = allRecs.join(userMovies).map{ case (userId, (predicted, actualWithIds)) =>
      val actual = actualWithIds.map(_._2)
      (predicted.toArray, actual.toArray)
    }
    val rankingMetrics = new RankingMetrics(predictedAndTrueForRanking)
    println("Mean Average Precision = " + rankingMetrics.meanAveragePrecision)
  }
}
