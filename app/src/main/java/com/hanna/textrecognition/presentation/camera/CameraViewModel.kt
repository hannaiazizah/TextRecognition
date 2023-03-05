package com.hanna.textrecognition.presentation.camera

import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.hanna.textrecognition.domain.core.Either
import com.hanna.textrecognition.domain.core.Failure
import com.hanna.textrecognition.domain.core.FlowUseCase
import com.hanna.textrecognition.domain.usecase.GetLastLocationUseCase
import com.hanna.textrecognition.domain.usecase.TextRecognitionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val textRecognitionUseCase: TextRecognitionUseCase,
    private val getLastLocationUseCase: GetLastLocationUseCase
) : ViewModel() {

    private val _imageUri by lazy { MutableStateFlow<Uri?>(null) }
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _visionResult by lazy { MutableStateFlow<Either<Failure, Text?>>(Either.success(null)) }
    val visionResult: StateFlow<Either<Failure, Text?>> = _visionResult

    private val _locationResult by lazy { MutableStateFlow<Either<Failure, Location?>>(Either.success(null)) }
    val locationResult: StateFlow<Either<Failure, Location?>> = _locationResult

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
            val params = TextRecognitionUseCase.Params(image)

            val textVisionFlow = textRecognitionUseCase.run(params)
            val lastLocationFlow = getLastLocationUseCase.run(FlowUseCase.None())

            combine(textVisionFlow, lastLocationFlow) { text, location ->
                Pair(text, location)
            }.collect {
                _isLoading.emit(false)
                _shouldNavigate.emit(true)
                _visionResult.emit(it.first)
                _locationResult.emit(it.second)
            }
        }
    }
}