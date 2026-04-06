import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, TextInput, KeyboardAvoidingView, Platform, Alert } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';

const Admin = () => {
    const [adminName, setAdminName] = useState('');
    const [idNumber, setIdNumber] = useState('');

    const navigation = useNavigation();

 const handleAdmin = async () => {
   const trimmedName = adminName.trim();
   const trimmedIdNumber = idNumber.trim();

   if (!trimmedName || !trimmedIdNumber) {
     Alert.alert('알림', '이름과 주민등록번호를 모두 입력해주세요.');
     return;
   }

   const requestBody = {
     adminName: trimmedName,
     idNumber: trimmedIdNumber,
   };

   console.log('보내는 값:', requestBody);

   try {
     const response = await fetch('http://192.168.137.222:8080/admin/email/re', {
       method: 'POST',
       headers: {
         'Content-Type': 'application/json',
       },
       body: JSON.stringify(requestBody),
     });

     const data = await response.json();
     console.log('서버 응답:', data);

     if (!response.ok) {
       throw new Error(data.message || '서버 오류');
     }

     const foundEmail = data.email;

     Alert.alert(
       '아이디 찾기',
       `회원님의 이메일은 ${foundEmail} 입니다.`,
       [
         {
           text: '확인',
           onPress: () => navigation.navigate('Login'),
         },
       ]
     );
   } catch (error) {
     console.error('에러:', error);
     Alert.alert('오류', error.message);
   }
 };
    return (
        <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            style={styles.pageContainer}
        >
            <View style={styles.adminContainer}>
                <View style={styles.adminBox}>
                    <View style={styles.toolbar}>
                        <Text style={styles.toolbarText}>Find Email</Text>
                    </View>
                    <LinearGradient
                        colors={['rgba(49, 58, 91, 0.9)', 'rgba(33, 39, 61, 0.9)']}
                        style={styles.adminBoxInput}
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
                            placeholder="주민번호"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={idNumber}
                            onChangeText={setIdNumber}
                        />
                        <TouchableOpacity
                            style={styles.button}
                            onPress={handleAdmin}
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
    adminContainer: {
        minHeight: 324,
        width: '100%',
        maxWidth: 287,
        flexDirection: 'column',
        alignItems: 'flex-end',
    },
    adminBox: {
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
    adminBoxInput: {
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

export default Admin;