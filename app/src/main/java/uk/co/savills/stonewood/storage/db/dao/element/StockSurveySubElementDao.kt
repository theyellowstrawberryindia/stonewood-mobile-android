package uk.co.savills.stonewood.storage.db.dao.element

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.element.StockSurveySubElementEntity

@Dao
interface StockSurveySubElementDao {
    @Query(
        "SELECT * FROM stock_survey_sub_element_table WHERE project_Id = :projectId " +
            "AND element_id = :elementId " +
            "ORDER BY number"
    )
    fun getElements(projectId: String, elementId: Int): List<StockSurveySubElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElements(elements: List<StockSurveySubElementEntity>)

    @Query("DELETE FROM stock_survey_sub_element_table WHERE project_id = :projectId")
    fun clearElements(projectId: String)

    @Query("DELETE FROM stock_survey_sub_element_table WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM stock_survey_sub_element_table")
    fun clearAll()
}
