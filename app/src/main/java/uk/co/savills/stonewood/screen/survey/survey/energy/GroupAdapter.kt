package uk.co.savills.stonewood.screen.survey.survey.energy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementGroupModel

class GroupAdapter(
    private val selectionListener: (EnergySurveyElementGroupModel) -> Unit
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    private var groups = listOf<EnergySurveyElementGroupModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position], selectionListener)
    }

    override fun getItemCount() = groups.size

    fun setGroups(groups: List<EnergySurveyElementGroupModel>) {
        this.groups = groups
        notifyDataSetChanged()
    }

    class ViewHolder private constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val titleText: TextView = view.findViewById(R.id.titleEnergyGroup)
        private val statusImage: ImageView = view.findViewById(R.id.statusImageEnergyGroup)

        fun bind(
            element: EnergySurveyElementGroupModel,
            itemClick: (EnergySurveyElementGroupModel) -> Unit
        ) {
            titleText.text = element.title

            statusImage.setImageResource(
                when {
                    element.isComplete -> R.drawable.ic_complete
                    else -> R.drawable.ic_incomplete
                }
            )

            view.setBackgroundResource(
                if (element.isSelected) R.color.colorSecondary else R.color.backgroundWhite
            )

            view.setOnClickListener { itemClick.invoke(element) }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_energy_group, parent, false)
                return ViewHolder(view)
            }
        }
    }
}
