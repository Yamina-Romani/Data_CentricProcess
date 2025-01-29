import os
import string


def put_class(class_, f):
    with open(f, "a") as file:
        file.write(class_)


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def match_class_table(project):
    directory = "Data/" + project
    clasTable = directory + "/ClassTable.txt"
    with open(clasTable, "r") as file:
        for line in file:
            table = line.split(" ")[0]
            class_ = line.split(" ")[1]
            found = False
            for filename in os.listdir(directory):
                if found == False:
                    f = os.path.join(directory, filename)
                    if os.path.isfile(f) and "Service" in filename:
                        lines = open(f, 'r')
                        for l in lines:
                            if 'Tables' in l or "Service" in l:
                                punctuation = string.punctuation.replace("_", "")
                                translator = str.maketrans('', '', punctuation)
                                text = l.translate(translator)
                                text = text.split()
                                for word in text:
                                    if table.lower() == word.lower():
                                        put_class(class_, f)
                                        found = True
                                        break
                else:
                    break


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def label(directory):
    labledClasses = []
    match_class_table(directory)
    directory = "Data/" + directory
    for filename in os.listdir(directory):
        f = os.path.join(directory, filename)
        if os.path.isfile(f) and "Service" in filename:
            lines = open(f, 'r')
            classes = []
            pair = []
            for line in lines:
                if 'Tables' in line or "Service" in line:
                    for punctuation in string.punctuation:
                        line = line.replace(punctuation, " ")
                    split = line.split()
                    labl = split[1]

                if 'Tables' not in line and "Service" not in line:
                    classes.append(line.split()[0].lower())

            pair.append(labl)
            pair.append(classes)
            labledClasses.append(pair)
    """print("labled classes ")
    for l in labledClasses:
        print(l)"""
    return labledClasses


""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


def addLabledClasses(labledVectors, DatabaseClusters):
    for elemnt in labledVectors:
        for i, cluster in enumerate(DatabaseClusters):

            if int(elemnt[2]) == i:
                pair = [elemnt[0], elemnt[1]]
                cluster.append(pair)

    return DatabaseClusters
