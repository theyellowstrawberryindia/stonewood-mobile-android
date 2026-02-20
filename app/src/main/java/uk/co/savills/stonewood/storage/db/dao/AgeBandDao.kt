package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.AgeBandEntity

@Dao
interface AgeBandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBands(reasons: List<AgeBandEntity>)

    @Query("SELECT * FROM age_band_table WHERE project_id = :projectId ORDER BY lower_bound")
    fun getBands(projectId: String): List<AgeBandEntity>

    @Query("DELETE FROM age_band_table WHERE project_id = :projectId")
    fun clearBands(projectId: String)

    @Query("DELETE FROM age_band_table WHERE project_id in (:ids)")
    fun clearProjectBands(ids: List<String>)

    @Query("DELETE FROM age_band_table")
    fun clearAll()
}
