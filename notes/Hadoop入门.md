> 慕课网《Hadoop大数据平台架构与实践--基础篇》学习笔记

# 1. 初识Hadoop

## 历史
Google三驾马车：MapReduce, BigTable, GFS带来了革命性的变化

- 成本降低，可以用PC机
- 软件容错，可以使硬件故障视为常态
- 简化并行分布式计算

Google只是公布了论文，没有公布源码。于是模仿Google的开源Hadoop出现了！

## Hadoop功能

核心功能是分布式存储和分布式计算，HDFS + MapReduce

优势：高扩展，低成本，成熟的生态圈

## Hadoop生态圈

- HDFS, MapReduce,
- HIVE: SQL语句转化成Hadoop任务，降低人们使用门槛
- HBASE: 存储结构化数据的分布式数据库，放弃了事务特性，强调高扩展
- ZooKeeper: 运维工具

# 2. Hadoop的安装(MacOS环境)

## 安装JAVA

略去

## 安装Hadoop

```
brew install hadoop
```
安装完成后，brew有这么一段提示，留用
```
In Hadoop's config file:
  /usr/local/opt/hadoop/libexec/etc/hadoop/hadoop-env.sh,
  /usr/local/opt/hadoop/libexec/etc/hadoop/mapred-env.sh and
  /usr/local/opt/hadoop/libexec/etc/hadoop/yarn-env.sh
$JAVA_HOME has been set to be the output of:
  /usr/libexec/java_home
```

随后需要配置一系列的文件
```
1. /usr/local/Cellar/hadoop/2.6.0/libexec/etc/hadoop/hadoop-env.sh

export HADOOP_OPTS="$HADOOP_OPTS -Djava.net.preferIPv4Stack=true"
修改为:
export HADOOP_OPTS="$HADOOP_OPTS -Djava.net.preferIPv4Stack=true -Djava.security.krb5.realm= -Djava.security.krb5.kdc="

2. /usr/local/Cellar/hadoop/2.7.3/libexec/etc/hadoop/core-site.xml

<configuration>
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/usr/local/Cellar/hadoop/hdfs/tmp</value>
    <description>A base for other temporary directories.</description>
  </property>
  <property>
    <name>fs.default.name</name>
    <value>hdfs://localhost:9000</value>          
  </property>
</configuration>

3. /usr/local/Cellar/hadoop/2.6.0/libexec/etc/hadoop/mapred-site.xml

<configuration>
  <property>
    <name>mapred.job.tracker</name>
    <value>localhost:9010</value>
  </property>
</configuration>

4. /usr/local/Cellar/hadoop/2.6.0/libexec/etc/hadoop/hdfs-site.xml

<configuration>
  <property>
    <name>dfs.replication</name>
    <value>1</value>
  </property>
</configuration>

5. $ hadoop namenode -format
```

## 启动HDFS

```
$ ./start-dfs.sh  #启动HDFS
$ ./stop-dfs.sh  #停止HDFS
```

# 3. HDFS简介

## 基本概念

- 块存储，默认大小是64MB
- NameNode：存放元数据，即文件与数据块的映射表，数据块与数据节点DataNode的映射表
- DataNode：存放数据块

![HDFS](http://upload-images.jianshu.io/upload_images/4097708-8f973ab78059fe1e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 数据管理策略

- 每一个数据块都会存3份，两份在一个机架上，一份在另一个机架上。所以任意一个服务器挂了都不要紧。

- 心跳检测，DataNode定期向NameNode放松心跳检测

- Secondary NameNode：是指NameNode的备份，会定期去和NameNode同步

## 文件读写流程

### 读文件

1. 客户端发送文件读取请求
2. NameNode查询元数据并返回，比如A，C，D三个block，每个block要从哪里去找
3. 客户端去从DataNode获得Block

### 写文件

1. 文件拆分成Block
2. 通知NameNode，NameNode返回可用的DataNode
3. 客户端将Block写入相应DataNode
4. 流水线复制，因为每个Block要存3个DataNode
5. 更新NameNode
6. 重复345步骤直到写完每个Block

## HDFS特点

1. 数据冗余，硬件容错
2. 流式数据访问
3. 存储大文件，不适合存小文件
4. 适合一次写入多次读取

## HDFS使用

总体来说和Linux特别相似
```
$ hadoop fs -mkdir /xueshu
$ hadoop fs -ls /
$ hadoop fs -put ~/Downloads/FinalPre.pptx /xueshu
$ hadoop fs -cat /xueshu/FinalPre.pptx
$ hadoop fs -get /xueshu/FinalPre.pptx ~
$ hadoop dfsadmin -report
```

# 4. MapReduce简介

## 原理

- 分而治之，一个大任务分成多个小任务Map，并行执行后，合并结果Reduce

![MapReduce](http://upload-images.jianshu.io/upload_images/4097708-68fc020e55be24a8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

举个例子，有519999张扑克牌，是10000副扑克牌其中少了某一张，要把它找出来

1. 把牌平均分给4个人，split
2. 每个人去数各自手里每张牌出现了几次，map()
3. 交换数据，每个人去统计一个花色的每张牌出现了几次，reduce()，比如A去统计红桃的每张牌出现了几次，结果计为part 0
4. 筛选出结果

## 运行流程

### 基本概念

- Job: 刚才的找出缺失的扑克牌
- Task: 包括MapTask和ReduceTask任务

![MapReduce2](http://upload-images.jianshu.io/upload_images/4097708-c8f2a3227d9bb9ab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 步骤

1. 客户端把Job发给JobTracker: 完成(1)作业调度(2)分配任务，监控任务进度(3)监控TaskTracker状态
2. TaskTracker执行任务并汇报任务状态，下图为具体步骤

![MapReduce3](http://upload-images.jianshu.io/upload_images/4097708-54cb995a9dd861c0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 容错机制

1. 重复执行，一般重复4次
2. 推测执行，如果某个TaskTracker特别慢，会让其他TaskTracker去做，然后取快的一个

# 5. 实战演练 - WordCount and Sort

略去，见之后的文章
