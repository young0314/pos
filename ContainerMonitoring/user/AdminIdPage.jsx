import React, {useEffect, useState} from 'react';
import {View, Text, TouchableOpacity, Modal, StyleSheet, Alert} from 'react-native';
import Icon from "react-native-vector-icons/FontAwesome";
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from "@react-native-async-storage/async-storage";
const ProfileMenu = () => {
    const [adminId, setAdminId] = useState('');
    const navigation = useNavigation();
    const [modalVisible, setModalVisible] = useState(false);


    useEffect(() => {
        const fetchAdminId = async () => {
            const storedAdminId = await AsyncStorage.getItem('adminId');
            console.log("adminId 값:", storedAdminId);
            setAdminId(storedAdminId || "알 수 없음");
        };
        fetchAdminId();
    }, []);


    const handleLogout = async () => {
        Alert.alert(
            '로그아웃 하시겠습니까?',
            '',
            [
                {
                    text: "취소",
                    style: "cancel",
                },
                {
                    text: "확인",
                    onPress: async () => {
                        try {

                            const response = await fetch('http://192.168.137.243:8080/admin/logout', {
                                method: 'POST',
                                credentials: 'include',
                            });

                            if (!response.ok) {
                                throw new Error("서버 응답 오류");
                            }
                            await AsyncStorage.removeItem('adminId');
                            Alert.alert("로그아웃 되었습니다!");

                            // 로그인 페이지로 이동
                            navigation.navigate("Login");

                        } catch (error) {
                            console.error('로그아웃 중 오류 발생:', error);
                            Alert.alert("로그아웃 실패", "서버와 통신 중 오류가 발생했습니다.");
                        }
                    },
                },
            ]
        );
    };


    return (
        <View style={styles.container}>
            <TouchableOpacity style={styles.profileIcon} onPress={() => setModalVisible(true)}>
                <Icon name="user-circle" size={30} color="black" />
            </TouchableOpacity>

            <Modal transparent={true} visible={modalVisible} animationType="fade">
                <TouchableOpacity style={styles.modalOverlay} onPress={() => setModalVisible(false)} />
                <View style={styles.modalContent}>
                    <Text style={styles.adminIdText}>환영합니다 {adminId}님</Text>
                    <TouchableOpacity onPress={handleLogout}>
                        <Icon style={styles.logoutIcon} name="sign-out" size={24} color="#000" />
                        <Text style={styles.logoutText}>로그아웃</Text>
                    </TouchableOpacity>
                </View>
            </Modal>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        alignItems: 'flex-end',
        padding: 10,
    },
    logoutIcon: {
        position: "absolute",
        left: 5,
        flexDirection: "row",
    },
    profileIcon: {
        position: "absolute",
        right: 20,
        padding: 10,
    },
    logout: {
        flexDirection: "row",
        justifyContent: "space-between",
        marginBottom: 20,
    },
    modalOverlay: {
        flex: 1,
        backgroundColor: 'rgba(0,0,0,0.5)',
    },
    modalContent: {
        position: 'absolute',
        top: 50,
        right: 20,
        backgroundColor: 'white',
        padding: 15,
        borderRadius: 8,
        elevation: 5,
    },
    adminIdText: {
        fontSize: 16,
        marginBottom: 10,
    },

    logoutText: {
        color: 'black',
        fontSize: 14,
        marginLeft: 30,

    },
});

export default ProfileMenu;
