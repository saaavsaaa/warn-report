#!/usr/bin/env python
# -*- coding:utf-8 -*-

file = "/home/aaa/Downloads/sql-binlog/sqlmysql-bin.001527.txt"
file2 = "/home/aaa/Code/sql-binlog-result.txt"

# str = linecache.getline(file,1)
# print(str)
at_sta = 0
at_end = 0
time = 0
dictionary = {}
dictSqls = {}
s = 0
with open(file,"r",encoding='utf-8') as f,open(file2,"w+",encoding='utf-8') as f2:
    for line in f:
        if "BEGIN" in line:
            at_sta = 0
            at_end = 0
            time = 0

        if "COMMIT/" in line:
            con = at_end - at_sta
            # print("%s-%s=%s" %(at_end,at_sta,con))
            #f2.write("%s\n" %con)

        if at_sta == 0:
            if "# at " in line:
                at_sta = int(line.split()[2])
        else:
            if time == 0:
                time = line.split()[1]

                s = time[:-3]

                if dictionary.get(s):
                    dictionary[s] += 1
                else:
                    dictionary[s] = 1
                # print(time)
                #f2.write(time+"\t")
            else:
                if "UPDATE" in line or "INSERT" in line:
                    if dictSqls.get(s):
                        dictSqls[s] += 1
                    else:
                        dictSqls[s] = 1

            if "# at " in line:
                at_end = int(line.split()[2])

    sorted_key_list = sorted(dictSqls)
    for one in sorted_key_list:
        print("%s --- %s --- %s" %(one ,dictSqls[one], dictionary[one]))
    # sorted_key_list = sorted(dictionary)
    #
    # for one in sorted_key_list:
    #     if dictionary[one] > 40:
    #         print("%s --- %s" %(one ,dictionary[one]))

