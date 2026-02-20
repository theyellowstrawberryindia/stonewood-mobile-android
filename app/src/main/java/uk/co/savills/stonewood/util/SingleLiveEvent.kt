package uk.co.savills.stonewood.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

open class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(
            owner,
            {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(it)
                }
            }
        )
    }

    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    override fun postValue(value: T?) {
        pending.set(true)
        super.postValue(value)
    }

    fun call() {
        postValue(null)
    }
}
