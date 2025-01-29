def exist(pair, clusters):
    for cluster in clusters:
        for p in cluster:
            if p[0] == pair[0] and p[1] == pair[1]:
                return True
    return False


def getClusters(project):
    file = open("cluster", "r")
    Allclusters = []
    i = 0
    cluster = []
    for lines in file:

        if "cluster" in lines:
            if i != 0:
                Allclusters.append(cluster)
                cluster = []
            i = i + 1
        else:
            cluster.append(lines.split()[0])
    Allclusters.append(cluster)


    return Allclusters
