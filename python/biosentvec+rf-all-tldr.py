import tensorflow_decision_forests as tfdf

import os, io
import numpy as np
import pandas as pd
import tensorflow as tf
import math
import json
import sent2vec
from nltk import word_tokenize
from nltk.corpus import stopwords
from string import punctuation
from scipy.spatial import distance
import tensorflow_datasets as tfds
import tensorflow as tf
import pandas as pd

model_path = "/home/ralf/models/BioSentVec/BioSentVec_PubMed_MIMICIII-bigram_d700.bin"
model = sent2vec.Sent2vecModel()
try:
    model.load_model(model_path)
except Exception as e:
    print(e)
print('model successfully loaded')

stop_words = set(stopwords.words('english'))
def preprocess_sentence(text):
    text = text.replace('/', ' / ')
    text = text.replace('.-', ' .- ')
    text = text.replace('.', ' . ')
    text = text.replace('\'', ' \' ')
    text = text.lower()

    tokens = [token for token in word_tokenize(text) if token not in punctuation and token not in stop_words]

    return ' '.join(tokens)

# Assuming your dataset is stored in a CSV file
dataset_path = "/home/ralf/IdeaProjects/LitBall-training/EXP-Title+TLDR/"
text_key = "originalTitle"
bsize = 30

farr1 = []
larr1 = []
with open(dataset_path + "ROTVRSV") as file:
    lines = file.readlines()
    for line in lines:
        d = json.loads(line)
        farr1.append(np.asarray(model.embed_sentence(preprocess_sentence(d[text_key]))[0]).astype('float32'))
        larr1.append(int(d["label"]))
train_ds = tf.data.Dataset.from_tensor_slices((np.array(farr1), np.array(larr1))).batch(bsize)
print("Finished embedding ROTVRSV")

farr2 = []
larr2 = []
with open(dataset_path + "DENV") as file:
    test_lines = file.readlines()
    for line in test_lines:
        d = json.loads(line)
        farr2.append(np.asarray(model.embed_sentence(preprocess_sentence(d[text_key]))[0]).astype('float32'))
        larr2.append(int(d["label"]))
test_ds = tf.data.Dataset.from_tensor_slices((np.array(farr2), np.array(larr2))).batch(bsize)
print("Finished embedding DENV")

#tr_ds = tfdf.keras.pd_dataframe_to_tf_dataset(trds, label="label")
#te_ds = tfdf.keras.pd_dataframe_to_tf_dataset(teds, label="label")

print("Made datasets")

def prepare_dataset4(s):
    m = json.loads(s)
#sp = preprocess_sentence(m[text_key])
#sentence_vector = model.embed_sentence(sentence)
    return m

test_cases = list(map(prepare_dataset4, test_lines))
print(len(test_lines))
print("Data loaded.")

# Specify the model.
model_1 = tfdf.keras.RandomForestModel(num_trees=300, verbose=2, num_threads=3, check_dataset=False)

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

