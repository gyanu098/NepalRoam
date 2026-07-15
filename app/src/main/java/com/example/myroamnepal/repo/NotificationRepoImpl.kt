package com.example.myroamnepal.repo

import com.example.myroamnepal.model.NotificationModel
import com.google.firebase.database.*

class NotificationRepoImpl : NotificationRepo {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val notificationRef = database.getReference("notifications")

    override fun addNotification(notification: NotificationModel, callback: (Boolean, String) -> Unit) {
        val id = notificationRef.push().key ?: return callback(false, "Failed to generate ID")
        val newNotification = notification.copy(id = id)
        notificationRef.child(id).setValue(newNotification).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Notification sent")
            } else {
                callback(false, it.exception?.message ?: "Failed to send notification")
            }
        }
    }

    override fun getNotificationsForUser(userId: String, callback: (Boolean, List<NotificationModel>, String) -> Unit) {
        notificationRef.orderByChild("toUserId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<NotificationModel>()
                    for (notifSnapshot in snapshot.children) {
                        val notif = notifSnapshot.getValue(NotificationModel::class.java)
                        if (notif != null) notifications.add(notif)
                    }
                    // Sort by timestamp descending (newest first)
                    notifications.sortByDescending { it.timestamp }
                    callback(true, notifications, "Success")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, emptyList(), error.message)
                }
            })
    }

    override fun markAsRead(notificationId: String, callback: (Boolean, String) -> Unit) {
        notificationRef.child(notificationId).child("isRead").setValue(true).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Marked as read")
            } else {
                callback(false, it.exception?.message ?: "Failed to update notification")
            }
        }
    }
}
