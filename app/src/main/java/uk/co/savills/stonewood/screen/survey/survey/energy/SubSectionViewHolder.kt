package uk.co.savills.stonewood.screen.survey.survey.energy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import uk.co.savills.stonewood.databinding.ListItemEnergySubSectionBinding
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel

class SubSectionViewHolder private constructor(
    private val binding: ListItemEnergySubSectionBinding
) {
    private val map: MutableMap<String, SubSectionElementViewHolder> = mutableMapOf()

    fun bind(
        position: Int,
        title: String,
        elements: List<EnergySurveyElementModel>,
        getSubSectionElement: (Int) -> List<EnergySurveyElementModel>,
        updateListener: ElementUpdateListener
    ) {
        binding.titleEnergySubSection.text = title

        with(binding.copyTextEnergySubSection) {
            val canCopy = position != 0
            isVisible = canCopy

            if (canCopy) {
                setOnClickListener {
                    for (element in getSubSectionElement(position - 1)) {
                        if (
                            !element.isPreSelection &&
                            !element.titleShort.equals("Floor Construction", ignoreCase = true)
                        ) {
                            map[element.titleShort]?.setSubElement(element.entry.subElement)
                        }
                    }
                }
            }
        }

        with(binding.elementGridEnergySubSection) {
            map.clear()
            removeAllViews()

            for (element in elements.filterNot { it.isSkipped }) {
                val viewHolder = SubSectionElementViewHolder
                    .create(this, attachToParent = true)

                viewHolder.bind(element, updateListener)

                map[element.titleShort] = viewHolder
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup, attachToParent: Boolean): SubSectionViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemEnergySubSectionBinding.inflate(inflater, parent, attachToParent)
            return SubSectionViewHolder(binding)
        }
    }
}
