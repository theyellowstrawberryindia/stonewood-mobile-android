package uk.co.savills.stonewood.screen.projectlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.NavigationGraphDirections
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.NotificationService
import uk.co.savills.stonewood.util.Result

class ProjectListViewModel(application: Application) : BaseViewModel(application) {

    private val projectRepository
        get() = appContainer.projectRepository

    private val _projects = MutableLiveData<List<ProjectModel>>()
    val projects: LiveData<List<ProjectModel>>
        get() = _projects

    private val _isRefreshingProjects = MutableLiveData<Boolean>()
    val isRefreshingProjects: LiveData<Boolean>
        get() = _isRefreshingProjects

    val appVersion = BuildConfig.VERSION_NAME

    init {
        getProjects()
    }

    private fun getProjects() {
        _isBusy.value = true

        viewModelScope.launch(Dispatchers.IO) {
            _projects.postValue(projectRepository.getProjects())
        }.invokeOnCompletion {
            _isBusy.postValue(false)
        }
    }

    fun refreshProjects() {
        _isRefreshingProjects.value = true

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = apiService.getProjects()) {
                is Result.Success -> {
                    onProjectsFetched(result.data)
                }
                is Result.Error -> handleApiCallError(result.exception)
                else -> {}
            }
        }.invokeOnCompletion {
            _isRefreshingProjects.postValue(false)
        }
    }

    private fun onProjectsFetched(projects: List<ProjectModel>) {
        val openProjects = mutableListOf<ProjectModel>()
        val closedProjects = mutableListOf<ProjectModel>()

        projects.forEach { project ->
            if (project.isClosed) {
                closedProjects.add(project)
            } else {
                openProjects.add(project)
            }
        }

        appContainer.clearProjectData(closedProjects)

        projectRepository.insertProjects(openProjects)
        _projects.postValue(openProjects)
    }

    fun onProjectSelected(project: ProjectModel) {
        if (isBusy.value == true) return

        appState.currentProject = project
        appContainer.eventReportingService.reportProjectSelected()

        navigateToProjectScreen()
    }

    fun logout() {
        makeApiCall({ apiService.logout() }) {
            appState.clear()
            NotificationService.unregister(application)

            navigateToLoginScreen()
        }
    }

    private fun navigateToProjectScreen() {
        navigateTo(ProjectListFragmentDirections.moveToProjectScreen())
    }

    private fun navigateToLoginScreen() {
        navigateTo(NavigationGraphDirections.moveToLoginFragment())
    }
}
