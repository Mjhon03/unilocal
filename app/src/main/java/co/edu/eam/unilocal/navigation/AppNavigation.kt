package co.edu.eam.unilocal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.eam.unilocal.ui.screens.CreatePlaceScreen
import co.edu.eam.unilocal.ui.screens.EditProfileScreen
import co.edu.eam.unilocal.ui.screens.LoginScreen
import co.edu.eam.unilocal.ui.screens.ModerationPanelScreen
import co.edu.eam.unilocal.ui.screens.PlaceDetailScreen
import co.edu.eam.unilocal.ui.screens.PlacesListScreen
import co.edu.eam.unilocal.ui.screens.RegisterScreen
import co.edu.eam.unilocal.ui.screens.SearchScreen
import co.edu.eam.unilocal.viewmodels.AuthViewModel
import co.edu.eam.unilocal.viewmodels.AuthState
import co.edu.eam.unilocal.viewmodels.SharedPlaceViewModel
import co.edu.eam.unilocal.models.Place

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController: NavHostController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    
    // Crear una instancia única del SharedPlaceViewModel para toda la navegación
    val sharedPlaceViewModel: SharedPlaceViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = RouteScreen.Search
    ) {
        composable<RouteScreen.Search> {
            SearchScreen(
                authViewModel = authViewModel,
                sharedPlaceViewModel = sharedPlaceViewModel,
                onCrearClick = {
                    // Si está autenticado, ir directamente a crear; si no, pedir login
                    when (authState) {
                        is AuthState.Authenticated -> navController.navigate(RouteScreen.CreatePlace)
                        else -> navController.navigate(RouteScreen.Login)
                    }
                },
                onProfileClick = {
                    // Si está autenticado, ir al editor de perfil; si no, pedir login
                    when (authState) {
                        is AuthState.Authenticated -> navController.navigate(RouteScreen.EditProfile)
                        else -> navController.navigate(RouteScreen.Login)
                    }
                },
                onFavoritesClick = {
                    navController.navigate(RouteScreen.PlacesList) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onBackClick = {
                    // No hacer nada, ya estamos en la pantalla principal
                },
                onSeeAllClick = {
                    navController.navigate(RouteScreen.PlacesList) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onRequireAuth = {
                    navController.navigate(RouteScreen.Login) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onAdminClick = {
                    if (authViewModel.isUserAdmin()) {
                        navController.navigate(RouteScreen.ModerationPanel)
                    } else {
                        // No autorizado: llevar a Login o mostrar mensaje
                        navController.navigate(RouteScreen.Login) {
                            popUpTo(RouteScreen.Search) { inclusive = false }
                        }
                    }
                },
                onPlaceClick = { placeId ->
                    navController.navigate(RouteScreen.PlaceDetail)
                }
            )
        }
        
        composable<RouteScreen.Login> {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController,
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo<RouteScreen.Search> { inclusive = false }
                    }
                },
                onLoginClick = {
                    // Después del login exitoso, ir a SearchScreen
                    navController.navigate(RouteScreen.Search) {
                        popUpTo<RouteScreen.Login> { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    // TODO: Implementar recuperación de contraseña
                },
                onRegisterClick = {
                    navController.navigate(RouteScreen.Register)
                }
            )
        }
        
        composable<RouteScreen.Register> {
            RegisterScreen(
                authViewModel = authViewModel,
                navController = navController,
                onBackClick = {
                    navController.navigate(RouteScreen.Login) {
                        popUpTo(RouteScreen.Login) { inclusive = false }
                    }
                },
                onRegisterClick = {
                    // Después del registro exitoso, ir a SearchScreen
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Register) { inclusive = true }
                    }
                },
                onTermsClick = {
                    // TODO: Implementar términos y condiciones
                },
                onLoginClick = {
                    navController.navigate(RouteScreen.Login)
                }
            )
        }
        
        composable<RouteScreen.CreatePlace> {
            val coroutineScope = rememberCoroutineScope()

            CreatePlaceScreen(
                authViewModel = authViewModel,
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo<RouteScreen.Search> { inclusive = false }
                    }
                },
                onCreateClick = {
                    // Por ahora al crear volver a Search; la persistencia se maneja en ViewModel si aplica
                    navController.navigate(RouteScreen.Search) {
                        popUpTo<RouteScreen.CreatePlace> { inclusive = true }
                    }
                }
            )
        }
        
        composable<RouteScreen.EditProfile> {
            // Pasar el usuario actual para poblar los campos
            val currentUser = authViewModel.currentUser.collectAsState().value
            EditProfileScreen(
                authViewModel = authViewModel,
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo<RouteScreen.Search> { inclusive = false }
                    }
                },
                onLogoutClick = {
                    // Después de cerrar sesión, ir a SearchScreen
                    navController.navigate(RouteScreen.Search) {
                        popUpTo<RouteScreen.Search> { inclusive = false }
                    }
                }
                ,
                onFavoritesClick = {
                    navController.navigate(RouteScreen.PlacesList) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                }
            )
        }
        
        composable<RouteScreen.PlacesList> {
            PlacesListScreen(
                authViewModel = authViewModel,
                onCrearClick = {
                    // Navegación condicional: si autenticado ir a CreatePlace, si no pedir login
                    when (authState) {
                        is AuthState.Authenticated -> navController.navigate(RouteScreen.CreatePlace)
                        else -> navController.navigate(RouteScreen.Login)
                    }
                },
                onProfileClick = {
                    when (authState) {
                        is AuthState.Authenticated -> navController.navigate(RouteScreen.EditProfile)
                        else -> navController.navigate(RouteScreen.Login)
                    }
                },
                onFavoritesClick = {
                    // If user taps favorites while already on list, keep behavior. Otherwise navigate to PlacesList.
                    navController.navigate(RouteScreen.PlacesList) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onRequireAuth = {
                    navController.navigate(RouteScreen.Login) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onBackClick = {
                    navController.navigate(RouteScreen.Search)
                },
                onPlaceDetailClick = {
                    navController.navigate(RouteScreen.PlaceDetail)
                }
            )
        }
        
        composable<RouteScreen.PlaceDetail> {
            PlaceDetailScreen(
                sharedPlaceViewModel = sharedPlaceViewModel,
                authViewModel = authViewModel,
                onBackClick = {
                    // Limpiar el lugar seleccionado al volver
                    sharedPlaceViewModel.clearSelectedPlace()
                    navController.popBackStack()
                },
                onShareClick = {
                    // Lógica para compartir
                },
                onFavoriteClick = {
                    // Lógica para favoritos
                },
                onCallClick = {
                    // Lógica para llamar
                },
                onMapClick = {
                    // Lógica para abrir mapa
                }
            )
        }
        
        composable<RouteScreen.ModerationPanel> {
            ModerationPanelScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
    }
}