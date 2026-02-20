package uk.co.savills.stonewood.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "renewal_band_table",
    primaryKeys = ["project_id", "lower_bound"]
)
data class RenewalBandEntity(
    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "lower_bound")
    val lowerBound: Int,

    @ColumnInfo(name = "upper_bound")
    val upperBound: Int?
)
