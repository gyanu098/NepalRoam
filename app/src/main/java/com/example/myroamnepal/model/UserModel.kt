package com.example.myroamnepal.model

data class UserModel(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "user",
    val profileImageUrl: String = "",
    val favorites: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "role" to role,
            "profileImageUrl" to profileImageUrl,
            "favorites" to favorites
        )
    }
}