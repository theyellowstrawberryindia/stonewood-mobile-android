package uk.co.savills.stonewood.screen.survey.surveylist

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentSurveyListBinding
import uk.co.savills.stonewood.screen.base.CameraFragmentBase
import uk.co.savills.stonewood.screen.survey.ExternalPhotoDialog
import uk.co.savills.stonewood.screen.survey.survey.energy.selectexternalphoto.SelectExternalPhotoDialog
import uk.co.savills.stonewood.util.photo.viewPhoto

class SurveyListFragment : CameraFragmentBase<SurveyListViewModel>() {
    override lateinit var viewModel: SurveyListViewModel

    private lateinit var binding: FragmentSurveyListBinding

    private lateinit var surveyAdapter: SurveyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_survey_list, container, false)

        viewModel = SurveyListViewModel(requireActivity().application)
        binding.surveyListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSurveys()

        surveyAdapter = SurveyAdapter(viewModel::onSurveySelected, viewModel::isSurveyEnabled)
        binding.recyclerViewSurveyList.adapter = surveyAdapter
        viewModel.surveys.observe(surveyAdapter::submitList)

        binding.extPhotoButtonSurveyList.setOnClickListener {
            extPhotoSelectionDialog.show()
        }

        viewModel.takePhoto.observe {
            camera.takePhoto(
                viewModel.photoFileName,
                viewModel.photoFolderName,
                viewModel::onFrontDoorPhotoTaken
            )
        }

        viewModel.canTakeFrontDoorPhoto.observe {
            surveyAdapter.notifyDataSetChanged()
        }

        viewModel.frontDoorPhoto.observe { filePath ->
            if (filePath.isEmpty()) return@observe

            with(binding.frontDoorPhotoSurveyList) {
                val bitmap = BitmapFactory.decodeFile(filePath)
                setImageBitmap(bitmap)

                if (bitmap != null) setOnClickListener { context.viewPhoto(filePath) }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.canMoveBack.value == true) {
                viewModel.navigateBack()
            }
        }
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
            }
        ) {
            SelectExternalPhotoDialog(viewModel::onExistingPhotosSelected)
                .show(childFragmentManager, SelectExternalPhotoDialog::class.simpleName)
        }
    }
}
