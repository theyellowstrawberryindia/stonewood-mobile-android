package uk.co.savills.stonewood.storage.db.dao.element

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.element.EnergySurveySubElementEntity

@Dao
interface EnergySurveySubElementDao {
    @Query(
        "SELECT * FROM energy_survey_sub_element_table " +
            "WHERE project_Id = :projectId " +
            "AND element_id = :elementId " +
            "ORDER BY serial_number"
    )
    fun getElements(projectId: String, elementId: Int): List<EnergySurveySubElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElements(elements: List<EnergySurveySubElementEntity>)

    @Query("DELETE FROM energy_survey_sub_element_table WHERE project_id = :projectId")
    fun clearElements(projectId: String)

    @Query("DELETE FROM energy_survey_sub_element_table WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM energy_survey_sub_element_table")
    fun clearAll()
}
