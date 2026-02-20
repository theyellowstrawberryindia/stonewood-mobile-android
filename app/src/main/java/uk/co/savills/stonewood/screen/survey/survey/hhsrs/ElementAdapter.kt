package uk.co.savills.stonewood.screen.survey.survey.hhsrs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.element.HHSRSSurveyElementModel

class ElementAdapter(
    diffCallback: DiffUtil.ItemCallback<HHSRSSurveyElementModel>,
    private val selectionListener: (Int) -> Unit
) : ListAdapter<HHSRSSurveyElementModel, ElementAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)) { selectionListener.invoke(position) }
    }

    class ViewHolder private constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val titleText: TextView = view.findViewById(R.id.titleHHSRSElement)
        private val statusImage: ImageView = view.findViewById(R.id.statusImageHHSRSElement)

        fun bind(
            element: HHSRSSurveyElementModel,
            itemClick: () -> Unit
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

            view.setOnClickListener { itemClick.invoke() }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_hhsrs_element, parent, false)
                return ViewHolder(view)
            }
        }
    }
}
