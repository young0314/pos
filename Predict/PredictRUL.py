import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import MinMaxScaler
from sklearn.pipeline import make_pipeline
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
import matplotlib.pyplot as plt
import joblib

# 데이터 불러오기
data = pd.read_excel('C:/mean/LastCombinedData.xlsx')

# 고장 발생 시간을 기준으로 잔여수명 계산
data['RUL'] = data.groupby('detect')['Time'].transform(max) - data['Time']

# 필요한 변수 추출
inputDdata = data.drop(columns=['RUL'])  # RUL과 error_name 열 제외한 모든 열을 입력 변수로 사용
targetData = data['RUL']  # 타겟 변수: RUL

# 데이터를 학습용과 테스트용으로 분리
X_train, X_test, y_train, y_test = train_test_split(inputDdata, targetData, test_size=0.2, random_state=42)

# 모델 정의
model = make_pipeline(MinMaxScaler(), RandomForestRegressor())

# 모델 학습
model.fit(X_train, y_train)

# 테스트 데이터로 예측
predictions = model.predict(X_test)

# 일정 간격으로 마커 표시를 위한 인덱스 설정
interval = 500  # 간격 설정
indices = range(0, len(X_test), interval)

# 예측값과 실제값을 그래프로 나타내기
plt.figure(figsize=(10, 6))
plt.plot(X_test['Time'], y_test, label='Actual RUL', color='blue', linestyle='-', linewidth=2)
plt.scatter(X_test['Time'].iloc[indices], y_test.iloc[indices], color='blue', marker='o', s=100)  # 일정 간격으로 마커 표시
plt.plot(X_test['Time'], predictions, label='Predicted RUL', color='orange', linestyle='-', linewidth=3)
plt.title('Actual vs Predicted RUL over Time', fontsize=30)
plt.xlabel('Time', fontsize=30)
plt.ylabel('RUL', fontsize=30)
plt.legend(fontsize=30)
plt.xticks(fontsize=30)
plt.yticks(fontsize=30)
plt.show()

joblib.dump(model, '../random_forest_model.pkl')
