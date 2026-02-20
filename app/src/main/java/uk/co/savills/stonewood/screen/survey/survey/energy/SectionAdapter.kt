package uk.co.savills.stonewood.screen.survey.survey.energy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.databinding.ListItemEnergySurveySectionBinding
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementSectionModel

class SectionAdapter(
    private val updateListener: ElementUpdateListener
) : RecyclerView.Adapter<SectionAdapter.ViewHolder>() {

    private var sections = listOf<EnergySurveyElementSectionModel>()
    private var isSpecial = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sections[position], updateListener, isSpecial)
    }

    override fun getItemCount() = sections.size

    @SuppressLint("NotifyDataSetChanged")
    fun setSections(sections: List<EnergySurveyElementSectionModel>, isSpecial: Boolean) {
        this.sections = sections
        this.isSpecial = isSpecial

        notifyDataSetChanged()
    }

    class ViewHolder private constructor(
        private val binding: ListItemEnergySurveySectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var subSections = mutableListOf<List<EnergySurveyElementModel>>()

        fun bind(
            section: EnergySurveyElementSectionModel,
            updateListener: ElementUpdateListener,
            isSpecial: Boolean
        ) {
            binding.titleEnergySurveySection.text = section.title

            if (isSpecial) {
                setSubSectionsView(section.elements, updateListener)
            } else {
                setElementsView(section.elements, updateListener)
            }
        }

        private fun setSubSectionsView(
            elements: List<EnergySurveyElementModel>,
            updateListener: ElementUpdateListener
        ) {
            with(binding.elementLayoutEnergySurveySection) {
                subSections.clear()
                removeAllViews()

                elements.groupBy { it.subSection }.forEach { (key, value) ->
                    if (!value.all { it.isSkipped }) {
                        val subSection = value.filterNot { it.isSkipped }
                        SubSectionViewHolder
                            .from(this, attachToParent = true)
                            .bind(subSections.size, key, subSection, subSections::get, updateListener)

                        subSections.add(subSection)
                    }
                }
            }
        }

        private fun setElementsView(
            elements: List<EnergySurveyElementModel>,
            updateListener: ElementUpdateListener,
        ) {
            with(binding.elementLayoutEnergySurveySection) {
                removeAllViews()

                for (element in elements.filterNot { it.isSkipped }) {
                    val viewHolder = ElementViewHolder
                        .create(this, attachToRoot = true)

                    viewHolder.bind(element, updateListener)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemEnergySurveySectionBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
