package uk.co.savills.stonewood.screen.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentChangePasswordBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.customview.StandardDialog
import uk.co.savills.stonewood.util.hideKeyboard

class ChangePasswordFragment : BaseFragment<ChangePasswordViewModel>() {
    override val viewModel: ChangePasswordViewModel by viewModels()

    private val navArgs: ChangePasswordFragmentArgs by navArgs()

    lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_change_password,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        requireView().hideKeyboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventHandlers()
    }

    private fun setEventHandlers() {
        with(binding) {
            val viewModel = this@ChangePasswordFragment.viewModel
            if (navArgs.isStartScreen) {
                toolbarChangePassword.setBackButtonClickListener { viewModel.navigateToLogin() }
            }

            newPasswordEditTextChangePassword.let {
                it.addTextChangedListener { text ->
                    if (!viewModel.isValidPassword(text.toString())) {
                        it.error = getString(R.string.invalid_password_error_message)
                    }
                }
            }

            viewModel.passwordMismatch.observe { passwordMismatchDialog.show() }
        }
    }

    private val passwordMismatchDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.password_mismatch_error_message)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }
}
