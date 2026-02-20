package uk.co.savills.stonewood.screen.survey.survey.energy

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayout
import uk.co.savills.stonewood.databinding.ListItemSubSectionElementBinding
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.screen.survey.survey.energy.base.ElementViewHolderBase

class SubSectionElementViewHolder private constructor(
    binding: ListItemSubSectionElementBinding
) : ElementViewHolderBase(
    binding.root,
    binding.titleSubSectionElement,
    binding.comboBoxSubSectionElement,
    binding.userEntryEditTextSubSectionElement
) {
    val id: String
        get() = element.titleShort

    override fun bind(element: EnergySurveyElementModel, updateListener: ElementUpdateListener) {
        super.bind(element, updateListener)

        (view.layoutParams as FlexboxLayout.LayoutParams).flexBasisPercent =
            if (isDropDown) 0.655f else 0.31f
    }

    fun setSubElement(entry: String) {
        if (isDropDown) {
            val position = subElements.indexOfFirst { entry == it.title }
            comboBox.setSelection(position)
        } else {
            userEntryEditText.setText(entry)
        }
    }

    companion object {
        fun create(parent: ViewGroup, attachToParent: Boolean): SubSectionElementViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemSubSectionElementBinding.inflate(inflater, parent, attachToParent)
            return SubSectionElementViewHolder(binding)
        }
    }
}
