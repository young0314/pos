import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, TextInput, KeyboardAvoidingView, Platform, Alert } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Login = () => {
    const [adminId, setAdminId] = useState('');
    const [password, setPassword] = useState('');
    const navigation = useNavigation();
    const handleLogin = async () => {
        try {
            const response = await fetch('http://192.168.137.243:8080/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    adminId,
                    password,
                }),
            });

            console.log('Response Status:', response.status);

            const responseJson = await response.json();
            console.log('Raw Response:', responseJson);
            await AsyncStorage.removeItem('containers');

            // 로그인 성공 여부 확인
            if (responseJson.success === 'true') {
                console.log(`로그인 성공! 환영합니다, ${adminId}`);
                await AsyncStorage.setItem('adminId', adminId); // AsyncStorage에 adminId 저장
                navigation.navigate('List');
            } else {
                Alert.alert("오류", responseJson.message || "관리자 번호가 유효하지 않습니다.");
            }
        } catch (error) {
            Alert.alert(
                "오류",
                error.message || "로그인에 실패하였습니다.",
                [{ text: "확인" }]
            );
            console.error(error);
        }
    };


    return (
        <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            style={styles.pageContainer}
        >
            <View style={styles.loginContainer}>
                <View style={styles.loginBox}>
                    <View style={styles.toolbar}>
                        <Text style={styles.toolbarText}>Login</Text>
                    </View>
                    <LinearGradient
                        colors={['rgba(49, 58, 91, 0.9)', 'rgba(33, 39, 61, 0.9)']}
                        style={styles.loginBoxInput}
                        start={{ x: 0, y: 0 }}
                        end={{ x: 0, y: 1 }}
                    >
                        <TextInput
                            style={styles.input}
                            placeholder="관리자 번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={adminId}
                            onChangeText={setAdminId}
                        />
                        <TextInput
                            style={styles.input}
                            placeholder="비밀 번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={password}
                            onChangeText={setPassword}
                            secureTextEntry
                        />

                        <View style={styles.signupContainer}>
                            <TouchableOpacity onPress={() => navigation.navigate('Signup')}>
                                <Text style={styles.signupText}>회원 가입</Text>
                            </TouchableOpacity>
                            <TouchableOpacity onPress={() => navigation.navigate('NewPassword')}>
                                <Text style={styles.signupText}>비밀번호 찾기</Text>
                            </TouchableOpacity>
                        </View>
                        <View style={styles.buttonContainer}>
                            <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('Admin')}>
                                <Text style={styles.buttonText}>찾기</Text>
                            </TouchableOpacity>
                            <TouchableOpacity style={styles.confirmButton}
                                             onPress={handleLogin}
                              //onPress={() => navigation.navigate('List')}
                            >
                                <Text style={styles.confirmButtonText}>확인</Text>
                            </TouchableOpacity>
                        </View>
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
    loginContainer: {
        minHeight: 324,
        width: '100%',
        maxWidth: 287,
        flexDirection: 'column',
        alignItems: 'flex-end',
    },
    loginBox: {
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
        fontFamily: 'Montserrat, sans-serif',
        fontSize: 17,
        color: 'rgba(255, 255, 255, 1)',
        letterSpacing: -0.34,
    },
    loginBoxInput: {
        borderRadius: 17,
        paddingVertical: 40,
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
        justifyContent: 'center',
        paddingLeft: 10,
    },
    signupContainer: {
        flexDirection: 'row', // 가로 정렬
        justifyContent: 'space-between',
        width: '100%',
        marginTop: 15,
    },

    signupText: {
        fontSize: 13,
        fontWeight: 'bold',
        textDecorationLine: 'underline', // 강조된 밑줄
        marginTop: 1, // 위쪽 여백 추가
        color: 'rgba(255, 255, 255, 0.5)',
    },
    buttonContainer: {
        display: 'flex',
        marginTop: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
    },
    button: {
        borderRadius: 4,
        backgroundColor: 'rgba(180, 182, 185, 0.3)',
        paddingVertical: 5,
        paddingHorizontal: 30,
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: 23,
    },
    buttonText: {
        color: 'rgba(255, 255, 255, 1)',
        fontSize: 14,
        fontWeight: '500',
    },
    confirmButton: {
        borderRadius: 4,
        backgroundColor: 'rgba(180, 182, 185, 0.3)',
        paddingVertical: 5,
        paddingHorizontal: 30,
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: 23,
    },
    confirmButtonText: {
        color: 'rgba(255, 255, 255, 1)',
        fontSize: 14,
        fontWeight: '500',
    },
});
export default Login;
