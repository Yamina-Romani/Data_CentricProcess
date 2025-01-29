import collections
import copy
import os
import string
from array import array
from ctypes import sizeof

from kneed import KneeLocator
from scipy.sparse import lil_matrix
from sklearn.preprocessing import normalize
import psutil
import methodCalls

import numpy as np
import pandas as pd
import gensim.models
import nltk
import matplotlib.pyplot as plt
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
from nltk.translate import metrics
from numpy import savetxt
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans, AgglomerativeClustering, AffinityPropagation, SpectralClustering, DBSCAN
from sklearn.decomposition import PCA
from sklearn.metrics import silhouette_score, davies_bouldin_score, euclidean_distances
import ClassLabel
from ClassLabel import label
from Bert import bert
from ClusteringDatabase import split_camel_case
from Louvain import louvain
from sklearn.metrics.pairwise import cosine_similarity


def exist(param, labledVectors):
    # print("param = ", param)
    for lab in labledVectors:
        # print("lab = ", lab[0])
        if np.array_equal(param, lab[0]):
            return True
    return False


nltk.download('stopwords')
nltk.download('wordnet')

# ...............................   pre processing data .....................................................................
#    stop words
stop_words = set(stopwords.words('english'))

# punctuation
punctuation = set(string.punctuation)

# lemmatization

lemmatization = WordNetLemmatizer()


# ************ function cleaan  ************
def clean(documents):
    # split documents and remove stop words
    # Split camel case
    documents = split_camel_case(documents)

    split_doc = " ".join([i for i in documents.lower().split() if i not in stop_words])

    # remove punctuation
    punc_doc = ''.join([j for j in split_doc if j not in punctuation])

    # normalize the text
    normalized = " ".join([lemmatization.lemmatize(word) for word in punc_doc.split()])

    return normalized


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def classesName(documents):
    className = []
    for doc in documents:
        i = 0
        split_doc = " ".join([i for i in doc.lower().split() if i not in stop_words])

        # remove punctuation
        punc_doc = ''.join([j for j in split_doc if j not in punctuation])

        for w in punc_doc.split():
            if i == 0:
                # print("word : ", w)
                className.append(w)
                i = i + 1
            break
    # print("calasses :: ", className)
    return className


def vectoriztion(cleaned_docs, data):
    # bert
    Embeddings = bert.Bert(data)
    # TF_IDF
    tfidf_vectorizer = TfidfVectorizer(lowercase=False)
    tf_idf = tfidf_vectorizer.fit_transform(cleaned_docs)
    tf_size = tf_idf.toarray().shape[1]

    #  Concatenate Vectors
    array1 = tf_idf.toarray()
    vectors = []
    k = 0
    for v in array1:
        vector = np.concatenate([Embeddings[k], v])
        vectors.append(vector)
        k = k + 1
    return vectors


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def getOptimalClusterNumber(vectors):
    c = len(vectors)
    if 15 > len(vectors) > 10:
        c = c - 3
    else:
        if 25 > len(vectors) > 15:
            c = c - 5
        else:
            if len(vectors) > 30:
                c = c - 10

            if len(vectors) > 50:
                c = 30
            if len(vectors) >= 100:
                c = 50

    if c % 2 != 0:
        c = c + 1

    normalized_matrix = normalize(vectors)

    # Calculate WCSS for a range of k values

    k_values = range(1, c)
    wcss = [
        KMeans(n_clusters=k, init="k-means++", max_iter=300, n_init=30, random_state=77).fit(normalized_matrix).inertia_
        for k in k_values]

    # Use KneeLocator to find the "elbow" point
    kneedle = KneeLocator(k_values, wcss, curve="convex", direction="decreasing")
    optimal_k = kneedle.elbow
    return optimal_k


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def lableVector(vectors, Class_name, project):
    labeledClass = ClassLabel.label(project)
    labeledVectors = []

    for i in range(len(labeledClass)):
        cluster = labeledClass[i][1]
        label = labeledClass[i][0]
        for classe in cluster:
            index = Class_name.index(classe)
            pair = []

            pair.append(classe)
            vector = vectors[index]
            pair.append(vector)
            pair.append(label)
            labeledVectors.append(pair)

    return labeledVectors, labeledClass


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def getAllVectors(vectors, Class_name, DatabaseClusters2):
    AllVectors = []
    AllVectors_ = []
    for i in range(len(vectors)):
        className = Class_name[i]
        pair = []
        pair.append(Class_name[i])
        pair.append(vectors[i])
        trouve = False

        for elem in DatabaseClusters2:
            for ele in elem:
                if isinstance(ele, list):
                    # print(" ", ele.__getitem__(0))
                    if ele.__getitem__(0) == pair[0]:
                        # print("elm ", elem[i][0], " == ", pair[0])
                        trouve = True
                        break

        if not trouve:
            AllVectors.append(pair)
        AllVectors_.append(pair)

    return AllVectors, AllVectors_


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def main(classes, data_clusters, project):
    corpus = open(classes, "r")
    data = []
    for c in corpus:
        data.append(c)

    Class_name = classesName(data)

    clean_documents = [clean(doc) for doc in data]
    vectors = vectoriztion(clean_documents, data)
    c = getOptimalClusterNumber(vectors)
    labeledVectors, labeledClasses = lableVector(vectors, Class_name, project)

    DatabaseClusters2 = ClassLabel.addLabledClasses(labeledVectors, data_clusters)

    AllVectors, AllVectors_ = getAllVectors(vectors, Class_name, DatabaseClusters2)
    MethodCalls = methodCalls.calls(project, Class_name)

    louvain_clusters = louvain(AllVectors_, MethodCalls, labeledClasses, project, len(data_clusters), c)

    return louvain_clusters
