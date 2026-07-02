package com.example.myroamnepal.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myroamnepal.model.PlaceModel
import com.example.myroamnepal.repo.PlaceRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaceViewModel(private val repo: PlaceRepo) : ViewModel() {

    private val _places = MutableStateFlow<List<PlaceModel>>(emptyList())
    val places: StateFlow<List<PlaceModel>> = _places.asStateFlow()

    private val _userPlaces = MutableStateFlow<List<PlaceModel>>(emptyList())
    val userPlaces: StateFlow<List<PlaceModel>> = _userPlaces.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun getAllPlaces() {
        _loading.value = true
        repo.getAllPlaces { success, data, msg ->
            _loading.value = false
            if (success) {
                _places.value = data
            } else {
                _message.value = msg
            }
        }
    }

    fun getPlacesByUser(userId: String) {
        _loading.value = true
        repo.getPlacesByUser(userId) { success, data, msg ->
            _loading.value = false
            if (success) {
                _userPlaces.value = data
            } else {
                _message.value = msg
            }
        }
    }

    fun addPlace(
        name: String,
        description: String,
        tips: String,
        bestSeason: String,
        location: String,
        imageUri: Uri?,
        uploadedBy: String,
        uploadedByName: String
    ) {
        if (name.isBlank() || description.isBlank() || location.isBlank() || imageUri == null) {
            _message.value = "Please fill name, description, location and select an image"
            return
        }

        _loading.value = true
        repo.uploadImage(imageUri) { success, imageUrl ->
            if (success && imageUrl != null) {
                val place = PlaceModel(
                    name = name,
                    description = description,
                    tips = tips,
                    bestSeason = bestSeason,
                    location = location,
                    imageUrl = imageUrl,
                    uploadedBy = uploadedBy,
                    uploadedByName = uploadedByName
                )
                repo.addPlace(place) { addSuccess, msg ->
                    _loading.value = false
                    _message.value = msg
                }
            } else {
                _loading.value = false
                _message.value = imageUrl ?: "Image upload failed"
            }
        }
    }
}
