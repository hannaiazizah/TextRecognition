package com.hanna.textrecognition.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.asFlow(): Flow<CharSequence?> {
    return callbackFlow {
        val watcher = doAfterTextChanged {
            trySend(it)
        }
        awaitClose { removeTextChangedListener(watcher) }
    }.onStart { emit(text) }
}