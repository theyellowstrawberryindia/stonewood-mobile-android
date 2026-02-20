package uk.co.savills.stonewood.storage.db.dao.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.entry.StockSurveyElementEntryEntity

@Dao
interface StockSurveyElementEntryDao {
    @Query("SELECT * FROM stock_survey_element_result_table WHERE project_id = :projectId ORDER BY sequence_number")
    fun getEntries(projectId: String): List<StockSurveyElementEntryEntity>

    @Query(
        "SELECT * FROM stock_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn= :uprn " +
            "ORDER BY sequence_number"
    )
    fun getEntries(projectId: String, uprn: String): List<StockSurveyElementEntryEntity>

    @Query(
        "SELECT * FROM stock_survey_element_result_table WHERE element_id = :elementId " +
            "AND project_id = :projectId " +
            "AND property_uprn = :uprn "
    )
    fun getEntries(
        elementId: Int,
        projectId: String,
        uprn: String
    ): List<StockSurveyElementEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: StockSurveyElementEntryEntity)

    @Query(
        "DELETE FROM stock_survey_element_result_table WHERE project_id = :projectId " +
            "AND element_id =:elementId " +
            "AND property_uprn= :uprn " +
            "AND communal_part_number = :communalPartNumber"
    )
    fun clearEntry(elementId: Int, communalPartNumber: Int, projectId: String, uprn: String)

    @Query(
        "DELETE FROM stock_survey_element_result_table WHERE project_id = :projectId " +
            "AND communal_part_number IN (:communalPartNumbers)"
    )
    fun clearCommunalAreaEntries(communalPartNumbers: List<Int>, projectId: String)

    @Query(
        "DELETE FROM stock_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn IN (:propertyUPRNs)"
    )
    fun clearEntries(propertyUPRNs: List<String>, projectId: String)

    @Query("DELETE FROM stock_survey_element_result_table WHERE project_id in (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM stock_survey_element_result_table")
    fun clearAll()
}
