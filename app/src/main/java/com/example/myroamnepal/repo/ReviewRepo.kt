package com.example.myroamnepal.repo

import com.example.myroamnepal.model.ReviewModel

interface ReviewRepo {
    fun addReview(review: ReviewModel, callback: (Boolean, String) -> Unit)
    fun getReviewsByPlace(placeId: String, callback: (Boolean, List<ReviewModel>, String) -> Unit)
    fun getAverageRating(placeId: String, callback: (Float) -> Unit)
}
