import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import precision_recall_curve, classification_report, accuracy_score, log_loss
from lightgbm import LGBMClassifier
from sklearn.model_selection import train_test_split
import pickle

# 데이터 불러오기
data = pd.read_excel('C:/mean/LastCombinedData.xlsx')
print("LGBM 시작")

# 데이터 분할 (특징과 클래스 레이블 분리)
X = data.drop(columns=['error_name'])
y = data['error_name']

# 데이터 분할 (훈련 세트와 테스트 세트로)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# 최적의 하이퍼파라미터 설정
param = {
    'subsample': 0.8,  # 데이터의 80%만 사용해 학습
    'num_leaves': 5,  # 리프 노드 수를 더 줄이기
    'n_estimators': 200,  # 트리 수를 늘리기
    'min_child_samples': 50,  # 최소 샘플 수를 더 늘리기
    'learning_rate': 0.05,  # 학습률 줄이기
    'colsample_bytree': 0.5,  # 피처 비율 줄이기
    'feature_fraction': 0.6,  # 드롭아웃 방식으로 피처 일부 사용
    'bagging_fraction': 0.8,  # 드롭아웃 방식으로 데이터 일부 사용
    'bagging_freq': 5,  # 매 5번째 트리마다 배깅 적용
    'min_child_weight': 1.0,  # 리프 노드의 최소 가중치 증가
    'min_split_gain': 0.1,  # 분할을 위해 필요한 최소 손실 감소 설정
    'lambda_l1': 0.5,  # L1 정규화 추가
    'lambda_l2': 0.5,  # L2 정규화 추가
    'objective': 'multiclass',
    'num_class': len(y.unique()),
    'metric': 'multi_logloss'
}

# LightGBM 모델 생성 및 학습
lgbm = LGBMClassifier(**param)
model = lgbm.fit(X_train, y_train)

# 피클 파일로 모델 저장
with open('../classifiLGBM.pkl', 'wb') as f:
    pickle.dump(model, f)
print("피클 저장 완료")
