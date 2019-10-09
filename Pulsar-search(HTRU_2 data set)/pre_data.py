import pandas as pd
import numpy as np
np.set_printoptions(suppress=True)
# import warnings
# warnings.filterwarnings("ignore")
# import random

def load_csv():
    #dataPath = "/home/ts/项目测试/HTRU_2/HTRU_2.csv"
    dataPath = "C:\\project\\AnacondaProjects\\task1\\HTRU_2\\HTRU_2.csv"
    data =pd.read_csv(dataPath) #返回的是DataFrame变量
    # first_rows = data.head(1) #返回前n条数据,默认返回5条
    #lable = ["Profile_mean", "Profile_stdev", "Profile_skewness", "Profile_kurtosis" ,"DM_mean", "DM_stdev", "DM_skewness", "DM_kurtosis"]
    cols = data.columns #返回全部列名
    dimensison = data.shape #返回数据的格式，数组，（行数，列数）
    # data.values #返回底层的numpy数据
    row_data = np.array(data.as_matrix())#转化为矩阵
    return row_data
    
#不考虑训练集中正负例的比率
def load_data():
    row_data = load_csv()
    data_size = len(row_data)
    print("data size:", data_size)
    train_set = []
    test_set = []
    
    np.random.shuffle(row_data) #打散数据集
    
    train_size = (int)(0.7*data_size)
    train_set = row_data[0: train_size]
    test_set = row_data[train_size: data_size]
    test_size = len(test_set)
    
    print("train_set size:", len(train_set))
    print("test_set size:", len(test_set))
    #train_set_X, train_set_Y, test_set_X, test_set_Y
    return train_set.T[0:8].T, train_set.T[8:9].T.astype(int), test_set.T[0:8].T, test_set.T[8:9].T.astype(int)


#按比例决定训练集中正反例的数目
def load_data_by_percentage(n): 
    #固定大小的训练集和测试集
    train_set_size = 2294
    test_set_size = 1000
    
    p_test_size = 100
    n_test_size = 900

    row_data = load_csv()
    #取出样本中的正例和负例
    positive_set = []
    negative_set = []
    for row in row_data:
        if row[8] == 1:#第九列
            positive_set.append(row)
        else:
            negative_set.append(row)
            
    #训练集中正负样本比例为 1：n
    p_train_size = int(train_set_size * 1/(1+n))#向下取整
    #训练集大小固定
    n_train_size = train_set_size - p_train_size
    
    np.random.shuffle(positive_set) #打乱
    np.random.shuffle(negative_set) 
    #print(len(positive_set))    
    #print(len(negative_set))
    train_set = []
    test_set = []

    #往训练/测试集中添加 正例
    train_set = positive_set[0:p_train_size]
    test_set = positive_set[p_train_size:(p_train_size + p_test_size)]#从剩下的中取100个
    #print(len(train_set))    
    #print(len(test_set))
    #往训练/测试集中添加 负例
    train_set = np.append(train_set, negative_set[0:n_train_size], axis=0)    
    test_set = np.append(test_set, negative_set[n_train_size:(n_train_size + n_test_size)], axis=0)
    #print(train_set.shape)    
    #print(test_set.shape)
    
    print("train_set size:", len(train_set))
    print("including positive case:",p_train_size)
    print("including negative case :",n_train_size)
    
    return train_set.T[0:8].T, train_set.T[8:9].T.astype(int), test_set.T[0:8].T, test_set.T[8:9].T.astype(int)

def load_for_novelty_detection():
    row_data = load_csv()
    #取出样本中的正例和负例
    positive_set = []
    negative_set = []
    for row in row_data:
        if row[8] == 1:#第九列
            positive_set.append(row)
        else:
            negative_set.append(row)
        
    np.random.shuffle(positive_set) #打乱
    np.random.shuffle(negative_set)
    
    train_size = int(len(negative_set)* 0.7)#与任务一保持相同的训练集负例数目
    test_size = int(len(row_data)* 0.3)#与任务一保持相同的测试集大小
    p_test_size = int(test_size*0.1)
    n_test_size = int(test_size*0.9)
    
    train_set = negative_set[0:train_size]
    #测试集中加入负例
    test_set = negative_set[train_size: (train_size+n_test_size)]#从后面接着取
    #测试集中加入正例
    test_set = np.append(test_set, positive_set[0:p_test_size], axis=0)
    train_set = np.array(train_set)
    return train_set.T[0:8].T, train_set.T[8:9].T.astype(int), test_set.T[0:8].T, test_set.T[8:9].T.astype(int)