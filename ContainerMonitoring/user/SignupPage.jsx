import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, TextInput, KeyboardAvoidingView, Platform, Alert } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Signup = () => {
    const [adminName, setAdminName] = useState('');
    const [phone, setPhone] = useState('');
    const [idNumber, setIdNumber] = useState('');
    const [password, setPassword] = useState('');
    const [adminId, setAdminId] = useState('');
    const navigation = useNavigation();

    const handleSignup = async () => {
        try {
            const response = await fetch('http://192.168.137.243:8080/admin/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    adminName,
                    phone,
                    idNumber,
                    password,
                    adminId,
                }),
            });

            if (!response.ok) {
                throw new Error('서버 응답이 올바르지 않습니다');
            }

            const data = await response.json();
            const generatedAdminId = data.adminId;

            console.log(data);
            Alert.alert(
                "발급 완료",
                `관리자 번호가 발급되었습니다: ${generatedAdminId}`,
                [{ text: "확인", onPress: () => {
                    AsyncStorage.setItem('adminId', 'true');
                    AsyncStorage.setItem('password', 'true');
                }}]
            );
            navigation.navigate('Login');
        } catch (error) {
            Alert.alert('오류', '회원가입 중 오류가 발생했습니다.');
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
                        <Text style={styles.toolbarText}>Sign Up</Text>
                    </View>
                    <LinearGradient
                        colors={['rgba(49, 58, 91, 0.9)', 'rgba(33, 39, 61, 0.9)']}
                        style={styles.signupBoxInput}
                        start={{ x: 0, y: 0 }}
                        end={{ x: 0, y: 1 }}
                    >
                        <TextInput
                            style={styles.input}
                            placeholder="이름"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={adminName}
                            onChangeText={setAdminName}
                        />
                        <TextInput
                            style={styles.input}
                            placeholder="전화번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={phone}
                            onChangeText={setPhone}
                        />
                        <TextInput
                            style={styles.input}
                            placeholder="주민번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={idNumber}
                            onChangeText={setIdNumber}
                            secureTextEntry={idNumber.length > 6}  // 7자리 이상부터 비밀번호 처리
                        />


                        <TextInput
                            style={styles.input}
                            placeholder="비밀번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={password}
                            onChangeText={setPassword}
                            secureTextEntry
                        />
                        <TouchableOpacity
                            style={styles.button}
                            onPress={handleSignup}
                        >
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
        fontFamily: 'Montserrat, sans-serif',
        fontSize: 17,
        color: 'rgba(255, 255, 255, 1)',
        letterSpacing: -0.34,
    },
    signupBoxInput: {
        borderRadius: 17,
        paddingVertical: 40,
        paddingHorizontal: 24,
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

export default Signup;
