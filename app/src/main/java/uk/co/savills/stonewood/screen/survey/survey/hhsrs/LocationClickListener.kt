package uk.co.savills.stonewood.screen.survey.survey.hhsrs

interface LocationClickListener {
    fun onLocationClick(location: String, isSelected: Boolean)
    fun onOtherLocationClick(isSelected: Boolean)
}
