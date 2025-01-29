import numpy as np
import torch
from transformers import BertTokenizer, BertModel
import os
from sentence_transformers import SentenceTransformer

model = SentenceTransformer('distilbert-base-nli-mean-tokens')  # dimension = 768
model1 = SentenceTransformer('all-MiniLM-L6-v2')  # dimension = 384
model2 = SentenceTransformer('all-mpnet-base-v2')  # dimension = 768
model3 = SentenceTransformer('all-MiniLM-L12-v2')  # dimension = 384


def Bert(corpus):
    docs = []
    for c in corpus:
        if ',' in c or ':' in c:
            c = c.replace(',', '')
            c = c.replace(':', '')
            docs.append(c.replace('\n', ''))
        else:
            docs.append(c)

    embeddings = model1.encode(docs)
    embeddings2 = model3.encode(docs)
    embeddings3 = []
    for i in range(0, len(embeddings)):
        v = []
        v.append(embeddings[i])
        v.append(embeddings2[i])
        vect = np.mean(v, axis=0)
        embeddings3.append(vect)

    return embeddings3
