import argparse
import os
from pathlib import Path

import psutil
import ClusteringDatabase
import ClusteringSourceCode
import ParseJava


def ShowCluster(clusters, path):
    print(" obtained clusters \n")

    i = 0
    for c in clusters:
        print("cluster: ", )
        for cc in c:
            if isinstance(cc, list):
                print(cc.__getitem__(0))

        i = i + 1
    result = "Data/" + path + "/microservices.txt"
    with open(result, "w") as file:
        for i, c in enumerate(clusters):
            file.write("cluster: " + str(i) + "\n")
            for cc in c:
                if isinstance(cc, list):
                    file.write(cc.__getitem__(0) + "\n")


def delete_services(project):
    import os

    # Directory to search
    directory = "Data/" + project

    # Search for files containing "service" in their names and delete them
    for filename in os.listdir(directory):
        if "service" in filename.lower():  # Case-insensitive search
            file_path = os.path.join(directory, filename)
            try:
                if os.path.isfile(file_path):  # Ensure it's a file, not a directory
                    os.remove(file_path)
            except Exception as e:
                print(f"Failed to delete {file_path}: {e}")


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
physical_cores = psutil.cpu_count(logical=False)
logical_cores = psutil.cpu_count(logical=True)
os.environ['LOKY_MAX_CPU_COUNT'] = str(physical_cores)
parser = argparse.ArgumentParser(description="Pass multiple inputs to the script.")
parser.add_argument("--dataPath", type=str, required=True)
parser.add_argument("--sourcePath", type=str, required=True)
parser.add_argument("--cluster", type=int, required=True)
args = parser.parse_args()
print(f"data path: {args.dataPath}")
print(f"source path: {args.sourcePath}")
print(f"number of clusters: {args.cluster}")

project = Path(args.sourcePath).name
delete_services(project)
classes = "Data/" + project + "/AllClasses.txt"
Data_clusters = ClusteringDatabase.main(args.dataPath, project,args.cluster)
ParseJava.parse_java_code(args.sourcePath)
Microservices = ClusteringSourceCode.main(classes, Data_clusters, project)
#ShowCluster(Microservices, project)
