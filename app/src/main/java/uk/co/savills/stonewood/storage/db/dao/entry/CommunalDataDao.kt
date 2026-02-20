package uk.co.savills.stonewood.storage.db.dao.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.entry.CommunalDataEntity

@Dao
interface CommunalDataDao {
    @Query(
        "SELECT * FROM communal_data WHERE element = :element AND project_id = :projectId " +
            "AND (property_uprn LIKE '%' || :searchText || '%' " +
            "OR number LIKE '%' || :searchText || '%' " +
            "OR address1 LIKE '%' || :searchText || '%' " +
            "OR address2 LIKE '%' || :searchText || '%' " +
            "OR address3 LIKE '%' || :searchText || '%' " +
            "OR address4 LIKE '%' || :searchText || '%' " +
            "OR postal_code LIKE '%' || :searchText || '%' " +
            "OR surveyor LIKE '%' || :searchText || '%')"
    )
    fun getEntries(element: String, projectId: String, searchText: String): List<CommunalDataEntity>

    @Query("SELECT * FROM communal_data WHERE project_id = :projectId")
    fun getEntries(projectId: String): List<CommunalDataEntity>

    @Query("SELECT * FROM communal_data WHERE id IS NULL AND project_id = :projectId AND property_uprn in (:propertyUPRNs)")
    fun getNewEntries(projectId: String, propertyUPRNs: List<String>): List<CommunalDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntries(entries: List<CommunalDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: CommunalDataEntity)

    @Query("DELETE FROM communal_data WHERE element =:element AND communal_part_number =:communalPartNumber AND project_id =:projectId AND property_uprn=:propertyUPRN")
    fun delete(element: String, communalPartNumber: Int, projectId: String, propertyUPRN: String)

    @Query("DELETE FROM communal_data WHERE project_id = :projectId AND id != null")
    fun clearOldEntries(projectId: String)

    @Query("DELETE FROM communal_data WHERE project_id in (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM communal_data")
    fun clearAll()
}
