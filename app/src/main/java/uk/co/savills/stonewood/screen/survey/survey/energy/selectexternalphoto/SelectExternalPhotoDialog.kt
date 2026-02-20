package uk.co.savills.stonewood.screen.survey.survey.energy.selectexternalphoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uk.co.savills.stonewood.databinding.DialogSelectExtPhotoBinding
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.screen.base.DialogFragmentBase

class SelectExternalPhotoDialog(
    selectionListener: (ExtBlockPhotoModel) -> Unit
) : DialogFragmentBase() {

    private val viewModel by lazy {
        SelectExternalPhotoViewModel(
            requireActivity().application,
        ) {
            selectionListener.invoke(it)
            dismiss()
        }
    }

    private lateinit var binding: DialogSelectExtPhotoBinding

    private val adapter by lazy {
        ExternalPhotoAdapter(::onDataSelected)
    }

    private val confirmationDialog by lazy {
        ConfirmationDialog(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSelectExtPhotoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dataSelectExtPhoto.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, adapter::submitList)

        binding.toolbarSelectExtPhoto.setBackButtonClickListener {
            dismiss()
        }
    }

    private fun onDataSelected(data: ExtBlockPhotoModel) {
        confirmationDialog.setData(data)
            .setConfirmationListener {
                viewModel.onCommunalDataSelected(data)
            }
            .show()
    }
}
