package uk.co.savills.stonewood.storage.db.entity.property

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "property_stats",
    primaryKeys = ["project_id", "uprn"]
)
data class PropertyStatsEntity(
    @ColumnInfo(name = "project_id")
    val projectId: String,

    val section: String,

    val uprn: String,

    val strata: String,

    @ColumnInfo(name = "is_required")
    val isRequired: Boolean,

    @ColumnInfo(name = "is_complete")
    val isComplete: Boolean,
)
