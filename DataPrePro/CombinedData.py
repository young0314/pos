import pandas as pd
from sklearn.impute import SimpleImputer

import pandas as pd
from sklearn.impute import SimpleImputer

# ExcessOil 파일들 처리
file1 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/ExcessOil/eo32.xls")
file1["error_name"] = "1"
file1["detect"] = 1

file2 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/ExcessOil/eo50.xls")
file2["error_name"] = "1"
file2["detect"] = 1

# 응축기고장 파일들 처리
file3 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/응축기고장/cf12.xls")
file3["error_name"] = "2"
file3["detect"] = 1

file5 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/응축기고장/cf45.xls")
file5["error_name"] = "2"
file5["detect"] = 1

# Reduced condenser water flow 파일들 처리
file6 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Non-condensables in refrigerant/Reduced condenser water flow/fwc20.xls")
file6["error_name"] = "3"
file6["detect"] = 1

file7 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Non-condensables in refrigerant/Reduced condenser water flow/fwc40.xls")
file7["error_name"] = "3"
file7["detect"] = 1

# Reduced evaporator water flow 파일들 처리
file8 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Reduced evaporator water flow/fwe40.xls")
file8["error_name"] = "4"
file8["detect"] = 1

file9 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Reduced evaporator water flow/fwe20.xls")
file9["error_name"] = "4"
file9["detect"] = 1

# Non-condensables in refrigerant 파일들 처리
file10 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Non-condensables in refrigerant/nc trace.xls")
file10["error_name"] = "5"
file10["detect"] = 1

file11 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Non-condensables in refrigerant/nc2.xls")
file11["error_name"] = "5"
file11["detect"] = 1

file12 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Non-condensables in refrigerant/nc5.xls")
file12["error_name"] = "5"
file12["detect"] = 1

# 냉매결함 파일들 처리
file13 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/냉매결함/rl20.xls")
file13["error_name"] = "7"
file13["detect"] = 1

file14 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/냉매결함/rl40.xls")
file14["error_name"] = "7"
file14["detect"] = 1

file15 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/냉매결함/ro20.xls")
file15["error_name"] = "6"
file15["detect"] = 1

file16 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/냉매결함/ro40.xls")
file16["error_name"] = "6"
file16["detect"] = 1

# 정상 데이터 처리
data2 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Benchmark Tests/normal.xls")
data3 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Benchmark Tests/normal1.xls")
data4 = pd.read_excel(r"C:/졸작컨테이너/ASHRAE-D-16919/Chiller FDD Data/Benchmark Tests/normal2.xls")

data2["error_name"] = "0"
data2["detect"] = 0

data3["error_name"] = "0"
data3["detect"] = 0

data4["error_name"] = "0"
data4["detect"] = 0

# 각 파일별로 RUL 계산
for data in [file1, file2, file3, file5, file6, file7, file8, file9, file10, file11, file12, file13, file14, file15, file16, data2, data3, data4]:
    # 해당 고장 유형의 고장 시점을 찾음
    failure_time = data['Time'].min()
    # RUL 계산하여 새로운 컬럼에 추가
    data['RUL'] = data['Time'] - failure_time
    # RUL이 음수인 경우 0으로 처리
    data['RUL'] = data['RUL'].apply(lambda x: max(0, x))

# 모든 데이터를 하나의 데이터프레임으로 합치기
combined_data = pd.concat([file1, file2, file3, file5, file6, file7, file8, file9, file10, file11, file12,
                           file13, file14, file15, file16, data2, data3, data4])

print(combined_data.info())
# 각 고장 유형별 RUL 계산



nan_values_count = combined_data.isna().sum().sum()
if nan_values_count == 0:
    print("전처리 전 null값 없음")
else:
    print("전처리 전 null값:", nan_values_count, "/NaN values remaining in the DataFrame.")

# NaN 값 처리
combined_data = combined_data.drop(columns=["VH", "VE"])
imputer = SimpleImputer(strategy='mean')
combined_data = pd.DataFrame(imputer.fit_transform(combined_data), columns=combined_data.columns)

#결측치 0으로 채우기
columns_to_fill = ['Heat Balance (kW)', 'Heat Balance']
for column in columns_to_fill:
    combined_data[column] = combined_data[column].fillna(0)

nan_values_count = combined_data.isna().sum().sum()
if nan_values_count == 0:
    print("전처리 후 null값 없음")
else:
    print("전처리 후 null값:", nan_values_count, "/NaN values remaining in the DataFrame.")
combined_data.drop_duplicates(inplace=True)

print(combined_data.info())
# 데이터프레임을 엑셀 파일로 저장
combined_data.to_excel("C:/mean/LastCombinedData_with_RUL.xlsx", index=False)
