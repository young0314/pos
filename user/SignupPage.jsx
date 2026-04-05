import React, { useState, useEffect} from 'react';
import { View, Text, TouchableOpacity, StyleSheet, TextInput, KeyboardAvoidingView, Platform, Alert } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Signup = () => {
    const [adminName, setAdminName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [idNumber, setIdNumber] = useState('');
    const [password, setPassword] = useState('');
    const [adminId, setAdminId] = useState('');
    const [authCode, setAuthCode] = useState('');
    const [isVerified, setIsVerified] = useState(false);
    const navigation = useNavigation();
    const [showVerify, setShowVerify] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0);
    const handleEmail = async () => {
        if(!email) {
            Alert.alert('오류', '이메일을 입력해주세요.');
            return;
            }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if(!emailRegex.test(email)) {
            Alert.alert('오류', '이메일 형식이 틀립니다.');
            return;
            }

        try {

            // 이메일 중복 체크
            console.log('1. 이메일 중복체크 시작');
        const res = await fetch('http://192.168.137.222:8080/admin/email/check', {
            method: 'POST',
            headers: {'Content-Type' : 'application/json'},
            body: JSON.stringify({
                email,
                type: 'check',
                }),
            });

        console.log('2. 중복체크 응답 받음', res.status);
         if (!res.ok) {
              throw new Error(`중복체크 실패: ${res.status}`);
            }
        const data = await res.json();

         console.log('3. 중복체크 결과', data);

        if (!data.success) {
          Alert.alert('오류', '이미 사용중인 이메일 입니다');
          return;
        }
        Alert.alert('성공', '사용 가능한 이메일입니다.');

        console.log('4. 이메일 전송 요청 시작');
        // 이메일 전송 요청
        const sendRes = await fetch('http://192.168.137.222:8080/admin/email/send', {
            method: 'POST',
            headers: { 'Content-type': 'application/json'},
            body: JSON.stringify({
                email,
                type:'send',
                }),
            });
            console.log('5. 이메일 전송 응답 받음', sendRes.status);

            if (!sendRes.ok) {
              throw new Error(`이메일 전송 실패: ${sendRes.status}`);
            }

            setShowVerify(true);
            setTimeLeft(180);
            Alert.alert('완료', '인증 메일이 전송 되었습니다!');
          } catch (e) {
            console.log('에러 발생:', e);
            Alert.alert('실패', `이메일 인증 요청 실패: ${e.message}`);
          }
        };

// const handleEmail = () => {
//   setShowVerify(true);
//   setTimeLeft(180);
// };

const handleCheckCode = async () => {
  try {
    const res = await fetch('http://192.168.137.222:8080/admin/email/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email,
        authCode,
        type: 'verify',
      }),
    });

    if (!res.ok) {
      throw new Error(`인증 확인 실패: ${res.status}`);
    }

    const data = await res.json();
    console.log('인증 확인 결과:', data);

    if (data.success) {
      setIsVerified(true);
      setShowVerify(false);
      setTimeLeft(0);
      setAuthCode('');
      Alert.alert('성공', '이메일 인증이 완료되었습니다.');
    } else {
      Alert.alert('실패', data.responseMessage || '인증번호가 올바르지 않습니다.');
    }
  } catch (e) {
    console.log('인증 확인 오류:', e);
    Alert.alert('오류', '인증 확인 중 오류가 발생했습니다.');
  }
};
    useEffect(() => {
        //0이하면 실행 취소
        if (timeLeft <= 0) return;
       //1초마다 실행되는 타이머 생성
        const timer = setInterval(() => {
            // 이전 시간부터 1씩 감소
            setTimeLeft(prev => prev - 1);
            }, 1000); //1초
        //이전 타이머 제거
        return () => clearInterval(timer);
        }, [timeLeft]); //시간 끝날때 마다 이 코드 다시 실행

    const formatTime = () => {
        //초를 분으로 변환
        const min = Math.floor(timeLeft / 60);
        //분으로 변환 한 후 남은 초 계산
        const sec = timeLeft % 60;
        // 00:00 형태의 문자열로 변환 0은 한자리일때 붙임
        return `${min}:${sec < 10 ? '0' : ''}${sec}`;
        };
    useEffect(() => {
      if (showVerify && timeLeft === 0) {
        setShowVerify(false);
        setAuthCode('');
        Alert.alert('시간 만료', '인증 시간이 만료되었습니다. 다시 인증해주세요.');
      }
    }, [showVerify, timeLeft]);

     // 회원가입
    const handleSignup = async () => {
        try {
            if (!adminName || !email || !phone || !idNumber || !password) {
                            Alert.alert("오류", "모든 정보를 입력해주세요.");
                            return;
                         };
            const response = await fetch('http://192.168.137.222:8080/admin/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    adminName,
                    email,
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
                    <View style={styles.inputContainer}>
                        <TextInput
                            style={styles.inputNoBorder}
                            placeholder="이메일"
                            placeholderTextColor="rgba(180, 182, 185, 1)"
                            value={email}
                            onChangeText={setEmail}
                        />
                        <TouchableOpacity style={styles.emailButton} onPress={handleEmail}>
                            <Text style={styles.emailText}>인증</Text>
                            </TouchableOpacity>
                    </View>
                    {showVerify && (
                      <View style={styles.verifyBox}>
                        <TextInput
                          style={styles.verifyInput}
                          placeholder="인증번호"
                          placeholderTextColor="#aaa"
                          value={authCode}
                          onChangeText={setAuthCode}
                        />
                        <Text style={styles.timerText}>{formatTime()}</Text>
                        <TouchableOpacity style={styles.verifyButton} onPress={handleCheckCode}>
                          <Text style={styles.verifyButtonText}>확인</Text>
                        </TouchableOpacity>
                      </View>
                    )}

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
                            secureTextEntry={idNumber.length > 6}
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
    inputContainer: {
            flexDirection: 'row',
            alignItems: 'center',
            borderWidth: 1,
            borderColor: 'rgba(223, 225, 227, 1)',
            borderRadius: 4,
            marginBottom: 15,
            paddingHorizontal: 10,
            height: 40,

    },
//이메일
    inputNoBorder: {
        flex: 1,
        height: '100%',
       },
    emailButton: {
        justifyContent: 'center',
        alignItems: 'center',
        paddingHorizontal: 10,
        height: '100%',
        },
    emailText: {
        color: '#4A90E2',
        fontWeight: '600',
        },
    input: {
        borderRadius: 4,
        borderWidth: 1,
        borderColor: 'rgba(223, 225, 227, 1)',
       // padding: 6,
       height: 40,
        marginBottom: 15,
        justifyContent: 'center',
        paddingLeft: 10,
    },
    verifyBox: {
        flexDirection: 'row',
        alignItems: 'center'
        },
    verifyBox: {
      flexDirection: 'row',
      alignItems: 'center',
      borderWidth: 1,
      borderColor: 'rgba(223, 225, 227, 1)',
      borderRadius: 4,
      paddingHorizontal: 10,
      height: 40,
      marginBottom: 15,
    },

    verifyInput: {
      flex: 1,
      height: '100%',
      paddingLeft: 0,
    },

    timerText: {
      color: 'red',
      marginLeft: 8,
      fontSize: 13,
    },

    verifyButton: {
      justifyContent: 'center',
      alignItems: 'center',
      marginLeft: 12,
      height: '100%',
    },

    verifyButtonText: {
      color: '#4A90E2',
      fontWeight: '600',

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
