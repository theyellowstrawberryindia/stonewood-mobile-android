package uk.co.savills.stonewood.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T : Any> LiveData<T>.getNonNullValue() = requireNotNull(value)

fun <T : Any> MutableLiveData<T>.notify() {
    value = value
}
