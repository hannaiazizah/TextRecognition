package com.hanna.textrecognition.data.model


sealed class ResultState<out R> {

    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val exception: Exception) : ResultState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

    fun onSuccess(
        block: (R) -> Unit
    ): ResultState<R> {
        if (this is Success) block.invoke(this.data)
        return this
    }

    fun onError(
        block: (Exception) -> Unit
    ): ResultState<R> {
        if (this is Error) block.invoke(this.exception)
        return this
    }

}

fun <T> ResultState<T>.isSuccess(): Boolean {
    return this is ResultState.Success
}

inline fun <T, R> ResultState<T>.map(transform: (T) -> R): ResultState<R> {
    return when (this) {
        is ResultState.Success -> {
            ResultState.Success(transform.invoke(this.data))
        }
        is ResultState.Error -> {
            ResultState.Error(this.exception)
        }
    }
}