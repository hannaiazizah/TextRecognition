package com.hanna.textrecognition.domain.core

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    object DataFormatFailure : Failure()
    object UnknownFailure: Failure()
    class ServerFailure(val exception: Throwable) : Failure()
    object Empty : Failure()
}

object UnknownErrorResult: Throwable()