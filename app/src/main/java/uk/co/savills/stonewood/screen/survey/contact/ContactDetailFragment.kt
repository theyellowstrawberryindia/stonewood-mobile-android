package uk.co.savills.stonewood.screen.survey.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentContactDetailBinding
import uk.co.savills.stonewood.screen.base.BaseFragment

class ContactDetailFragment : BaseFragment<ContactDetailViewModel>() {
    override val viewModel: ContactDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentContactDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_detail, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onPause() {
        viewModel.saveNotes()
        super.onPause()
    }
}
