import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import Icon from "react-native-vector-icons/FontAwesome";
import { useNavigation } from '@react-navigation/native';

const NavigationItem = ({ iconName, label, isActive, onPress }) => {
    return (
        <TouchableOpacity style={styles.navigationItem} onPress={onPress}>
            <View style={[styles.iconContainer, isActive && styles.activeIconContainer]}>
                <Icon
                    name={iconName}
                    size={24}
                    color={'#1D1B20'}
                />
            </View>
            <Text style={styles.label}>{label}</Text>
        </TouchableOpacity>
    );
};

const Navigation = () => {
    const navigation = useNavigation();
    const navigationItems = [
        { iconName: 'map-marker', label: 'Monitoring', onPress: () => navigation.navigate('Monitoring')},// 위치 아이콘
        { iconName: 'home', label: 'Home', onPress: () => navigation.navigate('MainPage')}, // 집 아이콘
        { iconName: 'file', label: 'Container', onPress: () => navigation.navigate('List') }, // 파일 아이콘
    ];

    return (
        <View style={styles.navigation}>
            {navigationItems.map((item, index) => (
                <NavigationItem
                    key={index}
                    iconName={item.iconName}
                    label={item.label}
                    onPressIn={() => setIsHovered(true)}
                    onPressOut={() => setIsHovered(false)}
                    onPress={item.onPress}
                />
            ))}
        </View>
    );
};

const styles = StyleSheet.create({
    navigation: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center',
        backgroundColor: '#fff',
    },
    navigationItem: {
        paddingTop: 12,
        paddingBottom: 16,
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        flex: 1,

    },
    iconContainer: {
        borderRadius: 16,
        alignSelf: 'center',
        width: 64,
        height: 64,
        justifyContent: 'center',
        alignItems: 'center',

    },

    label: {
        color: '#1D1B20',
        textAlign: 'center',
        fontFamily: 'Roboto',
        fontSize: 12,
        fontWeight: '600',
        lineHeight: 16,
        letterSpacing: 0.5,
        marginTop: 4,


    },
});

export default Navigation;