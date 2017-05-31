> 做数据分析最好的语言当然要数Python，虽然Hadoop由JAVA写成，但Python也可以很好地操控他。O’Reilly新书Hadoop with Python就介绍了如何使用Python Hadoop。书里面同时简要介绍了一些Hadoop的基本概念，因此笔记里包含一些关键知识点以及Python操作Hadoop的基本方法。[书籍链接](http://www.oreilly.com/programming/free/hadoop-with-python.csp)，右边填入个人信息就可以免费下载。

# 第一章 HDFS

## 1.1  简介

HDFS由两个主要进程：NameNode和DataNode，往往一个HDFS集群由一个专门服务器跑NameNode，上千个机器跑DataNode

### NameNode

主要存储filenames，file permissions，location of each block of each file。

Secondary NameNode保存备份以防止NameNode挂掉。

### DataNode

存储blocks，并且一个挂掉后NameNode会立刻复制其中的信息。

![An HDFS cluster with a replication factor of two; the NameNode contains the mapping of  files to blocks, and the DataNodes store the blocks and their replicas](http://upload-images.jianshu.io/upload_images/4097708-420311aff74869ff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 1.2 命令行HDFS交互

```
$ hdfs dfs -ls /
$ hdfs dfs -mkdir /user/hduser
$ hdfs dfs -put /home/hduser/input.txt /user/hduser
$ hdfs dfs -get input.txt /home/hduser
```

## 1.3 Snakebite实现Python与HDFS交互

Snakebite是一个python库，可以通过两种方式实现对HDFS的操作。第一种还是通过命令行，第二种则是通过Python脚本实现。官方文档在[这里](http://snakebite.readthedocs.io/en/latest/)。

首先安装Snakebite，目前仅支持python2
```
$ pip install snakebite
```

### 第一种. CLI交互

首先要做一下配置，一种简便的方法是创建`~/.snakebiterc`并写入如下信息。
注意host和port以及namenodes的数量要随实际情况修改。
```
{
  "config_version": 2,
  "use_trash": true,
  "namenodes": [
    {"host": "localhost", "port": 9000, "version": 9},
    {"host": "namenode-ha2", "port": 8020, "version": 9}
  ]
}
```

然后就可以通过`snakebite option`来进行操作了。
相比于`hdfs bfs -option`省去了-，更加美观方便。
```
$ snakebite --help #获取帮助信息
$ snakebite ls /
Found 1 items
drwxr-xr-x   - c          supergroup          0 2017-04-03 15:47 /xueshu
```

### 第二种. 程序交互

程序交互肯定更为实用啦，以后一个python脚本完成配置加运行不再是梦想，立刻来尝试几个基本功能。

先来看一个ls命令

```python
from snakebite.client import Client

client = Client('localhost', 9000)
for x in client.ls(['/']):
    print x
```
首先通过`Client('localhost', 9000)`建立起连接。然后每一条操作都是Client类的一个方法，注意为了效率，每个方法返回的都是一个Iterator。所以要完成全部操作，必须写入一个for循环中，同时把返回信息写出。

另外一些常用操作列在下面，注意每个方法返回的都是Python Iterator：
```
client.mkdir(['/foo/bar', '/input'], create_parent=True) # 创建目录
client.delete(['/foo', '/input'], recurse=True) # 删除文件或目录
client.copyToLocal(['/input/input.txt'], '/tmp') # 下载文件
client.text(['/input/input.txt']) # 显示文件
```

# 第二章 MapReduce

## 2.1 数据流

## 第一阶段 Map

**Mapper函数**接受Key-Value对的输入并输出Key-Value对。

## 第二阶段 Shuffle and Sort

把Mapper的中间结果移动到Reducer的过程叫做Shuffling。

Shuffling由一个**Partitioner函数**完成，Partitioner函数接收Mapper输出的Key以及Reducer的数量，输出对应的Reducer号。

Shuffling保证了相同Key的数据进入同一个Reducer，默认的Partitioner是hash法映射的。

在Reducer拿到数据前还要进行Sort。

## 第三阶段 Reduce

**Reducer函数**接收Iterator形式的数据流，并且将相同Key的数据做运算获得输出。

![Reducer实例](http://upload-images.jianshu.io/upload_images/4097708-978995c3a3d1e9c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 2.2 Hadoop Streaming

Hadoop Streaming提供了一个便于进行MapReduce编程的工具包，使用它可以基于一些可执行命令、脚本语言或其他编程语言来实现Mapper和 Reducer，从而充分利用Hadoop并行计算框架的优势和能力，来处理大数据。

### 工作原理

对Hadoop而言，Mapper和Reducer都是从标准输入读取并输出到标准输出

所以Hadoop Streaming就是做：

1. 从标准输入依次读取文件的每一行
2. 执行函数
3. 把标准输出转化成Key-Value对，或者Key-Null对

### Python实例

这个例子是WordCount的Python版本，分别实现了mapper和reducer，它们都是从stdin读取一行处理一行并将结果输出在stdout

```python
mapper.py
#!/usr/bin/env python
import sys

for line in sys.stdin:
	words = line.split()
	for word in words:
		print '{0}\t{1}'.format(word, 1)
```
首先，程序第一行指定python路径，用`#!/usr/bin/env python`可以满足多数需求，这样子可以用./mapper.py直接运行

mapper函数依次读取stdin的每一行并输出每一个单词和出现次数的Key-Value对，这里Value总是1。

```
reducer.py
#!/usr/bin/env python
import sys

curr_word = None
curr_count = 0

for line in sys.stdin:
    word, count = line.split('\t')
    count = int(count)
    if word == curr_word:
        curr_count += count
    else:
        if curr_word:
            print '{0}\t{1}'.format(curr_word, curr_count)
        curr_word = word
        curr_count = count

if curr_word == word:
    print '{0}\t{1}'.format(curr_word, curr_count)
```

Reducer函数也是从stdin读取每一行，然后把每个单词的出现次数统计出来。注意reducer接收的输入是排好序的！

为了使文件可以直接执行，即executable，运行这一行
```
chmod a+x mapper.py reducer.py
```

为了测试mapper和reducer的可用性，可以用这行linux命令，具体不多解释了

```
echo 'jack be nimble jack be quick' | ./mapper.py| sort -t 1 | ./reducer.py
```

接下来要做的事情就是交给hadoop完成词频统计吧。在我的系统下命令是这样的，具体实现因人而异。

实际上我们就是利用了hadoop streaming这样一个jar包，来将mapper和reducer函数指定为某个脚本，只要我们保证mapper和reducer是可以接收标准输入并把结果输出在标准输出的脚本即可，python、shell、R之类的都是无所谓的。注意input和output的文件路径都是指HDFS的路径，要事先将输入文件放入。

```
$ hadoop jar \
$HADOOP_HOME/libexec/share/hadoop/tools/lib/hadoop-streaming-2.7.3.jar \
 -files mapper.py,reducer.py \
-mapper mapper.py \
-reducer reducer.py \
-input /user/hduser/input.txt \
-output /user/hduser/output
```

我们的输入input.txt是这样的
```
jack be nimble jack be quick
quick jack me
```

在HDFS的/user/hduser/output/part-00000中得到以下输出，可以看到，我们已经成功了。
```
be	2
jack	3
me	1
nimble	1
quick	2
```

## 2.3 mrjob实现Python操控Hadoop Streaming

mrjob是一个Python库，实现了Hadoop的MapReduce操作。它封装了Hadoop streaming，可以让人用全Python的脚本实现Hadoop计算。它甚至可以让人在没有Hadoop环境下完成测试，并允许用户将mapper和reducer写进一个类里。简直是神器！

### 安装

一句话，pip依然那么轻松写意dyis
```
$ pip install mrjob
```

### Python代码

```
from mrjob.job import MRJob

class MRWordCount(MRJob):
    def mapper(self, _, line):
        for word in line.split():
            yield(word, 1)

    def reducer(self, word, counts):
        yield(word, sum(counts))

if __name__ == '__main__':
    MRWordCount.run()
```

运行
```
$ python word_count.py input.txt
"be"	2
"jack"	3
"me"	1
"nimble"	1
"quick"	2
```

就这样好了？我们都不需要配置Hadoop环境吗？答案是不需要，这就是mrjob的强大之处，帮你模拟了Hadoop的MR计算方式。

### 详解

1. mapper很容易理解，不多说。接收key和value并输出key和value的tuple，往往会缺省key。

2. combiner在这里没有定义，实际上combiner()是运行在mapper和reducer之间的过程。
书里的定义如下：The combiner’s input is a key, which was yielded by the mapper, and a value, which is a generator that yields all values yielded by one mapper that corresponds to the key. The combiner yields tuples of (output_key, output_value) as output. 
简单说就是combiner把mapper结果中key相同的值组合起来，生成一个generator把它作为该key新的value，并交给reducer去做。

3. reducer也是接收(key, value)，注意这里的value已经是一个Generator了。输出(key, value)。

### 更多选项

`-r inline`: 默认选项，单线程Python运行
`-r local`: 多进程
`-r hadoop`: 运行在Hadoop上

尝试了一下`-r hadoop`发现有报错，暂时不去管它，至少给了我们一个没有hadoop环境也可以做测试的方法。
