package com.example.yourweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yourweather.database.ReposetoryHelper

class ScreenStoreFactory(private val repository: ReposetoryHelper): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScreenStore::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScreenStore(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }
}