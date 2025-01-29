import os


def methodCalls(projectName, Classes):
    calls = open("Data/" + projectName + '/methodCalls.txt', "r")

    listCalls = []
    list = []
    for line in calls:
        trouve = False
        i = 0

        target = ""
        while not trouve and i in range(len(Classes)):
            if line.strip():
                if line.lower().split()[0] == Classes[i]:
                    if len(list) > 1:
                        listCalls.append(list)
                        list = []
                    else:
                        if len(list) == 1:
                            list = []

                    source = Classes[i]
                    list.append(source)
                    trouve = True
            i = i + 1
        if not trouve:
            if line.__contains__("source"):
                lineSplit = line.lower().split()
                target = lineSplit[len(lineSplit) - 1]
                list.append(target)

    # print("list calls \n ", listCalls)

    return listCalls


def numberCalls(calls, c1, c2):
    nbr = 0
    for call in calls:
        if call[0] == c1:
            nbr = 0
            for i in range(len(call)):

                if i != 0 and call[i] == c2:
                    nbr = nbr + 1
    return nbr


def callExist(calls, list):
    list2 = []
    list2.append(list[1])
    list2.append(list[0])
    list2.append(list[2])
    for c in calls:
        if list2 == c:
            return True

    return False


def calls(project, classes):
    listCalls = []
    listCalls = methodCalls(project, classes)
    calls = []
    for i in range(len(classes)):
        j = i + 1
        for j in range(len(classes)):
            nbr = 0
            if i != j:
                nbr = numberCalls(listCalls, classes[i], classes[j]) + numberCalls(listCalls, classes[j], classes[i])
                if nbr != 0:
                    list = []
                    list.append(classes[i])
                    list.append(classes[j])
                    list.append(nbr)

                    if not callExist(calls, list):
                        calls.append(list)

    """for call in calls:
        print("All calls \n ", call)"""
    return calls


def regesterInFiles(listCalls, projectName):

    filename = "SMQ_" + projectName + ".txt"
    if os.path.exists("data/" + projectName + "/" + filename):
        return filename
    file2 = open("data/" + projectName + "/" + filename, "a")
    for list in listCalls:
        for i in range(len(list)):
            if i != 0:
                file2.write(list[0] + " ----> " + list[i] + "\n")
    file2.close()

    return filename
