package uk.co.savills.stonewood.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "no_access_reason_table")
data class NoAccessReasonEntity(
    @ColumnInfo(name = "project_id")
    val projectId: String,

    val reason: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
