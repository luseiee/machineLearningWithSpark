### $SPARK_HOME/bin/spark-submit movielens_feature.py 
### Feature extraction of movielens dataset.
from pyspark import SparkContext
import matplotlib.pyplot as plt
import numpy as np
from datetime import datetime
import re
from scipy import sparse as sp
from pyspark.mllib.feature import Normalizer

sc = SparkContext("local", "Movielens Analysis")
sc.setLogLevel("ERROR") 
PATH = "/Users/c/xueshu/bigdata/machineLearningWithSpark"

## 1. Occupation feature
user_data = sc.textFile("%s/ml-100k/u.user" % PATH)
user_fields = user_data.map(lambda line: line.split('|'))
occupation_data = user_fields.map(lambda user_fields: user_fields[3])
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

## 2. Time stamp => classification feature
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
print
print("Converting timestamps to features.")
print(hour_of_day.take(5))
print(time_of_day.take(5))

## 3. Extract text feature, using bag-of-word method.
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
all_terms = title_terms.flatMap(lambda x: x).distinct().collect()
all_terms_dict = {}
for i, term in enumerate(all_terms):
    all_terms_dict[term] = i
print
print("Total number of terms: %d" % len(all_terms_dict))
# create sparse vector for each title
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

## 4. Feature normalization
np.random.seed(42)
x = np.random.randn(4)
norm_x = np.linalg.norm(x)
normalized_x = x / norm_x
print
print("x: %s" % x)
print("2-norm of x: %.4f" % norm_x)
print("normalized x: %s" % normalized_x)
normalizer = Normalizer()
vector = sc.parallelize([x])
normalized_x_mllib = normalizer.transform(vector).first().toArray()
print("MLlib normalized x: %s" % normalized_x)

