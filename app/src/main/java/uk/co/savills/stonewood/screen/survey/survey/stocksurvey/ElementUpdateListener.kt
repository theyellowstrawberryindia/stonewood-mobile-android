package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel

interface ElementUpdateListener {
    fun onSubElementSelected()
    fun onEntryUpdate(element: StockSurveyElementModel)
    fun onRenewalQuantityUpdate(element: StockSurveyElementModel)
    fun onAddPhoto(element: StockSurveyElementModel, position: Int)
    fun onPreviousCommunalDataRequest(element: StockSurveyElementModel, position: Int)
}
