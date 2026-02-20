package uk.co.savills.stonewood.storage.db.dao.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uk.co.savills.stonewood.storage.db.entity.entry.RiskAssessmentSurveyElementEntryEntity

@Dao
interface RiskAssessmentSurveyElementEntryDao : SurveyElementEntryDao<RiskAssessmentSurveyElementEntryEntity> {
    @Query("SELECT * FROM risk_assessment_survey_element_result_table WHERE project_id = :projectId")
    override fun getEntries(projectId: String): List<RiskAssessmentSurveyElementEntryEntity>

    @Query(
        "SELECT * FROM risk_assessment_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn = :uprn"
    )
    override fun getEntries(projectId: String, uprn: String): List<RiskAssessmentSurveyElementEntryEntity>

    @Query(
        "SELECT * FROM risk_assessment_survey_element_result_table WHERE element_id = :elementId " +
            "AND project_id = :projectId " +
            "AND property_uprn = :uprn "
    )
    override fun getEntry(elementId: String, projectId: String, uprn: String): RiskAssessmentSurveyElementEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insertEntry(entry: RiskAssessmentSurveyElementEntryEntity)

    @Update
    override fun updateEntry(entry: RiskAssessmentSurveyElementEntryEntity)

    @Query(
        "DELETE FROM risk_assessment_survey_element_result_table WHERE project_id = :projectId " +
            "AND element_id = :elementId " +
            "AND property_uprn= :uprn"
    )
    override fun clearEntry(elementId: String, projectId: String, uprn: String)

    @Query(
        "DELETE FROM risk_assessment_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn IN (:propertyUPRNs)"
    )
    override fun clearEntries(propertyUPRNs: List<String>, projectId: String)

    @Query("DELETE FROM risk_assessment_survey_element_result_table WHERE project_id in (:ids)")
    override fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM risk_assessment_survey_element_result_table")
    override fun clearAll()
}
