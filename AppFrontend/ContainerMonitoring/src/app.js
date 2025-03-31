import React from 'react';
import { StyleSheet, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import LoginPage from '../user/LoginPage';
import SignupPage from '../user/SignupPage';
import ListPage from '../user/ListPage';
import AdminPage from '../user/AdminPage';
import NewPassword from "../user/NewPassword";
import AddContainerPage from '../user/AddContainerPage';
import NavigationPage from '../user/NavigationPage';
import MonitoringPage from '../user/MonitoringPage';
import SelectPassword from "../user/SelectPassword";
import MainPage from "../user/MainPage";
import ProfileMenu from "../user/adminIdPage";

const Stack = createStackNavigator();

function App() {
    return (
        <NavigationContainer>
            <View style={styles.container}>
                <Stack.Navigator>
                    <Stack.Screen
                        name="Login"
                        component={LoginPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="Signup"
                        component={SignupPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="Admin"
                        component={AdminPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="SelectPassword"
                        component={SelectPassword}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="ProfileMenu"
                        component={ProfileMenu}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="NewPassword"
                        component={NewPassword}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="MainPage"
                        component={MainPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="List"
                        component={ListPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="AddContainer"
                        component={AddContainerPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="Navigation"
                        component={NavigationPage}
                        options={{ headerShown: false }}
                    />
                    <Stack.Screen
                        name="Monitoring"
                        component={MonitoringPage}
                        options={{ headerShown: false }}
                    />
                </Stack.Navigator>
            </View>
        </NavigationContainer>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1, // 화면을 꽉 채우도록 설정
    },
});

export default App;