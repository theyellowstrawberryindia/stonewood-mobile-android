package uk.co.savills.stonewood.screen.statistics.ondevice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemOnDeviceStatisticsBinding
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType

class StatisticsAdapter : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    private var statistics = listOf<Pair<PropertySurveyType, Int>>()

    override fun getItemCount() = statistics.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (type, quantity) = statistics[position]
        holder.bind(type, quantity)
    }

    fun setStatistics(statistics: List<Pair<PropertySurveyType, Int>>) {
        this.statistics = statistics
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ListItemOnDeviceStatisticsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(surveyType: PropertySurveyType, quantity: Int) = with(binding) {
            nameOnDeviceStatistics.setText(
                when (surveyType) {
                    PropertySurveyType.I -> R.string.internal
                    PropertySurveyType.E -> R.string.external
                    PropertySurveyType.IE -> R.string.internal_or_external
                    PropertySurveyType.EC -> R.string.external_or_communal
                    PropertySurveyType.ISAP -> R.string.internal_sap
                    PropertySurveyType.IESAP -> R.string.internal_or_external_sap
                    PropertySurveyType.SAP -> R.string.sap
                }
            )

            quantityOnDeviceStatistics.text = quantity.toString()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemOnDeviceStatisticsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}
