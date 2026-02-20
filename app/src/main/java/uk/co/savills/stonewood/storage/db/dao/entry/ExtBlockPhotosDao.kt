package uk.co.savills.stonewood.storage.db.dao.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.entry.ExtBlockPhotoEntity

@Dao
interface ExtBlockPhotosDao {
    @Query(
        "SELECT * FROM energy_ext_photo WHERE project_id = :projectId " +
            "AND (property_uprn LIKE '%' || :searchText || '%' " +
            "OR number LIKE '%' || :searchText || '%' " +
            "OR address1 LIKE '%' || :searchText || '%' " +
            "OR address2 LIKE '%' || :searchText || '%' " +
            "OR address3 LIKE '%' || :searchText || '%' " +
            "OR address4 LIKE '%' || :searchText || '%' " +
            "OR postal_code LIKE '%' || :searchText || '%' " +
            "OR surveyor LIKE '%' || :searchText || '%')"
    )
    fun getEntries(projectId: String, searchText: String): List<ExtBlockPhotoEntity>

    @Query("SELECT * FROM energy_ext_photo WHERE project_id = :projectId")
    fun getEntries(projectId: String): List<ExtBlockPhotoEntity>

    @Query("SELECT * FROM energy_ext_photo WHERE project_id = :projectId AND property_uprn = :propertyUPRN")
    fun getEntry(projectId: String, propertyUPRN: String): ExtBlockPhotoEntity?

    @Query("SELECT * FROM energy_ext_photo WHERE id IS NULL AND project_id = :projectId AND property_uprn in (:propertyUPRNs)")
    fun getNewEntries(projectId: String, propertyUPRNs: List<String>): List<ExtBlockPhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntries(entries: List<ExtBlockPhotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: ExtBlockPhotoEntity)

    @Query("DELETE FROM energy_ext_photo WHERE project_id = :projectId AND property_uprn = :propertyUPRN")
    fun clearEntry(projectId: String, propertyUPRN: String)

    @Query("DELETE FROM energy_ext_photo WHERE project_id = :projectId AND id != null")
    fun clearOldEntries(projectId: String)

    @Query("DELETE FROM energy_ext_photo WHERE project_id in (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM energy_ext_photo")
    fun clearAll()
}
