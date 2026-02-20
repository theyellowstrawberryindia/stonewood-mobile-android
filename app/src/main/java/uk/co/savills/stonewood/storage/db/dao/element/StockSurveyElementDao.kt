package uk.co.savills.stonewood.storage.db.dao.element

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.element.StockSurveyElementEntity

@Dao
interface StockSurveyElementDao {
    @Query(
        "SELECT * FROM stock_survey_element_table WHERE project_Id = :projectId " +
            "AND survey_type IN (:surveyTypes) " +
            "ORDER BY sequence_number"
    )
    fun getElements(projectId: String, surveyTypes: List<String>): List<StockSurveyElementEntity>

    @Query("SELECT * FROM stock_survey_element_table WHERE project_id = :projectId AND title = :title")
    fun getElement(projectId: String, title: String): StockSurveyElementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElements(elements: List<StockSurveyElementEntity>)

    @Query("DELETE FROM stock_survey_element_table WHERE project_id = :projectId")
    fun clearElements(projectId: String)

    @Query("DELETE FROM stock_survey_element_table WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM stock_survey_element_table")
    fun clearAll()
}
