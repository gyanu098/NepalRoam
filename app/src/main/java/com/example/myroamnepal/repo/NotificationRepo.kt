package com.example.myroamnepal.repo

import com.example.myroamnepal.model.NotificationModel

interface NotificationRepo {
    fun addNotification(notification: NotificationModel, callback: (Boolean, String) -> Unit)
    fun getNotificationsForUser(userId: String, callback: (Boolean, List<NotificationModel>, String) -> Unit)
    fun markAsRead(notificationId: String, callback: (Boolean, String) -> Unit)
}
