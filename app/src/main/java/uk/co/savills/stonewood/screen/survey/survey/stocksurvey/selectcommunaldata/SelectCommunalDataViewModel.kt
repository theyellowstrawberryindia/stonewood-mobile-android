package uk.co.savills.stonewood.screen.survey.survey.stocksurvey.selectcommunaldata

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.screen.base.BaseViewModel

class SelectCommunalDataViewModel(
    application: Application,
    val elementTitle: String,
    private val selectionListener: (CommunalDataModel) -> Unit
) : BaseViewModel(application) {

    private val _data = MutableLiveData<List<CommunalDataModel>>()
    val data: LiveData<List<CommunalDataModel>>
        get() = _data

    var searchText: String = ""
        set(value) {
            field = value
            onSearchTextChanged()
        }

    lateinit var searchJob: Job

    private val repository
        get() = appContainer.communalDataRepository

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _data.postValue(repository.getEntries(elementTitle, appState.currentProject.id))
        }
    }

    private fun onSearchTextChanged() {
        viewModelScope.launch(Dispatchers.IO) {
            if (::searchJob.isInitialized) searchJob.cancelAndJoin()

            searchJob = launch(Dispatchers.IO) {
                if (::searchJob.isInitialized && searchJob.isActive) {
                    _data.postValue(
                        repository.getEntries(elementTitle, appState.currentProject.id, searchText)
                    )
                }
            }
        }
    }

    fun onCommunalDataSelected(data: CommunalDataModel) = selectionListener.invoke(data)
}
