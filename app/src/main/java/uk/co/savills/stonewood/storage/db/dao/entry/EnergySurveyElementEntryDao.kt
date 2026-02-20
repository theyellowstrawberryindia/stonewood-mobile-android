package uk.co.savills.stonewood.storage.db.dao.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.entry.EnergySurveyElementEntryEntity

@Dao
interface EnergySurveyElementEntryDao {
    @Query("SELECT * FROM energy_survey_element_result_table WHERE project_id = :projectId")
    fun getEntries(projectId: String): List<EnergySurveyElementEntryEntity>

    @Query(
        "SELECT * FROM energy_survey_element_result_table " +
            "WHERE project_id = :projectId AND property_uprn = :propertyUPRN"
    )
    fun getEntries(
        projectId: String,
        propertyUPRN: String
    ): List<EnergySurveyElementEntryEntity>

    @Query(
        "SELECT * FROM energy_survey_element_result_table WHERE element_id = :elementId " +
            "AND project_id = :projectId " +
            "AND property_uprn = :uprn "
    )
    fun getEntry(
        elementId: Int,
        projectId: String,
        uprn: String
    ): EnergySurveyElementEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: EnergySurveyElementEntryEntity)

    @Query(
        "DELETE FROM energy_survey_element_result_table " +
            "WHERE element_id = :elementId AND " +
            "property_uprn = :propertyUPRN AND " +
            "project_id = :projectId"
    )
    fun clearEntry(elementId: Int, propertyUPRN: String, projectId: String)

    @Query(
        "DELETE FROM energy_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn IN (:propertyUPRNs)"
    )
    fun clearEntries(propertyUPRNs: List<String>, projectId: String)

    @Query("DELETE FROM energy_survey_element_result_table WHERE project_id IN (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM energy_survey_element_result_table")
    fun clearAll()
}
