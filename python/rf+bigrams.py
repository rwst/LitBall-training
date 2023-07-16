import os
import numpy as np
import math
import json

dataset_path = "/home/gtrwst9/"
text_key = "preprocessedText"

# Addition of bigrams
# 1. fill bigram set
all_bigrams = set()
bigramset = set()
bigramset1 = set()
worddict = {}
wordidx = 0
bigramdict = {}
bigramidx = 0
bgchar = 'xxx'

with open(dataset_path + "ROTVRSV") as file:
    lines = file.readlines()
    for line in lines:
        d = json.loads(line)
        words = d[text_key].split(' ')
        for w in words:
            if not (w in worddict.keys()):
                worddict[w] = wordidx
                wordidx = wordidx + 1
        for i in range(len(words)-1):
            s = words[i] + bgchar + words[i+1]
            if s in bigramset1:
                bigramset.add(s)
            elif s in all_bigrams:
                bigramset1.add(s)
            else:
                all_bigrams.add(s)
        for i in range(len(words)-1):
            s = words[i] + bgchar + words[i+1]
            if s in bigramset and not (s in bigramdict.keys()):
                bigramdict[s] = bigramidx
                bigramidx = bigramidx + 1
with open(dataset_path + "DENV") as file:
    lines = file.readlines()
    for line in lines:
        d = json.loads(line)
        words = d[text_key].split(' ')
        for w in words:
            if not (w in worddict.keys()):
                worddict[w] = wordidx
                wordidx = wordidx + 1
        for i in range(len(words)-1):
            s = words[i] + bgchar + words[i+1]
            if s in bigramset1:
                bigramset.add(s)
            elif s in all_bigrams:
                bigramset.add(s)
            else:
                all_bigrams.add(s)
        for i in range(len(words)-1):
            s = words[i] + bgchar + words[i+1]
            if s in bigramset and not (s in bigramdict.keys()):
                bigramdict[s] = bigramidx
                bigramidx = bigramidx + 1

print("wordidx: {}, bigramidx: {}".format(wordidx, bigramidx))
print("Number of all bigrams: {}".format(len(all_bigrams)))
print("Number of common bigrams: {}".format(len(bigramset)))

def common_bigrams(text):
    outstr = ''
    words = text.split(' ')
    for i in range(len(words)-1):
        s = words[i] + bgchar + words[i+1]
        if s in bigramset:
            outstr = outstr + s + ' '
    return outstr

# Load the dataset
import tensorflow_datasets as tfds
import tensorflow as tf
import pandas as pd
import tensorflow_decision_forests as tfdf

bsize = 100

farr1 = []
larr1 = []
with open(dataset_path + "ROTVRSV") as file:
    lines = file.readlines()
    for line in lines:
        d = json.loads(line)
        s = d[text_key]
        arr = [0] * (wordidx + bigramidx)
        for w in s.split(' '):
            i = worddict.get(w)
            if i is not None:
                arr[i] = 1
        for w in common_bigrams(s).split(' '):
            i = bigramdict.get(w)
            if i is not None:
                arr[i + wordidx] = 1
        farr1.append(arr)
        larr1.append(int(d["label"]))
train_ds = tf.data.Dataset.from_tensor_slices((np.asarray(farr1),
                                               np.asarray(larr1))).batch(bsize)
print("Finished processing ROTVRSV")

farr1 = []
larr1 = []
with open(dataset_path + "DENV") as file:
    test_lines = file.readlines()
    for line in test_lines:
        d = json.loads(line)
        s = d[text_key]
        arr = [0] * (wordidx + bigramidx)
        for w in s.split(' '):
            i = worddict.get(w)
            if i is not None:
                arr[i] = 1
        for w in common_bigrams(s).split(' '):
            i = bigramdict.get(w)
            if i is not None:
                arr[i + wordidx] = 1
        farr1.append(arr)
        larr1.append(int(d["label"]))
test_ds = tf.data.Dataset.from_tensor_slices((np.asarray(farr1),
                                               np.asarray(larr1))).batch(bsize)
print("Finished processing DENV")

print("Made datasets")


# Specify the model.
model_1 = tfdf.keras.RandomForestModel(num_trees=300, verbose=2, num_threads=15)

# Train the model.
model_1.fit(x=train_ds, batch_size=None)

model_1.compile(metrics=["accuracy"])
evaluation = model_1.evaluate(test_ds, batch_size=None)

print(f"BinaryCrossentropyloss: {evaluation[0]}")
print(f"Accuracy: {evaluation[1]}")

p = model_1.predict(test_ds)

#Fish for best cut
for cut in np.arange(0.25, .8, .05):
    tp = 0
    tn = 0
    fp = 0
    fn = 0
    N = len(p)
    for i in range(N):
        tru = test_cases[i]["label"] == '1'
        prd = p[i] > cut
        if tru and prd == tru:
            tp = tp + 1
        if tru and prd != tru:
            fn = fn + 1
        if tru == False and prd == tru:
            tn = tn + 1
        if tru == False and prd != tru:
            fp = fp + 1
    precision = tp/(tp+fp)
    recall = tp/(tp+fn)
    f1_score = 2 * (precision * recall) / (precision + recall)
    print("cut: {}, acc: {}, prec: {}, rec: {}, f1: {}".format(cut, (tp+tn)/N, precision, recall, f1_score))


