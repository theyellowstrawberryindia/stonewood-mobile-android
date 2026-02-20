package uk.co.savills.stonewood.storage.db.dao.element

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.element.QualityStandardSurveyElementEntity

@Dao
interface QualityStandardSurveyElementDao {
    @Query("SELECT * FROM quality_standard_survey_element_table WHERE project_Id = :projectId ORDER BY sequence_number")
    fun getElements(projectId: String): List<QualityStandardSurveyElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElements(elements: List<QualityStandardSurveyElementEntity>)

    @Query("DELETE FROM quality_standard_survey_element_table WHERE project_id = :projectId")
    fun clearElements(projectId: String)

    @Query("DELETE FROM quality_standard_survey_element_table WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM quality_standard_survey_element_table")
    fun clearAll()
}
