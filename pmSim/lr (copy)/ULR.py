# coding:utf-8

# Created by Neil on 2019/1/7.

import pandas as pd
from pandas import DataFrame, Series
import numpy as np
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression

examDict = {'学习时间': [0.50, 0.75, 1.00, 1.25, 1.50, 1.75, 1.75,
                     2.00, 2.25, 2.50, 2.75, 3.00, 3.25, 3.50, 4.00, 4.25, 4.50, 4.75, 5.00, 5.50],
            '分数': [10, 22, 13, 43, 20, 22, 33, 50, 62,
                   48, 55, 75, 62, 73, 81, 76, 64, 82, 90, 93]}

examDf = DataFrame(examDict)
print(examDf)

plt.scatter(examDf.分数, examDf.学习时间, color='b', label="Exam Data")

plt.xlabel("Hours")
plt.ylabel("Score")
plt.show()

rDf = examDf.corr()
print(rDf)

# 将原数据集拆分训练集和测试集
X_train, X_test, Y_train, Y_test = train_test_split(examDf.分数, examDf.学习时间)
# X_train为训练数据标签,X_test为测试数据标签,exam_X为样本特征,exam_y为样本标签，train_size 训练数据占比

print("原始数据特征:", examDf.分数.shape,
      ",训练数据特征:", X_train.shape,
      ",测试数据特征:", X_test.shape)

print("原始数据标签:", examDf.学习时间.shape,
      ",训练数据标签:", Y_train.shape,
      ",测试数据标签:", Y_test.shape)

# 散点图
plt.scatter(X_train, Y_train, color="blue", label="train data")
plt.scatter(X_test, Y_test, color="red", label="test data")

# 添加图标标签
plt.legend(loc=2)
plt.xlabel("Hours")
plt.ylabel("Pass")
# 显示图像
plt.show()


model = LinearRegression()

#对于模型错误我们需要把我们的训练集进行reshape操作来达到函数所需要的要求
# model.fit(X_train,Y_train)

#reshape如果行数=-1的话可以使我们的数组所改的列数自动按照数组的大小形成新的数组
#因为model需要二维的数组来进行拟合但是这里只有一个特征所以需要reshape来转换为二维数组
X_train = X_train.values.reshape(-1,1)
X_test = X_test.values.reshape(-1,1)

model.fit(X_train,Y_train)


a  = model.intercept_#截距

b = model.coef_#回归系数

print("最佳拟合线:截距",a,",回归系数：",b)


#训练数据的预测值
y_train_pred = model.predict(X_train)
#绘制最佳拟合线：标签用的是训练数据的预测值y_train_pred
plt.plot(X_train, y_train_pred, color='black', linewidth=3, label="best line")

#测试数据散点图
plt.scatter(X_test, Y_test, color='red', label="test data")

#添加图标标签
plt.legend(loc=2)
plt.xlabel("Hours")
plt.ylabel("Score")
#显示图像
plt.show()


score = model.score(X_test,Y_test)

print("score:", score)
