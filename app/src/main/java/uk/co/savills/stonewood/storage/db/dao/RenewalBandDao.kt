package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.RenewalBandEntity

@Dao
interface RenewalBandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBands(reasons: List<RenewalBandEntity>)

    @Query("SELECT * FROM renewal_band_table WHERE project_id = :projectId  ORDER BY lower_bound")
    fun getBands(projectId: String): List<RenewalBandEntity>

    @Query("DELETE FROM renewal_band_table WHERE project_id = :projectId")
    fun clearBands(projectId: String)

    @Query("DELETE FROM renewal_band_table WHERE project_id in (:ids)")
    fun clearProjectBands(ids: List<String>)

    @Query("DELETE FROM renewal_band_table")
    fun clearAll()
}
