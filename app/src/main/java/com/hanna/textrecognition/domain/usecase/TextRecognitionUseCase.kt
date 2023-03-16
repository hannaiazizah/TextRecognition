package com.hanna.textrecognition.domain.usecase

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.hanna.textrecognition.domain.core.FlowUseCase
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TextRecognitionUseCase @Inject constructor(): FlowUseCase<Text, TextRecognitionUseCase.Params>() {
    data class Params(
        val image: InputImage
    )

    override suspend fun run(params: Params): Flow<Text?> {
        return callbackFlow {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(params.image)
                .addOnSuccessListener { texts ->
                    trySend(texts)
                }
                .addOnFailureListener { // Task failed with an exception
                    trySend(null)
                }

            awaitClose { recognizer.close() }
        }
    }
}