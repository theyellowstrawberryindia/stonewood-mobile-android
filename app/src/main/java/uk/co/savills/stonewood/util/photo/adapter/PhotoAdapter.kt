package uk.co.savills.stonewood.util.photo.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.util.photo.PhotoClickListener
import uk.co.savills.stonewood.util.photo.viewPhoto

class PhotoAdapter(
    private val clickListener: PhotoClickListener
) : ListAdapter<String, PhotoAdapter.ViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != itemCount - 1) {
            holder.bind(getItem(position), clickListener::onRemovePhotoClick)
        } else {
            holder.setupAddImage(clickListener::onAddPhotoClick)
        }
    }

    fun setList(list: List<String>) {
        submitList(list + "")
    }

    class ViewHolder private constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        private val viewHolderScope = CoroutineScope(Dispatchers.Default)
        private val image: ImageView = view.findViewById(R.id.photoNoAccessImage)
        private val removeImage: ImageView = view.findViewById(R.id.removeNoAccessImage)

        fun bind(filePath: String, onRemoveImage: ((String) -> Unit)?) {
            viewHolderScope.launch {
                withContext(Dispatchers.Main) {
                    val bitmap = BitmapFactory.decodeFile(filePath)

                    if (bitmap != null) {
                        image.setImageBitmap(bitmap)
                    } else {
                        onRemoveImage?.invoke(filePath)
                    }
                }
            }

            image.setOnClickListener { image.context.viewPhoto(filePath) }

            removeImage.isVisible = onRemoveImage != null
            removeImage.setOnClickListener { onRemoveImage?.invoke(filePath) }
        }

        fun setupAddImage(onAddImageClick: () -> Unit) {
            removeImage.isVisible = false

            view.setOnClickListener {
                onAddImageClick.invoke()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_no_access_image, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}
