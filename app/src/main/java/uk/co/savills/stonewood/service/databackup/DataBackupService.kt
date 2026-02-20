@file:Suppress("ClassName", "SpellCheckingInspection", "LocalVariableName")

package uk.co.savills.stonewood.service.databackup

import android.content.Context
import androidx.annotation.WorkerThread
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import uk.co.savills.stonewood.AppContainer
import uk.co.savills.stonewood.DATA_BACKUP_DIRECTORY
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.network.remoteDb.HHSRSDataTable
import uk.co.savills.stonewood.network.remoteDb.NoAccessTable
import uk.co.savills.stonewood.network.remoteDb.QualityStandardDataTable
import uk.co.savills.stonewood.network.remoteDb.RiskAssessmentDataTable
import uk.co.savills.stonewood.network.remoteDb.SAPDataTable
import uk.co.savills.stonewood.network.remoteDb.StockDataTable
import uk.co.savills.stonewood.network.remoteDb.SurveyHeaderTable
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.HHSRS_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.NO_ACCESS_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.PROJECT_ID_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.QUALITY_STANDARD_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.RISK_ASSESSMENT_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.SAP_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.STOCK_KEY
import uk.co.savills.stonewood.service.databackup.DataBackupUploadWorker.Companion.SURVEY_HEADER_KEY
import java.io.File
import java.lang.reflect.Type

class DataBackupService(
    private val context: Context
) {
    private val appContainer: AppContainer
        get() = (context.applicationContext as MainApplication).appContainer

    private val surveyorName: String
        get() = appContainer.appState.profile?.userName.orEmpty()

    private val converter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val mutex = Mutex()

    @WorkerThread
    suspend fun backupData(projectId: String) {
        mutex.withLock {
            val fileData = getFileData(projectId)

            if (fileData.isNotEmpty()) {
                val data = fileData.apply { add(0, PROJECT_ID_KEY to projectId) }
                DataBackupUploadWorker.begin(context, projectId, data)
            }
        }
    }

    private fun getFileData(projectId: String): MutableList<Pair<String, String>> {
        val data = mutableListOf<Pair<String, String>>()

        getFile(projectId, NO_ACCESS_FILE_NAME) {
            appContainer.noAccessEntryRepository.getEntries(projectId).map {
                NoAccessTable.from(it, surveyorName)
            }
        }?.also {
            data.add(NO_ACCESS_KEY to it)
        }

        getFile(projectId, HHSRS_FILE_NAME) {
            appContainer.hhsrsSurveyElementEntryRepository.getEntries(projectId).map {
                HHSRSDataTable.from(it, surveyorName)
            }
        }?.also {
            data.add(HHSRS_KEY to it)
        }

        getFile(projectId, QUALITY_STANDARD_FILE_NAME) {
            appContainer.qualityStandardSurveyElementEntryRepository.getEntries(projectId).map {
                QualityStandardDataTable.from(it, surveyorName)
            }
        }?.also {
            data.add(QUALITY_STANDARD_KEY to it)
        }

        getFile(projectId, RISK_ASSESSMENT_FILE_NAME) {
            appContainer.riskAssessmentSurveyElementEntryRepository.getEntries(projectId).map {
                RiskAssessmentDataTable.from(it, surveyorName)
            }
        }?.also {
            data.add(RISK_ASSESSMENT_KEY to it)
        }

        getFile(projectId, SAP_FILE_NAME) {
            appContainer.energySurveyElementEntryRepository.getProjectEntries(projectId).map {
                SAPDataTable.from(it, surveyorName)
            }
        }?.also {
            data.add(SAP_KEY to it)
        }

        getFile(projectId, STOCK_FILE_NAME) {
            appContainer.stockStandardSurveyElementEntryRepository.getEntries(projectId).map {
                StockDataTable.from(it, surveyorName)
            }
        }?.also {
            data.add(STOCK_KEY to it)
        }

        getFile(projectId, SURVEY_HEADER_FILE_NAME) {
            appContainer.propertyRepository.getProperties(projectId).map(SurveyHeaderTable::from)
        }?.also {
            data.add(SURVEY_HEADER_KEY to it)
        }

        return data
    }

    private inline fun <reified T : Any> getFile(
        projectId: String,
        fileName: String,
        dataProvider: () -> List<T>
    ): String? {
        val data = dataProvider.invoke()
        return saveToFile(projectId, data, fileName)
    }

    private inline fun <reified T : Any> saveToFile(
        projectId: String,
        data: List<T>,
        fileName: String
    ): String {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            T::class.java
        )
        val jsonAdapter: JsonAdapter<List<T>> = converter.adapter(type)
        val jsonData = jsonAdapter.toJson(data)

        val dataBackUpDir = File(context.filesDir, DATA_BACKUP_DIRECTORY)
        if (!dataBackUpDir.exists()) dataBackUpDir.mkdir()

        val directory = File(dataBackUpDir, projectId)
        if (!directory.exists()) directory.mkdir()

        val file = File(directory, fileName)

        file.writeText(jsonData)

        return file.path
    }

    companion object {
        private const val NO_ACCESS_FILE_NAME = "tblNoAccess.json"
        private const val HHSRS_FILE_NAME = "tblSurveyData_HHSRS.json"
        private const val QUALITY_STANDARD_FILE_NAME = "tblSurveyData_QualityStandard.json"
        private const val RISK_ASSESSMENT_FILE_NAME = "tblSurveyData_RiskAssessment.json"
        private const val SAP_FILE_NAME = "tblSurveyData_SAP.json"
        private const val STOCK_FILE_NAME = "tblSurveyData_Stock.json"
        private const val SURVEY_HEADER_FILE_NAME = "tblSurveyHeader.json"
    }
}
