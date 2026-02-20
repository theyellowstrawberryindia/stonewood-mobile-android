package uk.co.savills.stonewood.screen.survey.survey.energy

import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveySubElementModel

interface ElementUpdateListener {
    fun onPlaceHolderSelected(element: EnergySurveyElementModel)

    fun onSubElementSelected(
        subElement: EnergySurveySubElementModel,
        element: EnergySurveyElementModel
    )

    fun onUserEntryUpdate(entry: String, element: EnergySurveyElementModel)

    fun onUserEntryComplete(element: EnergySurveyElementModel)
}
