# $SPARK_HOME/bin/spark-submit movielens_analysis.py 
from pyspark import SparkContext
import matplotlib.pyplot as plt
import numpy as np

sc = SparkContext("local", "Movielens Analysis")
sc.setLogLevel("ERROR") 
PATH = "/Users/c/xueshu/bigdata/machineLearningWithSpark"

## 1. Do some statistics
user_data = sc.textFile("%s/ml-100k/u.user" % PATH)
user_fields = user_data.map(lambda line: line.split('|'))
num_users = user_fields.count()
num_genders = user_fields.map(lambda fields: fields[2]).distinct().count()
num_occupations = user_fields.map(lambda fields: fields[3]).distinct().count()
num_zipcodes = user_fields.map(lambda fields: fields[4]).distinct().count()
print("Users:%d, genders:%d, occupations:%d, ZIP codes:%d" 
    %(num_users, num_genders, num_occupations, num_zipcodes))

## 2. Draw histgrams of age
ages = user_fields.map(lambda fields: int(fields[1])).collect()
fig1 = plt.figure()
plt.hist(ages, bins = 20, edgecolor='black')
plt.title("Age histogram")
plt.xlabel("Age")
plt.ylabel("Number")

## 3. Draw job distribution
occupations = user_fields.map(lambda 
    fields: (fields[3], 1)).reduceByKey(lambda 
    x, y: x + y).sortBy(lambda x: x[1]).collect()
fig2 = plt.figure(figsize=(9, 5), dpi=100)
x_axis = [occu[0] for occu in occupations]
y_axis = [occu[1] for occu in occupations]
pos = np.arange(len(x_axis))
width = 1.0
ax = plt.axes()
ax.set_xticks(pos + 0.5)
ax.set_xticklabels(x_axis)
plt.bar(pos, y_axis, width, edgecolor='black')
plt.xticks(rotation=30)
plt.ylabel("Number")
plt.title("Job distribution")

## 4. Draw movie year distribution
movie_data = sc.textFile("%s/ml-100k/u.item" % PATH)
movie_fields = movie_data.map(lambda line: line.split('|'))
num_movies = movie_fields.count()
def get_year(fields):
    try:
        return int(fields[2][-4:])
    except:
        return 1900
movie_years = movie_fields.map(get_year)
movie_ages = movie_years.filter(lambda 
    year: year != 1900).map(lambda year: 1998 - year).countByValue()
y_axis = movie_ages.values()
x_axis = movie_ages.keys()
fig3 = plt.figure()
plt.bar(x_axis, y_axis, edgecolor='black')
plt.title("Movie age distribution")
plt.xlabel("Movie age")
plt.ylabel("Number")

## 5. Do statistics on the rating data
rating_data = sc.textFile("%s/ml-100k/u.data" % PATH)
rating_fields = rating_data.map(lambda line: line.split('\t'))
ratings = rating_fields.map(lambda fields: int(fields[2]))
num_ratings = ratings.count()
min_rating = ratings.reduce(lambda x, y: min(x, y))
max_rating = ratings.reduce(lambda x, y: max(x, y))
mean_rating = ratings.reduce(lambda x, y: x + y) / float(num_ratings)
median_rating = np.median(ratings.collect())
ratings_per_user = num_ratings / float(num_users)
ratings_per_movie = num_ratings / float(num_movies)
print("Min rating:%d, max rating:%d, average rating:%.2f, median rating:%d" 
    %(min_rating, max_rating, mean_rating, median_rating))
print("Average # of rating per user: %.1f" %(ratings_per_user))
print("Average # of rating per movie: %.1f" %(ratings_per_movie))

## 6. Draw movie rating distribution
ratings_count = ratings.countByValue()
x_axis = ratings_count.keys()
y_axis = ratings_count.values()
fig4 = plt.figure()
plt.bar(x_axis, y_axis, edgecolor='black')
plt.title("Movie ratings distribution")
plt.xlabel("Movie rate")
plt.ylabel("Number")

## 7. User rating number distribution
user_rating_number = rating_fields.map(lambda 
    fields: (int(fields[0]), 1)).reduceByKey(lambda 
    x, y: x + y).sortBy(lambda x: x[1], ascending=False)
x_axis = np.arange(num_users)
y_axis = user_rating_number.values().collect()
fig5 = plt.figure(figsize=(9, 5), dpi=100)
plt.bar(x_axis, y_axis)
plt.title("User rating numbers rank")
plt.xlabel("Number of movie rated")
plt.ylabel("User")

plt.show()