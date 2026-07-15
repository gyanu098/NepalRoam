package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myroamnepal.repo.NotificationRepo
import com.example.myroamnepal.repo.PlaceRepo
import com.example.myroamnepal.repo.ReviewRepo

class ReviewViewModelFactory(
    private val repo: ReviewRepo,
    private val placeRepo: PlaceRepo? = null,
    private val notificationRepo: NotificationRepo? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repo, placeRepo, notificationRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
