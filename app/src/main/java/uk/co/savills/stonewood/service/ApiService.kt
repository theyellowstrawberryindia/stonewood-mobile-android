package uk.co.savills.stonewood.service

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.AppVersionModel
import uk.co.savills.stonewood.model.UserModel
import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel
import uk.co.savills.stonewood.model.survey.ImageRequestModel
import uk.co.savills.stonewood.model.survey.datatransfer.AlterationModel
import uk.co.savills.stonewood.model.survey.datatransfer.DataTransferRequestModel
import uk.co.savills.stonewood.model.survey.datatransfer.DataTransferResponseModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertySurveyStatus
import uk.co.savills.stonewood.network.dto.AlterationEmailDto
import uk.co.savills.stonewood.network.dto.AuthRequestDto
import uk.co.savills.stonewood.network.dto.PasswordChangeRequestDto
import uk.co.savills.stonewood.network.exception.ServerException
import uk.co.savills.stonewood.network.exception.UnauthorizedException
import uk.co.savills.stonewood.network.source.Api
import uk.co.savills.stonewood.network.util.ApiResponse
import uk.co.savills.stonewood.util.Result
import uk.co.savills.stonewood.util.mapper.mapToDto
import uk.co.savills.stonewood.util.mapper.mapToModel
import java.io.File

class ApiService(
    private val api: Api,
    private val appState: AppState,
    private val authService: AuthService,
) {
    suspend fun getAppVersion(): Result<AppVersionModel>? {
        return execute(
            api::getAppVersionInfo,
            ::mapToModel
        )
    }

    suspend fun login(email: String, password: String): Result<UserModel>? {
        return execute(
            { api.login(AuthRequestDto(email, password)) },
            ::mapToModel,
            isAuthenticated = false
        )
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>? {
        val email = appState.profile?.email ?: return null
        val request = PasswordChangeRequestDto(
            oldPassword,
            newPassword
        )

        return execute(
            { api.changePassword(email, request) }
        )
    }

    suspend fun getProjects(): Result<List<ProjectModel>>? {
        val userId = appState.profile?.id ?: return null
        return execute(
            { api.getAssignedProjects() },
            { it.map(::mapToModel) }
        )
    }

    suspend fun getSurveySpecifications(): Result<DataTransferResponseModel>? {
        val userId = appState.profile?.id ?: return null
        val projectId = appState.currentProject.id
        return execute(
            { api.getSurveySpecifications(projectId, userId) },
            ::mapToModel
        )
    }

    suspend fun transferData(dataTransferRequest: DataTransferRequestModel): Result<Unit>? {
        val request = appState.profile?.let { mapToDto(dataTransferRequest, it) } ?: return null
        return execute({ api.transferData(appState.currentProject.id, request) })
    }

    suspend fun getUpdatedProperties(projectId: String): Result<List<PropertyModel>>? {
        val surveyorId = appState.profile?.id ?: return null
        return execute(
            { api.getUpdatedProperties(projectId, surveyorId) },
            { it.map(::mapToModel) }
        )
    }

    suspend fun sendAlterationEmail(alteration: AlterationModel, properties: List<PropertyModel>): Result<Unit>? {
        val request = AlterationEmailDto(
            appState.profile?.id ?: return null,
            properties.map { it.id },
            mapToDto(alteration),
        )

        return execute(
            { api.sendAlterationEmail(appState.currentProject.id, request) }
        )
    }

    suspend fun uploadImages(projectId: String, files: List<File>): Result<Unit>? {
        val surveyorId = appState.profile?.id ?: return null

        val requestBuilder = MultipartBody
            .Builder()
            .setType(MultipartBody.FORM)

        for (file in files) {
            requestBuilder.addFormDataPart(
                "files",
                file.name,
                file.asRequestBody("image/jpeg".toMediaType())
            )
        }

        return execute({ api.uploadImages(projectId, surveyorId, requestBuilder.build()) })
    }

    suspend fun getCommunalImages(request: List<ImageRequestModel>): Result<ResponseBody>? {
        return execute(
            { api.downloadCommunalImages(appState.currentProject.id, request.map(::mapToDto)) }
        )
    }

    suspend fun getExtBlockImages(request: List<String>): Result<ResponseBody>? {
        return execute(
            { api.downloadExtBlockImages(appState.currentProject.id, request) }
        )
    }

    suspend fun uploadExtBlockImages(files: List<File>): Result<Unit>? {
        val projectId = appState.currentProject.id
        val surveyorId = appState.profile?.id ?: return null

        val requestBuilder = MultipartBody
            .Builder()
            .setType(MultipartBody.FORM)

        for (file in files) {
            requestBuilder.addFormDataPart(
                "files",
                file.name,
                file.asRequestBody("image/jpeg".toMediaType())
            )
        }

        return execute({ api.uploadExtBlockImages(projectId, surveyorId, requestBuilder.build()) })
    }

    suspend fun updatePropertyStatus(
        projectId: String,
        id: Int,
        status: PropertySurveyStatus
    ): Result<Unit>? {
        return execute({ api.updatePropertyStatus(projectId, id, status.ordinal) })
    }

    @SuppressLint("InvalidMethodName")
    suspend fun reportHHSRSSevereIssue(projectId: String, issue: HHSRSSevereIssueModel): Result<Unit>? {
        return execute({ api.reportHHSRSSevereIssue(projectId, mapToDto(issue)) })
    }

    suspend fun backupData(projectId: String, files: List<File>): Result<Unit>? {
        val surveyorId = appState.profile?.id ?: return null

        val requestBuilder = MultipartBody
            .Builder()
            .setType(MultipartBody.FORM)

        for (file in files) {
            requestBuilder.addFormDataPart(
                "files",
                file.name,
                file.asRequestBody("text/plain".toMediaType())
            )
        }

        return execute({ api.uploadDataBackup(projectId, surveyorId, requestBuilder.build()) })
    }

    suspend fun logout(): Result<Unit>? {
        return execute({ api.logout() })
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any, V : Any> execute(
        request: suspend () -> Deferred<ApiResponse<T>>,
        onCompleteListener: (T) -> V = { it as V },
        isAuthenticated: Boolean = true
    ): Result<V>? {
        return try {
            if (isAuthenticated) authService.validateAuthToken()

            when (val response = request().await()) {
                is ApiResponse.Success -> Result.Success(onCompleteListener.invoke(response.data))
                is ApiResponse.Error -> Result.Error(ServerException(response.message))
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> null

                is UnauthorizedException -> {
                    authService.invalidateSession()
                    null
                }

                else -> Result.Error(e)
            }
        }
    }
}
