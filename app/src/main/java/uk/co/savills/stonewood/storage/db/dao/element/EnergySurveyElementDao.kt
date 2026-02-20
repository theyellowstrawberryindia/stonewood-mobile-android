package uk.co.savills.stonewood.storage.db.dao.element

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import uk.co.savills.stonewood.storage.db.entity.element.EnergySurveyElementEntity

@Dao
interface EnergySurveyElementDao {
    @Transaction
    @Query("SELECT * FROM energy_survey_element_table WHERE project_Id = :projectId ORDER BY serial_number")
    fun getElements(projectId: String): List<EnergySurveyElementEntity>

    @Query("SELECT * FROM energy_survey_element_table WHERE project_id = :projectId AND title_short = :title")
    fun getElement(projectId: String, title: String): EnergySurveyElementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElements(elements: List<EnergySurveyElementEntity>)

    @Query("DELETE FROM energy_survey_element_table WHERE project_id = :projectId")
    fun clearElements(projectId: String)

    @Query("DELETE FROM energy_survey_element_table WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM energy_survey_element_table")
    fun clearAll()
}
