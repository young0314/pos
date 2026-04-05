import { SafeAreaView, View, ScrollView, Text, StyleSheet, TouchableOpacity, Alert, Modal } from "react-native";
import Icon from "react-native-vector-icons/FontAwesome";
import { useNavigation } from '@react-navigation/native';
import React, { useEffect, useState } from 'react';
import NavigationPage from './NavigationPage';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Swipeable } from 'react-native-gesture-handler';
import AdminIdPage from "./AdminIdPage";
import LinearGradient from "react-native-linear-gradient";

const List = () => {
  const [containers, setContainers] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedContainer, setSelectedContainer] = useState(null);
  const navigation = useNavigation();

  const listContainers = async () => {
    try {
      const storedContainers = await AsyncStorage.getItem('containers_${userId}');
      if (storedContainers) {
        setContainers(JSON.parse(storedContainers));
      } else {
        const response = await fetch('http://192.168.137.243:8080/container/list');
        if (!response.ok) {
          throw new Error('데이터 로딩 실패.');
        }
        const responseJson = await response.json();
        console.log('서버 응답 데이터:', responseJson);
        setContainers(responseJson);
        await AsyncStorage.setItem('containers', JSON.stringify(responseJson));
      }
    } catch (error) {
      console.error('서버 데이터 불러오기 실패:', error);
      Alert.alert("오류", "데이터 로딩 실패");
    }
  };

  useEffect(() => {
    listContainers(); // 컨테이너 목록 업로드
  }, []);

  const handleContainerPress = async (containNumber, event) => {
    event.persist();

    await AsyncStorage.setItem('selectedContainer', containNumber.toString());
    navigation.navigate('Monitoring', { containNumber });
    console.log('컨테이너 정보 전달 완료', {containNumber});
  };

  const fetchContainerInfo = async (containNumber) => {
    try {
      const response = await fetch(`http://192.168.137.243:8080/container/info/${containNumber}`);
      console.log('Response Status:', response.status);
      if (!response.ok) {
        throw new Error("컨테이너 정보를 가져올 수 없습니다.");
      }
      const responseJson = await response.json();
      console.log("서버 응답 데이터:", responseJson);

      if (responseJson.containNumber === containNumber) {
        setSelectedContainer(responseJson);
        setModalVisible(true);
      } else {
        Alert.alert("오류", responseJson.message || "컨테이너 정보를 가져올 수 없습니다.");
      }
    } catch (error) {
      console.error("컨테이너 정보 불러오기 실패:", error);
      Alert.alert("오류", "컨테이너 정보를 가져오는 데 실패했습니다.");
    }
  };

  const handleDelete = async (index, containNumber) => {
    try {
      await fetch(`http://192.168.137.243:8080/container/delete/${containNumber}`, { method: 'DELETE' });
      const newContainers = containers.filter((_, i) => i !== index);
      setContainers(newContainers);
      await AsyncStorage.setItem('containers', JSON.stringify(newContainers));
    } catch (error) {
      console.error('컨테이너 삭제 중 오류 발생:', error);
      Alert.alert("삭제 실패", "서버와 통신 중 오류가 발생했습니다.");
    }
  };

  return (
      <SafeAreaView style={styles.container}>
        <ScrollView
            style={styles.scrollView}
            contentContainerStyle={{ flexGrow: 1 }}
            keyboardShouldPersistTaps="handled"
        >
          <View style={{ flex: 1 }}>
            <View style={styles.adminIcon}>
              <AdminIdPage />
            </View>
            <View style={styles.row}>
              <ScrollView style={styles.box}></ScrollView>
            </View>
            <View style={styles.column}>
              <View style={styles.column2}>
                {containers.length === 0 ? (
                    <Text style={styles.text}>등록된 컨테이너가 없습니다.</Text>
                ) : (
                    containers.map((container, index) => (
                        <Swipeable
                            key={index}
                            renderRightActions={() => (
                                <TouchableOpacity
                                    style={styles.deleteButton}
                                    onPress={() => handleDelete(index, container.containNumber)}
                                >
                                  <Icon name="trash" size={24} color="#FFF" />
                                </TouchableOpacity>
                            )}
                        >
                          <View key={index} style={styles.row2}>
                            <View style={styles.infoBox}></View>
                            <TouchableOpacity
                                style={styles.infoButton}
                                onPress={(event) => fetchContainerInfo(container.containNumber)}
                            >
                              <Icon name="info" size={22} color="#000" />
                            </TouchableOpacity>
                            <Text style={styles.text}>{container.containNumber}</Text>
                            <View style={styles.box}></View>
                            <TouchableOpacity
                                style={styles.iconButton}
                                onPress={(event) => handleContainerPress(container.containNumber, event)}
                            >
                              <Icon name="angle-right" size={22} color="#000" />
                            </TouchableOpacity>
                          </View>
                        </Swipeable>

                    ))
                )}
                <TouchableOpacity
                    style={styles.addButton}
                    onPress={() => navigation.navigate('AddContainer')}
                >
                  <Icon name="plus" size={24} color="#000" />
                </TouchableOpacity>
              </View>

              <View>
                <View style={styles.box3}></View>
                <View style={styles.row3}>

                </View>
              </View>

              <LinearGradient
                  colors={['rgba(49, 58, 91, 0.9)', 'rgba(33, 39, 61, 0.9)']}
                  style={styles.headerContainer}
                  start={{ x: 0, y: 0 }}
                  end={{ x: 0, y: 1 }}
              >
                <View style={styles.headerTextContainer}>
                  <Text style={styles.text2}>{"Container List"}</Text>
                </View>
              </LinearGradient>
            </View>
            <View style={styles.column3} />
          </View>
        </ScrollView>
        <View>
          <NavigationPage />
        </View>

        {/* 팝업창 UI */}
        <Modal animationType="slide" transparent={true} visible={modalVisible} onRequestClose={() => setModalVisible(false)}>
          <View style={styles.infoContainer}>
            <View style={styles.infoView}>
              <Text style={styles.infoTitle}>컨테이너 정보</Text>
              <View style={styles.infoBox}>
                <Icon name="id-card-o" size={24} color="#000" />
                <View style={styles.infoTextContainer}>
                  <Text style={styles.infoName}>컨테이너 번호 </Text>
                  <Text style={styles.infoText}>{selectedContainer?.containNumber}</Text>
                </View>
              </View>
              <View style={styles.separator} />
              <View style={styles.infoBox}>
                <Icon name="location-arrow" size={24} color="#000" />
                <View style={styles.infoTextContainer}>
                  <Text style={styles.infoName}>도착지</Text>
                  <Text style={styles.infoText}>{selectedContainer?.destination}</Text>
                </View>
              </View>
              <View style={styles.separator} />
              <View style={styles.infoBox}>
                <Icon name="shopping-basket" size={24} color="#000" />
                <View style={styles.infoTextContainer}>
                  <Text style={styles.infoName}>물건 종류</Text>
                  <Text style={styles.infoText}>{selectedContainer?.cargo}</Text>
                </View>
              </View>
              <View style={styles.separator} />
              <View style={styles.infoBox}>
                <Icon name="users" size={24} color="#000" />
                <View style={styles.infoTextContainer}>
                  <Text style={styles.infoName}>컨테이너 소유자</Text>
                  <Text style={styles.infoText}>{selectedContainer?.containerOwner}</Text>
                </View>
              </View>

              <TouchableOpacity
                  style={styles.closeButton}
                  onPress={() => setModalVisible(false)}
              >
                <Icon name="close" size={24} color="#000" />
              </TouchableOpacity>
              <Text style={styles.closeButtonText}>닫기</Text>
            </View>
          </View>
        </Modal>
      </SafeAreaView>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",

  },
  adminIcon: {
    position: "absolute",
    width: "100%",
    top: -12,
  },
  headerTextContainer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    width: "100%",
    paddingHorizontal: 15,
  },
  headerContainer: {
    borderRadius: 8,
    position: "absolute",
    top: 1,
    right: 0,
    left: 0,
    height: 56,
    backgroundColor: "#30395B",
    borderColor: "#FFFFFF",
    borderWidth: 1,
    paddingHorizontal: 14,

  },
  infoBox: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
    paddingLeft: 10,
  },
  infoTextContainer: {
    flexDirection: 'column',
    width: "100%",
    paddingLeft: 10,
  },
  box: {
    flex: 1,
    alignSelf: "stretch",
  },
  box2: {
    height: 1,
    backgroundColor: "#CAC4D0",
    marginBottom: 21,
    marginHorizontal: 19,
  },
  box3: {
    height: 1,
    backgroundColor: "#CAC4D0",
    marginBottom: 20,
    marginHorizontal: 19,
  },
  column: {
    marginBottom: 9,
    marginHorizontal: 27,
  },
  column2: {
    backgroundColor: "#FFFFFF",
    borderColor: "#D9D9D9",
    borderRadius: 8,
    borderWidth: 1,
    paddingTop: 80,
    paddingBottom: 23,
    minHeight: 520,
    padding: 10,
  },
  column3: {
    backgroundColor: "#FFFFFF1C",
    borderRadius: 20,
    paddingVertical: 12,
    paddingRight: 31,
    marginHorizontal: 6,
  },
  infoButton: {
    padding: 10,
    borderRadius: 10,
    marginRight: 10,
  },
  infoContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    paddingBottom: 0,
  },
  infoView: {
    width: "90%",
    backgroundColor: "#fff",
    padding: 20,
    borderRadius: 20,
    alignItems: "center",
    marginBottom: 10,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 10,
    elevation: 5,
  },

  iconButton: {
    padding: 10,
    borderRadius: 10,
    backgroundColor: "#EEE",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 10,
  },
  addButton: {
    position: "absolute",
    bottom: 10,
    right: 30,
    padding: 10,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
  },


  iconInfo: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 10,
    textAlign: "center",
  },
  infoTitle: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 40,
    textAlign: "left",
  },
  infoName: {
    fontSize: 16,
    marginBottom: 0,
    width: "100%",
    paddingLeft: 20,

  },
  infoText: {
    fontSize: 14,
    marginBottom: 0,
    width: "100%",
    paddingLeft: 20,

  },
  iconTitle: {
    fontSize: 27,
    fontWeight: "bold",
    textAlign: "center",
  },
  separator: {
    width: "100%",
    borderBottomWidth: 1,
    borderBottomColor: "#aaa",
    marginBottom: 10,
    opacity: 0.5,
    alignSelf: "center",

  },
  closeButton: {
    position: "absolute",
    top: 10,
    right: 10,
  },

  closeButtonText: {
    color: "#fff",
    fontSize: 16,
  },

  row: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 30,
    marginHorizontal: 15,
  },
  row2: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 22,
    marginHorizontal: 33,
  },
  row3: {
    flexDirection: "row",
    justifyContent: "flex-end",
    alignItems: "center",
    marginHorizontal: 28,
  },
  row5: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 7,
    marginLeft: 50,
  },
  row6: {
    flexDirection: "row",
    alignItems: "center",
    marginLeft: 32,
  },
  scrollView: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    paddingTop: 18,
  },
  text: {
    color: "#1D1B20",
    fontSize: 16,
  },
  text2: {
    color: "#FFFFFF",
    fontSize: 20,
    marginTop: 10,
  },
  text3: {
    color: "#1D1B20",
    fontSize: 12,
    marginRight: 4,
    flex: 1,
  },
  text4: {
    color: "#1D1B20",
    fontSize: 12,
    marginRight: 75,
  },
  text5: {
    color: "#1D1B20",
    fontSize: 12,
  },
  view: {
    width: 64,
    backgroundColor: "#EADDFF",
    paddingHorizontal: 22,
  },
  navigationContainer: {
    position: "absolute",
    bottom: 0,
    width: "100%",
  },
  deleteButton: {
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "red",
    width: 55,
    height: 55,
    borderRadius: 8,
  },
});

export default List;