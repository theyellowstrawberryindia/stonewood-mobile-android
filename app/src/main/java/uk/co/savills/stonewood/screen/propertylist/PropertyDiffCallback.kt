package uk.co.savills.stonewood.screen.propertylist

import androidx.recyclerview.widget.DiffUtil
import uk.co.savills.stonewood.model.survey.property.PropertyModel

class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyModel>() {
    override fun areItemsTheSame(oldItem: PropertyModel, newItem: PropertyModel): Boolean {
        return oldItem.UPRN == newItem.UPRN
    }

    override fun areContentsTheSame(oldItem: PropertyModel, newItem: PropertyModel): Boolean {
        return oldItem == newItem
    }
}
