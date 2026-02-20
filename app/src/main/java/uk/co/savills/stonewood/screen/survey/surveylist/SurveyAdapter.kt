package uk.co.savills.stonewood.screen.survey.surveylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemStandardBinding
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType

class SurveyAdapter(
    private val itemClick: (SurveyType) -> Unit,
    private var isEnabled: (SurveyModel) -> Boolean
) : ListAdapter<SurveyModel, SurveyAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val survey = getItem(position)
        val isEnabled = isEnabled(survey)

        holder.bind(survey, itemClick, isEnabled)
    }

    class ViewHolder private constructor(
        private val binding: ListItemStandardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            survey: SurveyModel,
            itemClick: (SurveyType) -> Unit,
            isEnabled: Boolean
        ) = with(binding) {
            root.setOnClickListener { itemClick.invoke(survey.type) }
            root.isEnabled = isEnabled

            titleStandard.text = survey.getTitle(root.context)
            titleStandard.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (isEnabled) R.color.textDefault else R.color.textDisabled
                )
            )

            if (isEnabled) {
                arrowImageStandard.setImageResource(if (survey.isComplete) R.drawable.ic_complete else R.drawable.ic_next)
            } else {
                arrowImageStandard.setImageBitmap(null)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemStandardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SurveyModel>() {
        override fun areItemsTheSame(oldItem: SurveyModel, newItem: SurveyModel): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: SurveyModel, newItem: SurveyModel): Boolean {
            return oldItem == newItem
        }
    }
}
