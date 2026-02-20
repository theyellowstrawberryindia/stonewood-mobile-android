package uk.co.savills.stonewood.storage.db.entity.entry

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity

@SuppressLint("InvalidMethodName")
@Entity(
    tableName = "energy_ext_photo",
    primaryKeys = ["property_uprn", "project_id"]
)
class ExtBlockPhotoEntity(
    var id: Int?,

    @ColumnInfo(name = "property_uprn")
    val propertyUPRN: String,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    val number: String,

    val address1: String,

    val address2: String,

    val address3: String,

    val address4: String,

    @ColumnInfo(name = "postal_code")
    val postalCode: String,

    val surveyor: String,

    @ColumnInfo(name = "image_paths")
    var imagePaths: String,

    @ColumnInfo(name = "entry_timestamp")
    val entryTimestamp: String,

    @ColumnInfo(name = "sync_id")
    val syncId: Int?
)
