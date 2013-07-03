import string

f = open("all_phrases.txt", "r")
linecounter = 0
for line in f:
    linecounter = linecounter + 1
    if linecounter == 1:
        print "//" + line,
    elif linecounter == 2:
        print string.lower(line.split(":")[1][2:]),
    elif linecounter == 4:
        print line[43:],
    elif linecounter == 5:
        print line[43:],
    elif linecounter == 9:
        linecounter = 0
        print ""

#if line[0:43] == "Reference Date:                            ":
    #    print line[43:],
    #    exit()
