import React, { useEffect, useState } from "react";
import { View, Text, StyleSheet } from "react-native";
import { useRoute } from "@react-navigation/native";
import Icon from "react-native-vector-icons/FontAwesome";
import NavigationPage from './NavigationPage';
import AsyncStorage from '@react-native-async-storage/async-storage';
import LinearGradient from "react-native-linear-gradient";

const Main = () => {
    const route = useRoute();
    const [containNumber, setContainNumber] = useState("");
    const [data, setData] = useState({
        lifespan: "",
        doorStatus: "",
        errorStatus: "",
        containNumber: "",
        temperature: "",
        humidity: "",
    });

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
                console.error("컨테이너 연동 실패:", error);
            }
        };

        fetchContainNumber();
    }, []);

    useEffect(() => {
        if (containNumber) {
            fetch(`http://192.168.137.222:8080/container/monitoring/${containNumber}`, {
                method: "GET",
            })
                .then((response) => response.json())
                .then((responseData) => {
                    setData({
                        lifespan: responseData.lifespan,
                        doorStatus: responseData.doorStatus,
                        errorStatus: responseData.errorStatus,
                        containNumber: responseData.containNumber,
                        temperature: responseData.temperature,
                        humidity: responseData.humidity,
                    });
                })
                .catch((error) => {
                    console.error("데이터 가져오기 실패:", error);
                });
        }
    }, [containNumber]);

    return (
        <View style={styles.container}>
            <View style={styles.contentWrapper}>
                <View style={styles.statusContainer}>
                    <StatusItem icon="thermometer-half" value={`${data.temperature}°C`} />
                    <StatusItem icon="tint" value={`${data.humidity}%`} />
                    <StatusItem icon={data.doorStatus === 1 ? "unlock" : "lock"} />
                </View>

                <InfoCard title="Life Time">
                    <Text style={styles.subText}>{data.lifespan ? `${data.lifespan} left` : ""}</Text>
                </InfoCard>

                <InfoCard title="Chiller Condition">
                    <View style={styles.iconContainer}>
                        <Text
                            style={[
                                data.errorStatus === 0 ? styles.okText : styles.errorText,
                            ]}
                        >
                            {data.errorStatus === 0 ? "O" : "X"}
                        </Text>
                    </View>
                </InfoCard>
            </View>

            <View style={styles.navigationContainer}>
                <NavigationPage />
            </View>
        </View>
    );
};
const StatusItem = ({ icon, value }) => (
    <LinearGradient
        colors={['#6C648B', '#5A5375']}
        style={styles.statusItem}
        start={{ x: 0, y: 0 }}
        end={{ x: 0, y: 1 }}
    >
        <Icon name={icon} size={20} color="#fff" />
        <Text style={styles.statusText}>{value}</Text>
    </LinearGradient>
);
const InfoCard = ({ title, children }) => (
    <LinearGradient
        colors={['#2C3E5C', '#253554']}
        style={styles.card}
        start={{ x: 0, y: 0 }}
        end={{ x: 0, y: 1 }}
    >
        <Text style={styles.cardTitle}>{title}</Text>
        <View style={styles.separator} />
        <View style={styles.cardContent}>{children}</View>
    </LinearGradient>
);

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#FFFFFF",
    },
    contentWrapper: {
        flex: 1,
        padding: 40,
    },
    statusContainer: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: 42,
    },
    statusItem: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#6C648B",
        paddingVertical: 15,
        paddingHorizontal: 20,
        borderRadius: 15,
        minWidth: 104,
        shadowOffset: { width: 0, height: 10 },
        shadowOpacity: 0.2,
        shadowRadius: 10,
        elevation: 5,
    },
    statusText: {
        color: "#fff",
        marginLeft: 7,
    },
    card: {
        backgroundColor: "#2C3E5C",
        borderRadius: 15,
        padding: 40,
        marginBottom: 30,
        borderWidth: 1,
        borderColor: "#444",
        height: 180,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 10 },
        shadowOpacity: 0.2,
        shadowRadius: 10,
        elevation: 5,
    },
    cardTitle: {
        color: "#fff",
        fontSize: 18,
        fontWeight: "bold",
        position: "absolute",
        top: 10,
        left: 10,
    },
    separator: {
        width: "330",
        borderBottomWidth: 1,
        borderBottomColor: "#aaa",
        marginVertical: 10,
        opacity: 0.5,
        alignSelf: "flex-start",
        marginLeft: -40,
    },
    cardContent: {
        marginTop: 40,
        alignItems: "center",
    },
    iconContainer: {
        position: "absolute",
        top: -20,
        alignItems: "center",
        width: "100%",
    },
    errorText: {
        color: "red",
        fontSize: 60,
        fontWeight: "bold",
        bottom: 10,
    },
    okText: {
        color: "green",
        fontSize: 60,
        fontWeight: "bold",
        bottom: 10,
    },
    subText: {
        color: "#fff",
        fontSize: 30,
        bottom: 10,
    },
    navigationContainer: {
        position: "absolute",
        bottom: 0,
        width: "100%",
    },
});

export default Main;
