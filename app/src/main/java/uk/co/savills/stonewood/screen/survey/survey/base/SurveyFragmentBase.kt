package uk.co.savills.stonewood.screen.survey.survey.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import uk.co.savills.stonewood.screen.base.CameraFragmentBase
import uk.co.savills.stonewood.screen.survey.survey.SurveyTabFragment
import uk.co.savills.stonewood.util.connectionerror.ServerConnectionErrorDispatcher
import uk.co.savills.stonewood.util.navigation.Navigator

abstract class SurveyFragmentBase<T> : CameraFragmentBase<T>()
    where T : ViewModel, T : Navigator, T : ServerConnectionErrorDispatcher, T : SurveyViewModel {

    private var isVisibleForTheFirstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.surveyUpdated.observe { (parentFragment as SurveyTabFragment).onSurveyUpdate() }
        viewModel.elementFind.observe { (type, id) ->
            (parentFragment as SurveyTabFragment).onFindElement(type, id)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isVisibleForTheFirstTime) onVisibleFirstTime()
    }

    open fun onVisibleFirstTime() {
        isVisibleForTheFirstTime = false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        isVisibleForTheFirstTime = true
    }
}
