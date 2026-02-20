package uk.co.savills.stonewood.storage.db.dao

import android.annotation.SuppressLint
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.HHSRSSevereIssueEntity

@SuppressLint("InvalidClassName")
@Dao
interface HHSRSSevereIssueDao {

    @Query("SELECT * FROM hhsrs_severe_issue")
    fun getAll(): List<HHSRSSevereIssueEntity>

    @Query("SELECT * FROM hhsrs_severe_issue WHERE is_reported = 0")
    fun getUnreportedIssues(): List<HHSRSSevereIssueEntity>

    @Query("SELECT * FROM hhsrs_severe_issue WHERE element_id = :elementId AND property_id = :propertyId AND project_id = :projectId")
    fun getIssue(
        elementId: String,
        propertyId: Int,
        projectId: String
    ): HHSRSSevereIssueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIssue(issue: HHSRSSevereIssueEntity)

    @Query("DELETE FROM hhsrs_severe_issue WHERE element_id = :elementId AND property_id = :propertyId AND project_id = :projectId")
    fun clearIssue(
        elementId: String,
        propertyId: Int,
        projectId: String
    )

    @Query("DELETE FROM hhsrs_severe_issue WHERE project_id in (:ids)")
    fun clearProjectEntries(ids: List<String>)

    @Query("DELETE FROM hhsrs_severe_issue")
    fun clearAll()
}
