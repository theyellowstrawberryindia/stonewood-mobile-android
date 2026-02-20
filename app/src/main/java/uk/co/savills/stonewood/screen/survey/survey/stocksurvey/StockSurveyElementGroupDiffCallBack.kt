package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import androidx.recyclerview.widget.DiffUtil
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementGroupModel

class StockSurveyElementGroupDiffCallBack : DiffUtil.ItemCallback<StockSurveyElementGroupModel>() {
    override fun areItemsTheSame(
        oldItem: StockSurveyElementGroupModel,
        newItem: StockSurveyElementGroupModel
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: StockSurveyElementGroupModel,
        newItem: StockSurveyElementGroupModel
    ): Boolean {
        return oldItem == newItem
    }
}
