import React, { useState, useEffect } from "react";
import { SafeAreaView, View, ScrollView, Text, StyleSheet, Image } from "react-native";
import NavigationPage from "./NavigationPage";
import AsyncStorage from '@react-native-async-storage/async-storage';
import Icon from "react-native-vector-icons/FontAwesome";

const Monitoring = () => {
  const [data, setData] = useState({
    lifespan: "",
    doorstatus: "",
    errorstatus: "",
    containNumber: "",
    temperature: "",
    humidity: "",
    chillerImage: "",
  });

  const [chillerImage, setChillerImage] = useState(""); // 유니티에서 받은 이미지 URL 저장
  const [containNumber, setContainNumber] = useState(null);

  useEffect(() => {
    const fetchContainNumber = async () => {
      try {
        const storedContainNumber = await AsyncStorage.getItem('selectedContainer');
        if (storedContainNumber) {
          setContainNumber(storedContainNumber);
        } else {
          console.error('컨테이너 번호를 찾을 수 없습니다.');
        }
      } catch (error) {
        console.error("AsyncStorage에서 컨테이너 번호를 가져오는 데 실패했습니다:", error);
      }
    };

    fetchContainNumber();
  }, []);

  useEffect(() => {
    if (containNumber) {
      fetch(`http://192.168.137.222:8080/container/monitoring/${containNumber}`, {
        method: "GET"
      })
          .then((response) => {
            console.log("응답 상태:", response.status);
            if (!response.ok) {
              throw new Error(`서버 오류: ${response.statusText}`);
            }
            return response.json();
          })
          .then((responseData) => {
            console.log("서버로부터 받은 데이터:", responseData);
            console.log("받은 이미지 URL:", responseData.chillerImage);
            setData({
              lifespan: responseData.lifespan,
              doorStatus: responseData.doorStatus,
              errorStatus: responseData.errorStatus,
              containNumber: responseData.containNumber,
              temperature: responseData.temperature,
              humidity: responseData.humidity,
              chillerImage: responseData.chillerImage,
            });

            if (responseData.chillerImage) {
              setChillerImage(responseData.chillerImage);
            } else {
              setChillerImage("");
            }
          })
          .catch((error) => {
            console.error("데이터 가져오기 실패:", error);
          });
    }
  }, [containNumber]);

  const errorMessages = {
    0: "정상",
    1: "오일 과다",
    2: "응축기 고장",
    3: "응축기 냉각수 감소",
    4: "증발기 냉각수 감소",
    5: "질소 유입",
    6: "냉매 과충전",
    7: "냉매 부족",
  };
  const doorMessages = {
    0: "닫힘",
    1: "열림",
  };



  return (
      <SafeAreaView style={styles.container}>
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <View style={styles.header}>
            <View style={styles.contents}>
              <Text style={styles.headerText}>Monitoring</Text>
              {chillerImage ? (
                  <Image
                      source={{ uri: chillerImage }}
                      style={styles.unityImage}
                      onError={(e) => console.log('이미지 로딩 오류', e.nativeEvent.error)}
                  />
              ) : (
                  <Text style={styles.loadingText}>이미지 없음</Text>
              )}
            </View>
          </View>
          <View style={styles.informationContainer}>
            <View style={styles.informationItem}>
              <Icon name="id-card-o" size={24} color="#000" style={styles.iconTitle} />
              <Text style={styles.description}>
                {data.containNumber}
              </Text>
            </View>
            <View style={styles.informationItem}>
              <Icon name="warning" size={24} color="#000" style={styles.iconTitle} />
              <Text style={styles.description}>
                {errorMessages[data.errorStatus]}
              </Text>
            </View>
            <View style={styles.informationItem}>
              <Icon name="heartbeat" size={24} color="#000" style={styles.iconTitle} />
              <Text style={styles.description}>{data.lifespan}</Text>
            </View>
            <View style={styles.informationItem}>
              <Icon name="thermometer-half" size={24} color="#000" style={styles.iconTitle} />
              <Text style={styles.description}>{data.temperature}°C</Text>
            </View>
            <View style={styles.informationItem}>
              <Icon name="tint" size={24} color="#000" style={styles.iconTitle} />
              <Text style={styles.description}>{data.humidity}%</Text>
            </View>
            <View style={styles.informationItem}>
              <Icon
                  name={data.doorStatus === 1 ? "unlock" : "lock"}
                  size={70}
                  color="#000"
                  style={styles.iconTitle}
              />
              <Text style={styles.description}>
                {doorMessages[data.doorStatus]}
              </Text>
            </View>
          </View>
        </ScrollView>
        <View style={styles.navigationContainer}>
          <NavigationPage />
        </View>
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },
  scrollContainer: {
    paddingBottom: 80,
  },
  header: {
    marginTop: 20,
    width: "100%",
    alignItems: "center",
    backgroundColor: "#FFFFFF"
  },
  contents: {
    borderRadius: 17,
    backgroundColor: "rgba(49, 58, 91, 1)",
    width: 360,
    padding: 15,
    alignItems: "flex-start",
    marginTop: 15,
  },
  headerText: {
    color: "rgba(255, 255, 255, 1)",
    fontSize: 17,
    fontWeight: "400",
  },

  unityImage: {
    width: 500,
    height: 202,
    marginTop: 10,
    resizeMode: "contain",
    alignSelf: 'center'
  },
  loadingText: {
    color: "#fff",
    fontSize: 14,
    marginTop: 10,
  },
  informationContainer: {
    marginTop: 15,
    width: "88%",
    alignSelf: "center",
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
  },
  informationItem: {
    width: "48%",
    backgroundColor: "#ddd",
    borderRadius: 10,
    padding: 10,
    marginBottom: 10,
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    fontSize: 13,
    fontWeight: "bold",
    textAlign: "center",
  },
  iconTitle: {
    fontSize: 27,
    fontWeight: "bold",
    textAlign: "center",
  },

  description: {
    fontSize: 15,
    color: "#333",
    fontWeight: "600",
    marginTop: 5,
  },

  navigationContainer: {
    position: "absolute",
    bottom: 0,
    width: "100%",

  },
});
export default Monitoring;