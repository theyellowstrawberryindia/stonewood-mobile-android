package uk.co.savills.stonewood.screen.survey.survey.riskassessment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentRiskAssessmentSurveyBinding
import uk.co.savills.stonewood.screen.survey.ExternalPhotoDialog
import uk.co.savills.stonewood.screen.survey.survey.CloseEndedQuestionSurveyAdapter
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyFragmentBase
import uk.co.savills.stonewood.screen.survey.survey.energy.selectexternalphoto.SelectExternalPhotoDialog
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.customview.StandardDialog

class RiskAssessmentSurveyFragment : SurveyFragmentBase<RiskAssessmentSurveyViewModel>() {
    override val viewModel: RiskAssessmentSurveyViewModel by lazy {
        RiskAssessmentSurveyViewModel(
            requireActivity().application,
            requireActivity() as LocationTracker
        )
    }

    private lateinit var binding: FragmentRiskAssessmentSurveyBinding

    private val adapter by lazy { CloseEndedQuestionSurveyAdapter(viewModel::onQuestionAnswered) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_risk_assessment_survey,
            container,
            false
        )

        return binding.root
    }

    override fun onVisibleFirstTime() {
        super.onVisibleFirstTime()

        setViews()
        setBindings()
    }

    private fun setViews() {
        binding.recyclerViewRiskAssessmentSurvey.adapter = adapter
    }

    private fun setBindings() {
        viewModel.elements.observe(adapter::setList)

        viewModel.allQuestionsAnswered.observe { (isSuccessful, needsFrontDoorPhoto) ->
            onAllQuestionsAnswered(isSuccessful, needsFrontDoorPhoto)
        }

        viewModel.frontDoorPhotoSuccessfullyAdded.observe {
            frontDoorPhotoAddedDialog.show()
        }
    }

    private fun onAllQuestionsAnswered(isSuccessful: Boolean, isPhotoRequired: Boolean) {
        val shouldShowPhotoRationale = (isSuccessful && isPhotoRequired) || !isSuccessful

        val positiveButtonText = if (isSuccessful) {
            if (isPhotoRequired) R.string.front_door_photo_required_dialog_positive_button else R.string.dialog_standard_button_text
        } else {
            R.string.confirm_risk_assessment_failure
        }

        val positiveButtonClickListener =
            if (isSuccessful) {
                if (isPhotoRequired) ::takeFrontDoorPhoto else if (viewModel.areExtPhotosRequired) extPhotoSelectionDialog::show else null
            } else {
                ::takeNoAccessPhoto
            }

        val negativeButtonText = if (isSuccessful) null else R.string.retry

        completionStatusDialog
            .setStatus(isSuccessful)
            .setMessageVisibility(shouldShowPhotoRationale)
            .setPositiveButton(positiveButtonText, positiveButtonClickListener)
            .setNegativeButton(negativeButtonText)
            .show()
    }

    private fun takeFrontDoorPhoto() {
        camera.takePhoto(
            viewModel.frontDoorPhotoFileName,
            viewModel.frontDoorPhotoFolderName,
            viewModel::onFrontDoorPhotoTaken
        )
    }

    private fun takeNoAccessPhoto() {
        camera.takePhoto(
            viewModel.noAccessPhotoFileName,
            viewModel.frontDoorPhotoFolderName,
            viewModel::onNoAccessPhotoTaken
        )
    }

    private val completionStatusDialog by lazy {
        AssessmentCompleteDialog.Builder(requireContext())
            .build()
    }

    private val frontDoorPhotoAddedDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.front_door_photo_added_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text) {
                if (viewModel.areExtPhotosRequired) extPhotoSelectionDialog.show()
            }
            .build()
    }

    private val extPhotoSelectionDialog by lazy {
        ExternalPhotoDialog(
            requireContext(),
            {
                camera.takePhoto(
                    viewModel.extPhotoFileName,
                    viewModel.extPhotoFolderName,
                    viewModel::addExtBlockPhoto
                )
            },
            {
                SelectExternalPhotoDialog(viewModel::onExistingPhotosSelected)
                    .show(childFragmentManager, SelectExternalPhotoDialog::class.simpleName)
            }
        )
    }
}
