import string

f = open("dataset.txt")
linecounter = 0
phrases = {}
words = {}
for line in f:
    linecounter = linecounter + 1
    if (linecounter - 4) % 8 == 0:
        #when printing words vs lines, this "-1" has to change for some reason.
        line = line[:-1]
        if line in phrases:
            phrases[line] = phrases[line] + 1
        else:
            phrases[line] = 1
        for word in line.split():
            if word in words:
                words[word] = words[word] + 1
            else:
                words[word] = 1
for pair in sorted(words.items(), key=lambda item: item[1]):
#for pair in sorted(phrases.items(), key=lambda item: item[1]):
    print pair
