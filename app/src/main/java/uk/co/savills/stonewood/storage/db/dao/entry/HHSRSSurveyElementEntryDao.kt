package uk.co.savills.stonewood.storage.db.dao.entry

import android.annotation.SuppressLint
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uk.co.savills.stonewood.storage.db.entity.entry.HHSRSSurveyElementEntryEntity

@SuppressLint("InvalidClassName")
@Dao
interface HHSRSSurveyElementEntryDao : SurveyElementEntryDao<HHSRSSurveyElementEntryEntity> {
    @Query("SELECT * FROM hhsrs_survey_element_result_table WHERE project_id = :projectId")
    override fun getEntries(projectId: String): List<HHSRSSurveyElementEntryEntity>

    @Query(
        "SELECT * FROM hhsrs_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn = :uprn"
    )
    override fun getEntries(projectId: String, uprn: String): List<HHSRSSurveyElementEntryEntity>

    @Query(
        "SELECT * FROM hhsrs_survey_element_result_table WHERE element_id = :elementId " +
            "AND project_id = :projectId " +
            "AND property_uprn = :uprn "
    )
    override fun getEntry(elementId: String, projectId: String, uprn: String): HHSRSSurveyElementEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insertEntry(entry: HHSRSSurveyElementEntryEntity)

    @Update
    override fun updateEntry(entry: HHSRSSurveyElementEntryEntity)

    @Query(
        "DELETE FROM hhsrs_survey_element_result_table WHERE project_id = :projectId " +
            "AND element_id = :elementId " +
            "AND property_uprn= :uprn"
    )
    override fun clearEntry(elementId: String, projectId: String, uprn: String)

    @Query(
        "DELETE FROM hhsrs_survey_element_result_table WHERE project_id = :projectId " +
            "AND property_uprn IN (:propertyUPRNs)"
    )
    override fun clearEntries(propertyUPRNs: List<String>, projectId: String)

    @Query("DELETE FROM hhsrs_survey_element_result_table WHERE project_id in (:ids)")
    override fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM hhsrs_survey_element_result_table")
    override fun clearAll()
}
