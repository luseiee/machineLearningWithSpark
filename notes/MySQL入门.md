> 作为一个数据库小白，在面试过程中无数次被问到有没有数据库经验，十分尴尬。于是准备从MySQL开始入门一下，只求学会基本操作。学习的是慕课网的《与MySQL的零距离接触》，个人觉得足够日常使用了。

# 1. 初涉MySQL
 
## 安装MySQL

mac下使用homebrew
```
brew install mysql
```
不用配置什么文件，使用下面的命令就可以使用root身份进入mysql服务器
```
mysql.server start
mysql -u root -p
回车（因为没有密码）
```

## 数据库基本操作

查看现有数据库
```
SHOW DATABASES;
```
创建一个数据库
```
CREATE DATABASE d1;
```
打开一个数据库
```
USE d1;
```
修改数据库编码方式
```
ALTER DATABASE d1 CHARACTER SET = utf8;
```
删除数据库
```
DROP DATABASE d1;
```

# 2. 数据类型和操作数据表

## 数据类型

- 整形有5种：INT，SMALLINT等等
- 浮点型2种：FLOAT[(M,D)]，DOUBLE[(M,D)]，M是总位数，D是小数点后位数
- 日期时间型：YEAR，TIME，DATE，DATETIME，TIMESTAMP
- 字符型：CHAR(M)，VARCHAR(M), TEXT等，ENUM('value1', 'value2',...)，SET('value1', 'value2'...)

## 数据表操作

创建数据表
```
mysql> CREATE TABLE tb1(
    -> username VARCHAR(20),
    -> age TINYINT UNSIGNED,
    -> salary FLOAT(8,2) UNSIGNED);
```
查看创建数据表详细
```
SHOW CREATE TABLE tb1;
```
查看数据表
```
SHOW TABLES [FROM db_name];
```
查看当前数据库
```
SELECT DATABASE();
```
查看数据表结构
```
SHOW COLUMNS FROM tb1;
```
插入数据
```
INSERT tb1 VALUES('Tom',25,7863.25);
INSERT tb1(username, salary) VALUES('John', 1700.25);
```
查找数据
```
SELECT * FROM tb1;
```
空值与非空，即某个字段不能为空
```
mysql> CREATE TABLE tb2(
    -> username VARCHAR(20) NOT NULL,
    -> age TINYINT UNSIGNED NULL);
```
自动编号与主键

- 一个表只能有一个主键
- 主键必定是NOT NULL，而且不允许重复值出现
- 主键不一定要AUTO_INCREMENT，反之不对

```
mysql> CREATE TABLE tb3(
    -> id SMALLINT UNSIGNED AUTO_INCREMENT KEY,
    -> username VARCHAR(30) NOT NULL);
mysql> INSERT tb3(username) VALUES('Tom');
```
Unique Key, 保证唯一性，NULL也只能有一个
```
username VARCHAR(20) NOT NULL UNIQUE KEY,
```
Default
```
sex ENUM('1','2','3') DEFAULT '3'
```

# 3.  约束以及修改数据表

## 约束
- NOT NULL 非空约束
- PRIMARY KEY 主键约束
- UNIQUE KEY 唯一约束
- DEFAULT 默认约束
- FOREIGN KEY 外键约束

DEFAULT和NOT NULL必是列级约束，其他不一定

## 外键
- 数据库引擎必须为InnoDB
- 被参照的叫父表，参照的叫子表，上父下子

定义外键方法
```
mysql> CREATE TABLE provinces(
    -> id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    -> pname VARCHAR(20) NOT NULL);
mysql> CREATE TABLE users(
    -> id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    -> username VARCHAR(10) NOT NULL,
    -> pid TINYINT UNSIGNED,  %这里必须要和上面相同
    -> FOREIGN KEY(pid) REFERENCES provinces (id));
```
外键约束的参照操作
```
FOREIGN KEY(pid) REFERENCES provinces (id) ON DELETE CASCADE
```
将会导致父表中某条记录删除时，子表中相应记录也删除，此外还有 SET NULL，RESTRICT

## 修改数据表
添加列
```
ALTER TABLE user ADD age TINYINT UNSIGNED DEFAULT 10;
```
删除列
```
ALTER TABLE user DROP age;
```
添加约束
```
ALTER TABLE users2 ADD CONSTRAINT PRIMARY KEY(id);
ALTER TABLE users2 ADD CONSTRAINT UNIQUE KEY(username);
ALTER TABLE users2 ADD CONSTRAINT FOREIGN KEY (pid) REFERENCES province(id);
ALTER TABLE users2 ALTER age SET DEFAULT 15;
```
删除约束
```
ALTER TABLE users2 DROP PRIMARY KEY;
% 删除唯一约束前先要查看key name
SHOW INDEXES FROM users2;
ALTER TABLE users2 DROP KEY username;
% 删除外键约束前要先查看constriant的名字
SHOW CREATE TABLE users2;
ALTER TABLE users2 DROP FOREIGN KEY users2_ibfk_1;
% 外键虽然删除，但是key还在，需要进一步删除key，方法同删除唯一约束
% 删除默认约束
ALTER TABLE users2 ALTER age DROP DEFAULT;
```
修改列的定义
```
% AFTER column name意思是放在某个列后面，FIRST意思是放在第一个
ALTER TABLE users2 MODIFY id TINYINT AFTER username;
% 使用CHANGE可以改列名字以及定义
ALTER TABLE users2 CHANGE pid new_pid TINYINT UNSIGNED;
```
修改数据表的名字
```
ALTER TABLE users2 RENAME users3;
```

# 4. 操作数据表中的记录

## 插入记录

创建数据表
```
mysql> CREATE TABLE users(
    -> id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    -> username VARCHAR(20) NOT NULL,
    -> password VARCHAR(32) NOT NULL,
    -> age TINYINT UNSIGNED NOT NULL DEFAULT 10,
    -> sex BOOLEAN);
```
插入记录的第一种方法
```
% 不指定对哪些COLUMN赋值的话，AUTO_INCREMENT的可以用NULL或者DEFAULT来代替
INSERT users VALUES(NULL,'Tom','123',24,1);
% 赋值可以使用表达式
INSERT users VALUES(DEFAULT,'John','123',3*7-1,1);
% 赋值还可以是函数，如md5
INSERT users VALUES(DEFAULT,'Phil',md5('123'),DEFAULT,0);
% 插入多条记录用逗号隔开即可
```
使用SET做插入可以使用子查询
```
INSERT users SET username='Bob',password='1223';
```
第三种是INSERT ... SELECT

## 更改记录
```
% 这样会更新所有记录
UPDATE users SET age = age + 2, sex = 0;
% 增加一些条件
UPDATE users SET age = age + 10 WHERE id % 2 = 0;
```

## 删除记录
```
DELETE [users] FROM users WHERE id = 3;
% 注意这个时候再插入一条记录id号会变成原有的最大的+1
```

## 查找记录
```
% 查找某些函数，表达式
SELECT NOW();
SELECT 3 + 5;
% 查询某几列，可以自定义顺序
SELECT username, id FROM users;
% 字段可以更改别名
SELECT id AS userID, username AS uname FROM users;
% 使用WHERE来指定条件
SELECT * FROM users WHERE id = 2;
% 使用GROUP BY进行结果分组，这里不太懂
SELECT sex FROM users GROUP BY sex;
% HAVING语句设置分组条件，这里也会报错，不懂
SELECT sex,age FROM users GROUP BY sex HAVING age > 10;
% ORDER BY对查询结果进行排序
SELECT * FROM users ORDER BY id DESC;
SELECT * FROM users ORDER BY password,username;
% LIMIT限制查询返回结果的数量，例子中返回第4，5两个结果
SELECT * FROM users LIMIT 3,2;
```
## 插入查询的结果
```
mysql> CREATE TABLE test(
    -> id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    -> username VARCHAR(20) NOT NULL);
mysql> INSERT test(username) SELECT username FROM users WHERE age > 20;
```

# 5. 子查询与连接

## 子查询

子查询指嵌套在查询内部，且必须出现在圆括号内

创建数据库，这是一个包含了一些淘宝商品信息的TABLE: tdb_goods
```
  CREATE TABLE IF NOT EXISTS tdb_goods(
    goods_id    SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    goods_name  VARCHAR(150) NOT NULL,
    goods_cate  VARCHAR(40)  NOT NULL,
    brand_name  VARCHAR(40)  NOT NULL,
    goods_price DECIMAL(15,3) UNSIGNED NOT NULL DEFAULT 0,
    is_show     BOOLEAN NOT NULL DEFAULT 1,
    is_saleoff  BOOLEAN NOT NULL DEFAULT 0
  );
```

使用比较运算符的子查询

```
查询大于平均价格的商品
SELECT * FROM tdb_goods WHERE goods_price > (SELECT ROUND(AVG(goods_price),2) FROM tdb_goods);
```
用ANY, SOME, ALL修饰比较运算符(ANY = SOME)
```
... WHERE goods_price > ANY  (SELECT .....);
```

## 使用INSERT...SELECT插入记录

创建一个tdb_goods_cates新表，用来记录tdb_goods中的所有商品种类
```
INSERT tdb_goods_cates (cate_name) (SELECT goods_cate FROM tdb_goods GROUP BY goods_cate);
```

## 使用INNER JOIN进行多表更新

更新tdb_goods表使得goods_cate中存的是tab_goods_cates表中种类的id

```
UPDATE tdb_goods INNER JOIN tdb_goods_cates 
-> ON goods_cate = cate_name SET goods_cate = cate_id;
```

## 把创建和INSERT合成一步

这里要做的是品牌的连接
```
mysql> CREATE TABLE tdb_goods_brands(
    -> brand_id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    -> brand_name VARCHAR(40) NOT NULL)
    -> SELECT brand_name FROM tdb_goods GROUP BY brand_name;
```

多表更新的时候如果两张表有同名COLUMN则最好使用别名
```
mysql> UPDATE tdb_goods AS g INNER JOIN tdb_goods_brands AS b ON
    -> g.brand_name = b.brand_name SET g.brand_name = b.brand_id;
```

最后把brand和category的定义给改成INT实现数据表减肥
```
mysql> ALTER TABLE tdb_goods
    -> CHANGE goods_cate cate_id SMALL_INT UNSIGNED NOT NULL,
    -> CHANGE brand_name brand_id SMALL_INT UNSIGNED NOT NULL;
```

那我们要不要使用外键？
不一定要使用物理外键，这样子的三张表实际上有外键的关系。

## 连接

- 内连接，左外连接，右外连接
- 使用ON关键字来设定连接条件，使用WHERE也可以

举个例子，列出所有的货物以及它的名字和种类（注意种类名字是在tdb_goods_cates表中的，tdb_goods里只有种类id）
```
SELECT goods_id, goods_name, cate_name FROM tdb_goods 
INNER JOIN tdb_goods_cates ON tdb_goods.cate_id = tdb_goods_cates.cate_id;
```

左外连接就是显示左表中的全部和右表中符合条件的 LEFT JOIN，对于左表中出现但是不符合右表条件的，会在相应字段显示NULL
```
SELECT goods_id, goods_name, cate_name FROM tdb_goods 
LEFT JOIN tdb_goods_cates ON tdb_goods.cate_id = tdb_goods_cates.cate_id;
% 会查询出某一条记录，这条记录在的cate_name找不到
24 |  LaserJet Pro P1606dn 黑白激光打印机 | NULL 
```

## 多表连接

其实就是多个INNER JOIN，返回了最初减肥前的结果
```
SELECT goods_id, goods_name, cate_name, brand_name, goods_price FROM tdb_goods AS g 
INNER JOIN tdb_goods_cates AS c ON g.cate_id = c.cate_id 
INNER JOIN tdb_goods_brands AS b ON g.brand_id = b.brand_id;
```

## 无限级分类表设计

比如分类有服装，服装底下又有男装女装，男装下面又有鞋子之类的。

用到一张表模拟多表连接
```
+---------+-----------------+-----------+
| type_id | type_name       | parent_id |
+---------+-----------------+-----------+
|       1 | 家用电器        |         0 |
|       2 | 电脑、办公      |         0 |
|       3 | 大家电          |         1 |
|       4 | 生活电器        |         1 |
|       5 | 平板电视        |         3 |
...
+---------+-----------------+-----------+
```

列出所有类的父类（暂时无法递归查询）
``` 
SELECT s.type_id, s.type_name, p.type_name FROM tdb_goods_types AS p
INNER JOIN tdb_goods_types AS s ON s.parent_id = p.type_id;
```

列出所有类的子类数目
```
SELECT p.type_id,p.type_name,count(s.type_name) AS children_count 
FROM tdb_goods_types AS p LEFT JOIN tdb_goods_types AS s 
ON s.parent_id = p.type_id GROUP BY p.type_id ORDER BY p.type_id;

+---------+-----------------+----------------+
| type_id | type_name       | children_count |
+---------+-----------------+----------------+
|       1 | 家用电器        |              2 |
|       2 | 电脑、办公      |              2 |
|       3 | 大家电          |              2 |
...
```

## 多表删除

首先找出出现了多次的商品，之前碰到的GROUP BY的问题了解了，MySQL高版本中要保证使用GROUP BY之后不能取出多个记录，有多个的话一定要用聚类函数类似MAX，COUNT这种
```
SELECT COUNT(goods_id), goods_name AS num 
FROM tdb_goods GROUP BY goods_name HAVING COUNT(goods_id) > 1;
```

删除重复商品中编号大的那个，由于GROUP BY的问题，这个会报错
```
DELETE t1 FROM tdb_goods AS t1 
LEFT JOIN (SELECT goods_id,goods_name FROM tdb_goods GROUP BY goods_name HAVING count(goods_name) >= 2 ) 
AS t2  ON t1.goods_name = t2.goods_name  WHERE t1.goods_id > t2.goods_id;
```

# 6. 运算符和函数

## 字符函数
```
% 合并两个数据
CONCAT('imooc', 'MySQL');
% 合并两个字段
SELECT CONCAT(first_name, last_name) AS fullname FROM name;
% 带分隔符连接，imooc-MySQL
SELECT CONCAT_WS('-', 'imooc', 'MySQL');
% 得到12,560.8
SELECT FORMAT(12560.75, 1)
% 大小写转换
SELECT LOWER('mysql');
% 取得左侧或右侧几个字符，得到My
SELECT LEFT('MySQL', 2);
% LENGTH取得字符串长度
% LTRIM 删除前导空格，RTRIM删除后导空格，TRIM删除前后空格
SELECT TRIM('  MySQL  ');
% 删除前导后导的问号，LEADING，TRAILING
SELECT TRIM(LEADING '?' FROM '??MySQL');
% 字符串替换
SELECT REPLACE('??My??SQL??', '?', '');
% 取子串，得到ySQ，起始位置，长度
SELECT SUBSTRING('MySQL', 2, 3);
% 模式匹配，%任意个任意字符，_任意一个字符，返回1或者0
SELECT 'MySQL' LIKE '%S%';
```

## 数值运算符及函数

```
SELECT CEIL(3.01); % 4
SELECT FLOOR(3.99); % 3
SELECT 3 DIV 4; % 0
SELECT 3 / 4; % 0.75
SELECT POWER(3, 2); % 9
SELECT ROUND(3.652, 2); % 3.65
SELECT TRUNCATE(125.89, 1); % 125.8
SELECT 1 IS NULL;
```

## 日期时间函数

NOW()等等，还可以实现日期时间的加减等等
```
SELECT DATE_FORMAT(NOW(), '%Y年%m月%d日%h时');
```

## 信息函数

```
DATABASE(); % 当前数据库
USER(); % 当前用户
ROW_COUNT(); % 上一条操作影响的记录数
等等
```

## 聚合函数

```
AVG(), COUNT(), MAX(), MIN(), SUM()
```

## 加密函数

```
MD5(); % 尽量使用MD5();
PASSWORD();
```

# 7. 自定义函数

```
mysql> CREATE FUNCTION f(num1 SMALLINT, num2 SMALLINT)
    -> RETURNS FLOAT(10,2) 
    -> RETURN (num1 + num2) / 2;
% 如果函数有多句一定要用BEGIN AND，同时要暂时修改结束符
DELIMITER //
函数定义...
DELIMITER ;
```

# 8. MySQL存储过程

- 存储过程就是预先编译的SQL语句，可以使得在使用的时候大大加快速度。因为平时每句都是做语法分析，编译执行。
- IN代表输入，OUT代表输出，INOUT代表输入再输出
- INTO是存入某个变量
- @代表用户变量，可以用SET @a = 3来设置
- 可以返回多个值，函数只能返回一个值

```
DELIMITER //
CREATE PROCEDURE removeUserByID(IN del_id INT UNSIGNED, OUT userNums INT UNSIGNED)
BEGIN
DELETE FROM users WHERE id = del_id;
SELECT count(id) FROM users INTO userNums;
END//

DELIMITER ;
CALL removeUserByID(3, @nums);
SELECT @nums;
```

# 9. MySQL存储引擎

## 简介

- 每一种存储引擎使用不同的存储机制，索引技巧，锁定水平，最终提供广泛而且不同的功能。
- MySQL支持：MyISAM, InnoDB, Memory, CSV

## 并发控制

- 当多个用户操作同一个记录时，使用锁技术
- 共享锁(读锁)：多个用户可以同时读取，读取过程中数据不变
- 排他锁(写锁): 只有一个用户可以写入，同时阻塞其他读写锁
- 锁颗粒
表锁，开销小；
行锁，开销大，并发度高

## 事务处理

- 事务用于保证数据库完整性
- 比如转账(A减少钱，B增加钱)
- 原子性，一致性，隔离型，持久性。简称ACID

## 索引

- 是对数据表中的一列或者多列的值进行排序的一种结构

## 各种存储引擎特点

![](http://upload-images.jianshu.io/upload_images/4097708-5b27e92cf39a2d8a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 设置存储引擎

- 改配置文件

- 创建表的时候制定
`ENGINE = MyISAM;`

# 10. 管理工具

- 可以实现备份等等操作
- PHPMyAdmin：需要PHP支持
- Navicat
- MySQL Workbench
