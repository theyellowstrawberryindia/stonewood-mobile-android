package uk.co.savills.stonewood.screen.statistics.projecttotal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemProjectTotalStatisticsBinding

class StatisticsAdapter : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    private var statistics = listOf<ProjectTotalStats>()

    override fun getItemCount() = statistics.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(statistics[position])
    }

    fun setStatistics(statistics: List<ProjectTotalStats>) {
        this.statistics = statistics
        notifyDataSetChanged()
    }

    class ViewHolder private constructor(
        private val binding: ListItemProjectTotalStatisticsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stats: ProjectTotalStats) = with(binding) {
            strataProjectTotalStatistics.text = stats.strata
            requiredProjectTotalStatistics.text = stats.required.toString()
            achievedProjectTotalStatistics.text = stats.achieved.toString()

            val remaining = stats.required - stats.achieved

            remainingProjectTotalStatistics.text = remaining.toString()

            remainingProjectTotalStatistics.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (remaining > 0) R.color.incomplete else R.color.textDefault
                )
            )

            val completionPercent = ProjectTotalStats.getCompletionPercent(stats.required, stats.achieved)
            completeProjectTotalStatistics.text = root.context.getString(R.string.percent, completionPercent)

            containerProjectTotalStatistics.setBackgroundResource(
                if (remaining > 0) R.color.backgroundWhite else R.color.property_surveyed
            )
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemProjectTotalStatisticsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}
