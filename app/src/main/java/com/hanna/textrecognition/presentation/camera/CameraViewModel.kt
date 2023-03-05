package com.hanna.textrecognition.presentation.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.hanna.textrecognition.data.core.Either
import com.hanna.textrecognition.data.core.Failure
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _imageUri by lazy { MutableStateFlow<Uri?>(null) }
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _visionResult by lazy { MutableStateFlow<Either<Failure, Text?>>(Either.success(null)) }
    val visionResult: StateFlow<Either<Failure, Text?>> = _visionResult

    private val _isLoading by lazy { MutableSharedFlow<Boolean>() }
    val isLoading: SharedFlow<Boolean> = _isLoading

    private val _shouldNavigate by lazy { MutableSharedFlow<Boolean>() }
    val shouldNavigate: SharedFlow<Boolean> = _shouldNavigate

    fun setImageUri(uri: Uri?) {
        viewModelScope.launch {
            _isLoading.emit(true)
            _imageUri.emit(uri)
            _isLoading.emit(false)
        }
    }

    fun runTextRecognition(image: InputImage) {
        viewModelScope.launch {
            _isLoading.emit(true)
            callbackFlow<Either<Failure, Text>> {
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { texts ->
                        trySend(Either.success(texts))
                    }
                    .addOnFailureListener { e -> // Task failed with an exception
                        trySend(Either.fail(Failure.RecognitionFailure))
                    }

                awaitClose { recognizer.close() }
            }.collectLatest {
                _isLoading.emit(false)
                _visionResult.emit(it)
                _shouldNavigate.emit(true)
            }
        }
    }
}