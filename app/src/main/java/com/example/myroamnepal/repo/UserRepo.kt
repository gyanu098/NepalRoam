package com.example.myroamnepal.repo

import com.example.myroamnepal.model.UserModel

interface UserRepo {
    fun sendPasswordResetEmail(email: String, callback: (Boolean, String) -> Unit)
    fun logOut(callback: (Boolean, String) -> Unit)
    fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit)
    fun login(email: String, password: String, callback: (Boolean, String, UserModel?) -> Unit)
    fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String) -> Unit)
    fun updateUser(userModel: UserModel, callback: (Boolean, String) -> Unit)
    fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit)
    fun rollbackCurrentUserRegistration()
    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit)
    fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit)
    fun deleteUser(uid: String, callback: (Boolean, String) -> Unit)
    fun toggleFavorite(userId: String, placeId: String, callback: (Boolean, String) -> Unit)
}