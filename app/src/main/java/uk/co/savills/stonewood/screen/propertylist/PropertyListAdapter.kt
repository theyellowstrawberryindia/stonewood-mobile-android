package uk.co.savills.stonewood.screen.propertylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemNoAccessEntryBinding
import uk.co.savills.stonewood.databinding.ListItemPropertyBinding
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatus

class PropertyListAdapter(
    private val clickListener: PropertyClickListener
) : PagingDataAdapter<PropertyModel, PropertyListAdapter.ViewHolder>(PropertyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(
        private val binding: ListItemPropertyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            property: PropertyModel?,
            clickListener: PropertyClickListener
        ) {
            if (property == null) return

            binding.property = property
            binding.clickListener = clickListener
            binding.takePhotoProperty.isVisible = property.hasExternalPhoto
            binding.executePendingBindings()

            setNoAccessHistoryView(property.noAccessHistory)
        }

        private fun setNoAccessHistoryView(noAccessHistory: List<NoAccessEntryModel>) {
            binding.noAccessEntryHistoryViewProperty.removeAllViews()

            for ((index, noAccessEntry) in noAccessHistory.reversed().withIndex()) {
                if (index > 2) break

                ListItemNoAccessEntryBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    binding.noAccessEntryHistoryViewProperty,
                    true
                ).apply {
                    this.noAccessEntry = noAccessEntry
                    executePendingBindings()
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPropertyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

@BindingAdapter("status")
fun setStatus(view: View, status: PropertyStatus) {
    view.setBackgroundResource(
        when (status) {
            PropertyStatus.SURVEYED -> R.color.property_surveyed
            PropertyStatus.VOID -> R.color.property_void
            PropertyStatus.REFUSED_OR_PRIVATE -> R.color.property_private_or_refused
            PropertyStatus.REPEATED_NO_ACCESS_OR_FAILED -> R.color.property_repeated_failed_or_no_access
            PropertyStatus.NO_ACCESS_OR_FAILED -> R.color.property_failed_or_no_access
            PropertyStatus.CONTACT_AVAILABLE -> R.color.property_contact_available
            PropertyStatus.CONTACT_UNAVAILABLE -> R.color.transparent
        }
    )
}
