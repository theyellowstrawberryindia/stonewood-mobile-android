package uk.co.savills.stonewood.screen.propertylist

import uk.co.savills.stonewood.model.survey.property.PropertyModel

interface PropertyClickListener {
    fun onClick(property: PropertyModel)

    fun onContactNumberClick(number: String)

    fun onLocationClick(address: String)

    fun onNoAccessClick(property: PropertyModel)

    fun onTakePhoto(property: PropertyModel)
}
