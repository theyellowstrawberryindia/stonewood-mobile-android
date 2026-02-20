package uk.co.savills.stonewood.screen.survey.survey.hhsrs

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import uk.co.savills.stonewood.model.survey.element.HHSRSSurveyElementModel

@SuppressLint("InvalidClassName")
class HHSRSSurveyElementDiffCallback : DiffUtil.ItemCallback<HHSRSSurveyElementModel>() {
    override fun areItemsTheSame(
        oldItem: HHSRSSurveyElementModel,
        newItem: HHSRSSurveyElementModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: HHSRSSurveyElementModel,
        newItem: HHSRSSurveyElementModel
    ): Boolean {
        return oldItem.isComplete == newItem.isComplete && oldItem.isSelected == newItem.isSelected
    }
}
