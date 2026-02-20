package uk.co.savills.stonewood.storage.db.entity.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class ProjectEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val description: String,

    @ColumnInfo(name = "is_external_type_only")
    val isExternalOnlyType: Boolean,

    @ColumnInfo(name = "are_repairs_available")
    val areRepairsAvailable: Boolean,

    @ColumnInfo(name = "photo_count")
    val numberOfEnergyExtPhotos: Int,

    @ColumnInfo(name = "is_closed")
    val isClosed: Boolean,
)
