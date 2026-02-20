package uk.co.savills.stonewood.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uk.co.savills.stonewood.storage.db.entity.ImageUploadEntity

@Dao
interface ImageUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageUpload: ImageUploadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageUploads: List<ImageUploadEntity>)

    @Query("SELECT * FROM image_upload_history WHERE project_id = :projectId")
    fun getHistory(projectId: String): List<ImageUploadEntity>

    @Query("DELETE FROM image_upload_history WHERE project_id = :projectId")
    fun deleteHistory(projectId: String)

    @Query("DELETE FROM image_upload_history")
    fun deleteAll()
}
