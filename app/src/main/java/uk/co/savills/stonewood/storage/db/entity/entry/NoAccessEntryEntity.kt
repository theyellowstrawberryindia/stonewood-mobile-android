package uk.co.savills.stonewood.storage.db.entity.entry

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@SuppressLint("InvalidMethodName")
@Entity(tableName = "no_access_entry_table")
data class NoAccessEntryEntity(
    @ColumnInfo(name = "property_key")
    val propertyKey: String,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "uprn")
    val UPRN: String,

    val reason: String,

    val remarks: String,

    @ColumnInfo(name = "image_paths")
    val imagePaths: String,

    val latitude: Double,

    val longitude: Double,

    @ColumnInfo(name = "created_at")
    val createdAt: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
