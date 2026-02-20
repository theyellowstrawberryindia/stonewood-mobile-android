package uk.co.savills.stonewood.storage.db.entity.entry

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity

@SuppressLint("InvalidMethodName")
@Entity(
    tableName = "communal_data",
    primaryKeys = ["element", "communal_part_number", "property_uprn", "project_id"]
)
data class CommunalDataEntity(
    var id: Int?,

    val element: String,

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

    @ColumnInfo(name = "communal_part_number")
    val communalPartNumber: Int,

    @ColumnInfo(name = "sub_element_number")
    val subElementNumber: Int,

    @ColumnInfo(name = "sub_element")
    val subElement: String,

    @ColumnInfo(name = "sub_element_user_entry")
    val subElementUserEntry: String,

    val description: String,

    val repair: Boolean?,

    @ColumnInfo(name = "repair_description")
    val repairDescription: String,

    @ColumnInfo(name = "repair_spot_price")
    val repairSpotPrice: Int?,

    @ColumnInfo(name = "life_renewal_band")
    val lifeRenewalBand: Int?,

    @ColumnInfo(name = "life_renewal_units")
    val lifeRenewalUnits: Int?,

    @ColumnInfo(name = "as_built")
    val asBuilt: Boolean?,

    @ColumnInfo(name = "existing_age_band")
    val existingAgeBand: Int?,

    @ColumnInfo(name = "image_paths")
    val imagePaths: String,

    @ColumnInfo(name = "no_access_reason")
    val noAccessReason: String,

    @ColumnInfo(name = "entry_timestamp")
    val entryTimestamp: String,

    @ColumnInfo(name = "sync_id")
    val syncId: Int?
)
