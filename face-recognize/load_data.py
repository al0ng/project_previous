import os, shutil
import random
import operator

train_set_path = "C:\\Users\\chen\\Desktop\\face recognize\\data\\train_data"
test_set_path = "C:\\Users\\chen\\Desktop\\face recognize\\data\\test_data"
train_set = os.listdir(train_set_path)
test_set = os.listdir(test_set_path)

def load_dataset(lables, rank):
    return_train_set = []
    return_test_set = []
    for train_item in train_set:
        sitem = str(train_item)
        str_name = sitem.replace(".jpg", "").split("_")[rank]
        count = 0
        for lable in lables:
            if operator.eq(lable, str_name):
#                 print([str_name[rank], count],[item, count])
                return_train_set.append([train_item, count])
            count += 1;
    for test_item in test_set:
        sitem = str(test_item)
        str_name = sitem.replace(".jpg", "").split("_")[rank]
        count = 0
        for lable in lables:
            if operator.eq(lable, str_name):
                return_test_set.append([test_item, count])
            count += 1;
    
    return return_train_set, return_test_set