package uk.co.savills.stonewood.screen.survey.survey.energy.selectexternalphoto

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.savills.stonewood.databinding.ListItemCommunalDataBinding
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.property.AddressModel

class ExternalPhotoAdapter(
    private val selectionListener: (ExtBlockPhotoModel) -> Unit
) : ListAdapter<ExtBlockPhotoModel, ExternalPhotoAdapter.ViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectionListener)
    }

    class ViewHolder private constructor(
        private val binding: ListItemCommunalDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val viewHolderScope = CoroutineScope(Dispatchers.IO)

        fun bind(data: ExtBlockPhotoModel, selectionListener: (ExtBlockPhotoModel) -> Unit) {
            setAddress(data.address)
            binding.surveyorCommunalData.text = data.surveyor
            setPhotos(data.imagePaths)

            binding.root.setOnClickListener {
                selectionListener.invoke(data)
            }
        }

        private fun setPhotos(photoFilePaths: MutableList<String>) {
            if (photoFilePaths.isNotEmpty()) {
                viewHolderScope.launch {
                    val firstPhoto = BitmapFactory.decodeFile(photoFilePaths.first())
                    withContext(Dispatchers.Main) {
                        firstPhoto?.let(binding.firstPhotoCommunalData::setImageBitmap)
                    }

                    if (photoFilePaths.size > 1) {
                        val secondPhoto = BitmapFactory.decodeFile(photoFilePaths[1])
                        withContext(Dispatchers.Main) {
                            secondPhoto?.let(binding.secondPhotoCommunalData::setImageBitmap)
                        }
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun setAddress(address: AddressModel) {
            with(binding) {
                addressLine1CommunalData.text = "${address.number}, ${address.line1}"
                addressLine2CommunalData.text = address.line2

                postCodeCommunalData.text = address.postalCode
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemCommunalDataBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ItemCallback : DiffUtil.ItemCallback<ExtBlockPhotoModel>() {
        override fun areItemsTheSame(
            oldItem: ExtBlockPhotoModel,
            newItem: ExtBlockPhotoModel
        ): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(
            oldItem: ExtBlockPhotoModel,
            newItem: ExtBlockPhotoModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
