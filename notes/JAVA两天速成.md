> 找工作的时候发现某公司在线编程题只支持JAVA和C家族，作为一个天天python的我简直是灾难，两天速成一下！

# 第一部分：Java基础

## 封装与接口

- 在一个.java文件中，有且只能有一个类带有public关键字。不带关键字的也是可见的，后面会说到。

## Interface接口

```Java
  interface Cup {
      void addWater(int w);
      void drinkWater(int w);
  }
  
  class MusicCup implements Cup 
  {
      public void addWater(int w) 
      {
          this.water = this.water + w;
      }
  
      public void drinkWater(int w)
      {
          this.water = this.water - w;
      }
  
      private int water = 0;
  }
```

- 更易于管理
- 一个类可以使用多个interface

## 包

- 在.java文件的第一行写上`package com.vamei.society`

- 一个Java文件中只能有一个public的类,这个类要和文件同名！

- 一个Java类的完整路径由它的包和类名共同构成，比如com.vamei.society.Human。相应的Human.java程序要放在com/vamei/society/下。

- 一个类可以没有public关键字，它实际上也表示一种权限: 该类在它所在的包中可见。

### 包的调用

- 我们只需要将Human.java编译的Human.class放入相应的文件夹就可以了。比如，我将Human.class放入com/vamei/society/中。实际上，你也可以把.java文件放入相应路径，Java会在使用时自动编译。

- 如果整个包(也就是com文件夹)位于当前的工作路径中，那么不需要特别的设置，就可以使用包了。

- 可以这样import
```
  import com.vamei.society.*;
```

- 也可以直接提供完整路径使用，则不需要import

- 如果包没有放在当前工作路径下，我们在使用包时，需要通知Java。设置系统的CLASSPATH环境变量。

## 继承

```Java
  class Woman extends Human
  {
      /**
      * new method
      */
      public Human giveBirth()
      {
          System.out.println("Give birth");
          return (new Human(20));
      }
  }
```

- 基类和衍生类

- 衍生类不能访问基类的私有对象，public的是一样的都可以访问，衍生类可以访问自己的private，外部不能访问衍生类的private。

- protected表示自己不能被外部访问，但是可以被衍生类访问。

- super和this类似，但是super指父类，比如父类有个getHeight()方法，那么在子类定义新的getHeight()时可以在内部先执行super.getHeight()。当子类和父类有同样方法时(参数都一样)，那么对外呈现子类的，内部可以用super或者this区分。

- 调用父类的构造方法直接输入super即可。
```java
  class Woman extends Human
  {
      /**
      * constructor
      */
      public Woman(int h)
      {
          super(h); // base class constructor
          System.out.println("Hello, Pandora!");
      }
  }
```

## static 类数据与类方法

- 带static的成员变量是类成员变量，被所有对象共享

- 如果一个方法声明为static，那么它只能调用static的数据和方法，而不能调用非static的数据和方法。

- 调用类方法时，我们可以通过class.method()的方式调用，也可以通过object.method()的方式调用。

- 如果变量前加final，那么这个变量一旦赋值不能更改(当然如果这时类的实例，这个实例可以照常操作)

## 接口的继承与抽象类

- 接口可以继承接口，而且可以多重继承，但是类不行。
```java
  interface MusicCup extends Cup, Player 
  {
      void display();
  }
```

- 抽象类
```java
  abstract class Food {
      public abstract void eat();
      public void happyFood()
      {
          System.out.println("Good! Eat Me!");
      }
  }
```

- 抽象类中有抽象方法，继承的时候一定要覆盖这些抽象方法，抽象类与接口不同的是它也有具体方法以及具体变量。

## 对象引用

```java
  Human aPerson = new Human(160);
```
- 首先看等号的右侧。new是在内存中为对象开辟空间。具体来说，new是在内存的堆(heap)上为对象开辟空间。这一空间中，保存有对象的数据和方法。

- 再看等号的左侧。aPerson指代一个Human对象，被称为对象引用(reference)。实际上，aPerson并不是对象本身，而是类似于一个指向对象的指针。aPerson存在于内存的栈(stack)中。

- 当我们用等号赋值时，是将右侧new在堆中创建对象的地址赋予给对象引用。

- 这里的内存，指的是JVM (Java Virtual Machine)虚拟出来的Java进程内存空间。

- new关键字的完整含义是，在堆上创建对象。栈比较快。

- 基本类型(primitive type)的对象，比如int, double，保存在栈上。

- 一个对象可以有多个引用 (一个人可以放多个风筝)。当程序通过某个引用修改对象时，通过其他引用也可以看到该修改。

## 垃圾回收

- 随着方法调用的结束，引用和基本类型变量会被清空。由于对象存活于堆，所以对象所占据的内存不会随着方法调用的结束而清空。进程空间可能很快被不断创建的对象占满。

- Java内建有垃圾回收(garbage collection)机制，用于清空不再使用的对象，以回收内存空间。

- 当没有任何引用指向某个对象时，该对象被清空。

## 参数传递

- 基本类型变量的值传递，意味着变量本身被复制，并传递给Java方法。Java方法对变量的修改不会影响到原变量。

- 引用的值传递，意味着对象的地址被复制，并传递给Java方法。Java方法根据该引用的访问将会影响对象。

- 就是new出来的是引用传递，普通的是值传递，普通的是存在栈中的。

## 类型转换与多态

- Java是一种强类型(strongly typing)语言，它会对类型进行检查。比如奖Cup类的对象赋给Human类的引用就会报错。

- 为什么python也是强类型？

```python
  >>> 3+6
  9
  >>> "3"+6
  error
```

- python看起来是弱类型因为它的引用不用指定类型！！

### 基本类型变换

- 注意，只是基本类型！！！

```java
  int a;
  a = (int) 1.23;  // narrowing conversion
  int a = 3;
  double b;
  b = a; // widening conversion
```

- widening可以不加变量类型的

### upcast与多态

```java
  public class Test
  {
      public static void main(String[] args)
      { 
          Cup aCup;
          BrokenCup aBrokenCup = new BrokenCup();
          aCup = aBrokenCup; // upcast
          aCup.addWater(10); // method binding
      }
  }
```
- 我们随后调用了aCup(我们声明它为Cup类型)的addWater()方法。尽管aCup是Cup类型的引用，它实际上调用的是BrokenCup的addWater()方法！也就是说，即使我们经过upcast，将引用的类型宽松为其基类，Java依然能正确的识别对象本身的类型，并调用正确的方法。Java可以根据当前状况，识别对象的真实类型，这叫做多态(polymorphism)。多态是面向对象的一个重要方面。

- Java告诉我们，一个衍生类对象可以当做一个基类对象使用，而Java会正确的处理这种情况。

### downcast

- 我们可以将一个基类引用向下转型(downcast)成为衍生类的引用，但要求该基类引用所指向的对象，已经是所要downcast的衍生类对象。比如可以将上面的hisCup向上转型为Cup类引用后，再向下转型成为BrokenCup类引用。

### object类

- Java中，所有的类实际上都有一个共同的继承祖先，即Object类。

# 第二部分：JAVA进阶

## String

- String也是引用传递，不过它不需要new

- String是不可变对象，即使用replace也只是返回一个新的String

- 常用String的方法
```java
  s.length()                        返回s字符串长度
  s.charAt(2)                       返回s字符串中下标为2的字符 
  s.substring(0, 4)                 返回s字符串中下标0到4的子字符串
  s.indexOf("Hello")                返回子字符串"Hello"的下标
  s.startsWith(" ")                 判断s是否以空格开始
  s.endsWith("oo")                  判断s是否以"oo"结束
  s.equals("Good World!")           判断s是否等于"Good World!"
                                    ==只能判断字符串是否保存在同一位置。需要使用equals()判断字符串的内容是否相同。 
  s.compareTo("Hello Nerd!")        比较s字符串与"Hello Nerd!"在词典中的顺序，
                                    返回一个整数，如果<0，说明s在"Hello Nerd!"之前； 
                                                如果>0，说明s在"Hello Nerd!"之后；
                                                如果==0，说明s与"Hello Nerd!"相等。
  s.trim()                          去掉s前后的空格字符串，并返回新的字符串
  s.toUpperCase()                   将s转换为大写字母，并返回新的字符串
  s.toLowerCase()                   将s转换为小写，并返回新的字符串
  s.replace("World", "Universe")    将"World"替换为"Universe"，并返回新的字符串
```

## 异常处理
```java
  try {
    ...;
  }
  catch(IOException e) {
      e.printStackTrace();
      System.out.println("IO problem");
  }
  catch() {
    ...;
  }
  finally {
    ...;
  }
```
- 下图中红色的是unchecked错误，应该由程序员编程解决。蓝色的是由于和编程环境互动产生的错误，应该做异常处理。

![java异常处理](http://upload-images.jianshu.io/upload_images/4097708-497a44913a5bb281.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 抛出异常
```java
    private void test(double p) throws Exception // 包含throws关键词不一定会throw错误，只是做检查
    {
        if (p < 0) {
            Exception e = new Exception("p must be positive");
            throw e;
        }
    }
```

- 我们在useBattery()中有异常处理器。由于test()方法不直接处理它产生的异常，而是将该异常抛给上层的useBattery()，所以在test()的定义中，我们需要throws Exception来说明。

- 我们可以自定义异常类，只要继承现有异常类就可以了。

## IO基础

- 基本操作,读文件，文件要在project目录下
```java
    public static void main(String[] args) {
        try {
            BufferedReader br =
                    new BufferedReader(new FileReader("file.txt"));

            String line = br.readLine();

            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IO Problem");
        }
    }
```

- BufferedReader()是一个装饰器(decorator)，它接收一个原始的对象，并返回一个经过装饰的、功能更复杂的对象。

- 基本操作，写文件
```java
   try {
            String content = "Thank you for your fish.";

            File file = new File("new.txt");

            // create the file if doesn't exists
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        }
        catch(IOException e) {
            System.out.println("IO Problem");
        }
    }
```

## 内存管理与垃圾回收

- 在Java中，JVM中的栈记录了线程的方法调用。每个线程拥有一个栈。在某个线程的运行过程中，如果有新的方法调用，那么该线程对应的栈就会增加一个存储单元，即帧(frame)。在frame中，保存有该方法调用的参数、局部变量和返回地址。

- Java的参数和局部变量只能是基本类型的变量(比如int)，或者对象的引用(reference)。因此，在栈中，只保存有基本类型的变量和对象引用。引用所指向的对象保存在堆中。(引用可能为Null值，即不指向任何对象)

- 当被调用方法运行结束时，该方法对应的帧将被删除，参数和局部变量所占据的空间也随之释放。线程回到原方法，继续执行。当所有的栈都清空时，程序也随之运行结束。

### 循环引用

- 我们以栈和static数据为根(root)，从根出发，跟随所有的引用，就可以找到所有的可到达对象。也就是说，一个可到达对象，一定被根引用，或者被其他可到达对象引用。

![java循环引用.png](http://upload-images.jianshu.io/upload_images/4097708-52d5740e9b181169.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## JVM垃圾回收

- JVM的垃圾回收是多种机制的混合。JVM会根据程序运行状况，自行决定采用哪种垃圾回收。

- 我们先来了解"mark and sweep"。这种机制下，每个对象将有标记信息，用于表示该对象是否可到达。当垃圾回收启动时，Java程序暂停运行。JVM从根出发，找到所有的可到达对象，并标记(mark)。随后，JVM需要扫描整个堆，找到剩余的对象，并清空这些对象所占据的内存。

- 另一种是"copy and sweep"。这种机制下，堆被分为两个区域。对象总存活于两个区域中的一个。当垃圾回收启动时，Java程序暂停运行。JVM从根出发，找到可到达对象，将可到达对象复制到空白区域中并紧密排列，修改由于对象移动所造成的引用地址的变化。最后，直接清空对象原先存活的整个区域，使其成为新的空白区域。

- 可以看到，"copy and sweep"需要更加复杂的操作，但也让对象可以紧密排列，避免"mark and sweep"中可能出现的空隙。在新建对象时，"copy and sweep"可以提供大块的连续空间。因此，如果对象都比较"长寿"，那么适用于"mark and sweep"。如果对象的"新陈代谢"比较活跃，那么适用于"copy and sweep"。

- 上面两种机制是通过分代回收(generational collection)混合在一起的。每个对象记录有它的世代(generation)信息。所谓的世代，是指该对象所经历的垃圾回收的次数。世代越久远的对象，在内存中存活的时间越久。

## 容器

### 数组

```java
  Human[] persons = new Human[2];              // array size 2
  persons[0] = new Human(160);
  persons[1] = new Human(170);

  int[] a = {1, 2, 3, 7, 9};                   // array size 5
  System.out.println(a[2]);

  String[] names = {"Tom", "Jerry", "Luffy"};  // array size 3
  System.out.println(names[0]);
```

### List

```java
  import java.util.*;

  public class Test
  {
      public static void main(String[] args)
      {
          List<String> l1 = new ArrayList<String>();
          l1.add("good");
          l1.add("bad");
          l1.add("shit");
          l1.remove(0);
          System.out.println(l1.get(1));
          System.out.println(l1.size());
          Iterator i = l1.iterator();
          while(i.hasNext()) {
              System.out.println(i.next());
        }
      }
  }
```
- 输出可以用Iterator

- List是接口，ArrayList是实现

### 集合Set

```java
  import java.util.*;

  public class Test
  {
      public static void main(String[] args)
      {
          Set<Integer> s1 = new HashSet<Integer>();
          s1.add(4);
          s1.add(5);
          s1.add(4);
          s1.remove(5);
          System.out.println(s1);
          System.out.println(s1.size());
      }
  }
```

### Map 如HashMap

```java
  import java.util.*;

  public class Test
  {
      public static void main(String[] args)
      {
          Map<String, Integer> m1 = new HashMap<String, Integer>();
          m1.put("Vamei", 12);
          m1.put("Jerry", 5);
          m1.put("Tom", 18);
          System.out.println(m1.get("Vamei"));
  
      }
  }
```
