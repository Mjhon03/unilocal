package co.edu.eam.unilocal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.eam.unilocal.ui.screens.CreatePlaceScreen
import co.edu.eam.unilocal.ui.screens.EditProfileScreen
import co.edu.eam.unilocal.ui.screens.LoginScreen
import co.edu.eam.unilocal.ui.screens.RegisterScreen
import co.edu.eam.unilocal.ui.screens.SearchScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RouteScreen.Login
    ) {
        composable<RouteScreen.Search> {
            SearchScreen(
                onCrearClick = {
                    navController.navigate(RouteScreen.CreatePlace)
                },
                onProfileClick = {
                    navController.navigate(RouteScreen.EditProfile)
                },
                onFavoritesClick = {
                    navController.navigate(RouteScreen.Search)
                },
                onBackClick = {
                    navController.navigate(RouteScreen.Login)
                }
            )
        }
        
        composable<RouteScreen.Login> {
            LoginScreen(
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Aquí puedes agregar lógica de autenticación
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(RouteScreen.Register)
                }
            )
        }
        
        composable<RouteScreen.Register> {
            RegisterScreen(
                onBackClick = {
                    navController.navigate(RouteScreen.Login) {
                        popUpTo(RouteScreen.Login) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    // Aquí puedes agregar lógica de registro
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(RouteScreen.Login)
                }
            )
        }
        
        composable<RouteScreen.CreatePlace> {
            CreatePlaceScreen(
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = true }
                    }
                },
                onCreateClick = {
                    // Aquí puedes agregar lógica para crear el lugar
                    navController.popBackStack()
                }
            )
        }
        
        composable<RouteScreen.EditProfile> {
            EditProfileScreen(
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = true }
                    }
                }
            )
        }
    }
}
