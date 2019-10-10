import urllib
import sys
import os
import os.path

import jieba
import jieba.posseg as pseg
import jieba.analyse
import xlwt #写入Excel表的库

rootdir = "E:\Curriculum\DistributedCompute\stopdata"
for parent, dirnames, filenames in os.walk(rootdir):
    for filename in filenames:
        # txtFile = open(os.path.join(parent,filename), 'r', encoding="utf-8")
        print(os.path.join(parent,filename))

        wbk = xlwt.Workbook(encoding='ascii')
        # sheet = wbk.add_sheet("wordCount")  # Excel单元格名字
        word_lst = []
        key_list = []
        for line in open(os.path.join(parent,filename), encoding='utf-8'):  # 1.txt是需要分词统计的文档

            item = line.strip('\n\r').split('\t')  # 制表格切分
            # print item
            tags = pseg.cut(item[0])  # jieba分词
            for word,flag in tags:
                if flag=='ns':# or flag=='r' or flag=='a':
                    word_lst.append(word)
                else:
                    continue

        word_dict = {}
        with open("E:\Curriculum\DistributedCompute\jieba_txt\wordCount"+filename, 'w',encoding='utf-8') as wf2:  # 打开文件
            for item in word_lst:
                if item not in word_dict:  # 统计数量
                    word_dict[item] = 1
                else:
                    word_dict[item] += 1

            orderList = list(word_dict.values())
            orderList.sort(reverse=True)
            # print orderList
            for i in range(len(orderList)):
                for key in word_dict:
                    if word_dict[key] == orderList[i]:
                        wf2.write(key + ' ' + str(word_dict[key]) + '\n')  # 写入txt文档
                        key_list.append(key)
                        word_dict[key] = 0

        # for i in range(len(key_list)):
        #     sheet.write(i, 1, label=orderList[i])
        #     sheet.write(i, 0, label=key_list[i])
        # wbk.save('E:\Curriculum\DistributedCompute\jieba_result\jieba_xls\wordCount'+filename+'.xls',)  # 保存为 wordCount.xls文件

        #txtFile.close()