package uk.co.savills.stonewood.screen.survey.noaccess

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.imageupload.ImageUploadWorker
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.photo.deletePhotoFile
import uk.co.savills.stonewood.util.photo.getFileName
import java.io.File
import java.time.Instant
import java.util.Locale
import kotlin.math.max

@SuppressLint("InvalidMethodName", "DefaultLocale")
class NoAccessViewModel(
    application: Application,
    private val locationTracker: LocationTracker
) : BaseViewModel(application) {
    private var propertyId: Int = 0
    private lateinit var propertyUPRN: String

    private val _reasons = MutableLiveData<List<String>>(listOf())
    val reasons: LiveData<List<String>>
        get() = _reasons

    private val reason = MutableLiveData<String>()

    val remarks = MutableLiveData("")

    val canSubmit = Transformations.switchMap(reason) { reason ->
        Transformations.map(photoFilePaths) { !reason.isNullOrBlank() && it.size >= MIN_PHOTOS_REQUIRED }
    }

    private val _photoFilePaths = MutableLiveData<List<String>>(listOf())
    val photoFilePaths: LiveData<List<String>>
        get() = _photoFilePaths

    val remainingPhotoCount = Transformations.map(photoFilePaths) {
        max(0, MIN_PHOTOS_REQUIRED - it.size)
    }

    val photoFolderName: String
        get() = "${appState.currentProject.id}_$propertyUPRN"

    val photoFileName: String
        get() {
            val surveyor = requireNotNull(appState.profile).userName
            return getFileName(surveyor, propertyUPRN, _photoFilePaths.getNonNullValue().size + 1)
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _reasons.postValue(appContainer.noAccessReasonRepository.getReasons())
        }
    }

    fun setProperty(propertyId: Int, propertyUPRN: String) {
        this.propertyId = propertyId
        this.propertyUPRN = propertyUPRN
    }

    fun setReason(index: Int) {
        reason.value = reasons.getNonNullValue()[index]
    }

    fun onImageAdded(filePath: String) {
        _photoFilePaths.value = mutableListOf(filePath).apply {
            addAll(_photoFilePaths.getNonNullValue())
        }
    }

    fun onImageRemoved(filePath: String) {
        _photoFilePaths.value = _photoFilePaths.getNonNullValue().toMutableList().apply {
            remove(filePath)
        }

        viewModelScope.launch(Dispatchers.Default) {
            deletePhotoFile(filePath)
        }
    }

    fun submit() {
        val newFilePaths: MutableList<String> = mutableListOf()

        for (filePath in photoFilePaths.getNonNullValue()) {
            val file = File(filePath)

            val reason = reason.getNonNullValue().split(" ")
                .joinToString("") { it.capitalize(Locale.getDefault()) }
            val insert = "_NoAccess_$reason"

            val lastIndex = file.path.lastIndexOf('_')
            val newName =
                file.path.substring(0, lastIndex) + insert + file.path.substring(lastIndex)

            file.renameTo(File(newName))
            newFilePaths.add(newName)
        }

        val noAccessEntry = NoAccessEntryModel(
            propertyUPRN,
            reason.getNonNullValue(),
            remarks.getNonNullValue(),
            newFilePaths,
            locationTracker.getCurrentLocation(),
            Instant.now()
        )

        viewModelScope.launch(Dispatchers.IO) {
            appContainer.noAccessEntryRepository.insertEntry(noAccessEntry, propertyId)
            ImageUploadWorker.beginWork(application)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)

            navigateBack()
        }
    }

    companion object {
        private const val MIN_PHOTOS_REQUIRED = 1
    }
}
