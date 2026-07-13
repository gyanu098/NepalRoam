package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myroamnepal.repo.ImageRepo

class ImageViewModelFactory(private val repo: ImageRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
