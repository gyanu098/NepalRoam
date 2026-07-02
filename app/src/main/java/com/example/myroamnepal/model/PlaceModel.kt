package com.example.myroamnepal.model

data class PlaceModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val tips: String = "",
    val bestSeason: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val uploadedBy: String = "", // User UID
    val uploadedByName: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "tips" to tips,
            "bestSeason" to bestSeason,
            "location" to location,
            "imageUrl" to imageUrl,
            "uploadedBy" to uploadedBy,
            "uploadedByName" to uploadedByName
        )
    }
}
