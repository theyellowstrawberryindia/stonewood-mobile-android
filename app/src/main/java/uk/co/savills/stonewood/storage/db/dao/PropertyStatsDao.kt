package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.property.PropertyStatsEntity

@Dao
interface PropertyStatsDao {

    @Query("SELECT * FROM property_stats WHERE project_id = :projectId")
    fun getStats(projectId: String): List<PropertyStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStats(statistics: List<PropertyStatsEntity>)

    @Query("DELETE FROM property_stats WHERE project_id in (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM property_stats WHERE project_id = :projectId")
    fun clear(projectId: String)

    @Query("DELETE FROM property_stats")
    fun clearAll()
}
