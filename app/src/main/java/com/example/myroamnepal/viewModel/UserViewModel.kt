package com.example.myroamnepal.viewModel

import androidx.lifecycle.ViewModel
import com.example.myroamnepal.model.UserModel
import com.example.myroamnepal.repo.UserRepo
import com.example.myroamnepal.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(private val repo: UserRepo = UserRepoImpl()) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val allUsers: StateFlow<List<UserModel>> = _allUsers.asStateFlow()


    fun clearMessage() {
        _message.value = null
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _message.value = "Please fill all fields"
            return
        }
        _loading.value = true
        repo.login(email.trim(), password) { success, msg, userData ->
            _loading.value = false
            _message.value = msg
            if (success && userData != null) {
                _user.value = userData as UserModel?
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _message.value = "Please enter email"
            return
        }
        _loading.value = true
        repo.sendPasswordResetEmail(email.trim()) { _, msg ->
            _loading.value = false
            _message.value = msg
        }
    }

    fun logOut() {
        _loading.value = true
        repo.logOut { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) {
                _user.value = null
                _isLoggedOut.value = true
            }
        }
    }

    fun loadCurrentUser() {
        repo.getCurrentUser { success, userData ->
            if (success && userData != null) {
                _user.value = userData as UserModel?
            }
        }
    }

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        role: String = "user",
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _message.value = "Please fill all fields"
            return
        }

        if (password != confirmPassword) {
            _message.value = "Passwords do not match"
            return
        }

        repo.register(email, password) { success, message, uid ->
            if (success) {
                val user = UserModel(
                    uid = uid,
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    role = role
                )

                repo.addUser(uid, user) { addSuccess, addMessage ->
                    if (addSuccess) {
                        _message.value = "Signup Successful"
                        onSuccess()
                    } else {
                        repo.rollbackCurrentUserRegistration()
                        _message.value = addMessage
                    }
                }
            } else {
                _message.value = if (message.contains("already", ignoreCase = true)) {
                    "Email already in use."
                } else {
                    message
                }
            }
        }
    }

    fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUser(id, model, callback)
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _message.value = "All fields are required"
            return
        }

        if (newPassword.length < 6) {
            _message.value = "Password must be at least 6 characters"
            return
        }

        if (newPassword != confirmPassword) {
            _message.value = "New Password and Confirm Password do not match"
            return
        }

        if (oldPassword == newPassword) {
            _message.value = "New password cannot be same as old password"
            return
        }

        _loading.value = true
        repo.changePassword(oldPassword, newPassword) { success, msg ->
            _loading.value = false
            _message.value = msg
        }
    }

    fun updateUser(uid: String, fullName: String, phone: String, profileImageUrl: String) {
        if (fullName.isBlank() || phone.isBlank()) {
            _message.value = "All fields are required"
            return
        }

        _loading.value = true
        repo.updateUser(UserModel(uid = uid, fullName = fullName, phone = phone, profileImageUrl = profileImageUrl)) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) {
                loadCurrentUser() // Refresh local user data
            }
        }
    }

    fun toggleFavorite(placeId: String) {
        val currentUser = _user.value ?: return
        repo.toggleFavorite(currentUser.uid, placeId) { success, msg ->
            if (success) {
                loadCurrentUser() // Refresh user data to get updated favorites list
            }
            _message.value = msg
        }
    }

    fun getAllUsers() {
        repo.getAllUsers { success, msg, users ->
            if (success) {
                _allUsers.value = users
            } else {
                _message.value = msg
            }
        }
    }

    fun deleteUser(
        uid: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteUser(uid) { success, message ->
            callback(success, message)
            if (success) {
                getAllUsers()
            }
        }
    }
}
