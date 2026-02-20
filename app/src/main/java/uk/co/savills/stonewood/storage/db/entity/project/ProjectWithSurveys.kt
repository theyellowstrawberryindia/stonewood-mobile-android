package uk.co.savills.stonewood.storage.db.entity.project

import androidx.room.Embedded
import androidx.room.Relation

data class ProjectWithSurveys(
    @Embedded val project: ProjectEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "project_id"
    )
    val surveys: List<SurveyEntity>
)
