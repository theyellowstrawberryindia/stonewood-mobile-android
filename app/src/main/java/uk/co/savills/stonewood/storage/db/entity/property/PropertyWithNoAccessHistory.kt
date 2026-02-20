package uk.co.savills.stonewood.storage.db.entity.property

import androidx.room.Embedded
import androidx.room.Relation
import uk.co.savills.stonewood.storage.db.entity.entry.NoAccessEntryEntity

data class PropertyWithNoAccessHistory(
    @Embedded val property: PropertyEntity,

    @Relation(
        parentColumn = "key",
        entityColumn = "property_key"
    )
    val noAccessHistory: List<NoAccessEntryEntity>
)
