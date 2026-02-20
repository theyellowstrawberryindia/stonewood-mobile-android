package uk.co.savills.stonewood.screen.imageviewer

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath = intent.getStringExtra(IMAGE_PATH_KEY)

        val binding = ActivityImageViewerBinding.inflate(layoutInflater).apply {
            photoImageViewer.setImageBitmap(BitmapFactory.decodeFile(imagePath))
            backButtonImageViewer.setOnClickListener { finish() }
        }

        setContentView(binding.root)
    }

    companion object {
        const val IMAGE_PATH_KEY = "${BuildConfig.APPLICATION_ID}.imagePath"
    }
}
