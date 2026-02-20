package uk.co.savills.stonewood.screen.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import uk.co.savills.stonewood.R

abstract class DialogFragmentBase : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
        setFullscreenLayout()
    }

    @Suppress("DEPRECATION")
    private fun setFullscreenLayout() {
        val window = dialog?.window ?: return

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
}
