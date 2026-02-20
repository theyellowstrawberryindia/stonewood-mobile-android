package uk.co.savills.stonewood.screen.project

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentProjectBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.customview.StandardDialog

class ProjectFragment : BaseFragment<ProjectViewModel>() {
    private val viewModelFactoryProducer = {
        ProjectViewModelFactory(requireActivity().application, requireActivity() as LocationTracker)
    }
    override val viewModel: ProjectViewModel by viewModels(factoryProducer = viewModelFactoryProducer)

    private lateinit var binding: FragmentProjectBinding

    private val helpDialog by lazy { createHelpDialog() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_project, container, false)

        binding.viewModel = viewModel.apply {
            initialize()
        }
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.helpViewProject.setOnClickListener { helpDialog.show() }
    }

    private fun createHelpDialog(): StandardDialog {
        val contactNumber = viewModel.helpContact
        val helpString = getString(R.string.help_message, contactNumber)
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.onHelpContactClick()
                helpDialog.dismiss()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.typeface = Typeface.DEFAULT_BOLD
                ds.color = ContextCompat.getColor(requireContext(), R.color.textPrimary)
            }
        }
        val contactNumberStart = helpString.indexOf(contactNumber)
        val contactNumberEnd = contactNumberStart + contactNumber.length
        val messageSpannable = SpannableStringBuilder(helpString).apply {
            setSpan(
                clickSpan,
                contactNumberStart,
                contactNumberEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return StandardDialog.Builder(requireContext())
            .setTitle(R.string.standard_dialog_header)
            .setDescription(messageSpannable)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }
}
