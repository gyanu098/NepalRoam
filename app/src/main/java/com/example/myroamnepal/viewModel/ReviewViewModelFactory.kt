package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myroamnepal.repo.ReviewRepo

class ReviewViewModelFactory(private val repo: ReviewRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
