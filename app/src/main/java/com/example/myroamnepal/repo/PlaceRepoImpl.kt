package com.example.myroamnepal.repo

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myroamnepal.model.PlaceModel
import com.google.firebase.database.*

class PlaceRepoImpl(private val context: Context) : PlaceRepo {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val placeRef = database.getReference("places")

    override fun uploadImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val uploadPreset = "yxqloe0j"

        MediaManager.get().upload(imageUri)
            .unsigned(uploadPreset)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"] as? String
                    callback(true, imageUrl)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    callback(false, error.description)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }

    override fun addPlace(place: PlaceModel, callback: (Boolean, String) -> Unit) {
        val id = placeRef.push().key ?: return callback(false, "Failed to generate ID")
        val newPlace = place.copy(id = id)
        placeRef.child(id).setValue(newPlace).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Place added successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to add place")
            }
        }
    }

    override fun getAllPlaces(callback: (Boolean, List<PlaceModel>, String) -> Unit) {
        placeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val places = mutableListOf<PlaceModel>()
                for (placeSnapshot in snapshot.children) {
                    val place = placeSnapshot.getValue(PlaceModel::class.java)
                    if (place != null) places.add(place)
                }
                callback(true, places, "Success")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList(), error.message)
            }
        })
    }

    override fun getPlacesByUser(userId: String, callback: (Boolean, List<PlaceModel>, String) -> Unit) {
        placeRef.orderByChild("uploadedBy").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val places = mutableListOf<PlaceModel>()
                    for (placeSnapshot in snapshot.children) {
                        val place = placeSnapshot.getValue(PlaceModel::class.java)
                        if (place != null) places.add(place)
                    }
                    callback(true, places, "Success")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, emptyList(), error.message)
                }
            })
    }

    override fun getPlaceById(id: String, callback: (Boolean, PlaceModel?, String) -> Unit) {
        placeRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val place = snapshot.getValue(PlaceModel::class.java)
                callback(true, place, "Success")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null, error.message)
            }
        })
    }

    override fun updatePlace(place: PlaceModel, callback: (Boolean, String) -> Unit) {
        placeRef.child(place.id).setValue(place).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Place updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to update place")
            }
        }
    }

    override fun deletePlace(id: String, callback: (Boolean, String) -> Unit) {
        placeRef.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Place deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to delete place")
            }
        }
    }
}
