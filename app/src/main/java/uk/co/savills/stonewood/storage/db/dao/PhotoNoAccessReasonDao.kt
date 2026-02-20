package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.PhotoNoAccessReasonEntity

@Dao
interface PhotoNoAccessReasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReasons(reasons: List<PhotoNoAccessReasonEntity>)

    @Query("SELECT * FROM photo_no_access_reason WHERE project_id = :projectId ORDER BY id")
    fun getReasons(projectId: String): List<PhotoNoAccessReasonEntity>

    @Query("DELETE FROM photo_no_access_reason WHERE project_id = :projectId")
    fun clearReasons(projectId: String)

    @Query("DELETE FROM photo_no_access_reason WHERE project_id in (:ids)")
    fun clearProjectReasons(ids: List<String>)

    @Query("DELETE FROM photo_no_access_reason")
    fun clearAll()
}
