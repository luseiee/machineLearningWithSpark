# RDD编程

> RDD (Resilient Distributed Dataset 弹性分布式数据集)是Spark中最核心的概念。学好了RDD就理解了Spark，这一章就是通过一些最简单的例子来实现RDD的Scala编程。RDD的核心操作有三个，创建，转化操作，行动操作。

## 1. 概述

下面这个最简单的例子代表了RDD的一系列典型操作。下面为Python代码，Scala也基本一样。

```python
lines = sc.textFile("README.md") //创建
pythonLines = lines.filter(lambda line: "Python" in line) //转化操作
pythonLines.first() //行动操作
```

- 第一行创建了一个RDD

- 第二行对RDD执行了一个转化操作，返回的还是RDD。注意Spark 只会惰性计算这些 RDD，意味着程序只是记录了你的操作，但并不会实际执行它，文件并没有被读取，filter操作并没有执行。RDD 的转化操作是返回一 个新的RDD 的操作，比如 map()和filter()。

- 第三行对应了行动操作，此时才会实际读取文件，执行操作。行动操作则是向驱动器程序返回结果或把结果写入外部系统的操作，会触发实际的计算，比如count()和first()。而返回的也会是类似于Int，Array，String等数据类型。

## 2. 创建RDD

### 第一种. 读取外部数据集

```scala
val lines = sc.textFile("README.md")
```

### 第二种. 利用已有集合

创建 RDD 最简单的方式就是把程序中一个已有的集合传给 SparkContext 的 parallelize() 方法

除了开发原型和测试时，这种方式用得并不多，毕竟这种方式需要把你的整个数据集先放在一台机器的内存中。

```scala
val lines = sc.parallelize(List("pandas", "i like pandas"))
```

## 3. 转化操作

### 针对各个元素的转化操作

1. map()接收一个函数，把这个函数用于 RDD中的每个元素，将函数的返回结果作为结果。
```scala
 val input = sc.parallelize(List(1, 2, 3, 4))
 val result = input.map(x => x * x)
```
如果有一个字符串 RDD，并且我们的map() 函数是用来把字符串解析并返回一个Double值的，那么此时我们的输入 RDD 类型就是 RDD[String]，而输出类型是RDD[Double]。

2. filter() 则接收一个函数，并将 RDD 中满足该函数的元素放入新的 RDD 中返回，注意传递的函数返回值必须为布尔型。
```scala
newlines = lines.filter(line => line.contains("error")) 
```

3. flatMap()的函数被分别应用到了输入RDD的每个元素上。不过返回的不是一个元素，而是一个返回值序列的迭代器。输出的 RDD 倒不是由迭代器组成的。我们得到的是一个包含各个迭代器可访问的所有元素的RDD。
```scala
val lines = sc.parallelize(List("hello world", "hi")) 
val words = lines.flatMap(line => line.split(" ")) 
words.first() // 返回"hello"
```

4. distinct()操作可以去除重复元素，不过这涉及到了数据混洗，效率十分低下。

### 伪集合操作，对两个RDD操作

下面的四个方法分别实现了并集，交集，差集和笛卡尔积。

```scala
val a = sc.parallelize(List(1, 2, 3))
val b = sc.parallelize(List(3, 4, 5))
```

1. union(other)是集合操作，它会返回一个包含两个RDD中所有元素的RDD。union不会去重。

2. intersection(other) 方法，只返回两个RDD中都有的元素。注意intersection()也会涉及到数据混洗，效率十分低下。

3. subtract(other) 函数接收另一个 RDD作为参数，返回一个由只存在于第一个 RDD 中而不存在于第二个 RDD 中的所有元素组成的 RDD。

4. cartesian(other) 转化操作会返回 所有可能的(a, b)对

```scala
a.cartesian(b) //{(1, 3), (1, 4), ... (3, 5)}，注意返回的还是RDD哦
```

## 4. 行动操作

1. collect()函数，可以用来获取整个RDD中的数据。只有当你的整个数据集能在单台机器的内存中放得下时，才能使用collect()，因此，collect()不能用在大规模数据集上。

2. reduce()函数，它接收一个函数作为参数，这个函数要操作两个RDD 的元素类型的数据并返回一个同样类型的新元素。
```scala
val sum = rdd.reduce((x, y) => x + y) //这个操作实现了求和
```

3. fold()和reduce()类似，接收一个与reduce()接收的函数签名相同的函数，再加上一个 “初始值”来作为每个分区第一次调用时的结果。
```scala
rdd.fold(0)((x, y) => x + y)
```

4. aggregate()函数则把我们从返回值类型必须与所操作的 RDD 类型相同的限制中解放出来。例如，在计算平均值时，需要记录遍历过程中的计数以及元素的数量，这就需要我们返回一个二元组。关于具体使用方法[看这里](http://blog.csdn.net/qingyang0320/article/details/51603243)。
```scala
val result = input.aggregate((0, 0))(
                     (acc, value) => (acc._1 + value, acc._2 + 1),
                     (acc1, acc2) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
val avg = result._1 / result._2.toDouble
```

5. take(n)返回RDD中的n个元素，并且尝试只访问尽量少的分区，因此该操作会得到一个不均衡的集合。

6. top()从RDD中获取前几个元素。

7. foreach()行动操作来对RDD中的每个元素进行操作，而不需要把RDD发回本地。foreach没有返回值。
```scala
line.foreach(println)

8. 有些函数只能用于特定类型的RDD，比如mean()和variance()只能用在数值RDD上， 而join()只能用在键值对RDD上。

## 5. persist(缓存)

Spark RDD是惰性求值的，而有时我们希望能多次使用同一个RDD。如果简单地对RDD调用行动操作，Spark 每次都会重算RDD以及它的所有依赖。这在迭代算法中消耗格外大，因为迭代算法常常会多次使用同一组数据。使用persist就可以解决这个问题，传入的参数决定了缓存级别。

```scala
  val result = input.map(x => x * x)
  result.persist(StorageLevel.DISK_ONLY)
  println(result.count())
  println(result.collect().mkString(","))
```

## 6. 一个需要注意的点

当你传递的对象是某个对象的成员，或者包含了对某个对象中一个字段的引用时(例 如 self.field)，Spark 就会把整个对象发到工作节点上，这可能比你想传递的东西大得多

python正确写法
```python
class WordFunctions(object):
        ...
  def getMatchesNoReference(self, rdd):
  # 安全:只把需要的字段提取到局部变量中 query = self.query
  return rdd.filter(lambda x: query in x)
```

Scala正确写法
```scala
def getMatchesNoReference(rdd: RDD[String]): RDD[String] = {
  // 安全:只把我们需要的字段拿出来放入局部变量中 val query_ = this.query
  rdd.map(x => x.split(query_))
}
```

## 7. Spark与Hadoop的一个区别

在类似Hadoop MapReduce的系统中，开发者常常花费大量时间考虑如何把操作组合到一起，以减少 MapReduce 的周期数。而在Spark中，写出一个非常复杂的映射并不见得能比使用很多简单的连续操作获得好很多的性能。根本原因就是Spark的转化操作是惰性操作。
