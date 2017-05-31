# Scala进阶

> 在后面的文章中，会涉及到一些Scala中我们还没有接触到的语法。这篇Scala进阶会在Scala基础上更进一步，完成对Scala语言特性的整体学习。

## 1. 闭包

看下面这个例子，multiplier函数的输出值不仅取决于输入参数i，还与变量factor相关，而factor是声明在函数外的。

有趣的是，函数可以对这个这个factor进行修改，仿佛factor是这个函数的一个私有变量。这就是闭包。

```scala
object Test {
    def multiplier (i: Int): Int = {
        factor = factor + 1
        return i * factor
    }

    var factor: Int = 6

    def main(args: Array[String]) {
        println(multiplier(1))
        println(multiplier(1))
    }
}
```

输出结果：

```
7
8
```

## 2. 字符串

Scala字符串使用的直接是java.lang.String，因此和Java的使用方法基本一致。

用几个例子就可以说明使用方法了。

```scala
// 创建字符串
val greeting: String = "Hello World"
//  String 对象是不可变的
// 如果你需要创建一个可以修改的字符串，可以使用 String Builder 类
val buf = new StringBuilder;
buf += 'a'
buf ++= "bcdef"
println( "buf is : " + buf.toString );
// 字符串长度
var len = palindrome.length();
// 字符串连接
"菜鸟教程官网： ".concat("www.runoob.com");
"菜鸟教程官网： " + ("www.runoob.com");
// 格式化输出
var fs = printf("浮点型变量为 " +
       "%f, 整型变量为 %d, 字符串为 " +
       " %s", floatVar, intVar, stringVar)
```

## 3. 数组Array

数组是Array，Array的特点是长度固定，元素可变，可以先定义后赋值

一维数组的创建方法，有三种

```scala
var z:Array[String] = new Array[String](3)
var z = new Array[String](3)
z(0) = "Runoob"; z(1) = "Baidu"; z(2) = "Google"
var z = Array("Runoob", "Baidu", "Google")
```

遍历数组

```scala
for ( x <- myList ) {
  println( x )
}
```

合并数组

```scala
var myList3 =  concat( myList1, myList2)
```

用range来创建区间数组

```scala
import Array._
for (x <- range(1,10,2)) {
  println(x)
}
```

多维数组的定义及使用

```scala
import Array._
var myMatrix = ofDim[Int](3,3)

for (i <- 0 to 2; j <- 0 to 2) {
  myMatrix(i)(j) = j;
}
```

## 4. Collections

Scala的集合包括以下五种

### 列表List

Scala 列表类似于数组，它们所有元素的类型都相同，但是它们也有所不同：列表是不可变的，值一旦被定义了就不能改变。

```scala
val nums: List[Int] = List(1, 2, 3, 4)
val nums = List(1, 2, 3, 4)
// 构造列表的两个基本单位是 Nil 和 ::
// Nil 也可以表示为一个空列表。
val nums = 1 :: (2 :: (3 :: (4 :: Nil)))
```

### 集合Set

Scala Set(集合)是没有重复的对象集合，所有的元素都是唯一的。Scala 集合分为可变的和不可变的集合。默认情况下，Scala 使用的是不可变集合。可变需要引入包。

```scala
val set:Set[Int] = Set(1,2,3)
val set = Set(1,2,3)
```

### 哈希表Map

Map(映射)是一种可迭代的键值对（key/value）结构。所有的值都可以通过键来获取。Map 中的键都是唯一的。默认情况下 Scala 使用不可变 Map。可变需要引入包。

```scala
var A:Map[Char,Int] = Map()
val colors = Map("red" -> "#FF0000", "azure" -> "#F0FFFF")
// 添加元素
A += ('I' -> 1)
A += ('J' -> 5)
A += ('K' -> 10)
// 访问元素
A('I')
```

### 元组Tuple

与列表一样，元组也是不可变的，但与列表不同的是元组可以包含不同类型的元素。

```scala
val t = (1, 3.14, "Fred") 
// 注意最多到Tuple22
val t = new Tuple3(1, 3.14, "Fred")
```

我们可以使用 t._1 访问第一个元素， t._2 访问第二个元素

### 选项Option

Scala Option(选项)类型用来表示一个值是可选的（有值或无值)。

Option[T] 是一个类型为 T 的可选值的容器： 如果值存在， Option[T] 就是一个 Some[T] ，如果不存在， Option[T] 就是对象 None 


```scala
val a:Option[Int] = Some(5)
val b:Option[Int] = None 
```

比如Map的get方法返回的就是Option对象

```scala
val myMap: Map[String, String] = Map("key1" -> "value")
val value1: Option[String] = myMap.get("key1")
val value2: Option[String] = myMap.get("key2")
 
println(value1) // Some("value1")
println(value2) // None
```

## 5. 迭代器Iterator

Scala Iterator（迭代器）不是一个集合，它是一种用于访问集合的方法。

迭代器 it 的两个基本操作是 next 和 hasNext。调用 it.next() 会返回迭代器的下一个元素，并且更新迭代器的状态。调用 it.hasNext() 用于检测集合中是否还有元素。

```scala
val it = Iterator("Baidu", "Google", "Runoob", "Taobao")
while (it.hasNext){
  println(it.next())
}
// 获取最值
println("最大元素是：" + ita.max )
println("最小元素是：" + itb.min )
// 获取长度
println("ita.size 的值: " + ita.size )
println("itb.length 的值: " + itb.length )
```

## 6. 类和对象

直接用一个例子体会一下就好了，语法和Java只有微小的区别

```scala
class Point(val xc: Int, val yc: Int) {
   var x: Int = xc
   var y: Int = yc
   def move(dx: Int, dy: Int) {
      x = x + dx
      y = y + dy
      println ("x 的坐标点 : " + x);
      println ("y 的坐标点 : " + y);
   }
}

class Location(xc: Int, yc: Int,
   val zc :Int) extends Point(xc, yc){
   var z: Int = zc

   def move(dx: Int, dy: Int, dz: Int) {
      x = x + dx
      y = y + dy
      z = z + dz
      println ("x 的坐标点 : " + x);
      println ("y 的坐标点 : " + y);
      println ("z 的坐标点 : " + z);
   }
}

object Test {
   def main(args: Array[String]) {
      val loc = new Location(10, 20, 15);

      // 移到一个新的位置
      loc.move(10, 10, 5);
   }
}
```

- Scala重写一个非抽象方法，必须用override修饰符。例子里面的move方法参数不同，算是重载了，所以不需要override。

Scala 单例对象：Scala有个非常简便的创建单例模式对象的关键词object，可以实现类似Java下单例代码的功能，object对象不能带有参数，写法如下

```
object Point {
   var x: Int = 1
   var y: Int = 2
   def move(dx: Int, dy: Int) {
      x = x + dx
      y = y + dy
      println ("x 的坐标点 : " + x);
      println ("y 的坐标点 : " + y);
   }
}

object Test {
   def main(args: Array[String]) {
      println(Point.x)
      Point.move(2,3)
   }
}
```

Java单例模式写法

```java
public class Singleton {
    private static Singleton instance;
    private Singleton (){}
    public static Singleton getInstance() {
     if (instance == null) {
         instance = new Singleton();
     }
     return instance;
    }
}
```

伴生对象，与类共享名字，可以访问类的私有属性和方法

## 7. 特征Trait

Scala Trait(特征) 相当于 Java 的接口，实际上它比接口还功能强大。与接口不同的是，它还可以定义属性和方法的实现。

一般情况下Scala的类只能够继承单一父类，但是如果是 Trait(特征) 的话就可以继承多个，从结果来看就是实现了多重继承。

```scala
trait Equal {
    def isEqual(x: Any): Boolean
    def isNotEqual(x: Any): Boolean = !isEqual(x)
}
```

当需要继承一个类，扩展多个trait时，这样写

```scala
class Location(xc: Int, override val yc: Int,
   val zc :Int) extends Point(xc, yc) with Equal
```

## 8. 模式匹配

模式匹配和C及Java中的switch有点类似，但是Scala的模式匹配更为强大，可以匹配不同数据类型。

```scala
object Test {
   def main(args: Array[String]) {
      println(matchTest("two"))
      println(matchTest("test"))
      println(matchTest(1))
      println(matchTest(6))

   }
   def matchTest(x: Any): Any = x match {
      case 1 => "one"
      case "two" => 2
      case y: Int => "scala.Int"
      case _ => "many"
   }
}
```

输出结果为：

```
2
many
one
scala.Int
```

补充：使用了case关键字的类定义就是就是样例类(case classes)，样例类是种特殊的类，经过优化以用于模式匹配。

## 9. 异常处理

### 抛出异常

```scala
throw new IllegalArgumentException
```

### 捕获异常

```scala
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

object Test {
   def main(args: Array[String]) {
      try {
         val f = new FileReader("input.txt")
      } catch {
         case ex: FileNotFoundException => {
            println("Missing file exception")
         }
         case ex: IOException => {
            println("IO Exception")
         }
      } finally {
         println("Exiting finally...")
      }
   }
}
```

执行结果为

```
Missing file exception
Exiting finally...
```

## 10. 提取器

Scala 提取器是一个带有unapply方法的对象。unapply方法算是apply方法的反向操作：unapply接受一个对象，然后从对象中提取值，提取的值通常是用来构造该对象的值。

## 11. 文件I/O

### 写文件

```scala
import java.io._

object Test {
   def main(args: Array[String]) {
      val writer = new PrintWriter(new File("test.txt" ))

      writer.write("菜鸟教程")
      writer.close()
   }
}
```

### 读取输入

```scala
object Test {
   def main(args: Array[String]) {
      print("请输入菜鸟教程官网 : " )
      val line = Console.readLine
      
      println("谢谢，你输入的是: " + line)
   }
}
```

### 读文件

```scala
import scala.io.Source

object Test {
   def main(args: Array[String]) {
      println("文件内容为:" )

      Source.fromFile("test.txt" ).foreach{ 
         print 
      }
   }
}
```
