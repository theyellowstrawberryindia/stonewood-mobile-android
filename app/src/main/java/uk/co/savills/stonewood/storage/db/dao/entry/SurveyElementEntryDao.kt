package uk.co.savills.stonewood.storage.db.dao.entry

import uk.co.savills.stonewood.storage.db.entity.entry.SurveyElementEntryEntity

interface SurveyElementEntryDao<Entity : SurveyElementEntryEntity> {
    fun getEntries(projectId: String): List<Entity>
    fun getEntries(projectId: String, uprn: String): List<Entity>
    fun getEntry(elementId: String, projectId: String, uprn: String): Entity?
    fun insertEntry(entry: Entity)
    fun updateEntry(entry: Entity)
    fun clearEntry(elementId: String, projectId: String, uprn: String)
    fun clearEntries(propertyUPRNs: List<String>, projectId: String)
    fun clearProjectEntries(ids: List<String>)
    fun clearAll()
}
