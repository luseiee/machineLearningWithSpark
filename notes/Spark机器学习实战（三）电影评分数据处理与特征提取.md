> 这部分主要讲了进行数据可视化之后如何进行必要的数据处理，原因是原始数据并非完整。随后，我们要从数据中提取出我们需要的特征。使用的数据集依然是[MovieLens 100k数据集](http://files.grouplens.org/datasets/movielens/ml-100k.zip)，平台为Python Spark。

> 文章中列出了关键代码，完整代码见我的[github repository](https://github.com/luseiee/machineLearningWithSpark)，这篇文章的代码在`chapter03/movielens_feature.py`

## 第1步：数据处理与转换

数据出现缺失或者异常时，常见的处理方法有：

- 过滤或删除非规整或有缺失的数据
- 填充非规整或有缺失的数据
- 对异常值作鲁棒处理
- 对可能的异常值进行转换

由于我们采用的数据集数据缺失问题几乎没有，因此这部分不用特别处理。

## 第2步：特征提取

特征主要包含以下三种：

- 数值特征：比如年龄，可以直接作为数据的一个维度
- 类别特征：多个类别中的一种，但是类别特征一般有多少个类就会有多少个维度
- 文本特征：如电影评论

### 数值特征

数值特征也需要进行转换，因为不是所有的数值特征都有意义。

比如年龄就是一个很好的数值特征，可以不加处理直接用，因为年龄的增加与减少与目标有直接关系。然而，如经纬度的位置特征，有时就不太好直接用，需要做一些处理，甚至可以转换为类别特征。

### 类别特征

k类的类别特征需要转换成一个k bits的向量

我们来对MovieLens数据集中的用户职业进行处理，转换为类别特征。

```python
all_occupations = occupation_data.distinct().collect()
all_occupations.sort()
occupation_dict = {}
for i, occu in enumerate(all_occupations):
    occupation_dict[occu] = i
user_tom_occupation = 'programmer'
tom_occupation_feature = np.zeros(len(all_occupations))
tom_occupation_feature[occupation_dict[user_tom_occupation]] = 1
print("Binary feature of tom's occupation (programmer) is:")
print(tom_occupation_feature)
```

结果为：

```
Binary feature of tom's occupation (programmer) is:
[ 0.  0.  0.  0.  0.  0.  0.  0.  0.  0.  0.  0.  0.  0.  1.  0.  0.  0.
  0.  0.  0.]
```

### 派生特征

派生特征是指从原始数据经过一些处理后得到的特征，如之前计算过的用户打分电影总数，电影年龄等等。

下面的例子，是把u.data中的时间戳特征转换为类别特征，表征这条评分是在一天中的什么时段给出的。

```python
rating_data = sc.textFile("%s/ml-100k/u.data" % PATH)
rating_fields = rating_data.map(lambda line: line.split('\t'))
timestamps = rating_fields.map(lambda fields: int(fields[3]))
hour_of_day = timestamps.map(lambda ts: datetime.fromtimestamp(ts).hour)
times_of_day_dict = {}
for hour in range(24):
    if hour in range(7, 12):
        times_of_day_dict[hour] = "morning"
    elif hour in range(12, 14):
        times_of_day_dict[hour] = "lunch"
    elif hour in range(14, 18):
        times_of_day_dict[hour] = "afternoon"
    elif hour in range(18, 23):
        times_of_day_dict[hour] = "evening"
    else:
        times_of_day_dict[hour] = "night"
time_of_day = hour_of_day.map(lambda hour: times_of_day_dict[hour])
print(hour_of_day.take(5))
print(time_of_day.take(5))
```

这段代码的运行结果为

```
[23, 3, 15, 13, 13]
['night', 'night', 'afternoon', 'lunch', 'lunch']
```

可以看到，时间戳先被转化为当天的小时点，随后转化为了时段，之后可以转化为类别特征

### 文本特征

理论上来说，文本特征也可以看作一个类别特征，然而文本很少出现重复，因此效果会很不理想。

下面用的是自然语言处理（NLP）常见的词袋法（bag-of-word），简而言之，词袋法就是把数据集中出现过的所有单词构成一个词典，比如说有K个单词。随后以一个K维向量表示一段文字，文字中出现过的单词记录为1，其余为0。由于大部分词不会出现，因此很适合用稀疏矩阵表示。

首先我们用正则表达式去除电影标题中括号内的年份信息，再把每个电影标题分解为单词的列表。

```python
def extract_title(raw):
    grps = re.search("\((\w+)\)", raw)
    if grps:
        return raw[:grps.start()].strip()
    else:
        return raw
movie_data = sc.textFile("%s/ml-100k/u.item" % PATH)
movie_fields = movie_data.map(lambda line: line.split('|'))
raw_titles = movie_fields.map(lambda fields: fields[1])
print
print("Remove year information in '()'")
for raw_title in raw_titles.take(5):
    print(extract_title(raw_title))
movie_titles = raw_titles.map(extract_title)
title_terms = movie_titles.map(lambda line: line.split(' '))
print
print("Split words.")
print(title_terms.take(5))
```

输出为：

```
Remove year information in '()'
Toy Story
GoldenEye
Four Rooms
Get Shorty
Copycat

Split words.
[[u'Toy', u'Story'], [u'GoldenEye'], [u'Four', u'Rooms'], [u'Get', u'Shorty'], [u'Copycat']]
```

再利用flatMap RDD操作把所有出现过的单词统计出来，构建成单词辞典，形式为(单词，编号)。

```python
all_terms = title_terms.flatMap(lambda x: x).distinct().collect()
all_terms_dict = {}
for i, term in enumerate(all_terms):
    all_terms_dict[term] = i
print
print("Total number of terms: %d" % len(all_terms_dict))
```

最后把标题映射成一个高维的稀疏矩阵，出现过的单词处为1。注意我们把词典all_terms_dict作为一个广播变量是因为这个变量会非常大，事先分发给每个计算节点会比较好。

```python
from scipy import sparse as sp
def create_vector(terms, term_dict):
    num_terms = len(term_dict)
    x = sp.csc_matrix((1, num_terms))
    for t in terms:
        if t in term_dict:
            idx = term_dict[t]
            x[0, idx] = 1
    return x
all_terms_bcast = sc.broadcast(all_terms_dict)
term_vectors = title_terms.map(lambda 
    terms: create_vector(terms, all_terms_bcast.value))
print
print("The first five terms of converted sparse matrix of title")
print(term_vectors.take(5))
```

输出为：

```
[<1x2645 sparse matrix of type '<type 'numpy.float64'>'
	with 2 stored elements in Compressed Sparse Column format>, 
..., <1x2645 sparse matrix of type '<type 'numpy.float64'>'
	with 1 stored elements in Compressed Sparse Column format>]
```

### 正则化特征

通常我们获得的特征需要进行一下正则化处理。正则化特征分为两种：

- 第一种为正则化某一个特征，比如对数据集中的年龄进行正则化，使它们的平均值为0，方差为1

- 第二种为正则化特征向量，就是对某一个sample的特征进行正则化，使得它的范数为 1（常见为二阶范数为1，二阶范数是指平方和开根号）

例子是第二种，正则化特征向量。第一种方式是用numpy的函数。

```python
np.random.seed(42)
x = np.random.randn(4)
norm_x = np.linalg.norm(x)
normalized_x = x / norm_x
print
print("x: %s" % x)
print("2-norm of x: %.4f" % norm_x)
print("normalized x: %s" % normalized_x)
```

输出为：

```
x: [ 0.49671415 -0.1382643   0.64768854  1.52302986]
2-norm of x: 1.7335
normalized x: [ 0.28654116 -0.07976099  0.37363426  0.87859535]
```

第二种方式是用MLlib正则化特征向量

```python
from pyspark.mllib.feature import Normalizer
normalizer = Normalizer()
vector = sc.parallelize([x])
normalized_x_mllib = normalizer.transform(vector).first().toArray()
print("MLlib normalized x: %s" % normalized_x)
```

结果自然是一样的，当然是选择使用MLlib自带函数更好了。

至此，这篇文章内容就结束了。
