package uk.co.savills.stonewood.screen.survey.survey.validation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemValidationCategoryBinding
import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory

class CategoryAdapter(
    private val selectionListener: (CategoryPresentationModel) -> Unit
) : ListAdapter<CategoryPresentationModel, CategoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectionListener)
    }

    class ViewHolder private constructor(
        private val binding: ListItemValidationCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            category: CategoryPresentationModel?,
            selectionListener: (CategoryPresentationModel) -> Unit
        ) {
            category?.let { _category ->
                binding.titleValidationCategory.setText(
                    when (_category.category) {
                        ValidationCategory.S_E -> R.string.se_validation_label_short
                        ValidationCategory.E_LOG -> R.string.e_log_validation_label_short
                        ValidationCategory.S_LOG -> R.string.s_log_validation_label_short
                    }
                )

                val isComplete = _category.groups.any { groupMap ->
                    groupMap.value.any { validation -> !validation.isValid }
                }

                binding.statusImageValidationCategory.setImageResource(
                    if (isComplete) R.drawable.ic_incomplete else R.drawable.ic_complete
                )

                with(binding.root) {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            if (_category.isSelected) R.color.colorSecondary else R.color.backgroundWhite
                        )
                    )

                    setOnClickListener {
                        if (!_category.isSelected) selectionListener.invoke(_category)
                    }
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemValidationCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryPresentationModel>() {
        override fun areItemsTheSame(
            oldItem: CategoryPresentationModel,
            newItem: CategoryPresentationModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CategoryPresentationModel,
            newItem: CategoryPresentationModel
        ): Boolean {
            return oldItem == newItem &&
                oldItem.groups.all { groupMap -> groupMap.value.all { it.isValid } } == newItem.groups.all { groupMap -> groupMap.value.all { it.isValid } } &&
                oldItem.isSelected == newItem.isSelected
        }
    }
}
