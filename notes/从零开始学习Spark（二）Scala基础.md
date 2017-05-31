# Scala基础

> Spark的原生语言是Scala，因此入门一下Scala是学习Spark的第一步，下面就快速入门一下，争取不花太多的时间。之后的简书中还会有Scala进阶，交代一些其他特性。这篇Scala基础应该可以暂时应付之后Spark的学习。

- Scala运行在JVM上
- Scala是纯面向对象的语言
- Scala是函数式编程语言
- Scala是静态类型语言

## 1. HelloWorld

```scala
object HelloWorld {
    def main(args: Array[String]): Unit = {
        println("Hello, world!")
    }
}
```

## 2. 交互式编程与脚本形式

交互式编程进入方式有两种，terminal中输入`scala`，或者输入`sbt`再输入`console`

脚本形式则类似Java的编译形式
```
$ scalac HelloWorld.scala
$ scala HelloWorld.scala
```
scalac编译生成.class文件，注意可以跳过这一步，直接使用scala来执行

## 3. Scala包

定义包
```scala
package com.runoob
class HelloWorld
```

第二种定义包的方法，可以一个文件中定义多个包
```scala
package com.runoob {
  class HelloWorld 
}
```

源文件的目录和包之间并没有强制的关联关系。你不需要将Employee.scala放在com/horstmann/impatient目录当中

```scala
package com {
  package horstmann {
    package impatient {
      class Manager
        ......
    }
  }
}
```

引用包
```scala
import com.runoob.HelloWorld
import java.awt._    //引入包内所有成员
```

注意scala中类和对象一般用大写字母开头

## 4. Scala变量与数据类型

### 数据类型

只罗列几个特殊的，注意所有的数据类型都是对象

- Byte, Short, Int(默认), Long, Float, Double(默认), Char, String
- Unit	表示无值，和其他语言中void等同。用作不返回任何结果的方法的结果类型。Unit只有一个实例值，写成()。
- Null	null 或空引用
- Nothing	Nothing类型在Scala的类层级的最低端；它是任何其他类型的子类型。
- Any	Any是所有其他类的超类
- AnyRef	AnyRef类是Scala里所有引用类(reference class)的基类

### 变量和常量

在 Scala 中，使用关键词 "var" 声明变量，使用关键词 "val" 声明常量

```scala
var myVar: String = "Foo"
var myVar2: Int //变量声明不一定要初始值
val myVal: String = "Too"  //常量不能修改
var myVar = 10;  //常量和变量的声明不一定要指明数据类型，编译器会自己推断
val myVal = "Hello, Scala!";  
```

还可以声明一个元组，变量常量都可以

```
scala> val pa = (40,"Foo")
pa: (Int, String) = (40,Foo)
```

## 5. Scala访问修饰符

### Scala访问权限

- private：仅在包含了成员定义的类或对象内部可见。Java中允许这两种访问，因为它允许外部类访问内部类的私有成员。

```
class Outer{
    class Inner{
      private def f(){println("f")}
    class InnerMost{
        f() // 正确
        }
    }
    (new Inner).f() //错误
}
```

- protected：只允许保护成员在定义了该成员的的类的子类中被访问。而在java中，用protected关键字修饰的成员，除了定义了该成员的类的子类可以访问，同一个包里的其他类也可以进行访问。

```scala
package p{
    class Super{
        protected def f() {println("f")}
    }
    class Sub extends Super{
        f()
    }
    class Other{
        (new Super).f() //错误
    }
}
```

- public：Scala中，如果没有指定任何的修饰符，则默认为 public。这样的成员在任何地方都可以被访问。

### JAVA的访问权限表

- public--都可访问(公有)

- protected--包内和子类可访问（保护）

- 不写(default)--包内可访问 （默认）

- private--类内可访问（私有）

### 作用域保护

`private[x]` 或 `protected[x]`

这里的x指代某个所属的包、类或单例对象。如果写成private[x],读作"这个成员除了对[…]中的类或[…]中的包中的类及它们的伴生对像可见外，对其它所有类都是private。

## 6. 运算符，条件，循环

和JAVA基本相同，举几个for循环使用的例子

<-表示为变量赋值，to表示包含后面的值

```scala
for( a <- 1 to 10){
  println( "Value of a: " + a );
}
```

until表示不包含后面的值

```scala
for( a <- 1 until 10){
  println( "Value of a: " + a );
}
```

在 for 循环 中你可以使用分号 (;) 来设置多个区间，它将迭代给定区间所有的可能值。

```scala
for( a <- 1 to 3; b <- 1 to 3){
  println( "Value of a: " + a );
  println( "Value of b: " + b );
}
```

for 循环会迭代所有集合的元素

```scala
val numList = List(1,2,3,4,5,6);
for( a <- numList ){
  println( "Value of a: " + a );
}
```

for循环还可以加入条件过滤

```scala
val numList = List(1,2,3,4,5,6,7,8,9,10);
// 下面的代码会输出1,2,4,5,6,7
for( a <- numList
  if a != 3; if a < 8 ){
  println( "Value of a: " + a );
}
```

利用yield来产生集合，和python不一样的

```scala
val numList = List(1,2,3,4,5,6,7,8,9,10);
// 下面的代码会输出1,2,4,5,6,7
var retVal =  for( a <- numList
  if a != 3; if a < 8 )yield a

retVal: List[Int] = List(1, 2, 4, 5, 6, 7)
```

## 7. 函数

### 声明

```
def functionName ([参数列表]) : [return type]
```

### 定义

```
def functionName ([参数列表]) : [return type] = {
   function body
   return [expr]
}
```

如果函数没有返回值，可以返回为 Unit，这个类似于 Java 的 void

### 例子

```scala
object Test {
    def main(args: Array[String]) {
        println("Hello\tWorld\n\n")
        println("3 + 4 = " + add(3, 4))
    }
    def add(a: Int, b: Int): Int = {
        var sum: Int = a + b
        return sum
    }
}
```

## 8. 函数高级特性

### 传名调用

传名调用(Call-by-name)：参数传入时，不事先计算，而是将表达式子代入其中

传入参数时要用`=>`

```scala
object Test {
   def main(args: Array[String]) {
        delayed(time());
   }

   def time() = {
      println("获取时间，单位为纳秒")
      System.nanoTime
   }
   def delayed( t: => Long ) = {
      println("在 delayed 方法内")
      println("参数： " + t)
      t
   }
}
```

这个时候，相当于函数中的每个t都编程time()，所以输出会变成

```
在 delayed 方法内
获取时间，单位为纳秒
参数： 241550840475831
获取时间，单位为纳秒
```

### 可变参数

Scala 允许你指明函数的最后一个参数可以是重复的，即我们不需要指定函数参数的个数，可以向函数传入可变长度参数列表。

Scala 通过在参数的类型之后放一个星号来设置可变参数(可重复的参数)。

```scala
object Test {
   def main(args: Array[String]) {
        printStrings("Runoob", "Scala", "Python");
   }
   def printStrings( args:String* ) = {
      var i : Int = 0;
      for( arg <- args ){
         println("Arg value[" + i + "] = " + arg );
         i = i + 1;
      }
   }
}
```

### 高阶函数

高阶函数（Higher-Order Function）就是操作其他函数的函数。

```scala
object Test {
   def main(args: Array[String]) {
      println( apply( layout, 10) )
   }
   // 函数 f 和 值 v 作为参数，而函数 f 又调用了参数 v
   def apply(f: Int => String, v: Int) = f(v)
   def layout[A](x: A) = "[" + x.toString() + "]"
}
```

### 偏应用函数

实际上就是固定函数的某些参数生成新的函数，使用下划线(_)替换缺失的参数列表

```scala
import java.util.Date
object Test {
   def main(args: Array[String]) {
      val date = new Date
      val logWithDateBound = log(date, _ : String) //logWithDateBound就变成函数了
      logWithDateBound("message1" )
      Thread.sleep(1000)
      logWithDateBound("message2" )
      Thread.sleep(1000)
      logWithDateBound("message3" )
   }
   def log(date: Date, message: String)  = {
     println(date + "----" + message)
   }
}
```

### 匿名函数

相当于python中的lambda

```scala
var inc = (x:Int) => x+1
var mul = (x: Int, y: Int) => x*y
var userDir = () => { System.getProperty("user.dir") }
//调用
var x = inc(7)-1
println(mul(3, 4))
println(userDir())
```

### 函数柯里化(Currying)

```scala
def add(x:Int)(y:Int) = x + y
// 调用
var a = add(1)(2)
```

add(1)(2) 实际上是依次调用两个普通函数（非柯里化函数），第一次调用使用一个参数 x，返回一个函数类型的值，第二次使用参数y调用这个函数类型的值。

# 参考资料

[Scala教程 - 菜鸟教程](http://www.runoob.com/scala/scala-tutorial.html)

[Scala学习(七)---包和引入](http://www.cnblogs.com/sunddenly/p/4436897.html)
