import joblib
import pandas as pd
from flask import Flask, request, jsonify
import pickle
import random

app = Flask(__name__)

# 피클로 저장된 SVM 모델 파일 불러오기
with open('../classifiSVM.pkl', 'rb') as f:
    svmModel = pickle.load(f)

# 피클로 저장된 LGBM 모델 파일 불러오기
with open('../classifiLGBM.pkl', 'rb') as f:
    lgbmModel = pickle.load(f)

# 냉매 문제에 대한 SVM 모델 학습
errorfile_path = pd.ExcelFile(r"C:/mean/2/NewError.xlsx")


@app.route('/rep_error_name', methods=['POST'])
def predict_error_type():
    # 클라이언트로부터 오류 번호 받기
    requestNumber = request.json.get('error_number')
    print("Received error number:", requestNumber)

    # 오류 번호가 1인 경우 랜덤으로 1부터 7 사이의 숫자 선택
    if requestNumber == 1:
        error_number = random.randint(1, 7)
    elif requestNumber in range(1, 8):
        error_number = requestNumber
    else:
        return jsonify({"error": "Invalid input"}), 400  # 잘못된 입력일 경우 400 에러 반환

    # 오류 번호에 따라 적절한 시트명 설정
    sheetName = {
        1: "eo50",  # 오일 과다
        2: "cf45",  # 응축기 고장
        3: "fwc40",  # 콘덴서 증발기량 감소
        4: "fwe40",  # 증발기 물 흐름 감소
        5: "nc5",  # 냉매의 비응축성 물질
        6: "ro40",  # 냉매과충전
        7: "rl40"  # 냉매부족
    }

    # 해당 시트명으로 데이터프레임 로드
    sheet = sheetName[error_number]
    print("Opening data file:", sheet)
    df = pd.read_excel(errorfile_path, sheet_name=sheet)

    # SVM 모델에 입력하여 예측
    X = df.drop(columns=['detect'])
    Xreshaped = X.values.reshape(1, -1)
    svmPrediction = svmModel.predict(Xreshaped)
    print("통과")
    # SVM 모델의 예측 결과에 따라 처리
    if svmPrediction[0] == 1:
        Y=df.drop(columns=['error_name'])
        Yreshaped = Y.values.reshape(1,-1)
        lgbmPrediction = lgbmModel.predict(Yreshaped)
        # LGBM 모델의 예측 결과를 predict 열에 저장하고 출력
        print("LGBM classfication Result:", lgbmPrediction[0])
        predictionResult = f"Life: Not available, Type: {lgbmPrediction[0]}"
    else:
        print("해당사항 없음")
        predictionResult = "Prediction Result: No relevant issue found"

    print("Opening data file for RF model:", sheet)

    # RUL 열 추가
    df['RUL'] = df.groupby('detect')['Time'].transform(max) - df['Time']

    # 새로운 테스트 데이터의 입력 변수 추출
    newInputdata = df.drop(columns=['RUL'])

    # 모델 로드
    rf_model = joblib.load('../random_forest_model.pkl')
    # 모델을 사용하여 테스트 데이터 예측
    newPredictions = rf_model.predict(newInputdata)

    # 예측 결과 출력
    for i, pred in enumerate(newPredictions):
        hours = int(pred // 3600)
        minutes = int((pred % 3600) // 60)
        seconds = int((pred % 3600) % 60)
        print("Error Name:", lgbmPrediction[0], "Predicted RUL:", f"{hours}시간 {minutes}분 {seconds}초")
        result = f"Life: {hours}H {minutes}M ,Type: {lgbmPrediction[0]}"

    # 예측 결과를 JSON 형태로 반환
    return jsonify({"prediction": result})


if __name__ == '__main__':
    app.run(debug=True, host="192.168.0.42", port=8080)
