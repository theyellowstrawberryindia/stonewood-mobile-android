package uk.co.savills.stonewood.util.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.PHOTO_DIRECTORY
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.util.customview.StandardDialog
import java.io.File

class Camera(
    private val context: Context,
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>

    private lateinit var currentPhotoPath: String

    private lateinit var successCallback: (String) -> Unit

    override fun onCreate(owner: LifecycleOwner) {
        takePhotoLauncher = registry.register(
            owner.toString(),
            owner,
            ActivityResultContracts.TakePicture()
        ) { isSuccessful ->
            if (isSuccessful) {
                onSuccessfulPhotoCapture()
            } else {
                failureDialog.show()
            }
        }
    }

    fun takePhoto(fileName: String, folderName: String, successCallback: (String) -> Unit) {
        this.successCallback = successCallback

        val file = createImageFile(fileName, folderName)
        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )

        takePhotoLauncher.launch(photoURI)
    }

    private fun createImageFile(fileName: String, folderName: String): File {
        val photosDirectory = File(context.filesDir, PHOTO_DIRECTORY)
        if (!photosDirectory.exists()) photosDirectory.mkdir()

        val directory = File(photosDirectory, folderName)
        if (!directory.exists()) directory.mkdir()

        return File(directory, fileName.plus(IMAGE_EXTENSION)).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun onSuccessfulPhotoCapture() {
        val photo = getPhoto()

        val photoAspectRatio = photo.height.toFloat() / photo.width.toFloat()
        if (photoAspectRatio in VALID_ASPECT_RATIO_RANGE) {
            savePhoto(photo)
            successCallback.invoke(currentPhotoPath)
        } else {
            deletePhotoFile(currentPhotoPath)
            invalidPhotoDialog.show()
        }
    }

    private fun getPhoto(): Bitmap {
        val rawPhoto = BitmapFactory.decodeFile(currentPhotoPath)

        val exifInfo = ExifInterface(currentPhotoPath)
        val orientation = exifInfo.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        val scale = IMAGE_WIDTH_FACTOR.toFloat() / rawPhoto.width.toFloat()

        val transformedBitmap = Bitmap.createBitmap(
            rawPhoto,
            0,
            0,
            rawPhoto.width,
            rawPhoto.height,
            Matrix().apply { postRotate(rotationAngle.toFloat()) }.apply { postScale(scale, scale) },
            true
        )

        rawPhoto.recycle()

        return transformedBitmap
    }

    private fun savePhoto(photo: Bitmap) {
        File(currentPhotoPath).outputStream().use { outputStream ->
            photo.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
        }
    }

    private val invalidPhotoDialog by lazy {
        StandardDialog.Builder(context)
            .setTitle(R.string.invalid_photo_dialog_header)
            .setDescription(R.string.invalid_photo_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    private val failureDialog by lazy {
        StandardDialog.Builder(context)
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.photo_capture_failure_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    companion object {
        private const val IMAGE_EXTENSION = ".jpg"
        private const val IMAGE_WIDTH_FACTOR = 1500
        private val VALID_ASPECT_RATIO_RANGE = 1.23..1.4
    }
}
