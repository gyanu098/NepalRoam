package com.example.myroamnepal.repo

import android.net.Uri
import com.example.myroamnepal.model.PlaceModel

interface PlaceRepo {
    fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit)
    fun addPlace(place: PlaceModel, callback: (Boolean, String) -> Unit)
    fun getAllPlaces(callback: (Boolean, List<PlaceModel>, String) -> Unit)
    fun getPlacesByUser(userId: String, callback: (Boolean, List<PlaceModel>, String) -> Unit)
    fun getPlaceById(id: String, callback: (Boolean, PlaceModel?, String) -> Unit)
}
