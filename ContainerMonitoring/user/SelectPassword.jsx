import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, TextInput, KeyboardAvoidingView, Platform, Alert } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function SelectPassword() {
    const [adminId, setAdminId] = useState('');
    const [idNumber, setIdNumber] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const navigation = useNavigation();

    // AsyncStorage에서 데이터 불러오기
    useEffect(() => {
        const loadUserData = async () => {
            try {
                const storedAdminId = await AsyncStorage.getItem('adminId');
                const storedIdNumber = await AsyncStorage.getItem('idNumber');
                if (storedAdminId && storedIdNumber) {
                    setAdminId(storedAdminId);
                    setIdNumber(storedIdNumber);
                }
            } catch (error) {
                console.error("AsyncStorage 오류:", error);
            }
        };
        loadUserData();
    }, []);

    const handlePasswordChange = async () => {
        if (!newPassword.trim()) {
            Alert.alert('오류', '새 비밀번호를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch('http://192.168.137.243:8080/admin/password/re', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    adminId,
                    newPassword,
                }),
                credentials: 'include',
            });

            console.log('서버 응답:', response);  // 응답이 정상적으로 오는지 확인
            if (!response.ok) {
                const errorResponse = await response.text();
                throw new Error(`비밀번호 변경 오류: ${errorResponse}`);
            }

            // 여기서 Alert이 호출되도록 해야함
            Alert.alert('성공', '비밀번호가 변경되었습니다.',
                [
                    { text: '확인', onPress: () => navigation.navigate('Login') }
                ]
            );

            setNewPassword('');
        } catch (error) {
            Alert.alert('오류', error.message);
            console.error(error);
        }
    };
    return (
        <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            style={styles.pageContainer}
        >
            <View style={styles.signupContainer}>
                <View style={styles.signupBox}>
                    <View style={styles.toolbar}>
                        <Text style={styles.toolbarText}>New Password</Text>
                    </View>
                    <LinearGradient
                        colors={['rgba(49, 58, 91, 0.9)', 'rgba(33, 39, 61, 0.9)']}
                        style={styles.signupBoxInput}
                        start={{ x: 0, y: 0 }}
                        end={{ x: 0, y: 1 }}
                    >
                        <TextInput
                            style={styles.input}
                            placeholder="새 비밀번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={newPassword}
                            onChangeText={setNewPassword}
                            secureTextEntry
                        />
                        <TouchableOpacity style={styles.button} onPress={handlePasswordChange}>
                            <Text style={styles.buttonText}>확인</Text>
                        </TouchableOpacity>
                    </LinearGradient>
                </View>
            </View>
        </KeyboardAvoidingView>
    );
}

const styles = StyleSheet.create({
    pageContainer: {
        backgroundColor: 'rgba(255, 255, 255, 1)',
        flex: 1,
        marginLeft: 'auto',
        marginRight: 'auto',
        maxWidth: 480,
        width: '100%',
        paddingHorizontal: 21,
        paddingVertical: 10,
        justifyContent: 'center',
        alignItems: 'center',
    },
    signupContainer: {
        minHeight: 324,
        width: '100%',
        maxWidth: 287,
        flexDirection: 'column',
        alignItems: 'flex-end',
    },
    signupBox: {
        width: '100%',
        flexDirection: 'column',
        alignItems: 'stretch',
        borderColor: 'rgba(180, 182, 185, 1)',
    },
    toolbar: {
        borderRadius: 17,
        backgroundColor: 'rgba(49, 58, 91, 1)',
        borderColor: 'rgba(255, 255, 255, 1)',
        borderWidth: 1,
        padding: 19,
        justifyContent: 'center',
        alignItems: 'flex-start',
        width: '100%',
    },
    toolbarText: {
        fontSize: 17,
        color: 'rgba(255, 255, 255, 1)',
        letterSpacing: -0.34,
    },
    signupBoxInput: {
        borderRadius: 17,
        paddingVertical: 43,
        paddingHorizontal: 26,
        flexDirection: 'column',
        alignItems: 'stretch',
    },
    input: {
        borderRadius: 4,
        borderWidth: 1,
        borderColor: 'rgba(223, 225, 227, 1)',
        padding: 6,
        marginBottom: 15,
        paddingLeft: 10,
    },
    button: {
        borderRadius: 4,
        backgroundColor: 'rgba(180, 182, 185, 0.3)',
        paddingVertical: 5,
        paddingHorizontal: 10,
        justifyContent: 'center',
        alignItems: 'center',
    },
    buttonText: {
        color: 'rgba(255, 255, 255, 1)',
        fontSize: 14,
        fontWeight: '500',
    },
});