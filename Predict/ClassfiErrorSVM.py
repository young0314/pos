import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score, confusion_matrix, roc_curve, auc
import matplotlib.pyplot as plt
import seaborn as sns
import warnings
import pickle
from flask import Flask, request, jsonify

app = Flask(__name__)
warnings.filterwarnings("ignore", category=UserWarning)
warnings.filterwarnings("ignore", category=RuntimeWarning)

# 데이터 파일 불러오기
combined_data = pd.read_excel(r"C:/mean/LastCombinedData.xlsx")  # 정상데이터 + 6가지 고장 데이터 합침
print(combined_data.info())

# 학습 데이터와 테스트 데이터 분리
X = combined_data.drop(columns=['detect'])
y = combined_data['detect']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

# 데이터 표준화
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# SVM 모델 학습
svmModel = SVC(kernel='rbf', C=1, gamma=0.1)
svmModel.fit(X_train_scaled, y_train)

# 피클 파일로 SVM 모델 저장

with open('../classifiSVM.pkl', 'wb') as f:
    pickle.dump(svmModel, f)
print("피클 저장 완료")


# 테스트 데이터에 대한 예측 수행
result_svm = svmModel.predict(X_test_scaled)
nan_values_count = combined_data.isna().sum().sum()
if nan_values_count == 0:
    print("No NaN values remaining in the DataFrame.")
else:
    print("There are still", nan_values_count, "NaN values remaining in the DataFrame.")

# 성능 평가
accuracy = accuracy_score(y_test, result_svm)
print("Accuracy:", accuracy)
"""
# 혼동행렬 계산
conf_matrix = confusion_matrix(y_test, result_svm)
print("Confusion Matrix:\n", conf_matrix)


# 혼동 행렬 시각화
plt.figure(figsize=(10, 7))
sns.heatmap(conf_matrix, annot=True, fmt='d', cmap='Blues', annot_kws={"size": 30})
plt.xlabel('Predicted Labels', fontsize=30)
plt.ylabel('True Labels', fontsize=30)
plt.title('Confusion Matrix', fontsize=30)
plt.xticks(fontsize=30)
plt.yticks(fontsize=30)
plt.show()
"""
