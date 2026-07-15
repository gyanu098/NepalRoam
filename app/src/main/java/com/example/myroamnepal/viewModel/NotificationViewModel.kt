package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import com.example.myroamnepal.model.NotificationModel
import com.example.myroamnepal.repo.NotificationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationViewModel(private val repo: NotificationRepo) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun getNotificationsForUser(userId: String) {
        _loading.value = true
        repo.getNotificationsForUser(userId) { success, data, msg ->
            _loading.value = false
            if (success) {
                _notifications.value = data
            } else {
                _message.value = msg
            }
        }
    }

    fun markAsRead(notificationId: String) {
        repo.markAsRead(notificationId) { success, msg ->
            if (success) {
                // Optionally update local list
                val updatedList = _notifications.value.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                }
                _notifications.value = updatedList
            }
        }
    }
}
