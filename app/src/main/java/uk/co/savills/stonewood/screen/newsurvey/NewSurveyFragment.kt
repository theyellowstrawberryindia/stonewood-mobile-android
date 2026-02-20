package uk.co.savills.stonewood.screen.newsurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentNewSurveyBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.customview.StandardDialog
import uk.co.savills.stonewood.util.hideKeyboard

class NewSurveyFragment : BaseFragment<NewSurveyViewModel>() {

    override val viewModel: NewSurveyViewModel by viewModels()

    private lateinit var binding: FragmentNewSurveyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewSurveyBinding.inflate(
            inflater,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.propertyAdded.observe {
            propertyAddedDialog.show()
        }
    }

    private val propertyAddedDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.survey_added_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text) {
                requireView().hideKeyboard()
                viewModel.navigateBack()
            }
            .build()
    }
}
