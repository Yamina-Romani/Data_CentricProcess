import json
import re
import string
import nltk
import numpy as np
import seaborn as seaborn
from matplotlib import pyplot as plt
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from nltk.corpus import wordnet
from pyclustering.cluster.center_initializer import kmeans_plusplus_initializer
from pyclustering.cluster.xmeans import xmeans
from scipy.cluster.hierarchy import linkage, dendrogram
from sklearn import metrics
from sklearn.metrics import davies_bouldin_score, silhouette_score, pairwise_distances_argmin_min
from sklearn.preprocessing import normalize, StandardScaler
import os

from sklearn.cluster import KMeans, DBSCAN, AgglomerativeClustering
from sklearn.feature_extraction.text import TfidfVectorizer
from transformers.models import bert
from Bert import bert

nltk.download('stopwords')
nltk.download('wordnet')

# Initialize stop words and lemmatizer
stop_words = set(stopwords.words('english'))
lemmatizer = WordNetLemmatizer()


def get_synonyms_for_text(docs_num, text: str):
    words = text.split()  # Split the text into words
    unique_words = list(set(words))
    synonyms = []

    for word in unique_words:
        synonym = getSynonyms(word, docs_num)  # Get synonyms for each word
        if synonym and synonym is not None:
            synonyms.append(synonym)

    return synonyms


def remove_none(item):
    if isinstance(item, list):
        return [remove_none(sub_item) for sub_item in item
                if sub_item is not None and not (isinstance(sub_item, str) and len(sub_item) == 1)]
    elif isinstance(item, tuple):
        return tuple(remove_none(sub_item) for sub_item in item
                     if sub_item is not None and not (isinstance(sub_item, str) and len(sub_item) == 1))
    else:
        return item


def create_and_write_file(file_path: str, tables):
    cleaned_data = []
    punctuation_without_comma = string.punctuation.replace('', '')  # Remove comma from the list of punctuation
    translator = str.maketrans('', '', punctuation_without_comma)
    for sublist in tables:
        sublist = remove_none(sublist)
        text = str(sublist)
        cleaned_sublist = text.translate(translator)
        cleaned_data.append(cleaned_sublist)
    with open(file_path, 'w') as file:
        enriched_data = []
        for d in cleaned_data:
            synonyms = get_synonyms_for_text(len(cleaned_data), str(d))
            if synonyms:
                synonyms = str(synonyms)
                synonyms = synonyms.translate(translator)

                cleaned_sublist = synonyms.translate(translator)
                d = d + ' ' + cleaned_sublist
            file.write(f"{d}\n")
            enriched_data.append(d)
    return enriched_data


def expand_abbreviations(text: str) -> str:
    # Dictionary mapping abbreviations to full forms
    abbreviations = {
        'qty': 'quantity',
        'addr': 'address',
        'db': 'database',
        'usr': 'user',
        'pwd': 'password',
        'langpref': 'language preference',
        'favcategory': 'favorite category',
        'mylistopt': 'my list options',
        'banneropt': 'banner options',
        'bannerdata': 'banner data',
        'orderdate': 'orders date',
        'orderstatus': 'orders status',
        'shipaddr': 'shipping address',
        'shipcity': 'shipping city',
        'shipstate': 'shipping state',
        'shipzip': 'shipping zip',
        'shipcountry': 'shipping country',
        'billaddr': 'billing address',
        'billcity': 'billing city',
        'billstate': 'billing state',
        'billzip': 'billing zip',
        'billcountry': 'billing country',
        'totalprice': 'total price',
        'billtofirstname': 'billing first name',
        'billtolastname': 'billing last name',
        'shiptofirstname': 'shipping first name',
        'shiptolastname': 'shipping last name',
        'creditcard': 'credit card',
        'exprdate': 'expiration date',
        'linenum': 'line number',
        'attr': 'attribute',
        'productid': 'product id',
        'suppid': 'supplier id',
        'userid': 'user id',
        'orderid': 'order id',
        'privmsgs': 'private messages',
        'itemid': 'item id',
        'userrole': 'user role',
        'oauthaccessor': 'oauth accessor',
        'oauthconsumer': 'oauth consumer',
        'unitprice': 'unit price',
        'attach': 'attachment',
        'mediafiledir': 'media file directory',
        'mediafile': 'media file',
        'catid': 'category id',
        'mediafiletag': 'media file tag',
        'pingqueueentry': 'ping queue entry',
        'queueentry': 'queue entry',
        'descn': 'description',
        'autoping': 'auto ping',
        'pingtarget': 'ping target',
        'lastping': 'last ping',
        'nextping': 'next ping',
        'weblogentrytag': 'weblog entry tag',
        'weblogentrytagagg': 'weblog entry tag aggregation',
        'weblogentry': 'weblog entry',
        'weblogcategory': 'weblog category'
    }

    # Split the text into words and check each word
    words = text.split()

    # Replace abbreviations with full forms
    expanded_words = [abbreviations.get(word, word) for word in words]

    expanded_text = ' '.join(expanded_words)
    return expanded_text


def foreign_keys_tables(json_data, key):
    table = []
    for table_name, table_info in json_data.items():
        # Check if the primaryKey is the foreign_key
        foreign_key = table_info.get('foreignKeys', None)
        # Handle cases where primaryKey can be a list
        if isinstance(foreign_key, list):
            if key == 'catid' or (len(key) > 0 and key[0] == 'catid') or (len(key) > 1 and key[1] == 'catid'):
                print('primary_key ', key, ' fk ', foreign_key, ' table ', table_name)
            if key:
                if (key[0] in foreign_key) or (len(key) > 1 and key[1] in foreign_key) or (key in foreign_key):
                    # print('key ', key, ' fk ', foreign_key, ' table ', table_name)
                    table_name = preprocess_text(table_name)
                    table.append(table_name)

    return table


def singleton_foreign_key(json_data, key):
    counter = 0
    for table_name, table_info in json_data.items():
        # Check if the primaryKey is the foreign_key
        foreign_key = table_info.get('foreignKeys', None)
        # Handle cases where primaryKey can be a list
        if isinstance(foreign_key, list):
            if key in foreign_key:
                counter = counter + 1

    if counter == 1:
        return True
    else:
        if counter > 1:
            return False


def find_origin_table_for_foreign_key(json_data, foreign_key):
    for table_name, table_info in json_data.items():
        # Check if the primaryKey is the foreign_key
        primary_key = table_info.get('primaryKey', None)

        # Handle cases where primaryKey can be a list
        if isinstance(primary_key, list):
            if foreign_key in primary_key:
                table_name = preprocess_text(table_name)
                return table_name
        elif primary_key == foreign_key:
            table_name = preprocess_text(table_name)
            return table_name

    return None


# Function to split camel case words
def split_camel_case(text):
    return re.sub(r'([a-z])([A-Z])', r'\1 \2', text)


# Function to preprocess text (columns, table names, etc.)
def preprocess_text(text):
    if not isinstance(text, str):
        return text
    text = re.sub(r'(\w+)\d+', r'\1', text)
    # Split camel case
    text = split_camel_case(text)

    text = text.lower()

    text = text.translate(str.maketrans(string.punctuation, ' ' * len(string.punctuation)))
    text = expand_abbreviations(text)
    text = text.lower()

    # Tokenize and remove stop words
    words = text.split()
    words = [word for word in words if word not in stop_words]

    # Lemmatize words
    lemmatized_words = [lemmatizer.lemmatize(word) for word in words]

    # Join back the processed words
    return ' '.join(lemmatized_words)


# Function to read a JSON file and preprocess its content
def read_and_preprocess_json(file_path):
    with open(file_path, 'r') as file:
        data = json.load(file)

    # Preprocess table names and columns
    tables_name = []
    tables = []
    tables_list = []
    for table_name, table_info in data.items():
        if isinstance(table_info, dict):
            # Preprocess table name and column names
            tables_name.append(table_name)
            processed_table_name = preprocess_text(table_name)
            processed_columns = [preprocess_text(column) for column in table_info.get('columns', [])]

            # Handle primaryKey which can be string or list
            primary_key = table_info.get('primaryKey', '')
            if isinstance(primary_key, list):
                processed_primary_key = [preprocess_text(pk) for pk in primary_key]
            else:
                processed_primary_key = preprocess_text(primary_key)

            # Handle foreignKeys if present
            foreign_keys = table_info.get('foreignKeys', [])
            processed_foreign_keys = [preprocess_text(fk) for fk in foreign_keys]
            origins = []

            for fk in foreign_keys:
                if fk:
                    origin_table = find_origin_table_for_foreign_key(data, fk)
                    singleton_fk = singleton_foreign_key(data, fk)
                    fk = preprocess_text(fk)
                    if singleton_fk:
                        for i in range(3):
                            origins.append((origin_table, fk))
                    else:
                        for i in range(2):
                            origins.append((origin_table, fk))

            foreign_keys_table = []
            tables_ = foreign_keys_tables(data, primary_key)
            if len(tables_) == 1:
                for i in range(4):
                    foreign_keys_table.append(tables_)
            else:
                if len(tables_) > 1:
                    for i in range(3):
                        foreign_keys_table.append(tables_)

            if origins and foreign_keys_table:
                tables.append(
                    (processed_table_name, processed_table_name, processed_table_name, processed_columns, origins,
                     foreign_keys_table))
            else:
                if origins:
                    tables.append(
                        (processed_table_name, processed_table_name, processed_table_name, processed_columns, origins))
                else:
                    if foreign_keys_table:
                        tables.append((processed_table_name, processed_table_name, processed_table_name,
                                       processed_columns, foreign_keys_table))
                    else:
                        tables.append((processed_table_name, processed_table_name, processed_table_name,
                                       processed_columns))

            tables_list.append({
                'table': processed_table_name,
                'columns': processed_columns,
                'primaryKey': processed_primary_key,
                'foreignKeys': processed_foreign_keys
            })
    file = 'Data/' + file_path.split('/').__getitem__(1) + '/preprocessTables.txt'
    enriched_data = create_and_write_file(file, tables)
    return tables_name, enriched_data


# .........................getSynonyms using wordnet.................................................................................;

def getSynonyms(name, doc_number):
    syn = []

    for word in name.split():
        if word != "blc":
            for synset in wordnet.synsets(word):
                for lemma in synset.lemmas():
                    if doc_number < 10:
                        if (str(lemma.name()).lower() not in syn) and (str(lemma.name()).lower() != word) and (
                                str(lemma.name()).lower() != 'None') \
                                and (str(lemma.name()).lower() is not None) and (len(syn) < 10):
                            syn.append(str(lemma.name()).lower().replace('_', ' '))  # add the synonyms

                    else:
                        if (str(lemma.name()).lower() not in syn) and (str(lemma.name()).lower() != word) and (
                                str(lemma.name()).lower() != 'None') \
                                and (str(lemma.name()).lower() is not None) and (
                                len(syn) < 10):
                            syn.append(str(lemma.name()).lower().replace('_', ' '))  # add the synonyms

    # print('Synonyms: ' + str(syn))
    return syn


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def vectorization(enriched_data):
    tfidf_vectorizer = TfidfVectorizer(lowercase=False)
    tf_idf = tfidf_vectorizer.fit_transform(enriched_data)
    tf_size = tf_idf.toarray().shape[1]
    vectors = tf_idf.toarray()
    documents = []
    for doc in enriched_data:
        # print("doc = ",doc.split())
        documents.append(doc.split())

    embeddings = bert.Bert(documents)
    matrix = []
    documents = []
    j = 0
    k = 0
    for doc in enriched_data:

        j = j + 1
        if doc != '':
            v = []
            v.append(embeddings[k])
            vect = np.concatenate([embeddings[k], vectors[k]])
            matrix.append(vect)
            k = k + 1
    # print("---------------vectors------------------------\n", matrix)
    return matrix


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def agglomerative_clustering(vectors, table_names, path, clusters):
        normalized_matrix = normalize(vectors)
        names = []


        # Apply Agglomerative Clustering
        agg_clustering = AgglomerativeClustering(n_clusters=int(clusters), linkage='ward')
        labels = agg_clustering.fit_predict(normalized_matrix)
        # ............Obtained Clusters..........................................................................................
        obtainedCluster = []
        ObtainedClusters = []
        for c in range(clusters):
            print(" Cluster", c)
            print("__________________________________________")
            count = 0
            cluseterI = []
            cluseterII = []
            cluseterI.append(c)

            for i in labels:

                if i == c and count < len(table_names):
                    print("Table: ", table_names[count], " ---> Document ", count)
                    cluseterI.append(table_names[count])
                    pair = []
                    pair.append(table_names[count])
                    names.append(table_names[count])
                    pair.append(vectors[count])
                    cluseterII.append(pair)
                count = count + 1

            obtainedCluster.append(cluseterI)
            ObtainedClusters.append(cluseterII)
            print("__________________________________________")

        return obtainedCluster


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def write_cluser(obtainedCluster, path):
    cc = 0

    for oc in obtainedCluster:
        # print("\n cluseter", cc, " -->", oc)
        cluste_name = 'PotentialService' + str(cc)
        file = 'Data/' + path + '/' + cluste_name

        if not os.path.exists(file):
            with open(file, 'w') as f:
                # f = open(cluste_name, "a")
                f.write(cluste_name)
                f.write(" " + str(oc) + "\n")

        cc = cc + 1


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def main(project_data, project, data_clusters):
    table_names, enriched_data = read_and_preprocess_json(project_data)
    vectors = vectorization(enriched_data)

    clusters = agglomerative_clustering(vectors, table_names, project, int(data_clusters))

    write_cluser(clusters, project)

    return clusters
