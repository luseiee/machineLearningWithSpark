/**
  * Created by c on 2017/5/28.
  */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object ScalaApp {

  def main(args: Array[String]) {
    val sc = new SparkContext("local[2]", "First Spark App")
    val data = sc.textFile("data/UserPurchaseHistory.csv")
      .map(line => line.split(','))
      .map(purchaseRecord => (purchaseRecord(0), purchaseRecord(1), purchaseRecord(2)))

    val numPurchases: Long = data.count()
    val uniqueUsers: Long = data.map { case (user, product, price) => user }.distinct.count()
    val totalRevenue: Double = data.map { case (user, product, price) => price.toDouble }.sum()
    val productsByPopularity = data.map { case (user, product, price) => (product, 1) }
      .reduceByKey((x, y) => x + y).sortByKey(ascending=false).collect()
    val mostPopular = productsByPopularity(0)

    println("Total purchases: " + numPurchases)
    println("Unique users: " + uniqueUsers)
    println("Total revenue: " + totalRevenue)
    println("Most popular product: %s with %d purchases".
      format(mostPopular._1, mostPopular._2))
  }
}
