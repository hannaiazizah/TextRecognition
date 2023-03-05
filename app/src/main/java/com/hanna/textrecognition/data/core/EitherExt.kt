package com.hanna.textrecognition.data.core

suspend fun <E, S, S2> Either<E, S>.carryOn(
    nextBlock: suspend (S) -> Either<E, S2>
): Either<E, S2> {
    return when (this) {
        is Either.Fail -> this
        is Either.Success -> nextBlock(value)
    }
}

fun <F, S> Either<F, S>.onSuccess(
    block: (S) -> Unit
): Either<F, S> {
    if (this is Either.Success) block.invoke(this.value)
    return this
}

fun <F, S> Either<F, S>.onFailure(
    block: (F) -> Unit
): Either<F, S> {
    if (this is Either.Fail) block.invoke(this.value)
    return this
}
