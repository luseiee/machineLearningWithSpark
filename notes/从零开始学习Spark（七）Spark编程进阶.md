# Spark编程进阶

> 这一部分将介绍一些没有提到的一些Spark的特性，都是非常有用的，内容之间关联性不是很强。主要包括，共享变量，分区操作，调用脚本以及统计操作。

## 1. 共享变量之累加器

通常在向 Spark 传递函数时，比如使用map()函数或者用filter()传条件时，可以使用驱 动器程序中定义的变量，但是集群中运行的每个任务都会得到这些变量的一份新的副本， 更新这些副本的值也不会影响驱动器中的对应变量。

下面这段scala程序读取文件的同时统计了空白行的数量

```scala
val sc = new SparkContext(...)
val file = sc.textFile("file.txt")
val blankLines = sc.accumulator(0) // 创建Accumulator[Int]并初始化为0
val callSigns = file.flatMap(line => {
  if (line == "") {
    blankLines += 1 // 累加器加1
  }
  line.split(" ")
})
callSigns.saveAsTextFile("output.txt")
println("Blank lines: " + blankLines.value)
```

注意，工作节点上的任务不能访问累加器的值。我的理解是转化操作都是惰性操作，使用累加器的值存在同步问题。

## 2. 共享变量之广播变量

Spark的第二种共享变量类型是广播变量，它可以让程序高效地向所有工作节点发送一个 较大的只读值，以供一个或多个Spark操作使用

广播变量为只读变量，它由运行SparkContext的驱动程序创建后发送给会参与计算的节点。

广播变量可以被非驱动程序所在的节点(即工作节点)访问，访问的方法是用该广播变量的value方法。

```scala
val broadcastAList = sc.broadcast(List("a", "b", "c", "d", "e"))
sc.parallelize(List("1", "2", "3")).map(x => broadcastAList.value ++ x).collect
```

## 3. 基于分区的操作

基于分区对数据进行操作可以让我们避免为每个数据元素进行重复的配置工作。诸如打开 数据库连接或创建随机数生成器等操作，都是我们应当尽量避免为每个元素都配置一次的 工作。Spark 提供基于分区的map和foreach，让你的部分代码只对RDD的每个分区运行 一次，这样可以帮助降低这些操作的代价。

## 4. 与外部程序间的管道

如果Scala、Java以及Python都不能实现你需要的功能，那么Spark也为这种情况提供了一种通用机制，可以将数据通过管道传给用其他语言编写的程序，比如R语言脚本。

有点类似Hadoop的Streaming

## 5. 数值RDD操作

Spark 对包含数值数据的RDD提供了一些描述性的统计操作。

之前已经使用过的类似rdd.count()的操作，Spark提供了一系列的操作，如果需要用到多个操作的话建议先调用rdd.stats()将这些值通过一次遍历全部计算出来。

Spark包含的统计值有：
```
count() RDD 中的元素个数 
mean() 元素的平均值 
sum() 总和
max() 最大值
min() 最小值
variance() 元素的方差 
sampleVariance() 从采样中计算出的方差 
stdev() 标准差
sampleStdev() 采样的标准差
```

下面这个例子用来筛选掉异常值

```scala
val distanceDouble = distance.map(string => string.toDouble)
val stats = distanceDoubles.stats()
val stddev = stats.stdev
val mean = stats.mean
val reasonableDistances = distanceDoubles.filter(x => math.abs(x-mean) < 3 * stddev) println(reasonableDistance.collect().toList)
```
