package uk.co.savills.stonewood

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.network.source.ApiFactory
import uk.co.savills.stonewood.repository.AgeBandRepository
import uk.co.savills.stonewood.repository.HHSRSLocationRepository
import uk.co.savills.stonewood.repository.HHSRSSevereIssueRepository
import uk.co.savills.stonewood.repository.ImageUploadHistoryRepository
import uk.co.savills.stonewood.repository.NoAccessReasonRepository
import uk.co.savills.stonewood.repository.PhotoNoAccessReasonRepository
import uk.co.savills.stonewood.repository.ProjectRepository
import uk.co.savills.stonewood.repository.PropertyStatsRepository
import uk.co.savills.stonewood.repository.RenewalBandRepository
import uk.co.savills.stonewood.repository.element.EnergySurveyElementRepository
import uk.co.savills.stonewood.repository.element.HHSRSSurveyElementRepository
import uk.co.savills.stonewood.repository.element.QualityStandardSurveyElementRepository
import uk.co.savills.stonewood.repository.element.RiskAssessmentSurveyElementRepository
import uk.co.savills.stonewood.repository.element.StockSurveyElementRepository
import uk.co.savills.stonewood.repository.element.ValidationElementRepository
import uk.co.savills.stonewood.repository.entry.CommunalDataRepository
import uk.co.savills.stonewood.repository.entry.EnergySurveyElementEntryRepository
import uk.co.savills.stonewood.repository.entry.ExtBlockPhotosRepository
import uk.co.savills.stonewood.repository.entry.NoAccessEntryRepository
import uk.co.savills.stonewood.repository.entry.StockSurveyElementEntryRepository
import uk.co.savills.stonewood.repository.entry.SurveyElementEntryRepository
import uk.co.savills.stonewood.repository.property.PropertyRepository
import uk.co.savills.stonewood.service.ApiService
import uk.co.savills.stonewood.service.AuthService
import uk.co.savills.stonewood.service.ErrorReportingService
import uk.co.savills.stonewood.service.EventReportingService
import uk.co.savills.stonewood.service.databackup.DataBackupService
import uk.co.savills.stonewood.storage.KeyValueStore
import uk.co.savills.stonewood.storage.db.SurveyDatabase
import uk.co.savills.stonewood.storage.db.dao.entry.HHSRSSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.QualityStandardSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.RiskAssessmentSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.entity.entry.HHSRSSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.QualityStandardSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.RiskAssessmentSurveyElementEntryEntity
import uk.co.savills.stonewood.util.NotificationManager
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel
import uk.co.savills.stonewood.util.photo.deletePhotosFolder
import java.io.File

class AppContainer(
    private val appContext: Context,
    isNetworkAvailable: () -> Boolean,
) {
    private val database = SurveyDatabase.getDatabase(appContext)
    val appState: AppState = AppState(KeyValueStore(appContext))

    val apiService: ApiService
    val authService: AuthService
    val errorReportingService: ErrorReportingService
    val eventReportingService: EventReportingService

    val notificationManager by lazy { NotificationManager(appContext) }

    val projectRepository: ProjectRepository

    val propertyRepository: PropertyRepository
    val noAccessReasonRepository: NoAccessReasonRepository
    val hhsrsLocationRepository: HHSRSLocationRepository
    val ageBandRepository: AgeBandRepository
    val renewalBandRepository: RenewalBandRepository

    val qualityStandardSurveyElementRepository: QualityStandardSurveyElementRepository
    val hhsrsSurveyElementRepository: HHSRSSurveyElementRepository
    val riskAssessmentSurveyElementRepository: RiskAssessmentSurveyElementRepository
    val energySurveyElementRepository: EnergySurveyElementRepository
    val stockSurveyElementRepository: StockSurveyElementRepository
    val validationElementRepository: ValidationElementRepository
    val photoNoAccessReasonRepository: PhotoNoAccessReasonRepository
    val communalDataRepository: CommunalDataRepository
    val externalPhotosRepository: ExtBlockPhotosRepository
    val imageUploadHistoryRepository = ImageUploadHistoryRepository(database.imageUploadDao())

    val energySurveyElementEntryRepository: EnergySurveyElementEntryRepository
    val hhsrsSurveyElementEntryRepository: SurveyElementEntryRepository<HHSRSSurveyElementEntryEntity, HHSRSSurveyElementEntryModel, HHSRSSurveyElementEntryDao>
    val qualityStandardSurveyElementEntryRepository: SurveyElementEntryRepository<QualityStandardSurveyElementEntryEntity, QualityStandardSurveyElementEntryModel, QualityStandardSurveyElementEntryDao>
    val riskAssessmentSurveyElementEntryRepository: SurveyElementEntryRepository<RiskAssessmentSurveyElementEntryEntity, RiskAssessmentSurveyElementEntryModel, RiskAssessmentSurveyElementEntryDao>
    val stockStandardSurveyElementEntryRepository: StockSurveyElementEntryRepository
    val noAccessEntryRepository: NoAccessEntryRepository
    val hhsrsSevereIssueRepository: HHSRSSevereIssueRepository

    val propertyStatsRepository: PropertyStatsRepository

    val dataBackUpService by lazy { DataBackupService(appContext) }

    init {
        val api = ApiFactory(
            { appState.profile?.authToken?.accessToken },
            isNetworkAvailable
        ).create()

        authService = AuthService(api, appState)
        appState.setExceptionHandler(::onStorageDataException)
        apiService = ApiService(api, appState, authService)

        if (!BuildConfig.DEBUG) {
            AppCenter.start(
                appContext as Application,
                APP_CENTER_KEY,
                Crashes::class.java,
                Analytics::class.java
            )
        }
        errorReportingService = ErrorReportingService()
        eventReportingService = EventReportingService(appContext as Application)

        projectRepository = ProjectRepository(database.projectDao())

        energySurveyElementEntryRepository = EnergySurveyElementEntryRepository(
            database.energySurveyElementEntryDao(),
            appState
        )

        hhsrsSurveyElementEntryRepository = SurveyElementEntryRepository(
            database.hhsrsSurveyElementEntryDao(),
            appState,
            ::mapToModel,
            ::mapToEntity
        )

        qualityStandardSurveyElementEntryRepository = SurveyElementEntryRepository(
            database.qualityStandardSurveyElementEntryDao(),
            appState,
            ::mapToModel,
            ::mapToEntity
        )

        riskAssessmentSurveyElementEntryRepository = SurveyElementEntryRepository(
            database.riskAssessmentSurveyElementEntryDao(),
            appState,
            ::mapToModel,
            ::mapToEntity
        )

        stockStandardSurveyElementEntryRepository = StockSurveyElementEntryRepository(
            database.stockSurveyElementEntryDao(),
            appState
        )

        noAccessEntryRepository = NoAccessEntryRepository(
            database.noAccessEntryDao(),
            appState
        )

        hhsrsSevereIssueRepository = HHSRSSevereIssueRepository(database.hhsrsSevereIssueDao())

        propertyRepository = PropertyRepository(
            database.propertyDao(),
            appState
        )

        noAccessReasonRepository = NoAccessReasonRepository(
            database.noAccessReasonDao(),
            appState
        )

        hhsrsLocationRepository = HHSRSLocationRepository(
            database.hhsrsLocationDao(),
            appState
        )

        ageBandRepository = AgeBandRepository(
            database.ageBandDao(),
            appState
        )

        renewalBandRepository = RenewalBandRepository(
            database.renewalBandDao(),
            appState
        )

        qualityStandardSurveyElementRepository = QualityStandardSurveyElementRepository(
            database.qualityStandardSurveyElementDao(),
            qualityStandardSurveyElementEntryRepository,
            appState
        )

        riskAssessmentSurveyElementRepository = RiskAssessmentSurveyElementRepository(
            database.riskAssessmentSurveyElementDao(),
            riskAssessmentSurveyElementEntryRepository,
            appState
        )

        hhsrsSurveyElementRepository = HHSRSSurveyElementRepository(
            database.hhsrsSurveyElementDao(),
            hhsrsSurveyElementEntryRepository,
            appState
        )

        energySurveyElementRepository = EnergySurveyElementRepository(
            database.energySurveyElementDao(),
            database.energySurveySubElementDao(),
            energySurveyElementEntryRepository,
            appState
        )

        stockSurveyElementRepository = StockSurveyElementRepository(
            appContext,
            database.stockSurveyElementDao(),
            database.stockSurveySubElementDao(),
            stockStandardSurveyElementEntryRepository,
            appState
        )

        validationElementRepository = ValidationElementRepository(
            database.validationElementDao(),
            stockSurveyElementRepository,
            energySurveyElementRepository,
        )

        photoNoAccessReasonRepository = PhotoNoAccessReasonRepository(
            database.photoNoAccessReasonDao(),
            appState
        )

        communalDataRepository = CommunalDataRepository(
            appContext,
            apiService,
            database.communalDataDao()
        )

        externalPhotosRepository = ExtBlockPhotosRepository(
            apiService,
            database.extBlockPhotosDao()
        )

        propertyStatsRepository = PropertyStatsRepository(
            database.propertyStatsDao()
        )
    }

    fun getAllImages(projectId: String): MutableList<File> {
        val properties = propertyRepository.getProperties(projectId)
        val noAccessEntries = noAccessEntryRepository.getEntries(projectId)
        val hhsrsData = hhsrsSurveyElementEntryRepository.getEntries(projectId)
        val stockData = stockStandardSurveyElementEntryRepository.getEntries(projectId)

        val history = imageUploadHistoryRepository.get(projectId)

        val photos = mutableListOf<String>().apply {
            addAll(properties.filter { it.frontDoorPhoto.isNotEmpty() }.map { it.frontDoorPhoto })
            addAll(noAccessEntries.flatMap { it.imagePaths })
            addAll(hhsrsData.flatMap { it.imagePaths })
            addAll(stockData.flatMap { it.imagePaths })
        }

        val files = mutableListOf<File>()

        for (photo in photos) {
            val file = File(photo)
            if (!history.contains(photo) && file.exists()) files.add(file)
        }

        return files
    }

    @WorkerThread
    fun clearProjectData(projects: List<ProjectModel>) {
        val ids = projects.map { it.id }

        projectRepository.clear(ids)
        propertyRepository.clearProjectProperties(ids)

        noAccessReasonRepository.clearProjectReasons(ids)
        hhsrsLocationRepository.clearProjectLocations(ids)
        ageBandRepository.clearProjectBands(ids)
        renewalBandRepository.clearProjectBands(ids)

        qualityStandardSurveyElementRepository.clearProjectElements(ids)
        riskAssessmentSurveyElementRepository.clearProjectElements(ids)
        hhsrsSurveyElementRepository.clearProjectElements(ids)
        energySurveyElementRepository.clearProjectElements(ids)
        stockSurveyElementRepository.clearProjectElements(ids)
        validationElementRepository.clearProjectElements(ids)
        photoNoAccessReasonRepository.clearProjectReasons(ids)

        noAccessEntryRepository.clearProjectEntries(ids)
        qualityStandardSurveyElementEntryRepository.clearProjectEntries(ids)
        riskAssessmentSurveyElementEntryRepository.clearProjectEntries(ids)
        hhsrsSurveyElementEntryRepository.clearProjectEntries(ids)
        hhsrsSevereIssueRepository.clearProjectEntries(ids)
        energySurveyElementEntryRepository.clearProjectEntries(ids)
        stockStandardSurveyElementEntryRepository.clearProjectEntries(ids)
        communalDataRepository.clearProjectEntries(ids)
        externalPhotosRepository.clearProjectEntries(ids)

        propertyStatsRepository.clearProjectEntries(ids)

        val redundantPhotoDirectories = ids.flatMap { projectId ->
            propertyRepository.getProperties(projectId).map { "${projectId}_${it.UPRN}" }
        }
        redundantPhotoDirectories.forEach { deletePhotosFolder(appContext, it) }

        for (id in ids) imageUploadHistoryRepository.clear(id)
    }

    @WorkerThread
    fun clear() {
        appState.clear()

        projectRepository.clearAll()

        propertyRepository.clearAll()
        noAccessReasonRepository.clearAll()
        hhsrsLocationRepository.clearAll()
        ageBandRepository.clearAll()
        renewalBandRepository.clearAll()

        qualityStandardSurveyElementRepository.clearAll()
        riskAssessmentSurveyElementRepository.clearAll()
        hhsrsSurveyElementRepository.clearAll()
        energySurveyElementRepository.clearAll()
        stockSurveyElementRepository.clearAll()
        validationElementRepository.clearAll()
        photoNoAccessReasonRepository.clearAll()

        noAccessEntryRepository.clearAll()
        qualityStandardSurveyElementEntryRepository.clearAll()
        riskAssessmentSurveyElementEntryRepository.clearAll()
        hhsrsSurveyElementEntryRepository.clearAll()
        energySurveyElementEntryRepository.clearAll()
        stockStandardSurveyElementEntryRepository.clearAll()
        communalDataRepository.clearAll()
        externalPhotosRepository.clearAll()

        propertyStatsRepository.clearAll()

        hhsrsSevereIssueRepository.clearAll()
        imageUploadHistoryRepository.clearAll()

        deletePhotosFolder(appContext)
    }

    private fun onStorageDataException(exception: Exception) {
        errorReportingService.reportError(exception)

        if (authService.isLoggedIn) authService.invalidateSession()
    }
}
