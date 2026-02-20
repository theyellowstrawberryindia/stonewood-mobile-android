package uk.co.savills.stonewood.screen.survey.survey.energy

import android.view.LayoutInflater
import android.view.ViewGroup
import uk.co.savills.stonewood.databinding.ListItemEnergyElementBinding
import uk.co.savills.stonewood.screen.survey.survey.energy.base.ElementViewHolderBase

class ElementViewHolder private constructor(
    binding: ListItemEnergyElementBinding
) : ElementViewHolderBase(
    binding.root,
    binding.titleEnergyElement,
    binding.comboBoxEnergyElement,
    binding.userEntryEditTextEnergyElement
) {
    companion object {
        fun create(parent: ViewGroup, attachToRoot: Boolean): ElementViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemEnergyElementBinding.inflate(inflater, parent, attachToRoot)
            return ElementViewHolder(binding)
        }
    }
}
