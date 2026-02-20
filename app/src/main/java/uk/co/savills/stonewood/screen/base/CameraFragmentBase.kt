package uk.co.savills.stonewood.screen.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import uk.co.savills.stonewood.util.connectionerror.ServerConnectionErrorDispatcher
import uk.co.savills.stonewood.util.navigation.Navigator
import uk.co.savills.stonewood.util.photo.Camera

abstract class CameraFragmentBase<T> : BaseFragment<T>()
    where T : ViewModel, T : Navigator, T : ServerConnectionErrorDispatcher {

    protected lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        camera = Camera(requireContext(), requireActivity().activityResultRegistry)
        lifecycle.addObserver(camera)
    }
}
