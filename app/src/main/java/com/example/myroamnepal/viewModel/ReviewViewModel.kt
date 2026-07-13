package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import com.example.myroamnepal.model.ReviewModel
import com.example.myroamnepal.repo.ReviewRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReviewViewModel(private val repo: ReviewRepo) : ViewModel() {

    private val _reviews = MutableStateFlow<List<ReviewModel>>(emptyList())
    val reviews: StateFlow<List<ReviewModel>> = _reviews.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating: StateFlow<Float> = _averageRating.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun addReview(review: ReviewModel) {
        if (review.rating == 0.0) {
            _message.value = "Please provide a rating"
            return
        }
        _loading.value = true
        repo.addReview(review) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) {
                // Refresh average rating after adding
                getAverageRating(review.placeId)
            }
        }
    }

    fun getReviewsByPlace(placeId: String) {
        _loading.value = true
        repo.getReviewsByPlace(placeId) { success, data, msg ->
            _loading.value = false
            if (success) {
                _reviews.value = data
            } else {
                _message.value = msg
            }
        }
    }

    fun getAverageRating(placeId: String) {
        repo.getAverageRating(placeId) { average ->
            _averageRating.value = average
        }
    }
}
