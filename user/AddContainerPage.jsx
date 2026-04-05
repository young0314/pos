import React, { useState } from 'react';
import { View, StyleSheet, Text, TouchableOpacity, TextInput, KeyboardAvoidingView, Platform, Alert } from "react-native";
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const AddContainer = () => {
  const [containerOwner, setContainerOwner] = useState('');
  const [cargo, setCargo] = useState('');
  const [containNumber, setContainNumber] = useState('');
  const [destination, setDestination] = useState('');
  const [containers, setContainers] = useState([]);
  const [deviceId, setDeviceId] = useState('');
  const navigation = useNavigation();

  const handleContainer = async () => {
    try {
      if (!containerOwner || !cargo || !containNumber || !destination || !deviceId) {
        Alert.alert("오류", "모든 정보를 입력해주세요.");
        return;
      }

      // 중복 체크
      const isDuplicate = containers.some(c => c.containNumber === containNumber);
      if (isDuplicate) {
        Alert.alert("오류", "이미 존재하는 컨테이너 번호입니다.");
        return;
      }

      const newContainer = {
        containerOwner,
        cargo,
        containNumber,
        destination,
        deviceId,
      };
      console.log("보낼 데이터:", newContainer);

      const response = await fetch('http://192.168.137.222:8080/container/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newContainer),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`서버 오류: ${response.status}, 응답: ${errorText}`);
      }

      const data = await response.json();
      console.log("응답 성공:", data);
      await fetchContainers();

      navigation.navigate('List');
    } catch (error) {
      Alert.alert('오류', '등록 중 오류가 발생했습니다.');
      console.error(error);
    }
  };

  const fetchContainers = async () => {
    try {
      const response = await fetch('http://192.168.137.222:8080/container/list');
      const data = await response.json();
      await AsyncStorage.setItem('containers', JSON.stringify(data));
    } catch (error) {
      console.error("컨테이너 불러오기 실패: ", error);
      Alert.alert("실패", "등록된 컨테이너가 없습니다");
    }
  };

  const renderInputField = (label, value, setValue) => (
    <View style={styles.inputField}>
      <Text>{label}</Text>
      <TextInput
        style={styles.input}
        value={value}
        onChangeText={setValue}
        placeholder="입력하세요"
      />
    </View>
  );

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={styles.pageContainer}
    >
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerText}>New Container</Text>
        </View>
        <View style={styles.formContainer}>
          {renderInputField("컨테이너 소유자", containerOwner, setContainerOwner)}
          {renderInputField("물건 종류", cargo, setCargo)}
          {renderInputField("컨테이너 고유 번호", containNumber, setContainNumber)}
          {renderInputField("도착지", destination, setDestination)}
          {renderInputField("Device ID", deviceId, setDeviceId)}

          <TouchableOpacity style={styles.submitButton} onPress={handleContainer}>
            <Text style={styles.submitButtonText}>확인</Text>
          </TouchableOpacity>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  pageContainer: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: "#FFFFFF",
  },
  container: {
    maxWidth: 430,
    width: "100%",
    padding: 20,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 10,
    elevation: 5,
  },
  header: {
    borderTopLeftRadius: 17,
    borderTopRightRadius: 17,
    borderColor: "rgba(255, 255, 255, 1)",
    borderWidth: 1,
    marginTop: 10,
    width: "100%",
    padding: 19,
    backgroundColor: "#30395B",
  },
  headerText: {
    fontSize: 17,
    color: "rgba(255, 255, 255, 1)",
    fontWeight: "400",
  },
  formContainer: {
    borderRadius: 8,
    borderColor: "rgba(217, 217, 217, 1)",
    borderWidth: 1,
    width: "100%",
    padding: 20,
    minHeight: 450,
  },
  inputField: {
    marginTop: 30,
    width: "100%",
    flexDirection: "column",
  },
  input: {
    borderRadius: 8,
    borderColor: "rgba(217, 217, 217, 1)",
    borderWidth: 1,
    minWidth: 240,
    marginTop: 8,
    padding: 12,
  },
  submitButton: {
    borderRadius: 8,
    borderColor: "rgba(44, 44, 44, 1)",
    borderWidth: 1,
    marginTop: 30,
    padding: 12,
    alignItems: "center",
    justifyContent: "center",
  },
  submitButtonText: {
    textAlign: "center",
  },
});

export default AddContainer;