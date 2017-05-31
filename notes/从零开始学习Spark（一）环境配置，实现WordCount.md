# 环境配置，Spark实现WordCount

> 本人准备参加腾讯实习，有关大数据与机器学习。由于本人对大数据一无所知，因此准备由Spark作为切入口开始自学，一步步完成机器学习各个算法在Spark上的应用。自学过程中的点点滴滴都会记录在简书上，希望可以与大家交流，共同学习。 

> 配环境永远是开始学习一个新领域最难的一部分，我花了两天时间配置成功了MacOS下的Spark开发环境，实现了基于Scala与sbt的WordCount，下面来一步步把步骤记录下来。

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

## 第3步：命令行形式操控Spark

### (1) Python Spark

terminal中执行命令
```
$ $SPARK_HOME/bin/pyspark
```

看到帅气的Spark logo就表示已经成功了

美中不足的是自带的python shell没有自动补全等功能，使用ipython可以完美解决

首先，安装ipython

```
$ pip install ipython
```

运行Spark

```
$ PYSPARK_DRIVER_PYTHON=ipython $SPARK_HOME/bin/pyspark
```

让我们来使用一些Spark的API来尝试一些命令

```
>>> lines = sc.textFile("README.md") # 创建一个名为lines的RDD
>>> lines.count() # 统计RDD中的元素个数 
127
>>> lines.first()
```

### (2) Scala Spark Shell

```
$ $SPARK_HOME/bin/spark-shell
```

同样完成一下行数统计的小应用

```
scala> val lines = sc.textFile("README.md") // 创建一个名为lines的RDD
lines: spark.RDD[String] = MappedRDD[...]
scala> lines.count() // 统计RDD中的元素个数 
res0: Long = 127
scala> lines.first() // 这个RDD中的第一个元素，也就是README.md的第一行 
res1: String = # Apache Spark
```

## 第4步：构建Spark独立应用，WordCount

上面的是shell形式下调用Spark，而现在进入更为重要的建立独立项目，我看了很多教程，但是每个教程都有一步两步讲的含糊不清，或者就是版本太老，留下了许多坑。现在我总结了一个可以跑通的例子。

首先，IntelliJ下创建sbt项目：打开IntelliJ → Create New Project → Scala → sbt → ProjectName = wordcount → Create

修改build.sbt，在最后加入一行Spark的包。注意scalaVersion一定要改成2.11，因为Spark2.1.0是基于Scala2.11的，默认的2.12会报错！

```
name := "wordcount"
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

我们需要写的代码主要放在`/src/main/scala里面`

下一步，我们开始写我们的代码，具体细节不用深究，本章节只是为了配通环境

新建目录`/src/main/scala/com/oreilly/learningsparkexamples/mini/scala`

添加第一个文件`/src/main/scala/com/oreilly/learningsparkexamples/mini/scala/BasicMap.scala`

```scala
/**
 * Illustrates a simple map in Scala
 */
package com.oreilly.learningsparkexamples.scala

import org.apache.spark._

object BasicMap {
    def main(args: Array[String]) {
      val master = args.length match {
        case x: Int if x > 0 => args(0)
        case _ => "local"
      }
      val sc = new SparkContext(master, "BasicMap", System.getenv("SPARK_HOME"))
      val input = sc.parallelize(List(1,2,3,4))
      val result = input.map(x => x*x)
      println(result.collect().mkString(","))
    }
}
```

添加第二个文件`/src/main/scala/com/oreilly/learningsparkexamples/mini/scala/WordCount.scala`

```
/**
 * Illustrates flatMap + countByValue for wordcount.
 */
package com.oreilly.learningsparkexamples.mini.scala

import org.apache.spark._
import org.apache.spark.SparkContext._

object WordCount {
    def main(args: Array[String]) {
      val inputFile = args(0)
      val outputFile = args(1)
      val conf = new SparkConf().setAppName("wordCount")
      // Create a Scala Spark Context.
      val sc = new SparkContext(conf)
      // Load our input data.
      val input =  sc.textFile(inputFile)
      // Split up into words.
      val words = input.flatMap(line => line.split(" "))
      // Transform into word and count.
      val counts = words.map(word => (word, 1)).reduceByKey{case (x, y) => x + y}
      // Save the word count back out to a text file, causing evaluation.
      counts.saveAsTextFile(outputFile)
    }
}
```

点击右上角的Build Project图标就编译成功了，如果没有报错，那么恭喜你，环境配置成功了。

## 第5步：使用spark-submit来运行应用

spark-submit脚本可以为我们配置 Spark 所要用到的一系列环境变量。

首先需要将我们编译好的项目打包，最方便的方式就是进入`wordcount`目录下，输入
```
$ sbt package
```

打包好的文件就在`/wordcount/target/scala-2.11/wordcount_2.11-1.0.jar`

接下来就是利用Spark为我们提供的spark-submit来运行应用了，进入`wordcount`目录下

```
$ $SPARK_HOME/bin/spark-submit \
--class com.oreilly.learningsparkexamples.mini.scala.WordCount  \
./target/scala-2.11/wc_2.11-1.0.jar \
./input.txt ./wordcounts
```

下面来简单解释一下上面的命令，`--class`为使用的Class，后面为jar包的路径，最后两个为wordcount的两个参数，分别为输入文件，和输出文件路径

我们的输入文件`\wordcount\input.txt`是这样的

```
one two three four
four five six
one five six
one one three
```

运行后，如果成功会在`\wordcount\wordcounts\part-00000`中看到

```
(two,1)
(one,4)
(six,2)
(three,2)
(five,2)
(four,2)
```

至此，我们的整个环境都配置成功啦，有问题请留言

# 参考资料
[Spark官方文档Quick-start](http://spark.apache.org/docs/latest/quick-start.html)

[用SBT编译Spark的WordCount程序](http://www.cnblogs.com/gaopeng527/p/4398225.html)

[Big Data Analysis with Scala and Spark 洛桑联邦理工学院 - Coursera](https://zh.coursera.org/learn/scala-spark-big-data)
