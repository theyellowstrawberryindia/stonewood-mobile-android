package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.NoAccessReasonEntity

@Dao
interface NoAccessReasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReasons(reasons: List<NoAccessReasonEntity>)

    @Query("SELECT * FROM no_access_reason_table WHERE project_id = :projectId")
    fun getReasons(projectId: String): List<NoAccessReasonEntity>

    @Query("DELETE FROM no_access_reason_table WHERE project_id = :projectId")
    fun clearReasons(projectId: String)

    @Query("DELETE FROM no_access_reason_table WHERE project_id in (:ids)")
    fun clearProjectReasons(ids: List<String>)

    @Query("DELETE FROM no_access_reason_table")
    fun clearAll()
}
