package uk.co.savills.stonewood.screen.datatransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentDataTransferBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.customview.StandardDialog

class DataTransferFragment : BaseFragment<DataTransferViewModel>() {

    override val viewModel: DataTransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentDataTransferBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_data_transfer, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataTransferComplete.observe { dataTransferCompleteDialog.show() }
        viewModel.internetConnectionIssueDetected.observe {
            internetConnectionIssueDialog.show()
        }
    }

    override fun handleUnexpectedError(message: String) {
        val errorMessage = getString(R.string.data_transfer_error_dialog_message, message)
        errorDialog.setDescription(errorMessage)
        errorDialog.show()
    }

    override fun onDestroy() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        super.onDestroy()
    }

    private val dataTransferCompleteDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.data_transfer_complete_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text) { viewModel.navigateBack() }
            .build()
    }

    private val errorDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.unexpected_api_error_dialog_header)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    private val internetConnectionIssueDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.unexpected_api_error_dialog_header)
            .setDescription(R.string.data_transfer_internet_issue_message)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.isTransferringData.value != true) {
                viewModel.navigateBack()
            }
        }
    }
}
