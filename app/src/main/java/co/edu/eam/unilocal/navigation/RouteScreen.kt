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

    @Serializable
    data object PlacesList: RouteScreen()

    @Serializable
    data object PlaceDetail: RouteScreen()

    @Serializable
    data object ModerationPanel: RouteScreen()

    @Serializable
    data class ModerationPlaceDetail(val placeId: String) : RouteScreen()

    @Serializable
    data object ForgotPassword: RouteScreen()

    @Serializable
    data object LocationPicker: RouteScreen()

    @Serializable
    data object PlaceMap: RouteScreen()
}