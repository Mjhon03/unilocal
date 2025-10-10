package co.edu.eam.unilocal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    // Obtener estado de autenticación para decidir navegación condicional
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = RouteScreen.Search
    ) {
        composable<RouteScreen.Search> {
            SearchScreen(
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
                    navController.navigate(RouteScreen.PlacesList)
                },
                onBackClick = {
                    // No hacer nada, ya estamos en la pantalla principal
                }
            )
        }
        
        composable<RouteScreen.Login> {
            LoginScreen(
                navController = navController,
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onLoginClick = {
                    // Después del login exitoso, ir a SearchScreen
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Login) { inclusive = true }
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
            CreatePlaceScreen(
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                },
                onCreateClick = {
                    // Después de crear el lugar, volver a SearchScreen
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.CreatePlace) { inclusive = true }
                    }
                }
            )
        }
        
        composable<RouteScreen.EditProfile> {
            EditProfileScreen(
                onBackClick = {
                    navController.navigate(RouteScreen.Search) {
                        popUpTo(RouteScreen.Search) { inclusive = false }
                    }
                }
            )
        }
        
        composable<RouteScreen.PlacesList> {
            PlacesListScreen(
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
                    navController.navigate(RouteScreen.PlacesList)
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
                onBackClick = {
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
                },
                onWriteReviewClick = {
                    // Lógica para escribir reseña
                },
                onSeeAllEventsClick = {
                    // Lógica para ver todos los eventos
                }
            )
        }
        
        composable<RouteScreen.ModerationPanel> {
            ModerationPanelScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onApprovePlace = { placeId ->
                    // Lógica para aprobar lugar
                    println("Aprobando lugar: $placeId")
                },
                onRejectPlace = { placeId ->
                    // Lógica para rechazar lugar
                    println("Rechazando lugar: $placeId")
                }
            )
        }
        
    }
}
