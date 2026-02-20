package uk.co.savills.stonewood.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "photo_no_access_reason",
    primaryKeys = ["id", "project_id"]
)
data class PhotoNoAccessReasonEntity(
    val id: String,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    val reason: String
)
