package uk.co.savills.stonewood.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentLoginBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.hideKeyboard

class LoginFragment : BaseFragment<LoginViewModel>() {
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        requireView().hideKeyboard()
    }
}
