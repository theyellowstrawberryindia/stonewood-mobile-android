package uk.co.savills.stonewood.util.photo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import uk.co.savills.stonewood.PHOTO_DIRECTORY
import uk.co.savills.stonewood.screen.imageviewer.ImageViewerActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale

fun Context.viewPhoto(filePath: String) {
    val intent = Intent(this, ImageViewerActivity::class.java).apply {
        putExtra(ImageViewerActivity.IMAGE_PATH_KEY, filePath)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

fun deletePhotoFile(filePath: String) {
    val file = File(filePath)

    if (file.exists()) file.delete()
}

fun deletePhotosFolder(context: Context, directoryName: String) {
    val photosDirectory = File(context.filesDir, PHOTO_DIRECTORY)

    if (photosDirectory.exists()) {
        val directory = File(photosDirectory, directoryName)

        if (directory.exists()) directory.deleteRecursively()
    }
}

fun deletePhotosFolder(context: Context) {
    val photosDirectory = File(context.filesDir, PHOTO_DIRECTORY)
    if (photosDirectory.exists()) photosDirectory.deleteRecursively()
}

fun getBase64StringFromFile(path: String): String {
    val bitmap = BitmapFactory.decodeFile(path) ?: return ""

    return ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()
        bitmap.recycle()

        Base64.getEncoder().encodeToString(byteArray)
    }
}

@SuppressLint("DefaultLocale")
fun getFileName(
    surveyor: String,
    propertyUPRN: String,
    number: Int? = null,
    elementName: String? = null
): String {
    val formatter = DateTimeFormatter
        .ofPattern("yyyMMdd_HHmmss")
        .withZone(ZoneId.from(ZoneOffset.UTC))

    var fileName = "${surveyor}_${formatter.format(Instant.now())}_$propertyUPRN"

    if (!elementName.isNullOrEmpty()) {
        fileName += "_$elementName"
    }

    if (number != null) {
        fileName += "_$number"
    }

    fileName = fileName.split(" ").joinToString("") { it.capitalize(Locale.getDefault()) }

    return fileName.replace("[^A-Za-z0-9_]".toRegex(), "")
}
