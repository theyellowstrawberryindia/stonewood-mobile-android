package uk.co.savills.stonewood.screen.survey.survey.stocksurvey.selectcommunaldata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogSelectCommunalDataBinding
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.screen.base.DialogFragmentBase

class SelectCommunalDataDialog(
    elementTitle: String,
    selectionListener: (CommunalDataModel) -> Unit
) : DialogFragmentBase() {

    private val viewModel by lazy {
        SelectCommunalDataViewModel(
            requireActivity().application,
            elementTitle
        ) {
            selectionListener.invoke(it)
            dismiss()
        }
    }

    private lateinit var binding: DialogSelectCommunalDataBinding

    private val adapter by lazy {
        CommunalDataAdapter(::onDataSelected)
    }

    private val confirmationDialog by lazy {
        ConfirmationDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSelectCommunalDataBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dataSelectCommunalData.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, adapter::submitList)

        binding.toolbarSelectCommunalData.setTitle(getString(R.string.existing_data_title, viewModel.elementTitle))
        binding.toolbarSelectCommunalData.setBackButtonClickListener {
            dismiss()
        }
    }

    private fun onDataSelected(data: CommunalDataModel) {
        confirmationDialog.setData(data)
            .setConfirmationListener {
                viewModel.onCommunalDataSelected(data)
            }
            .show()
    }
}
