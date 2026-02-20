package uk.co.savills.stonewood.storage.db.dao.element

import android.annotation.SuppressLint
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.element.HHSRSSurveyElementEntity

@SuppressLint("InvalidClassName")
@Dao
interface HHSRSSurveyElementDao {
    @Query("SELECT * FROM hhsrs_survey_element_table WHERE project_Id = :projectId ORDER BY sequence_number")
    fun getElements(projectId: String): List<HHSRSSurveyElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElements(elements: List<HHSRSSurveyElementEntity>)

    @Query("DELETE FROM hhsrs_survey_element_table WHERE project_id = :projectId")
    fun clearElements(projectId: String)

    @Query("DELETE FROM hhsrs_survey_element_table WHERE project_id in (:ids)")
    fun clearProjectElements(ids: List<String>)

    @Query("DELETE FROM hhsrs_survey_element_table")
    fun clearAll()
}
