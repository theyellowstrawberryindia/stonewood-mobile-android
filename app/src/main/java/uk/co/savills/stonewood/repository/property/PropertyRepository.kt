package uk.co.savills.stonewood.repository.property

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.storage.db.dao.PropertyDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class PropertyRepository(
    private val propertyDao: PropertyDao,
    private val appState: AppState
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getProperties(projectId: String): List<PropertyModel> {
        return propertyDao.getProperties(projectId).map(::mapToModel)
    }

    fun getProperty(projectId: String, id: Int): PropertyModel? {
        return propertyDao.getProperty("$projectId$id")?.let(::mapToModel)
    }

    @WorkerThread
    fun getPropertyCount() = propertyDao.getPropertyCount(projectId)

    @WorkerThread
    fun getPropertyPagingSource(): PropertyPagingSource {
        return PropertyPagingSource(
            propertyDao.getPropertiesWithNoAccessHistoryPagingSource(projectId)
        )
    }

    @WorkerThread
    fun insertProperties(properties: List<PropertyModel>) {
        for (property in properties) {
            val cached = propertyDao.getProperty("${projectId}${property.id}")

            cached?.apply {
                order = property.order
                hasExternalPhoto = property.hasExternalPhoto
                TA = property.TA
                strata = property.strata
                number = property.address.number
                address1 = property.address.line1
                address2 = property.address.line2
                address3 = property.address.line3
                address4 = property.address.line4
                postalCode = property.address.postalCode
                contactNumber1 = property.contact.number
                contactNumber2 = property.contact.numberSecondary
                contactNotes = property.contact.notes
            }

            if (cached != null) {
                propertyDao.clearProperties(listOf("${projectId}${cached.id}"))
                propertyDao.insert(listOf(cached))
            } else {
                propertyDao.insert(listOf(mapToEntity(property, projectId)))
            }
        }
    }

    @WorkerThread
    fun updateProperty(property: PropertyModel) {
        val entity = mapToEntity(property, projectId)
        propertyDao.update(entity)
    }

    @WorkerThread
    fun getSearchPropertyPagingSource(text: String): PropertyPagingSource {
        return PropertyPagingSource(
            propertyDao.searchPropertiesWithNoAccessHistoryPagingSource(projectId, text)
        )
    }

    @WorkerThread
    fun getFilteredPropertyCount(searchText: String) =
        propertyDao.getFilteredPropertyCount(projectId, searchText)

    @WorkerThread
    fun markPropertiesAsDeleted(projectId: String, id: Int) {
        val property = propertyDao.getProperty("$projectId$id") ?: return
        property.isDeleted = true
        propertyDao.update(property)
    }

    @WorkerThread
    fun clearProjectProperties(ids: List<String>) {
        propertyDao.clearProjectProperties(ids)
    }

    @WorkerThread
    fun clearProperties(propertyIds: List<Int>) {
        val keys = propertyIds.map { "$projectId$it" }
        keys.chunked(100).forEach(propertyDao::clearProperties)
    }

    @WorkerThread
    fun clearAll() {
        propertyDao.clearAll()
    }

    companion object {
        const val EXT_BLOCK_PHOTOS_DIRECTORY = "energy_ext_photos"
        const val EXT_BLOCK_PHOTO_TAG = "ExternalBlock"
    }
}
