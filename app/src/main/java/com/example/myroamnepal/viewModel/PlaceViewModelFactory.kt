package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myroamnepal.repo.PlaceRepo

class PlaceViewModelFactory(private val repo: PlaceRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
