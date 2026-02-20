package uk.co.savills.stonewood.storage.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uk.co.savills.stonewood.storage.db.entity.property.PropertyEntity
import uk.co.savills.stonewood.storage.db.entity.property.PropertyWithNoAccessHistory

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property_table WHERE project_Id = :projectId AND is_deleted = 0 ORDER BY `order`")
    fun getProperties(projectId: String): List<PropertyEntity>

    @Query("SELECT * FROM property_table WHERE `key` = :key")
    fun getProperty(key: String): PropertyEntity?

    @Query("SELECT COUNT(key) FROM property_table WHERE project_Id = :projectId AND is_deleted = 0")
    fun getPropertyCount(projectId: String): Int

    @Transaction
    @Query("SELECT * FROM property_table WHERE project_Id = :projectId AND is_deleted = 0 ORDER BY `order`")
    fun getPropertiesWithNoAccessHistoryPagingSource(projectId: String): PagingSource<Int, PropertyWithNoAccessHistory>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(properties: List<PropertyEntity>)

    @Update
    fun update(property: PropertyEntity)

    @Transaction
    @Query(
        "SELECT * FROM property_table WHERE project_Id = :projectId " +
            "AND (uprn LIKE '%' || :text || '%' " +
            "OR number LIKE '%' || :text || '%' " +
            "OR strata LIKE '%' || :text || '%' " +
            "OR address1 LIKE '%' || :text || '%' " +
            "OR address2 LIKE '%' || :text || '%' " +
            "OR address3 LIKE '%' || :text || '%' " +
            "OR address4 LIKE '%' || :text || '%') " +
            "ORDER BY `order`"
    )
    fun searchPropertiesWithNoAccessHistoryPagingSource(
        projectId: String,
        text: String
    ): PagingSource<Int, PropertyWithNoAccessHistory>

    @Query(
        "SELECT COUNT(key) FROM property_table WHERE project_Id = :projectId " +
            "AND (uprn LIKE '%' || :searchText || '%' " +
            "OR number LIKE '%' || :searchText || '%' " +
            "OR strata LIKE '%' || :searchText || '%' " +
            "OR address1 LIKE '%' || :searchText || '%' " +
            "OR address2 LIKE '%' || :searchText || '%' " +
            "OR address3 LIKE '%' || :searchText || '%' " +
            "OR address4 LIKE '%' || :searchText || '%')"
    )
    fun getFilteredPropertyCount(projectId: String, searchText: String): Int

    @Query("DELETE FROM property_table WHERE project_Id in (:ids)")
    fun clearProjectProperties(ids: List<String>)

    @Query("DELETE FROM property_table WHERE `key` IN (:propertyKeys)")
    fun clearProperties(propertyKeys: List<String>)

    @Query("DELETE FROM property_table")
    fun clearAll()
}
