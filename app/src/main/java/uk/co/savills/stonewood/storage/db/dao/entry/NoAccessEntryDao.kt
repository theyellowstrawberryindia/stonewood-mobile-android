package uk.co.savills.stonewood.storage.db.dao.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.entry.NoAccessEntryEntity

@Dao
interface NoAccessEntryDao {

    @Query("SELECT * FROM no_access_entry_table WHERE project_id = :projectId")
    fun getEntries(projectId: String): List<NoAccessEntryEntity>

    @Query(
        "SELECT * FROM no_access_entry_table WHERE project_id = :projectId " +
            "AND uprn = :propertyUPRN"
    )
    fun getEntries(projectId: String, propertyUPRN: String): List<NoAccessEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: NoAccessEntryEntity)

    @Query(
        "DELETE FROM no_access_entry_table WHERE project_id = :projectId " +
            "AND uprn IN (:propertyUPRNs)"
    )
    fun clearEntries(propertyUPRNs: List<String>, projectId: String)

    @Query("DELETE FROM no_access_entry_table WHERE project_id in (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM no_access_entry_table")
    fun clearAll()
}
