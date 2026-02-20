package uk.co.savills.stonewood.repository

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.storage.db.dao.ProjectDao
import uk.co.savills.stonewood.storage.db.entity.project.SurveyEntity
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class ProjectRepository(private val projectDao: ProjectDao) {

    @WorkerThread
    fun getProjects(): List<ProjectModel> {
        return projectDao.getProjects().map(::mapToModel)
    }

    @WorkerThread
    fun insertProjects(projects: List<ProjectModel>) {
        clearAll()

        projectDao.insertProjects(projects.map(::mapToEntity))

        val surveys = mutableListOf<SurveyEntity>()
        for (project in projects) {
            surveys.addAll(project.surveys.map { mapToEntity(it, project.id) })
        }
        projectDao.insertSurveys(surveys)
    }

    @WorkerThread
    fun clear(ids: List<String>) {
        projectDao.clearProjects(ids)
        projectDao.clearSurveys(ids)
    }

    @WorkerThread
    fun clearAll() {
        projectDao.clearProjects()
        projectDao.clearSurveys()
    }
}
