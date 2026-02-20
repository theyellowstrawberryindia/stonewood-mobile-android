package uk.co.savills.stonewood.screen.survey.survey.energy.selectexternalphoto

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.screen.base.BaseViewModel

class SelectExternalPhotoViewModel(
    application: Application,
    private val selectionListener: (ExtBlockPhotoModel) -> Unit
) : BaseViewModel(application) {
    private val _data = MutableLiveData<List<ExtBlockPhotoModel>>()
    val data: LiveData<List<ExtBlockPhotoModel>>
        get() = _data

    var searchText: String = ""
        set(value) {
            field = value
            onSearchTextChanged()
        }

    lateinit var searchJob: Job

    private val repository
        get() = appContainer.externalPhotosRepository

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _data.postValue(repository.getEntries(appState.currentProject.id))
        }
    }

    private fun onSearchTextChanged() {
        viewModelScope.launch(Dispatchers.IO) {
            if (::searchJob.isInitialized) searchJob.cancelAndJoin()

            searchJob = launch(Dispatchers.IO) {
                if (::searchJob.isInitialized && searchJob.isActive) {
                    _data.postValue(
                        repository.getEntries(appState.currentProject.id, searchText)
                    )
                }
            }
        }
    }

    fun onCommunalDataSelected(data: ExtBlockPhotoModel) = selectionListener.invoke(data)
}
