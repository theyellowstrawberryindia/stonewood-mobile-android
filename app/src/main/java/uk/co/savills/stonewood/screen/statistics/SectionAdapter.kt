package uk.co.savills.stonewood.screen.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemSectionBinding

class SectionAdapter(
    private val clickListener: (String) -> Unit
) : ListAdapter<String, SectionAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectedPosition == position) {
            if (position == selectedPosition) return@bind
            setSelectedPosition(position)
            clickListener.invoke(it)
        }
    }

    private fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
    }

    class ViewHolder private constructor(
        private val binding: ListItemSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            section: String,
            isSelected: Boolean,
            selectionListener: (String) -> Unit,
        ) = with(binding) {
            titleSection.text = root.context.getString(R.string.section_label, section)

            with(root) {
                setBackgroundResource(
                    if (isSelected) R.color.colorSecondary else R.color.backgroundWhite
                )

                setOnClickListener {
                    selectionListener.invoke(section)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemSectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
