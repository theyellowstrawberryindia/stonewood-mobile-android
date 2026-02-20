package uk.co.savills.stonewood.screen.survey.survey.validation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemValidationBinding
import uk.co.savills.stonewood.databinding.ListItemValidationGroupBinding
import uk.co.savills.stonewood.model.survey.element.validation.ValidationElementModel
import uk.co.savills.stonewood.model.survey.element.validation.ValidationOperand

class ValidationGroupAdapter(
    private val validationsClickListener: (ValidationOperand) -> Unit
) : ListAdapter<List<ValidationElementModel>, ValidationGroupAdapter.GroupViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GroupViewHolder.from(parent)

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position), validationsClickListener)
    }

    class GroupViewHolder private constructor(
        private val binding: ListItemValidationGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(validations: List<ValidationElementModel>, clickListener: (ValidationOperand) -> Unit) {
            with(binding) {
                titleValidationGroup.text = validations.firstOrNull()?.group.orEmpty()

                validationsValidationGroup.removeAllViews()

                validations.forEach {
                    validationsValidationGroup.addView(
                        ViewHolder.from(validationsValidationGroup).apply {
                            bind(it, clickListener)
                        }.itemView
                    )
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): GroupViewHolder {
                return GroupViewHolder(
                    ListItemValidationGroupBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ViewHolder private constructor(
        private val binding: ListItemValidationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(validation: ValidationElementModel, clickListener: (ValidationOperand) -> Unit) {
            setTexts(validation)
            setStatus(validation)
            setClickListeners(clickListener, validation)
        }

        private fun setClickListeners(
            clickListener: (ValidationOperand) -> Unit,
            validation: ValidationElementModel
        ) {
            binding.leftOperandLayoutValidation.setOnClickListener {
                clickListener.invoke(validation.leftOperand)
            }

            binding.rightOperandLayoutValidation.setOnClickListener {
                clickListener.invoke(validation.rightOperand)
            }
        }

        private fun setTexts(validation: ValidationElementModel) {
            with(binding) {
                setElementTitles(validation)

                leftOperandValidation.text = validation.leftOperand.answer
                rightOperandValidation.text = validation.rightOperand.answer

                errorValidation.text = validation.errorMessage
            }
        }

        private fun setElementTitles(validation: ValidationElementModel) {
            with(binding) {
                leftOperandTitleValidation.text = validation.leftOperand.elementTitle
                rightOperandTitleValidation.text = validation.rightOperand.elementTitle
            }
        }

        private fun setStatus(validation: ValidationElementModel) {
            with(binding) {
                statusImageValidation.setImageResource(
                    if (validation.isValid) R.drawable.ic_complete else R.drawable.ic_wrong
                )

                errorValidation.isVisible = !validation.isValid
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemValidationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<List<ValidationElementModel>>() {
    override fun areItemsTheSame(
        oldItems: List<ValidationElementModel>,
        newItems: List<ValidationElementModel>
    ): Boolean {
        return oldItems.all { oldItem -> newItems.any { it.id == oldItem.id } }
    }

    override fun areContentsTheSame(
        oldItems: List<ValidationElementModel>,
        newItems: List<ValidationElementModel>
    ): Boolean {
        return oldItems.all { oldItem ->
            val newItem = newItems.find { it.id == oldItem.id } ?: return false

            oldItem == newItem &&
                oldItem.isValid == newItem.isValid &&
                oldItem.leftOperand.answer == newItem.leftOperand.answer &&
                oldItem.rightOperand.answer == newItem.rightOperand.answer
        }
    }
}
