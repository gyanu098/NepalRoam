package com.example.myroamnepal.model

data class NotificationModel(
    val id: String = "",
    val toUserId: String = "",     // The owner of the place
    val fromUserId: String = "",   // The user who wrote the review
    val fromUserName: String = "",
    val placeId: String = "",
    val placeName: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "toUserId" to toUserId,
            "fromUserId" to fromUserId,
            "fromUserName" to fromUserName,
            "placeId" to placeId,
            "placeName" to placeName,
            "title" to title,
            "message" to message,
            "timestamp" to timestamp,
            "isRead" to isRead
        )
    }
}
