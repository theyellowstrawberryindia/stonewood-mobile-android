package uk.co.savills.stonewood.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "image_upload_history",
    primaryKeys = ["file_path", "project_id"]
)
data class ImageUploadEntity(
    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "project_id")
    val projectId: String
)
