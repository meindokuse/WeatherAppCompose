package com.example.yourweather.models

sealed class AppAction{
    object FirstEnter:AppAction()
    object UpdateData:AppAction()
    object UpdateLocation:AppAction()
    object UpdateAll:AppAction()
    data class AddLocation(
        val newLocation:String
    ):AppAction()
    data class DeleteLocation(
        val currentLocation:String
    ):AppAction()

}



