package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import com.example.myroamnepal.model.NotificationModel
import com.example.myroamnepal.model.ReviewModel
import com.example.myroamnepal.repo.NotificationRepo
import com.example.myroamnepal.repo.PlaceRepo
import com.example.myroamnepal.repo.ReviewRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReviewViewModel(
    private val repo: ReviewRepo,
    private val placeRepo: PlaceRepo? = null,
    private val notificationRepo: NotificationRepo? = null
) : ViewModel() {

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
            if (success) {
                // Refresh average rating after adding
                getAverageRating(review.placeId)
                
                // Send notification to the place owner
                sendNotificationToOwner(review)
            }
            _loading.value = false
            _message.value = msg
        }
    }

    private fun sendNotificationToOwner(review: ReviewModel) {
        if (placeRepo == null || notificationRepo == null) return

        placeRepo.getPlaceById(review.placeId) { success, place, _ ->
            if (success && place != null) {
                // Don't notify if the owner is reviewing their own place
                if (place.uploadedBy == review.userId) return@getPlaceById

                val notification = NotificationModel(
                    toUserId = place.uploadedBy,
                    fromUserId = review.userId,
                    fromUserName = review.userName,
                    placeId = place.id,
                    placeName = place.name,
                    title = "New Review",
                    message = "${review.userName} rated '${place.name}' ${review.rating} stars.",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                notificationRepo.addNotification(notification) { _, _ ->
                    // Notification sent
                }
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
