> 之前写了一个[从零开始学习Spark](http://www.jianshu.com/p/84c76d4a74d7)的系列，一共八篇文章，了解了基本的Scala语言，RDD操作。接下来准备再开一个专题，记录一下Spark用于机器学习的实战Project，目的是将Spark和机器学习用于各个数据集的实战中。

> 第一部分还是用一个很简单的例子来配通Spark的环境，大致过程与[从零开始学习Spark（一）环境配置，实现WordCount](http://www.jianshu.com/p/84c76d4a74d7)一致，只是为了完整性再记录一下。

> Spark可以运行在各种集群上，但在这里我们都运行在本地，所以数据集的规模不会太庞大。

> 这个系列的文章会完成一个完整的系统：利用机器学习为一个电影网站提供数据支持。文章中列出了关键代码，完整代码见我的github repository，这篇文章的代码在`chapter01`中

## 任务目标

这一部分的例子非常简单，数据集是一个销售报表，格式为：用户名，商品名称，价格，我们需要统计总售货数，用户数量，总收入，以及商品销量排行。数据如下：

```
John,iPhone Cover,9.99
John,Headphones,5.49
Jack,iPhone Cover,9.99
Jill,Samsung Galaxy Cover,8.95
Bob,iPad Cover,5.49
```

## 第1步：配置sbt在IntelliJ下编程环境

打开terminal

查看java版本，由于MacOS自带java，因此无需安装

```
$ java -version
```

安装sbt，这是编译scala的工具

```
$ brew install sbt
```

查看sbt与scala信息

```
$ sbt about
```

下载安装IntelliJ

安装Scala Plugin：打开IntelliJ，在选择项目界面，选择Configure → Plugins → Install JetBrains Plugins，搜索Scala并安装

选择默认SDK：Configure → Project defaults → Project structure，SDK选择Java1.8

至此scala在IntelliJ下的开发环境配置完毕

##第2步：配置Spark工具包

下载Spark：[下载地址](http://spark.apache.org/downloads.html)，注意如果已经安装了Hadoop的话要下载对应的版本，下面的命令可以查看Hadoop版本

```
$ hadoop version
```

下载完毕后解压并将其放在一个目录下，假设放在`/usr/shar/spark-2.1.0-bin-hadoop2.7`，那么我们往环境变量中添加Spark方便以后使用

```
$ vim .bash_profile
```

加入一行，保存后重启terminal即可

```
export SPARK_HOME=/usr/shar/spark-2.1.0-bin-hadoop2.7
```

至此，Spark环境配置完毕，是不是非常方便

## 第3步：构建我们的应用

首先，IntelliJ下创建sbt项目：打开IntelliJ → Create New Project → Scala → sbt → ProjectName = chapter01 → Create

修改build.sbt，在最后加入一行Spark的包。注意scalaVersion一定要改成2.11，因为Spark2.1.0是基于Scala2.11的，默认的2.12会报错！

```
name := "chapter01"
version := "1.0"
scalaVersion := "2.11.7"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"
```

让我们先来看一下sbt项目的目录结构
```
├── build.sbt
├── project
│   ├── build.properties
│   ├── plugins.sbt
│   ├── project
│   └── target
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   ├── scala
│   │   └── scala-2.12
│   └── test
│       ├── java
│       ├── resources
│       ├── scala
│       └── scala-2.12
└── target
    ├── resolution-cache
    ├── scala-2.12
    └── streams
```

我们首先将我们的数据集放在一个新建的data目录中，`/data/UserPurchaseHistory.csv`

```
John,iPhone Cover,9.99
John,Headphones,5.49
Jack,iPhone Cover,9.99
Jill,Samsung Galaxy Cover,8.95
Bob,iPad Cover,5.49
```

我们需要写的代码主要放在`/src/main/scala`里面

下一步，我们开始写我们的代码，具体细节不用深究，本章节只是为了配通环境

添加文件`/src/main/scala/ScalaApp.scala`

```scala
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
```

此时，我们在IntelliJ中的ScalaApp.scala代码浏览界面的object旁可以看到一个按钮，按一下就可以直接run了

![run project](http://upload-images.jianshu.io/upload_images/4097708-a5170c11579bd4b4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

下方Console中出现如下结果证明运行成功

```
Total purchases: 5
Unique users: 4
Total revenue: 39.91
Most popular product: iPhone Cover with 2 purchases
```
