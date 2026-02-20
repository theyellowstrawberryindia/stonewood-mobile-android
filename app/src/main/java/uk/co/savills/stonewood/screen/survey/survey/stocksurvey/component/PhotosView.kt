package uk.co.savills.stonewood.screen.survey.survey.stocksurvey.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.LayoutPhotosViewBinding
import uk.co.savills.stonewood.util.customview.ComboBox
import uk.co.savills.stonewood.util.photo.adapter.PhotoAdapter
import uk.co.savills.stonewood.util.setPhotoLabel

class PhotosView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    interface Listener {
        fun onAddPhoto()
        fun onRemovePhoto(filePath: String)
        fun onNoAccessReasonSelected(reason: String)
    }

    private val binding = LayoutPhotosViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var images = listOf<String>()
    private var reasons = listOf<String>()

    private var listener: Listener? = null

    private val canTakePhotos
        get() = binding.gridPhotosView.isVisible

    private var remainingNumberOfPhotos = 0

    init {
        binding.noAccessButtonPhotosView.setOnClickListener {
            onNoAccessButtonClick()
        }
    }

    fun setLabel(remainingNumberOfPhotos: Int) {
        this.remainingNumberOfPhotos = remainingNumberOfPhotos
        binding.labelPhotosView.setPhotoLabel(remainingNumberOfPhotos)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setNoAccessReasons(noAccessReasons: List<String>, selectedReason: String) {
        reasons = noAccessReasons

        with(binding) {
            val selectedItemPosition = noAccessReasons.indexOfFirst { selectedReason == it }
            noAccessReasonsPhotosView.setItems(
                noAccessReasons,
                reasonSelectionListener,
                selectedItemPosition,
                R.string.select_reason
            )

            setVisibility(selectedReason.isEmpty())
            setNoAccessButtonVisibility()
        }
    }

    fun setPhotos(imagePaths: MutableList<String>) {
        images = imagePaths

        val grid = binding.gridPhotosView
        grid.removeAllViews()

        for (path in imagePaths) {
            val viewHolder = PhotoAdapter.ViewHolder.from(grid)
            viewHolder.bind(path) {
                grid.removeView(viewHolder.itemView)
                binding.noAccessButtonPhotosView.isVisible = grid.childCount == 1 && reasons.isNotEmpty()
                listener?.onRemovePhoto(it)
            }
            grid.addView(viewHolder.itemView)
        }

        val viewHolder = PhotoAdapter.ViewHolder.from(grid)
        viewHolder.setupAddImage {
            listener?.onAddPhoto()
        }
        grid.addView(viewHolder.itemView)
        setNoAccessButtonVisibility()
    }

    fun resetPhotos() {
        with(binding.gridPhotosView) {
            removeAllViews()

            val viewHolder = PhotoAdapter.ViewHolder.from(this)
            viewHolder.setupAddImage {
                listener?.onAddPhoto()
            }
            addView(viewHolder.itemView)
        }
    }

    private fun onNoAccessButtonClick() {
        with(binding) {
            gridPhotosView.isVisible = !canTakePhotos
            noAccessReasonsPhotosView.isVisible = !canTakePhotos

            noAccessButtonPhotosView.setImageResource(
                if (canTakePhotos) R.drawable.ic_no_photo else R.drawable.ic_take_photo
            )

            labelPhotosView.setPhotoLabel(
                if (canTakePhotos) remainingNumberOfPhotos else 0
            )

            if (canTakePhotos) {
                listener?.onNoAccessReasonSelected("")
                noAccessReasonsPhotosView.reset()
            }
        }
    }

    private fun setNoAccessButtonVisibility() {
        binding.noAccessButtonPhotosView.isVisible = images.isEmpty() && reasons.isNotEmpty()
    }

    private fun setVisibility(canTakePhotos: Boolean) = with(binding) {
        gridPhotosView.isVisible = canTakePhotos
        noAccessReasonsPhotosView.isVisible = !canTakePhotos

        noAccessButtonPhotosView.setImageResource(
            if (canTakePhotos) R.drawable.ic_no_photo else R.drawable.ic_take_photo
        )

        labelPhotosView.setPhotoLabel(
            if (canTakePhotos) remainingNumberOfPhotos else 0
        )
    }

    private val reasonSelectionListener = object : ComboBox.OnItemSelectedListener {
        override fun onPlaceHolderSelected() {
            listener?.onNoAccessReasonSelected("")
        }

        override fun onItemSelected(position: Int, isUserSelected: Boolean) {
            listener?.onNoAccessReasonSelected(reasons[position])
        }
    }
}
