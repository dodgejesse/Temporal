#!/usr/local/bin/python

tens = ["twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety"]
ones = ["", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"]
counter = 1920
for i in tens:
    for j in ones:
        print "nineteen {0} {1} :- NP : {2}:r".format(i,j,counter)
        counter = counter + 1
