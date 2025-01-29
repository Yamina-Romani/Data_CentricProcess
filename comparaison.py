import time
import networkx as nx
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import pandas as pd
from networkx.algorithms.community import greedy_modularity_communities
from networkx.algorithms.community import modularity
from networkx.algorithms.community import louvain_communities
from networkx.algorithms.community import asyn_lpa_communities


# Generate a random graph
def create_random_graph():
    return nx.erdos_renyi_graph(n=50, p=0.1, seed=42)


# Compute modularity and runtime for Louvain algorithm
def run_louvain(graph):
    start = time.time()
    communities = louvain_communities(graph, seed=42)
    end = time.time()
    return modularity(graph, communities), end - start


# Compute modularity and runtime for Leiden algorithm (using approximation with LPA)
def run_leiden(graph):
    start = time.time()
    communities = list(asyn_lpa_communities(graph, weight=None, seed=42))
    end = time.time()
    return modularity(graph, communities), end - start


# Compute modularity and runtime for Girvan-Newman algorithm
def run_girvan_newman(graph):
    start = time.time()
    comp = nx.community.girvan_newman(graph)
    limited = tuple(sorted(c) for c in next(comp))
    end = time.time()
    return modularity(graph, limited), end - start


# Compute modularity and runtime for LPA (Label Propagation) algorithm
def run_lpa(graph):
    start = time.time()
    communities = list(asyn_lpa_communities(graph))
    end = time.time()
    return modularity(graph, communities), end - start

# Compute modularity and runtime for LPA (Label Propagation) algorithm
def run_lpa(graph):
    start = time.time()
    communities = list(asyn_lpa_communities(graph))
    end = time.time()
    return modularity(graph, communities), end - start


# Main code
def main(G):
    graph = G

    # Run algorithms
    louvain_modularity, louvain_time = run_louvain(graph)
    leiden_modularity, leiden_time = run_leiden(graph)
    girvan_modularity, girvan_time = run_girvan_newman(graph)
    lpa_modularity, lpa_time = run_lpa(graph)

    algorithms = ["Louvain", "Leiden", "Girvan-Newman", "LPA"]
    modularities = [louvain_modularity, leiden_modularity, girvan_modularity, lpa_modularity]
    runtimes = [louvain_time, leiden_time, girvan_time, lpa_time]

    # Set global font sizes
    plt.rcParams.update({
        'font.size': 24,
        'axes.titlesize': 20,
        'axes.labelsize': 20,
        'xtick.labelsize': 20,
        'ytick.labelsize': 20,
        'legend.fontsize': 20
    })

    #Plot 1: Separate Line Plots
    fig, axs = plt.subplots(2, 1, figsize=(10, 10), sharex=True)

    # Plot modularity
    axs[0].plot(algorithms, modularities, marker='o', label='Modularity', color='blue')
    axs[0].set_title('Modularity Comparison')
    axs[0].set_ylabel('Modularity')
    axs[0].grid(True, linestyle='--', alpha=0.6)

    # Plot runtime
    axs[1].plot(algorithms, runtimes, marker='o', label='Runtime (seconds)', color='orange')
    axs[1].set_title('Runtime Comparison')
    axs[1].set_ylabel('Runtime (seconds)')
    axs[1].set_xlabel('Clustering Algorithms')
    axs[1].grid(True, linestyle='--', alpha=0.6)

    plt.tight_layout()
    plt.show()

    # Plot 3: Heatmap
    # Data preparation
    data = pd.DataFrame({
        'Algorithm': algorithms,
        'Modularity': modularities,
        'Runtime': runtimes
    })

    # Heatmap
    plt.figure(figsize=(8, 6))
    sns.heatmap(data.set_index('Algorithm'), annot=True, cmap='Blues', fmt='.2f',annot_kws={"size": 14},  # Annotation font size
            cbar_kws={"label": "Intensity"})  # Colorbar label

    plt.title("Heatmap of Modularity and Runtime")
    plt.show()

    # Bar Plot
    bar_data = pd.DataFrame({
        'Algorithm': algorithms,
        'Modularity': modularities,
        'Runtime': runtimes
    })
    #tick_range = np.arange(0, 325, 5)
    # Plot
    bar_data.plot(x='Algorithm', kind='bar', figsize=(10, 6), color=['skyblue', 'salmon'])
    plt.title("Bar Plot: Modularity and Runtime Comparison", fontsize=14)
    plt.ylabel("Values", fontsize=12)
    plt.xlabel("Algorithm", fontsize=12)
    plt.grid(axis='y', linestyle='--', alpha=0.6)
    plt.legend(title="Metrics")
    #plt.yticks(tick_range)
    # Ensure x-axis labels are horizontal
    plt.xticks(rotation=0)
    plt.tight_layout()
    plt.show()


    """# Box Plot
    box_data = pd.DataFrame({
        'Algorithm': algorithms * 2,  # Duplicate for two metrics
        'Metric': ['Modularity'] * len(algorithms) + ['Runtime'] * len(algorithms),
        'Value': modularities + runtimes
    })

    sns.boxplot(x='Metric', y='Value', hue='Algorithm', data=box_data)
    plt.title('Box Plot: Modularity and Runtime')
    plt.show()"""