package uk.co.savills.stonewood.storage.db.entity

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@SuppressLint("InvalidClassName")
@Entity(tableName = "hhsrs_location_table")
data class HHSRSLocationEntity(
    val name: String,

    val type: Int,

    @ColumnInfo(name = "project_id")
    val projectId: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
