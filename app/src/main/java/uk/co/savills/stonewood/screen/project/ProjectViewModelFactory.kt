package uk.co.savills.stonewood.screen.project

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.co.savills.stonewood.util.LocationTracker

@Suppress("UNCHECKED_CAST")
class ProjectViewModelFactory(
    private val application: Application,
    private val locationTracker: LocationTracker
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProjectViewModel::class.java) -> {
                ProjectViewModel(application, locationTracker) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}