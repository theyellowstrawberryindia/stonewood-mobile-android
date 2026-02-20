package uk.co.savills.stonewood.storage.db.dao.element

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.element.ValidationElementEntity

@Dao
interface ValidationElementDao {

    @Query("SELECT * FROM validation_elements WHERE project_id = :projectId")
    fun get(projectId: String): List<ValidationElementEntity>

    @Query("SELECT * FROM validation_elements WHERE project_id = :projectId AND category in (:category)")
    fun get(projectId: String, category: List<Int>): List<ValidationElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(elements: List<ValidationElementEntity>)

    @Query("DELETE FROM validation_elements WHERE project_id = :projectId")
    fun clear(projectId: String)

    @Query("DELETE FROM validation_elements WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM validation_elements")
    fun clearAll()
}
