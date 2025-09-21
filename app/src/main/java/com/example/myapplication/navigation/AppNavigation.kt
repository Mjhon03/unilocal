package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.CreatePlaceScreen
import com.example.myapplication.ui.screens.EditProfileScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.RegisterScreen
import com.example.myapplication.ui.screens.SearchScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH_SCREEN
    ) {
        composable(Routes.SEARCH_SCREEN) {
            SearchScreen(
                onCrearClick = {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            )
        }
        
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(
                onBackClick = {
                    navController.navigate(Routes.SEARCH_SCREEN) {
                        popUpTo(Routes.SEARCH_SCREEN) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Aquí puedes agregar lógica de autenticación
                    navController.navigate(Routes.SEARCH_SCREEN) {
                        popUpTo(Routes.SEARCH_SCREEN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER_SCREEN)
                }
            )
        }
        
        composable(Routes.REGISTER_SCREEN) {
            RegisterScreen(
                onBackClick = {
                    navController.navigate(Routes.SEARCH_SCREEN) {
                        popUpTo(Routes.SEARCH_SCREEN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    // Aquí puedes agregar lógica de registro
                    navController.navigate(Routes.SEARCH_SCREEN) {
                        popUpTo(Routes.SEARCH_SCREEN) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN_SCREEN)
                }
            )
        }
        
        composable(Routes.CREATE_PLACE_SCREEN) {
            CreatePlaceScreen(
                onBackClick = {
                    navController.navigate(Routes.SEARCH_SCREEN) {
                        popUpTo(Routes.SEARCH_SCREEN) { inclusive = true }
                    }
                },
                onCreateClick = {
                    // Aquí puedes agregar lógica para crear el lugar
                    navController.popBackStack()
                }
            )
        }
        
        composable(Routes.EDIT_PROFILE_SCREEN) {
            EditProfileScreen(
                onBackClick = {
                    navController.navigate(Routes.SEARCH_SCREEN) {
                        popUpTo(Routes.SEARCH_SCREEN) { inclusive = true }
                    }
                }
            )
        }
    }
}
