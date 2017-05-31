# 数据读取与保存

> 到目前为止，所展示的示例都是从本地集合或者普通文件中进行数据读取和保存的。但有时候，数据量可能大到无法放在一台机器中，这时就需要探索别的数据读取和保存的方法了。

> Spark支持以下三种主要数据源：1. 文件格式（如JSON等） 2. 文件系统（如HDFS） 3. Spark SQL中的结构化数据源  4. 数据库与键值存储（如Cassandra，JDBC等）

> 这部分只是简单介绍一下Spark支持哪些数据形式，具体用到的时候可以再查阅手册。

## 1. 文件格式

### (1) 文本文件

当我们将一个文本文件读取为 RDD 时，输入的每一行 都会成为RDD的一个元素。也可以将多个完整的文本文件一次性读取为一个pair RDD，其中键是文件名，值是文件内容。

```scala
val input = sc.textFile("file:///home/holden/repos/spark/README.md")
```

SparkContext. wholeTextFiles() 方法，该方法会返回一个 pair RDD，其中键是输入文件的文件名。这个方法适合于读取一个文件夹下的多个文件。下面这段程序可以计算每个文件中的均值，假设文件中的值以逗号隔开。

```scala
val input = sc.wholeTextFiles("file://home/holden/salesFiles")
val result = input.mapValues{y =>
  val nums = y.split(" ").map(x => x.toDouble)
  nums.sum / nums.size.toDouble
}
```

注意一下上面的函数写法，第一次遇到。

输出结果，注意结果是分节点输出的。

```scala
result.saveAsTextFile(outputFile)
```

### (2) JSON

假设文件中的每一行都是一条JSON记录，Python中使用的是内建的库，而在 Java 和 Scala 中则会使用 Jackson。

### (3) CSV

逗号分隔值Comma-Separated Values(CSV)文件每行都有固定数目的字段，字段间用逗号隔开(在制表符分隔值文件，即TSV文件中用制表符隔开)。记录通常是一行一条。

Python我们会使用自带的csv库。在Scala和Java中则使用opencsv库。

### (4) Sequence File

SequenceFile是由没有相对关系结构的键值对文件组成的常用Hadoop格式。SequenceFile文件有同步标记，Spark可以用它来定位到文件中的某个点，然后再与记录的边界对齐。这可以让Spark使用多个节点高效地并行读取SequenceFile文件。

### (5) 对象文件

对象文件看起来就像是对SequenceFile的简单封装，它允许存储只包含值的RDD。和SequenceFile不一样的是，对象文件是使用Java序列化写出的。

### (6) Hadoop输入输出格式

### (7)  文件压缩

## 2. 文件系统

### (1) 本地常规文件系统

如果你的数据已经在这些系统中，那么你只需要指定输入为一个file://路径，只要这个文件系统挂载在每个节点的同一个路径下，Spark就会自动处理

```scala
val rdd = sc.textFile("file:///home/holden/happypandas.gz")
```

### (2) Amazon S3

### (3) HDFS

只需要将输入输出路径指定为 hdfs://master:port/path

## 3. Spark SQL中的结构化数据

在各种情况下，我们把一条SQL查询给Spark SQL，让它对一个数据源执行查询(选出 一些字段或者对字段使用一些函数)，然后得到由Row对象组成的RDD，每个Row对象表示一条记录。

这部分内容在以后会有详细介绍。

### (1) Apache Hive

Apache Hive是Hadoop上的一种常见的结构化数据源。Hive可以在HDFS内或者在其他存储系统上存储多种格式的表。这些格式从普通文本到列式存储格式，应有尽有。Spark SQL 可以读取 Hive 支持的任何表。

### (2) JSON

如果你有记录间结构一致的JSON数据，Spark SQL也可以自动推断出它们的结构信息， 并将这些数据读取为记录，这样就可以使得提取字段的操作变得很简单。

json文件如下，tweets.json

```json
{"user": {"name": "Holden", "location": "San Francisco"}, "text": "Nice day out today"}
{"user": {"name": "Matei", "location": "Berkeley"}, "text": "Even nicer here :)"}
```

```scala
val hiveCtx = new org.apache.spark.sql.hive.HiveContext(sc)
val tweets = hiveCtx.jsonFile("tweets.json")
tweets.registerTempTable("tweets")
val results = hiveCtx.sql("SELECT user.name, text FROM tweets")
```

## 3. 数据库

Spark可以访问一些常用的数据库系统

### (1) JAVA数据库连接

Spark 可以从任何支持 Java 数据库连接(JDBC)的关系型数据库中读取数据，包括 MySQL、Postgre等系统。

### (2) Cassandra

Apache Cassandra 是一套开源分布式Key-Value 存储系统。

### (3) HBase

HBase是一个开源的非关系型分布式数据库（NoSQL），它参考了谷歌的BigTable建模，实现的编程语言为Java。它是Apache软件基金会的Hadoop项目的一部分。

### (4) Elasticsearch

Elasticsearch是一个基于[Apache Lucene(TM)](https://lucene.apache.org/core/)的开源搜索引擎。无论在开源还是专有领域，Lucene可以被认为是迄今为止最先进、性能最好的、功能最全的搜索引擎库。
