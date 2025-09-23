package co.edu.eam.unilocal.navigation

import kotlinx.serialization.Serializable

sealed class RouteScreen {

    @Serializable
    data object Search: RouteScreen()

    @Serializable
    data object Login: RouteScreen()

    @Serializable
    data object Register: RouteScreen()

    @Serializable
    data object CreatePlace: RouteScreen()

    @Serializable
    data object EditProfile: RouteScreen()
}