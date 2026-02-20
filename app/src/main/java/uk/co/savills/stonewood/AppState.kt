package uk.co.savills.stonewood

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.UserModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.storage.KeyValueStore

class AppState(private val keyValueStore: KeyValueStore) {
    val isLoggedIn: Boolean get() = profile != null

    private var _profile: UserModel? = get(PROFILE_KEY)
    var profile
        get() = _profile
        set(value) {
            _profile = value
            set(PROFILE_KEY, value)
        }

    lateinit var currentProperty: PropertyModel

    lateinit var currentProject: ProjectModel

    lateinit var surveys: List<SurveyModel>

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var exceptionHandler: ((e: Exception) -> Unit)? = null

    fun setExceptionHandler(handler: ((e: Exception) -> Unit)?) {
        exceptionHandler = handler
    }

    private inline fun <reified T : Any> get(key: String): T? {
        return try {
            keyValueStore.get(key)
        } catch (e: Exception) {
            exceptionHandler?.invoke(e)
            null
        }
    }

    private inline fun <reified T : Any> set(key: String, value: T?) {
        scope.launch(Dispatchers.IO) {
            try {
                keyValueStore.set(key, value)
            } catch (e: Exception) {
                exceptionHandler?.invoke(e)
            }
        }
    }

    fun clear() {
        profile = null
    }

    companion object {
        private const val PROFILE_KEY = "profile"
    }
}
