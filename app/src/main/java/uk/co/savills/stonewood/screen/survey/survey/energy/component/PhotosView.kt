package uk.co.savills.stonewood.screen.survey.survey.energy.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import uk.co.savills.stonewood.databinding.LayoutEnergyPhotosBinding
import uk.co.savills.stonewood.util.photo.adapter.PhotoAdapter
import uk.co.savills.stonewood.util.setPhotoLabel

class PhotosView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    interface Listener {
        fun onAddPhoto()
    }

    private val binding = LayoutEnergyPhotosBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var listener: Listener? = null

    private var remainingNumberOfPhotos = 0

    fun setLabel(remainingNumberOfPhotos: Int) {
        this.remainingNumberOfPhotos = remainingNumberOfPhotos
        binding.labelEnergyPhotos.setPhotoLabel(remainingNumberOfPhotos)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setPhotos(
        imagePaths: MutableList<String>,
        removeListener: ((String) -> Unit)?,
    ) {
        val grid = binding.gridEnergyPhotos
        grid.removeAllViews()

        for (path in imagePaths) {
            val viewHolder = PhotoAdapter.ViewHolder.from(grid)
            viewHolder.bind(path, removeListener)
            grid.addView(viewHolder.itemView)
        }

        val viewHolder = PhotoAdapter.ViewHolder.from(grid)
        viewHolder.setupAddImage {
            listener?.onAddPhoto()
        }
        grid.addView(viewHolder.itemView)
    }

    fun resetPhotos() {
        with(binding.gridEnergyPhotos) {
            removeAllViews()

            val viewHolder = PhotoAdapter.ViewHolder.from(this)
            viewHolder.setupAddImage {
                listener?.onAddPhoto()
            }
            addView(viewHolder.itemView)
        }
    }
}
