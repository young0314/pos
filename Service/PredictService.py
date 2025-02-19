import joblib
import pandas as pd
from flask import Flask, request, jsonify
import pickle
import random
import numpy as np

app = Flask(__name__)

# 고장진단 모델 파일 불러오기
with open('../classifiSVM.pkl', 'rb') as f:
    svmModel = pickle.load(f)

# 유형분류 모델 파일 불러오기
with open('../classifiLGBM.pkl', 'rb') as f:
    lgbmModel = pickle.load(f)

# 에러 데이터
errorfile_path = pd.ExcelFile(r"C:/mean/2/NewError.xlsx")

@app.route('/rep_error_name', methods=['POST'])
def predict_error_type():
    # 클라이언트로부터 오류 번호 받기
    requestNumber = request.json.get('error_number')
    print("Received error number:", requestNumber)

    # 1부터 8까지 유형의 데이터를 랜덤으로 돌림
    if requestNumber == 1:
        error_number = random.randint(1, 8)
    elif requestNumber in range(1, 9):
        error_number = requestNumber
    else:
        return jsonify({"error": "Invalid input"}), 400  # 잘못된 입력일 경우 400 에러 반환

    sheetName = {
        1: "eo50",  # 오일 과다
        2: "cf45",  # 응축기 고장
        3: "fwc40",  # 콘덴서 증발기량 감소
        4: "fwe40",  # 증발기 물 흐름 감소
        5: "nc5",  # 냉매의 비응축성 물질
        6: "ro40",  # 냉매과충전
        7: "rl40",  # 냉매부족
        8: "nomal" # 노말(정상 상태)
    }

    sheet = sheetName[error_number]
    print("Opening data file:", sheet)
    df = pd.read_excel(errorfile_path, sheet_name=sheet)

    # LGBM 모델에 입력
    Y = df.drop(columns=['error_name'])
    Yreshaped = Y.values.reshape(1, -1)
    lgbmPrediction = lgbmModel.predict(Yreshaped)

    # LGBM 모델의 예측 결과를 출력
    print("LGBM classification Result:", lgbmPrediction[0])
    
    # LGBM 모델의 예측 결과가 0이면 RUL 계산 건너뛰고 SVM으로 바로 이동-->정상일 경우 수명 계산이 필요없기 떄문
    if lgbmPrediction[0] == 0:
        X = df.drop(columns=['detect', 'RUL'], errors='ignore')
        Xreshaped = X.values.reshape(1, -1)
        svmPrediction = svmModel.predict(Xreshaped)
        if svmPrediction[0] == 1:
            result = svmPrediction[0]
        else:
            result = "Prediction Result: No relevant issue found"

        if isinstance(result, (np.int64, np.int32)):
                result = int(result)

        return jsonify({"State:": result})

    # RUL 열 추가
    df['RUL'] = df.groupby('detect')['Time'].transform(max) - df['Time']

    newInputdata = df.drop(columns=['RUL'])

    # 잔여수명 모델 로드
    rf_model = joblib.load('../random_forest_model.pkl')
    newPredictions = rf_model.predict(newInputdata)



    # SVM 모델에 입력하여 예측
    X = df.drop(columns=['detect','RUL'])
    Xreshaped = X.values.reshape(1, -1)
    svmPrediction = svmModel.predict(Xreshaped)
    print(1)

    # SVM 모델의 예측 결과에 따라 처리
    if svmPrediction[0] == 1:
        print("SVM Prediction: 고장")
    else:
        print("SVM Prediction: 정상")
        result = "Prediction Result: No relevant issue found"
        # 예측 결과 출력
    for i, pred in enumerate(newPredictions):
        hours = int(pred // 3600)
        minutes = int((pred % 3600) // 60)
        seconds = int((pred % 3600) % 60)
        print("Error Name:", lgbmPrediction[0], "Predicted RUL:", f"{hours}시간 {minutes}분 {seconds}초")
        result = f"Life: {hours}H{minutes}M ,Type: {lgbmPrediction[0]}, State : {svmPrediction[0]}"
    # 예측 결과를 JSON 형태로 반환
    return jsonify({"prediction": result})



if __name__ == '__main__':
    app.run(debug=True, host="-", port=-)
