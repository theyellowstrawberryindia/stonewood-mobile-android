package uk.co.savills.stonewood.screen.survey.survey.energy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentEnergySurveyBinding
import uk.co.savills.stonewood.screen.survey.survey.ElementFinder
import uk.co.savills.stonewood.screen.survey.survey.SurveyTabFragment
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyFragmentBase
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.customview.WarningDialog

class EnergySurveyFragment : SurveyFragmentBase<EnergySurveyViewModel>(), ElementFinder {
    override val viewModel: EnergySurveyViewModel by lazy {
        EnergySurveyViewModel(requireActivity().application, requireActivity() as LocationTracker)
    }

    private lateinit var binding: FragmentEnergySurveyBinding

    private val groupAdapter by lazy {
        GroupAdapter {
            viewModel.onGroupSelected(it)
            with(binding.sectionRecyclerViewEnergySurvey) {
                post { scrollToPosition(0) }
            }
        }
    }

    private val sectionAdapter by lazy {
        SectionAdapter(viewModel)
    }

    private val excessiveValueWarningDialog by lazy {
        WarningDialog.Builder(requireContext()).build()
    }

    private val excessivelyLowValueWarningDialog by lazy {
        WarningDialog.Builder(requireContext()).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_energy_survey, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment as SurveyTabFragment).setEnergyUpdateListener(this)
    }

    override fun onVisibleFirstTime() {
        super.onVisibleFirstTime()

        setViews()
        setBindings()
    }

    private fun setViews() {
        with(binding) {
            groupRecyclerViewEnergySurvey.adapter = groupAdapter
            sectionRecyclerViewEnergySurvey.adapter = sectionAdapter
        }
    }

    private fun setBindings() {
        viewModel.groups.observe { groups ->
            groupAdapter.setGroups(groups.filterNot { it.isSkipped })
        }

        viewModel.selectedGroup.observe { group ->
            binding.sectionRecyclerViewEnergySurvey.post {
                sectionAdapter.setSections(
                    group.sections.filterNot { it.isSkipped },
                    group.isSpecial
                )
            }
        }

        viewModel.excessiveValueDetected.observe {
            excessiveValueWarningDialog
                .setDescription(
                    getString(
                        R.string.excessive_value_warning_dialog_message,
                        it.first,
                        it.second
                    )
                )
                .show()
        }

        viewModel.excessivelyLowValueDetected.observe {
            excessivelyLowValueWarningDialog
                .setDescription(
                    getString(
                        R.string.excessively_low_value_warning_dialog_message,
                        it.first,
                        it.second
                    )
                )
                .show()
        }
    }

    override fun onFindElement(elementId: Int) {
        val group = viewModel.groups.value?.find { group ->
            group.sections.filterNot {
                it.isSkipped
            }.any { section ->
                section.elements.filterNot { it.isSkipped }.any { it.id == elementId }
            }
        } ?: return

        if (!group.isSelected) viewModel.onGroupSelected(group)

        requireView().post {
            scrollToPosition(
                group.sections.filterNot {
                    it.isSkipped
                }.indexOfFirst { section ->
                    section.elements.any { it.id == elementId }
                }
            )
        }
    }

    private fun scrollToPosition(position: Int) {
        try {
            (binding.sectionRecyclerViewEnergySurvey.layoutManager as LinearLayoutManager).startSmoothScroll(
                getSmoothScroller(position)
            )
        } catch (exception: IllegalArgumentException) {
        }
    }

    private fun getSmoothScroller(position: Int): LinearSmoothScroller {
        return object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply {
            targetPosition = position
        }
    }
}
