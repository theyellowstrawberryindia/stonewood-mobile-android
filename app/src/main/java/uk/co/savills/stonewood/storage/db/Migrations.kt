@file:Suppress("InvalidMethodName")

package uk.co.savills.stonewood.storage.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE property_table ADD COLUMN 'is_deleted' INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE project_table ADD COLUMN 'are_repairs_available' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE project_table ADD COLUMN 'is_closed' INTEGER NOT NULL DEFAULT 0")

        database.execSQL("CREATE TABLE photo_no_access_reason('id' TEXT NOT NULL, project_id TEXT NOT NULL, reason TEXT NOT NULL, PRIMARY KEY(id, project_id))")

        database.execSQL("ALTER TABLE stock_survey_element_result_table ADD COLUMN 'no_access_reason' TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE stock_survey_element_result_table ADD COLUMN 'is_cloned' INTEGER")
        database.execSQL("ALTER TABLE stock_survey_element_result_table ADD COLUMN 'is_individual' INTEGER")

        database.execSQL("ALTER TABLE energy_survey_sub_element_table ADD COLUMN 'is_rare' INTEGER NOT NULL DEFAULT 0")

        database.execSQL("ALTER TABLE energy_survey_element_table ADD COLUMN 'warn_value' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE energy_survey_element_table ADD COLUMN 'warn_value_low' INTEGER NOT NULL DEFAULT 0")

        database.execSQL("ALTER TABLE stock_survey_element_table ADD COLUMN 'is_as_built_required' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE stock_survey_element_table ADD COLUMN 'use_quantity_adder' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE stock_survey_element_table ADD COLUMN 'is_communal' INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE stock_survey_element_table ADD COLUMN 'warn_value_low' INTEGER NOT NULL DEFAULT 0")

        database.execSQL(
            "CREATE TABLE communal_data(" +
                "'id' INTEGER, " +
                "element TEXT NOT NULL, " +
                "property_uprn TEXT NOT NULL, " +
                "project_id TEXT NOT NULL, " +
                "number TEXT NOT NULL, " +
                "address1 TEXT NOT NULL, " +
                "address2 TEXT NOT NULL, " +
                "address3 TEXT NOT NULL, " +
                "address4 TEXT NOT NULL, " +
                "postal_code TEXT NOT NULL, " +
                "surveyor TEXT NOT NULL, " +
                "communal_part_number INTEGER NOT NULL, " +
                "sub_element_number INTEGER NOT NULL, " +
                "sub_element TEXT NOT NULL, " +
                "sub_element_user_entry TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "repair INTEGER, " +
                "repair_description TEXT NOT NULL, " +
                "repair_spot_price INTEGER, " +
                "life_renewal_band INTEGER, " +
                "life_renewal_units INTEGER, " +
                "as_built INTEGER, " +
                "existing_age_band INTEGER, " +
                "image_paths TEXT NOT NULL, " +
                "no_access_reason TEXT NOT NULL, " +
                "entry_timestamp TEXT NOT NULL, " +
                "sync_id INTEGER, " +
                "PRIMARY KEY(element, communal_part_number, property_uprn, project_id))"
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE project_table ADD COLUMN 'photo_count' INTEGER NOT NULL DEFAULT 0")

        database.execSQL("ALTER TABLE energy_survey_sub_element_table ADD COLUMN 'is_shared_photos' INTEGER NOT NULL DEFAULT 0")

        database.execSQL("ALTER TABLE property_table ADD COLUMN 'energy_ext_photos' TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE property_table ADD COLUMN 'energy_ext_photos_cloned_from' TEXT NOT NULL DEFAULT ''")

        database.execSQL(
            "CREATE TABLE energy_ext_photo(" +
                "'id' INTEGER, " +
                "property_uprn TEXT NOT NULL, " +
                "project_id TEXT NOT NULL, " +
                "number TEXT NOT NULL, " +
                "address1 TEXT NOT NULL, " +
                "address2 TEXT NOT NULL, " +
                "address3 TEXT NOT NULL, " +
                "address4 TEXT NOT NULL, " +
                "postal_code TEXT NOT NULL, " +
                "surveyor TEXT NOT NULL, " +
                "image_paths TEXT NOT NULL, " +
                "entry_timestamp TEXT NOT NULL, " +
                "sync_id INTEGER, " +
                "PRIMARY KEY(property_uprn, project_id))"
        )
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE energy_survey_element_table ADD COLUMN 'limit_value' INTEGER NOT NULL DEFAULT 0")

        database.execSQL(
            "CREATE TABLE property_stats(" +
                "project_id TEXT NOT NULL, " +
                "section TEXT NOT NULL, " +
                "uprn TEXT NOT NULL, " +
                "strata TEXT NOT NULL, " +
                "is_required INTEGER NOT NULL DEFAULT 0, " +
                "is_complete INTEGER NOT NULL DEFAULT 0, " +
                "PRIMARY KEY(project_id, uprn))"
        )

        database.execSQL(
            "CREATE TABLE validation_elements(" +
                "'id' INTEGER NOT NULL, " +
                "project_id TEXT NOT NULL, " +
                "operator TEXT NOT NULL, " +
                "'group' TEXT NOT NULL, " +
                "left_survey_type INTEGER NOT NULL, " +
                "left_element TEXT NOT NULL, " +
                "left_sub_element TEXT NOT NULL, " +
                "right_survey_type INTEGER NOT NULL, " +
                "category INTEGER NOT NULL DEFAULT 0, " +
                "right_element TEXT NOT NULL, " +
                "right_sub_element TEXT NOT NULL, " +
                "error_message TEXT NOT NULL, " +
                "PRIMARY KEY(id, project_id))"
        )

        database.execSQL("ALTER TABLE property_table ADD COLUMN 'is_validation_complete' INTEGER NOT NULL DEFAULT 1")
    }
}

val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE hhsrs_severe_issue(" +
                "property_id INTEGER NOT NULL, " +
                "project_id TEXT NOT NULL, " +
                "element_id TEXT NOT NULL, " +
                "surveyor_id INTEGER NOT NULL, " +
                "element TEXT NOT NULL, " +
                "remarks TEXT NOT NULL, " +
                "images TEXT NOT NULL, " +
                "internal_locations TEXT NOT NULL, " +
                "external_locations TEXT NOT NULL, " +
                "PRIMARY KEY(project_id, property_id, element_id))"
        )
    }
}

val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE hhsrs_severe_issue ADD COLUMN 'is_reported' INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_7_8: Migration = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE image_upload_history(" +
                "file_path TEXT NOT NULL, " +
                "project_id TEXT NOT NULL, " +
                "PRIMARY KEY(file_path, project_id)" +
                ")"
        )
    }
}

val MIGRATION_8_9: Migration = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE energy_survey_sub_element_table_new(" +
                "`id` TEXT NOT NULL, " +
                "element_id INTEGER NOT NULL, " +
                "project_id TEXT NOT NULL, " +
                "serial_number INTEGER NOT NULL, " +
                "title TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "skip_codes TEXT NOT NULL, " +
                "is_rare INTEGER NOT NULL, " +
                "PRIMARY KEY(id, element_id, project_id)" +
                ")"
        )

        database.execSQL("INSERT INTO energy_survey_sub_element_table_new SELECT `id`, element_id, project_id, serial_number, title, description, skip_codes, is_rare FROM energy_survey_sub_element_table")

        database.execSQL("DROP TABLE energy_survey_sub_element_table")
        database.execSQL("ALTER TABLE energy_survey_sub_element_table_new RENAME TO energy_survey_sub_element_table")

        database.execSQL("ALTER TABLE property_table ADD COLUMN 'has_external_photo' INTEGER NOT NULL DEFAULT 0")
    }
}
