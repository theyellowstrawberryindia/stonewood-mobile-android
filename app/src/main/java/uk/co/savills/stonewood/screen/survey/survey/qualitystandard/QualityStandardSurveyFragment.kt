package uk.co.savills.stonewood.screen.survey.survey.qualitystandard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentQualityStandardSurveyBinding
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.element.QualityStandardSurveyElementModel
import uk.co.savills.stonewood.screen.survey.survey.CloseEndedQuestionSurveyAdapter
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyFragmentBase
import uk.co.savills.stonewood.util.LocationTracker

class QualityStandardSurveyFragment : SurveyFragmentBase<QualityStandardSurveyViewModel>() {
    private lateinit var adapter: CloseEndedQuestionSurveyAdapter<QualityStandardSurveyElementModel>
    override val viewModel: QualityStandardSurveyViewModel by lazy {
        QualityStandardSurveyViewModel(
            requireActivity().application,
            requireActivity() as LocationTracker
        )
    }

    private lateinit var binding: FragmentQualityStandardSurveyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_quality_standard_survey,
            container,
            false
        )

        return binding.root
    }

    override fun onVisibleFirstTime() {
        super.onVisibleFirstTime()

        setQualityStandardListAdapter()
        setBindings()
    }

    private fun setBindings() {
        viewModel.elements.observe { elements ->
            adapter.setList(elements)
        }
    }

    private fun setQualityStandardListAdapter() {
        adapter = CloseEndedQuestionSurveyAdapter(onOptionSelected)
        binding.recyclerViewQualityStandardSurvey.adapter = adapter
    }

    private val onOptionSelected =
        { element: QualityStandardSurveyElementModel, answer: CloseEndedQuestionAnswer ->
            viewModel.onQuestionAnswered(element, answer)
        }
}
