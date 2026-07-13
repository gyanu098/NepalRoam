package com.example.myroamnepal.repo

import com.example.myroamnepal.model.ReviewModel
import com.google.firebase.database.*

class ReviewRepoImpl : ReviewRepo {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reviewRef = database.getReference("reviews")

    override fun addReview(review: ReviewModel, callback: (Boolean, String) -> Unit) {
        val id = reviewRef.push().key ?: return callback(false, "Failed to generate ID")
        val newReview = review.copy(id = id)
        reviewRef.child(id).setValue(newReview).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Review added successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to add review")
            }
        }
    }

    override fun getReviewsByPlace(placeId: String, callback: (Boolean, List<ReviewModel>, String) -> Unit) {
        reviewRef.orderByChild("placeId").equalTo(placeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reviews = mutableListOf<ReviewModel>()
                    for (reviewSnapshot in snapshot.children) {
                        val review = reviewSnapshot.getValue(ReviewModel::class.java)
                        if (review != null) reviews.add(review)
                    }
                    callback(true, reviews, "Success")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, emptyList(), error.message)
                }
            })
    }

    override fun getAverageRating(placeId: String, callback: (Float) -> Unit) {
        reviewRef.orderByChild("placeId").equalTo(placeId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalRating = 0f
                    var count = 0
                    for (reviewSnapshot in snapshot.children) {
                        val rating = reviewSnapshot.child("rating").getValue(Float::class.java) ?: 0f
                        totalRating += rating
                        count++
                    }
                    val average = if (count > 0) totalRating / count else 0f
                    callback(average)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(0f)
                }
            })
    }
}
