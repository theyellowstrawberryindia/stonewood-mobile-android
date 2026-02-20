package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import uk.co.savills.stonewood.storage.db.entity.project.ProjectEntity
import uk.co.savills.stonewood.storage.db.entity.project.ProjectWithSurveys
import uk.co.savills.stonewood.storage.db.entity.project.SurveyEntity

@Dao
interface ProjectDao {

    @Transaction
    @Query("SELECT * FROM project_table ORDER BY id")
    fun getProjects(): List<ProjectWithSurveys>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProjects(projects: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveys(surveys: List<SurveyEntity>)

    @Query("DELETE FROM project_table WHERE id in (:ids)")
    fun clearProjects(ids: List<String>)

    @Query("DELETE FROM project_table")
    fun clearProjects()

    @Query("DELETE FROM survey_table WHERE project_id in (:projectIds)")
    fun clearSurveys(projectIds: List<String>)

    @Query("DELETE FROM survey_table")
    fun clearSurveys()
}
