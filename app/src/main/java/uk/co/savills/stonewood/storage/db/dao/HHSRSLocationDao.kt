package uk.co.savills.stonewood.storage.db.dao

import android.annotation.SuppressLint
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.HHSRSLocationEntity

@SuppressLint("InvalidClassName")
@Dao
interface HHSRSLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(reasons: List<HHSRSLocationEntity>)

    @Query("SELECT * FROM hhsrs_location_table WHERE project_id = :projectId")
    fun getLocations(projectId: String): List<HHSRSLocationEntity>

    @Query("DELETE FROM hhsrs_location_table WHERE project_id = :projectId")
    fun clearLocations(projectId: String)

    @Query("DELETE FROM hhsrs_location_table WHERE project_id in (:ids)")
    fun clearProjectLocations(ids: List<String>)

    @Query("DELETE FROM hhsrs_location_table")
    fun clearAll()
}
