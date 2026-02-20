package uk.co.savills.stonewood.screen.survey.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentSurveyTabBinding
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.hideKeyboard

class SurveyTabFragment : BaseFragment<SurveyTabViewModel>() {
    override val viewModel: SurveyTabViewModel by viewModels()

    private val selectedSurveyType by lazy {
        val navArgs: SurveyTabFragmentArgs by navArgs()
        navArgs.surveyType
    }

    private lateinit var binding: FragmentSurveyTabBinding

    private lateinit var stockFinder: ElementFinder
    private lateinit var energyFinder: ElementFinder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_survey_tab,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventHandlers()
    }

    override fun onNavAnimationEnd() {
        super.onNavAnimationEnd()

        requireView().postDelayed(::setViews, TAB_SETUP_DELAY)
    }

    override fun onStop() {
        super.onStop()

        requireView().hideKeyboard()
    }

    private fun setViews() {
        binding.addressTextSurveyTab.text = viewModel.addressText
        setupTabs(viewModel.surveys)
    }

    private fun setupTabs(surveys: List<SurveyModel>) {
        with(binding) {
            pagerSurveyTab.adapter = SurveyAdapter(this@SurveyTabFragment, surveys)
            pagerSurveyTab.offscreenPageLimit = surveys.size
            pagerSurveyTab.isUserInputEnabled = false

            TabLayoutMediator(tabLayoutSurveyTab, pagerSurveyTab) { tab, position ->
                val survey = surveys[position]
                tab.text = survey.getTitle(requireContext())
                tab.view.isClickable = viewModel.isEnabled(survey)
            }.attach()

            val selectedTabIndex = surveys.indexOfFirst { it.type == selectedSurveyType }
            tabLayoutSurveyTab.selectTab(tabLayoutSurveyTab.getTabAt(selectedTabIndex))
        }
    }

    private fun setEventHandlers() {
        binding.backButtonSurveyTab.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun setStockUpdateListener(listener: ElementFinder) {
        stockFinder = listener
    }

    fun setEnergyUpdateListener(listener: ElementFinder) {
        energyFinder = listener
    }

    fun onFindElement(surveyType: SurveyType, elementId: Int) {
        val selectedTabIndex = viewModel.surveys.indexOfFirst { surveyType == it.type }

        with(binding.tabLayoutSurveyTab) {
            selectTab(getTabAt(selectedTabIndex))

            when (surveyType) {
                SurveyType.STOCK -> stockFinder.onFindElement(elementId)
                SurveyType.ENERGY -> energyFinder.onFindElement(elementId)
                else -> Unit
            }
        }
    }

    fun onSurveyUpdate() {
        viewModel.surveys.forEachIndexed { index, survey ->
            binding.tabLayoutSurveyTab.getTabAt(index)?.view?.isClickable =
                viewModel.isEnabled(survey)
        }
    }

    companion object {
        private const val TAB_SETUP_DELAY = 50L
    }
}
