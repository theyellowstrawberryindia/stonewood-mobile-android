package uk.co.savills.stonewood.storage.db.entity

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity

@SuppressLint("InvalidClassName")
@Entity(
    tableName = "hhsrs_severe_issue",
    primaryKeys = ["project_id", "property_id", "element_id"]
)
data class HHSRSSevereIssueEntity(
    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "property_id")
    val propertyId: Int,

    @ColumnInfo(name = "element_id")
    val elementId: String,

    @ColumnInfo(name = "surveyor_id")
    val surveyorId: Int,

    val element: String,

    val remarks: String,

    val images: String,

    @ColumnInfo(name = "internal_locations")
    val internalLocations: String,

    @ColumnInfo(name = "external_locations")
    val externalLocations: String,

    @ColumnInfo(name = "is_reported")
    var isReported: Boolean
)
