package uk.co.savills.stonewood.network.source

import android.annotation.SuppressLint
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import uk.co.savills.stonewood.network.dto.AlterationEmailDto
import uk.co.savills.stonewood.network.dto.AppVersionDto
import uk.co.savills.stonewood.network.dto.AuthRequestDto
import uk.co.savills.stonewood.network.dto.HHSRSSevereIssueDto
import uk.co.savills.stonewood.network.dto.ImageRequestDto
import uk.co.savills.stonewood.network.dto.PasswordChangeRequestDto
import uk.co.savills.stonewood.network.dto.ProjectDto
import uk.co.savills.stonewood.network.dto.RefreshAuthTokenRequestDto
import uk.co.savills.stonewood.network.dto.UserDto
import uk.co.savills.stonewood.network.dto.datatransfer.DataTransferRequestDto
import uk.co.savills.stonewood.network.dto.datatransfer.DataTransferResponseDto
import uk.co.savills.stonewood.network.dto.datatransfer.PropertyDto
import uk.co.savills.stonewood.network.util.ApiResponse

@Suppress("DeferredIsResult")
interface Api {

    @Headers("isAuthorizable: false")
    @GET("/api/applicationInfo/1")
    fun getAppVersionInfo(): Deferred<ApiResponse<AppVersionDto>>

    @Headers("isAuthorizable: false")
    @POST("/api/users/authenticate")
    fun login(@Body authRequest: AuthRequestDto): Deferred<ApiResponse<UserDto>>

    @POST("/api/users/changePassword/{email}")
    fun changePassword(
        @Path("email") email: String,
        @Body request: PasswordChangeRequestDto
    ): Deferred<ApiResponse<Unit>>

    @Headers("isAuthorizable: false")
    @POST("/api/users/refresh")
    fun refreshAuthToken(@Body refreshRequest: RefreshAuthTokenRequestDto): Deferred<ApiResponse<UserDto>>

//    https://20.39.224.104.nip.io/api/surveyprojects
//    @GET("/api/surveyProjectUser/projects/{userId}")
    @GET("/api/surveyprojects")
    fun getAssignedProjects(): Deferred<ApiResponse<List<ProjectDto>>>

    @GET("/api/{projectId}/dataSync/dataDownload/{userId}")
    fun getSurveySpecifications(
        @Path("projectId") projectId: String,
        @Path("userId") userId: Int
    ): Deferred<ApiResponse<DataTransferResponseDto>>

    @POST("/api/{projectId}/dataSync/dataTransfer")
    fun transferData(
        @Path("projectId") projectId: String,
        @Body dataTransferRequest: DataTransferRequestDto
    ): Deferred<ApiResponse<Unit>>

    @POST("/api/{projectId}/dataSync/sendAlterationMail")
    fun sendAlterationEmail(
        @Path("projectId") projectId: String,
        @Body alterationDto: AlterationEmailDto
    ): Deferred<ApiResponse<Unit>>

    @POST("/api/{projectId}/dataSync/uploadImage/{surveyorId}")
    fun uploadImages(
        @Path("projectId") projectId: String,
        @Path("surveyorId") surveyorId: Int,
        @Body files: RequestBody
    ): Deferred<ApiResponse<Unit>>

    @POST("/api/{projectId}/dataSync/images")
    fun downloadCommunalImages(
        @Path("projectId") projectId: String,
        @Body request: List<ImageRequestDto>
    ): Deferred<ApiResponse<ResponseBody>>

    @POST("/api/{projectId}/dataSync/externalEnergyPhotos")
    fun downloadExtBlockImages(
        @Path("projectId") projectId: String,
        @Body request: List<String>
    ): Deferred<ApiResponse<ResponseBody>>

    @POST("/api/{projectId}/dataSync/uploadExternalEnergyPhotos/{surveyorId}")
    fun uploadExtBlockImages(
        @Path("projectId") projectId: String,
        @Path("surveyorId") surveyorId: Int,
        @Body files: RequestBody
    ): Deferred<ApiResponse<Unit>>

    @PUT("/api/{projectId}/propertyAddresses/updateStatus")
    fun updatePropertyStatus(
        @Path("projectId") projectId: String,
        @Query("id") id: Int,
        @Query("status") status: Int
    ): Deferred<ApiResponse<Unit>>

    @GET("/api/{projectId}/dataSync/updatedProperties/{surveyorId}")
    fun getUpdatedProperties(
        @Path("projectId") projectId: String,
        @Path("surveyorId") surveyorId: Int
    ): Deferred<ApiResponse<List<PropertyDto>>>

    @SuppressLint("InvalidMethodName")
    @POST("/api/{projectId}/dataSync/sendSevereIssueMail")
    fun reportHHSRSSevereIssue(
        @Path("projectId") projectId: String,
        @Body issue: HHSRSSevereIssueDto
    ): Deferred<ApiResponse<Unit>>

    @POST("/api/{projectId}/backup/upload/{surveyorId}")
    fun uploadDataBackup(
        @Path("projectId") projectId: String,
        @Path("surveyorId") surveyorId: Int,
        @Body files: RequestBody
    ): Deferred<ApiResponse<Unit>>

    @POST("/api/users/logout")
    fun logout(): Deferred<ApiResponse<Unit>>
}
