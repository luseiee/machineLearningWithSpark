> Hadoop中，键值对是最基本的操作对象。同样，Spark中，针对键值对类型的RDD有非常丰富的API可以被调用。利用这些API，可以比Hadoop更方便地完成大数据任务。

## 1. 创建Pair RDD

创建Pair RDD的方法是让RDD中的每一项都是包含两个元素的tuple。前一个元素会被当成Key，后一个元素会被当成value。

```
val pairs = sc.parallelize(List((1, 1), (2, 3)))
```

另外，通过map操作生成tuple也是一种很常见的做法。
```scala
val pairs = lines.map(x => (x.split(" ")(0), x))
```

## 2. Pair RDD的转化操作

### (1). 聚合操作

reduceByKey()可以实现对Pair RDD的相同键的元素进行聚合，注意这在普通RDD中是个行动操作，在这里是个转化操作。下面这个例子就可以实现单词记数了。

```scala
// 单词记数
val input = sc.textFile("s3://...")
val words = input.flatMap(x => x.split(" "))
val result = words.map(x => (x, 1)).reduceByKey((x, y) => x + y)
```

第一步，利用flatMap获取由每个单词组成的RDD，再利用Map得到Pair RDD，形式为(word, 1)，再把每个相同word(key)对应的value累加，就得到了对应(word, number)的Pair RDD。

-----

combineByKey()和RDD中的aggregate()类似，不过要复杂的多。它的目的也是为了使得返回值类型和输入类型不同。比如求每个键对应的平均值，我们的输入是(word, value)，现在我们要通过转化操作得到(word, (sum, num))，之后再进一步由sum/num得到平均值。

```scala
//使用combineByKey求每个键对应平均值
 val input = sc.parallelize(List(('a',3),('b',4),('c',5),('a',1),('a',5),('c',1)))
 val result = input.combineByKey(
       (v) => (v, 1),
       (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),
       (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2)
       ).map{ case (key, value) => (key, value._1 / value._2.toFloat) }
result.collectAsMap().map(println(_))
```

combineByKey接收三个函数作为参数，第一个函数是遇到第一次出现的Key时对Value进行的操作得到NewValue，第二个函数是遇到出现过的Key之后，对NewValue执行的reduce操作；第三个函数则是由于分区操作，对每个分区的中间结果也要进行reduce操作。

注意这里的map用了新的写法，这种形式可以记一下，对比较复杂的数据形式会比较好用。

最后出现的collectAsMap是把tuple转化成Map

-----

这里的大多数操作符都能接收第二个参数，这个参数用来指定分组结果或聚合结果的 RDD 的分区数，比如

```scala
sc.parallelize(data).reduceByKey((x, y) => x + y)
```

此外还可以将RDD用repartion()来重新分区，并用partition.size获得分区数

```
lines.repartition(10).partitions.size
```

### (2). 分组操作

groupByKey() 就会使用 RDD 中的键来对数据进行 分组。对于一个由类型 K 的键和类型 V 的值组成的 RDD，所得到的结果 RDD 类型会是 [K, Iterable[V]]。

```scala
val input = sc.parallelize(List(('a',3),('b',4),('c',5),('a',1),('a',5),('c',1)))
input.groupByKey().collect
```

输出为

```scala
Array[(Char, Iterable[Int])] = Array((a,CompactBuffer(3, 1, 5)), (b,CompactBuffer(4)), (c,CompactBuffer(5, 1)))
```

### (3). 连接

Scala还提供了许多API来实现数据库中的内连接外连接等操作，非常便捷。

下面用一些伪代码来举几个例子。

```
storeAddress = {
       (Store("Ritual"), "1026 Valencia St"), (Store("Philz"), "748 Van Ness Ave"),
       (Store("Philz"), "3101 24th St"), (Store("Starbucks"), "Seattle")}
storeRating = {
       (Store("Ritual"), 4.9), (Store("Philz"), 4.8))}
storeAddress.join(storeRating) == {
       (Store("Ritual"), ("1026 Valencia St", 4.9)),
       (Store("Philz"), ("748 Van Ness Ave", 4.8)),
       (Store("Philz"), ("3101 24th St", 4.8))}

storeAddress.leftOuterJoin(storeRating) ==
     {(Store("Ritual"),("1026 Valencia St",Some(4.9))),
       (Store("Starbucks"),("Seattle",None)),
       (Store("Philz"),("748 Van Ness Ave",Some(4.8))),
       (Store("Philz"),("3101 24th St",Some(4.8)))}
```

### (4) 排序

sortByKey()可以对RDD进行按key的排序

## 3. Pair RDD行动操作

相比于普通RDD而言多了以下三个操作

- countByKey()，返回(key, number)，其实可以直接用来实现wordcount
- collectAsMap()，把tuple转化成Map
- lookup(key)，返回这个key的所有value

## 4. 数据分区(进阶)

 Spark 可以确保同一组的键出现在同一个节点上。

通过数据分区，我们可以对一些操作进行优化，比如下面这个操作。假设这个操作每五分钟执行一次，且events一直在改变。

```scala
val userData = sc.sequenceFile[UserID, UserInfo]("hdfs://...").persist()
val joined = userData.join(events)
```

默认情况下，连接操作会将两个数据集中的所有键的哈希值都求出来，将该哈希值相同的记录通过网络传到同一台机器上，然后在那台机器上对所有键相同的记录进行连接操作。想要优化，可以执行下面操作。

```scala
val userData = sc.sequenceFile[UserID, UserInfo]("hdfs://...")
.partitionBy(new HashPartitioner(100)) // 构造100个分区 .persist()
```

当调用 userData. join(events) 时，Spark 只会对 events 进行数据混洗操作，将 events 中特定UserID 的记录发送到 userData 的对应分区所在的那台机器上。

能够从数据分区中获益的操作有cogroup()、 groupWith()、join()、leftOuterJoin()、rightOuterJoin()、groupByKey()、reduceByKey()、 combineByKey() 以及 lookup()。

此外，我们还可以自定义分区的方式。

## 5. 综合例子PageRank

PageRank 算法是以 Google 的拉里· 佩吉(Larry Page)的名字命名的，用来根据外部文档指向一个 文档的链接，对集合中每个文档的重要程度赋一个度量值。该算法可以用于对网页进行排 序，当然，也可以用于排序科技文章或社交网络中有影响的用户。

直接上代码

```scala
import org.apache.spark.HashPartitioner
var links = sc.parallelize(List((1,Seq(2,3,4)), (2,Seq(1,3)), (3,Seq(1)),(4,Seq(3)))).partitionBy(new HashPartitioner(100)).persist()
// 将每个页面的排序值初始化为1.0;由于使用mapValues，生成的RDD 
// 的分区方式会和"links"的一样
var ranks = links.mapValues(v => 1.0)
// 运行10轮PageRank迭代
for(i <- 0 until 10) {
  val contributions = links.join(ranks).flatMap {
   case (pageId, (links, rank)) =>
   links.map(dest => (dest, rank / links.size))
  }
  ranks = contributions.reduceByKey((x, y) => x + y).mapValues(v => 0.15 + 0.85*v)
}
// 写出最终排名 
ranks.saveAsTextFile("ranks")
```

数据准备，我们用最简单的数据来说明，links中的数据代表了：页面1中含有页面2，3，4的链接，以此类推。我们要做的任务是算出每个页面的重要程度。

我们把ranks初始化为1.o，因此会得到`rank = (1, 1.0), (2, 1.0), (3, 1.0), (4, 1.0)`。

算法的核心是一个迭代算法，一般迭代十次左右即可达到接近最优解。每次迭代是这样的：

1. 将每个页面的排序值初始化为 1.0。
2. 在每次迭代中，对页面 p，向其每个相邻页面(有直接链接的页面)发送一个值为 rank(p)/numNeighbors(p) 的贡献值。
3. 将每个页面的排序值设为0.15 + 0.85 * contributionsReceived。

下面我们来一步步分解核心操作的每一步得到了什么

```
1. 命令：links.join(ranks)
得到：Array((1,(List(2, 3, 4),1.0)), (2,(List(1, 3),1.0)), (3,(List(1),1.0)), (4,(List(3),1.0)))

2. 命令：links.join(ranks).flatMap {
   case (pageId, (links, rank)) =>
   links.map(dest => (dest, rank / links.size))
  }
得到：Array((2,0.33), (3,0.33), (4,0.33), (1,0.5), (3,0.5), (1,1.0), (3,1.0))

3. 命令：ranks = contributions.reduceByKey((x, y) => x + y).mapValues(v => 0.15 + 0.85*v)
得到：Array((1,1.42), (2,0.43), (3,1.71), (4,0.43))
```

这里第二步比较难理解，我们看`links.map()这一步`，对第一个数据而言，links=(2,3,4)，注意这里的map是在对List操作，不是RDD，所以links.size就是3。之后由于是flatMap，会把其中的Sequence给展开，就得到上面的结果。

执行十次之后，结果为：

```
1,1.515645550066273
2,0.5816554333073632
3,1.3210435833189986
4,0.5816554333073632
```

可以看到第1个页面最为重要，第3个页面尽管被链接次数最多，但是在被第1个页面链接中占的权重太小。



