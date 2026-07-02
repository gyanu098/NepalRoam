package com.example.myroamnepal

import android.app.Application
import com.cloudinary.android.MediaManager

class MyRoamNepalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "dkvkhar37", // Removed the leading space
            "api_key" to "577172215816845",
            "api_secret" to "52WBQ7oGCHKhaVYyFoGy7Nf1eIk"
        )
        MediaManager.init(this, config)
    }
}
