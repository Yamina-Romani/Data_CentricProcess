import numpy as np
from matplotlib import pyplot as plt
from sklearn.metrics.pairwise import cosine_similarity
import networkx as nx
import community
import igraph as ig
import leidenalg as la
import comparaison
from comparaison import main


def louvain(vectors, call_matrix, dataClasses, project, min_, max_):
    class_vectors = []
    class_names = []
    for v in vectors:
        class_names.append(v[0])
        class_vectors.append(v[1])

    # must link entities ans not link entities
    must_link_class = []
    not_link_class = []
    for classes in dataClasses:
        links = []

        if len(classes[1]) > 1:

            for elem in classes[1]:

                for i, v in enumerate(vectors):
                    if elem == v[0]:
                        # print("el =  ", elem, " v = ", v[0])
                        links.append(i)
            must_link_class.append(links)
        else:
            if len(classes[1]) == 1:
                for elem in classes[1]:
                    for i, v in enumerate(vectors):
                        if elem == v[0]:
                            not_link_class.append(i)

    # calculate cosine similarity
    cosine_sim_matrix = cosine_similarity(class_vectors)

    n_classes = len(class_vectors)
    calls = np.zeros((n_classes, n_classes))
    # method calls
    for c in call_matrix:
        for i in range(n_classes):

            for j in range(i + 1, n_classes):
                if vectors[i][0] == c[0] and vectors[j][0] == c[1]:
                    calls[i][j] = c[2] / len(call_matrix)
                    # print(" cosine sim = ", cosine_sim_matrix[i,j], "class_vectors[i] = ",vectors[i][0]," class_vectors[j] = ",vectors[j][0])
                    # similarity_matrix[i, j] = similarity_matrix[i, j] + 0

    Avg_call_matrix = calls

    # Combined similarity matrix
    combined_similarity_matrix = np.zeros((n_classes, n_classes))

    for i in range(n_classes):
        for j in range(n_classes):
            cosine_sim = cosine_sim_matrix[i][j]
            call_score = Avg_call_matrix[i][j]
            # print(" call score = ", call_score)
            if call_score:
                combined_similarity_matrix[i][j] = (cosine_sim + call_score)

            else:
                combined_similarity_matrix[i][j] = cosine_sim

    # create graph
    G = nx.Graph()
    G.add_nodes_from(range(n_classes))

    # Add edges with combined similarity as weight
    max_weight = np.max(combined_similarity_matrix)
    min_weight = np.min(combined_similarity_matrix)
    for i, l in enumerate(must_link_class):
        for j, ll in enumerate(must_link_class):
            if i != j:
                for elem in l:
                    for elem_ in ll:
                        combined_similarity_matrix[elem][elem_] = min_weight
    for i in range(n_classes):
        for j in range(i + 1, n_classes):
            weight = combined_similarity_matrix[i][j]
            done = False
            for links in must_link_class:
                if i in links and j in links:
                    weight = max_weight
                    G.add_edge(i, j, weight=weight)
                    done = True
                else:
                    if i in links and j in not_link_class:
                        weight = min_weight
                        G.add_edge(i, j, weight=weight)
                        done = True
                    else:
                        if j in links and i in not_link_class:
                            weight = min_weight
                            G.add_edge(i, j, weight=weight)
                            done = True
                        else:
                            if j in not_link_class and i in not_link_class:
                                weight = min_weight
                                G.add_edge(i, j, weight=weight)
                                done = True

            if not done:
                G.add_edge(i, j, weight=weight)
    # show graph
    # showGraph(G, class_names)
    # comparaison
    # comparaison.main(G)

    # Apply Louvain Algorithm for Community Detection
    np.random.seed(0)
    print("\n ")
    r = 1.0
    bestClusters = []
    bestClusters_ = []
    bestModularity = 0
    bestSMQ = 0
    Clusters = []
    while 1.0 >= r >= 0:
        partition = community.best_partition(G, resolution=r)

        # Get the community membership of each node
        n = len(partition)
        num_communities = len(set(partition.values()))
        if max_ >= num_communities >= min_:
            Clusters = []
            Clusters_ = []
            for i in range(num_communities):
                cluster = []
                cluster_ = []
                #print("community ", i)
                for class_index, community_id in partition.items():

                    if i == community_id:
                        pair = []
                        pair.append(vectors[class_index][0])
                        pair.append(vectors[class_index][1])
                        cluster.append(pair)
                        cluster_.append(vectors[class_index][0])
                        #print(f"{vectors[class_index][0]}")
                Clusters.append(cluster)
                Clusters_.append(cluster_)

            modularity = community.modularity(partition, G)
            #print(f"modularity : {modularity} , resolution : {r}\n")

            if bestModularity <= modularity:
                bestModularity = modularity
                bestClusters = Clusters

        r = r - 0.1
    print(f" The Modularity = {bestModularity} \n")
    print("===============================")
    for i, c in enumerate(bestClusters):
        print("cluster ", i)
        for elem in c:
            print(elem[0])
        print("===============================")
    return bestClusters


def showGraph(G, classes):
    # Obtain the node identifiers automatically
    node_ids = list(G.nodes)  # This will return a list of node identifiers: [1, 2, 3]

    # Convert the list to a dictionary, mapping each node to its label
    class_names = dict(zip(node_ids, classes))

    # select nodes
    selected_node = [3, 4, 5, 6, 7, 8, 10, 13]
    H = G.subgraph(selected_node)

    # List of labels (example labels for the selected nodes)
    j = 0
    class_names_list = []
    for i, elem in enumerate(classes):
        if j < len(selected_node) and i == selected_node[j]:
            class_names_list.append(elem)
            j = j + 1

    # Obtain the node identifiers of the selected nodes
    node_ids = list(H.nodes)
    class_names = dict(zip(node_ids, class_names_list))
    # Draw the graph
    # pos = nx.spring_layout(H)  # Positioning for nodes
    # pos = nx.circular_layout(H)
    pos = nx.shell_layout(H)

    # Draw nodes and edges
    nx.draw(H, pos, with_labels=False, node_color='lightblue', node_size=1000, font_size=15)
    # Draw custom node labels
    nx.draw_networkx_labels(H, pos, font_size=15)

    # Draw edge labels (weights)
    edge_labels = nx.get_edge_attributes(H, 'weight')  # Get edge weights
    # Format the edge labels to 2 decimal places
    formatted_edge_labels = {k: f"{v:.2f}" for k, v in edge_labels.items()}

    nx.draw_networkx_edge_labels(H, pos, edge_labels=formatted_edge_labels, font_size=12)

    # Show the plot
    plt.title("Weighted Graph")
    plt.show()
