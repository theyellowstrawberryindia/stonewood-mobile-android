package uk.co.savills.stonewood.screen.propertylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentPropertyListBinding
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.screen.base.CameraFragmentBase
import uk.co.savills.stonewood.util.customview.StandardDialog
import uk.co.savills.stonewood.util.hideKeyboard

class PropertyListFragment : CameraFragmentBase<PropertyListViewModel>(), PropertyClickListener {
    override val viewModel: PropertyListViewModel by viewModels()

    private lateinit var binding: FragmentPropertyListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_property_list, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPropertyList()
        setEventHandlers()
    }

    private fun setEventHandlers() {
        viewModel.iePropertySelected.observe {
            externalSurveyQuestionDialog.show()
        }
    }

    override fun onNavAnimationEnd() {
        super.onNavAnimationEnd()

        viewModel.refreshProperties()
    }

    private fun setupPropertyList() {
        val adapter = PropertyListAdapter(this)
        binding.propertyRecyclerViewPropertyList.adapter = adapter

        viewModel.propertyPagingDataChanged.observe {
            viewModel.propertyPagingData.observe { data ->
                lifecycleScope.launch {
                    adapter.submitData(data)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        requireView().hideKeyboard()
    }

    override fun onClick(property: PropertyModel) {
        viewModel.onPropertySelected(property)
    }

    override fun onContactNumberClick(number: String) = viewModel.callNumber(number)

    override fun onLocationClick(address: String) = viewModel.lookupAddress(address)

    override fun onNoAccessClick(property: PropertyModel) = viewModel.makeNoAccessEntry(property)

    override fun onTakePhoto(property: PropertyModel) {
        camera.takePhoto(viewModel.getPhotoFileName(property), viewModel.photoFolderName) {
            viewModel.onPhotoAdded(property, it)
            photoAddedDialog.show()
        }
    }

    private val externalSurveyQuestionDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.external_survey_question_dialog_title)
            .setDescription(R.string.external_survey_question_dialog_question)
            .setPositiveButton(R.string.yes, viewModel::changePropertySurveyType)
            .setNegativeButton(R.string.no, viewModel::surveyProperty)
            .build()
    }

    private val photoAddedDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.external_photo_added)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }
}
