package uk.co.savills.stonewood.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.co.savills.stonewood.storage.db.dao.AgeBandDao
import uk.co.savills.stonewood.storage.db.dao.HHSRSLocationDao
import uk.co.savills.stonewood.storage.db.dao.HHSRSSevereIssueDao
import uk.co.savills.stonewood.storage.db.dao.ImageUploadDao
import uk.co.savills.stonewood.storage.db.dao.NoAccessReasonDao
import uk.co.savills.stonewood.storage.db.dao.PhotoNoAccessReasonDao
import uk.co.savills.stonewood.storage.db.dao.ProjectDao
import uk.co.savills.stonewood.storage.db.dao.PropertyDao
import uk.co.savills.stonewood.storage.db.dao.PropertyStatsDao
import uk.co.savills.stonewood.storage.db.dao.RenewalBandDao
import uk.co.savills.stonewood.storage.db.dao.element.EnergySurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.EnergySurveySubElementDao
import uk.co.savills.stonewood.storage.db.dao.element.HHSRSSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.QualityStandardSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.RiskAssessmentSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.StockSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.StockSurveySubElementDao
import uk.co.savills.stonewood.storage.db.dao.element.ValidationElementDao
import uk.co.savills.stonewood.storage.db.dao.entry.CommunalDataDao
import uk.co.savills.stonewood.storage.db.dao.entry.EnergySurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.ExtBlockPhotosDao
import uk.co.savills.stonewood.storage.db.dao.entry.HHSRSSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.NoAccessEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.QualityStandardSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.RiskAssessmentSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.dao.entry.StockSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.entity.AgeBandEntity
import uk.co.savills.stonewood.storage.db.entity.HHSRSLocationEntity
import uk.co.savills.stonewood.storage.db.entity.HHSRSSevereIssueEntity
import uk.co.savills.stonewood.storage.db.entity.ImageUploadEntity
import uk.co.savills.stonewood.storage.db.entity.NoAccessReasonEntity
import uk.co.savills.stonewood.storage.db.entity.PhotoNoAccessReasonEntity
import uk.co.savills.stonewood.storage.db.entity.RenewalBandEntity
import uk.co.savills.stonewood.storage.db.entity.element.EnergySurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.EnergySurveySubElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.HHSRSSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.QualityStandardSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.RiskAssessmentSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.StockSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.StockSurveySubElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.ValidationElementEntity
import uk.co.savills.stonewood.storage.db.entity.entry.CommunalDataEntity
import uk.co.savills.stonewood.storage.db.entity.entry.EnergySurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.ExtBlockPhotoEntity
import uk.co.savills.stonewood.storage.db.entity.entry.HHSRSSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.NoAccessEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.QualityStandardSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.RiskAssessmentSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.StockSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.project.ProjectEntity
import uk.co.savills.stonewood.storage.db.entity.project.SurveyEntity
import uk.co.savills.stonewood.storage.db.entity.property.PropertyEntity
import uk.co.savills.stonewood.storage.db.entity.property.PropertyStatsEntity

@Database(
    version = 9,
    entities = [
        ProjectEntity::class,
        SurveyEntity::class,
        PropertyEntity::class,
        NoAccessReasonEntity::class,
        HHSRSLocationEntity::class,
        AgeBandEntity::class,
        RenewalBandEntity::class,
        EnergySurveyElementEntity::class,
        EnergySurveySubElementEntity::class,
        StockSurveyElementEntity::class,
        StockSurveySubElementEntity::class,
        PhotoNoAccessReasonEntity::class,
        QualityStandardSurveyElementEntity::class,
        RiskAssessmentSurveyElementEntity::class,
        HHSRSSurveyElementEntity::class,
        EnergySurveyElementEntryEntity::class,
        HHSRSSurveyElementEntryEntity::class,
        QualityStandardSurveyElementEntryEntity::class,
        RiskAssessmentSurveyElementEntryEntity::class,
        StockSurveyElementEntryEntity::class,
        ValidationElementEntity::class,
        NoAccessEntryEntity::class,
        CommunalDataEntity::class,
        ExtBlockPhotoEntity::class,
        PropertyStatsEntity::class,
        HHSRSSevereIssueEntity::class,
        ImageUploadEntity::class
    ],
    exportSchema = false
)
abstract class SurveyDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun propertyDao(): PropertyDao
    abstract fun noAccessReasonDao(): NoAccessReasonDao
    abstract fun hhsrsLocationDao(): HHSRSLocationDao
    abstract fun ageBandDao(): AgeBandDao
    abstract fun renewalBandDao(): RenewalBandDao

    abstract fun energySurveyElementDao(): EnergySurveyElementDao
    abstract fun energySurveySubElementDao(): EnergySurveySubElementDao
    abstract fun qualityStandardSurveyElementDao(): QualityStandardSurveyElementDao
    abstract fun hhsrsSurveyElementDao(): HHSRSSurveyElementDao
    abstract fun riskAssessmentSurveyElementDao(): RiskAssessmentSurveyElementDao
    abstract fun stockSurveyElementDao(): StockSurveyElementDao
    abstract fun stockSurveySubElementDao(): StockSurveySubElementDao
    abstract fun validationElementDao(): ValidationElementDao
    abstract fun photoNoAccessReasonDao(): PhotoNoAccessReasonDao

    abstract fun energySurveyElementEntryDao(): EnergySurveyElementEntryDao
    abstract fun hhsrsSurveyElementEntryDao(): HHSRSSurveyElementEntryDao
    abstract fun qualityStandardSurveyElementEntryDao(): QualityStandardSurveyElementEntryDao
    abstract fun riskAssessmentSurveyElementEntryDao(): RiskAssessmentSurveyElementEntryDao
    abstract fun stockSurveyElementEntryDao(): StockSurveyElementEntryDao
    abstract fun noAccessEntryDao(): NoAccessEntryDao
    abstract fun communalDataDao(): CommunalDataDao
    abstract fun extBlockPhotosDao(): ExtBlockPhotosDao
    abstract fun hhsrsSevereIssueDao(): HHSRSSevereIssueDao

    abstract fun propertyStatsDao(): PropertyStatsDao
    abstract fun imageUploadDao(): ImageUploadDao

    companion object {
        private const val NAME = "survey_database"

        fun getDatabase(context: Context): SurveyDatabase {
            return Room
                .databaseBuilder(context, SurveyDatabase::class.java, NAME)
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9
                )
                .build()
        }
    }
}
