package uk.co.savills.stonewood.screen.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.util.connectionerror.ServerConnectionErrorDispatcher
import uk.co.savills.stonewood.util.connectionerror.ServerConnectionErrorHandler
import uk.co.savills.stonewood.util.navigation.NavigationEventObserver
import uk.co.savills.stonewood.util.navigation.Navigator

abstract class BaseFragment<T> : Fragment()
    where T : ViewModel, T : Navigator, T : ServerConnectionErrorDispatcher {

    abstract val viewModel: T

    private val errorHandler: ServerConnectionErrorHandler
        get() = activity as ServerConnectionErrorHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvent.observe(NavigationEventObserver(findNavController()))

        viewModel.serverConnectionError.observe { errorHandler.onApiError(it) }
        viewModel.noConnectionError.observe { errorHandler.onNoConnectionError() }
        viewModel.unexpectedError.observe(::handleUnexpectedError)

        val navAnimTime = resources.getInteger(R.integer.navAnimTime)
        requireView().postDelayed(
            { onNavAnimationEnd() },
            navAnimTime.toLong()
        )
    }

    open fun onNavAnimationEnd() {
    }

    open fun handleUnexpectedError(message: String) = errorHandler.onUnexpectedError(message)

    protected fun <T> LiveData<T>.observe(observer: Observer<T>) =
        observe(viewLifecycleOwner, observer)
}
