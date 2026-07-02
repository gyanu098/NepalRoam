package com.example.myroamnepal.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myroamnepal.repo.ImageRepo

class ImageViewModel(val repo: ImageRepo) : ViewModel() {

    fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        repo.uploadImage(context, imageUri, callback)
    }
}
