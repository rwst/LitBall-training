import os, io
import numpy as np
import math
import json, sys
import sent2vec
from nltk import word_tokenize
from nltk.corpus import stopwords
from string import punctuation
from scipy.spatial import distance

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

dataset_path = "/home/ralf/IdeaProjects/LitBall-training/EXP-Title+TLDR/"
text_key = "originalTitle"

farr1 = []
larr1 = []
txt1 = []
nontopics = ['dengue', 'denv', 'Dengue', 'DENV', 'flavivirus', 'Flavivirus']
with open(dataset_path + "ROTVRSV") as file:
    lines = file.readlines()
    for line in lines:
        d = json.loads(line)
        txt = d[text_key]
        if any([nt in txt for nt in nontopics]):
            continue
        txt1.append(txt)
        farr1.append(model.embed_sentence(preprocess_sentence(d[text_key]))[0])
        larr1.append(int(d["label"]))
print("Finished embedding ROTVRSV")

farr2 = []
larr2 = []
txt2 = []
with open(dataset_path + "DENV") as file:
    test_lines = file.readlines()
    for line in test_lines:
        d = json.loads(line)
        txt2.append(d[text_key])
        farr2.append(model.embed_sentence(preprocess_sentence(d[text_key]))[0])
        larr2.append(int(d["label"]))
print("Finished embedding DENV")

p = []
N = len(farr2)
for i in range(N):
    dist = []
    vec = farr2[i]
    for j in range(len(farr1)):
        d = 1 - distance.cosine(vec, farr1[j])
        if (d == 1):
            continue
        dist.append((j, d))
    if len(dist) == 0:
        p.append(False)
        continue
    dist.sort(key = lambda D: D[1], reverse=True)
    print("----- {}".format(txt2[i]))
    NN = 5
    if i%100==99:
        NN=100
        print(i, file=sys.stderr)
    for ii in range(min(NN, len(dist))):
        print("{}: {}".format(dist[ii][1], txt1[dist[ii][0]]))
    print()
    p.append(larr1[dist[0][0]] == 1)

tp = 0
tn = 0
fp = 0
fn = 0
N = len(p)
for i in range(N):
    tru = larr2[i] == 1
    prd = p[i]
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
print("acc: {}, prec: {}, rec: {}, f1: {}".format((tp+tn)/N, precision, recall, f1_score))

