import React from 'react';
import { StyleSheet, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

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
import ProfileMenu from "../user/AdminIdPage";

const Stack = createNativeStackNavigator();

function App() {
    return (
        <GestureHandlerRootView style={{ flex: 1 }}>
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
        </GestureHandlerRootView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
});

export default App;