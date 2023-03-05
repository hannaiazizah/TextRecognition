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
import com.hanna.textrecognition.domain.core.onSuccess
import com.hanna.textrecognition.domain.model.DistanceUiModel
import com.hanna.textrecognition.domain.usecase.CalculateDistanceUseCase
import com.hanna.textrecognition.domain.usecase.GetLastLocationUseCase
import com.hanna.textrecognition.domain.usecase.PostDataUseCase
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
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val calculateDistanceUseCase: CalculateDistanceUseCase,
    private val postDataUseCase: PostDataUseCase
) : ViewModel() {

    private val _imageUri by lazy { MutableStateFlow<Uri?>(null) }
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _visionResult by lazy { MutableStateFlow<Either<Failure, Text?>>(Either.success(null)) }
    val visionResult: StateFlow<Either<Failure, Text?>> = _visionResult

    private val _locationResult by lazy {
        MutableStateFlow<Either<Failure, Location?>>(Either.success(null))
    }
    val locationResult: StateFlow<Either<Failure, Location?>> = _locationResult

    private val _distanceResult by lazy {
        MutableStateFlow<Either<Failure, DistanceUiModel>>(Either.fail(Failure.Empty))
    }
    val distanceResult: StateFlow<Either<Failure, DistanceUiModel>> = _distanceResult

    private val _isLoading by lazy { MutableSharedFlow<Boolean>() }
    val isLoading: SharedFlow<Boolean> = _isLoading

    private val _shouldNavigate by lazy { MutableSharedFlow<Either<Failure, Boolean>>() }
    val shouldNavigate: SharedFlow<Either<Failure, Boolean>> = _shouldNavigate

    private var dataUri: Uri? = null
    private var dataText: Text? = null
    private var dataLocation: Location? = null
    private var dataDistance: DistanceUiModel? = null

    fun setImageUri(uri: Uri?) {
        viewModelScope.launch {
            dataUri = uri
            _isLoading.emit(true)
            _imageUri.emit(uri)
            _isLoading.emit(false)
        }
    }

    // run text recognition
    // request current location
    // calculate distance and duration
    // submit data
    fun fetchImageAttributes(image: InputImage) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val recognitionParams = TextRecognitionUseCase.Params(image)

            val textVisionFlow = textRecognitionUseCase.run(recognitionParams)
            val lastLocationFlow = getLastLocationUseCase.run(FlowUseCase.None())

            combine(textVisionFlow, lastLocationFlow) { text, location ->
                Pair(text, location)
            }.collect {
                _visionResult.emit(it.first)
                _locationResult.emit(it.second)
                _isLoading.emit(false)
                it.second.onSuccess { location ->
                    dataLocation = location
                    calculateDistanceMatrix(location)
                }
                it.first.onSuccess { text ->
                    dataText = text
                }
            }
        }
    }

    private fun calculateDistanceMatrix(location: Location) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val params = CalculateDistanceUseCase.Params(
                location.latitude,
                location.longitude
            )
            val result = calculateDistanceUseCase.run(params)
            result.onSuccess {
                dataDistance = it
            }
            _distanceResult.emit(result)
            _isLoading.emit(false)
            submitData()
        }
    }

    private fun submitData() {
        viewModelScope.launch {
            _isLoading.emit(true)
            val params = PostDataUseCase.Params(
                imageUri = dataUri,
                imageText = dataText,
                latitude = dataLocation?.latitude,
                longitude = dataLocation?.longitude,
                distance = dataDistance?.distance,
                duration = dataDistance?.estimatedTime
            )

            val result = postDataUseCase.run(params)
            _shouldNavigate.emit(result)
            _isLoading.emit(false)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            dataDistance = null
            dataUri = null
            dataText = null
            dataLocation = null
            _shouldNavigate.emit(Either.success(false))
        }
    }
}