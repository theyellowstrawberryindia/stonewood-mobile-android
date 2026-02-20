package uk.co.savills.stonewood.model.survey

import android.annotation.SuppressLint

@SuppressLint("InvalidClassName")
data class HHSRSLocationModel(
    val id: Int,
    val name: String,
    val type: PropertyLocationType,
)
