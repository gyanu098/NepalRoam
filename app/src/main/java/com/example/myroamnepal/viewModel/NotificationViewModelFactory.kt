package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myroamnepal.repo.NotificationRepo

class NotificationViewModelFactory(private val repo: NotificationRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
