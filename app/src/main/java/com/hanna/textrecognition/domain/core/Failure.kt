package com.hanna.textrecognition.domain.core

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    object NetworkConnection : Failure()
    object UnknownError: Failure()
    object RecognitionFailure: Failure()
    object PermissionFailure: Failure()
    object LocationFailure: Failure()
    class ServerError(val exception: Exception) : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}