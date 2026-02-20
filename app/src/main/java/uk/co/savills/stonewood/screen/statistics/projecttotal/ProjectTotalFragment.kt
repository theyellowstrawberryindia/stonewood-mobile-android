package uk.co.savills.stonewood.screen.statistics.projecttotal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentProjectTotalBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.screen.statistics.SectionAdapter

class ProjectTotalFragment : BaseFragment<ProjectTotalViewModel>() {
    override val viewModel: ProjectTotalViewModel by viewModels()

    private lateinit var binding: FragmentProjectTotalBinding

    private val sectionAdapter by lazy {
        SectionAdapter(viewModel::onSectionSelected)
    }

    private val statsAdapter by lazy {
        StatisticsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentProjectTotalBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews()
        setBindings()
    }

    private fun setViews() {
        binding.sectionsProjectTotal.adapter = sectionAdapter
        binding.statisticsProjectTotal.adapter = statsAdapter
    }

    private fun setBindings() {
        viewModel.sections.observe { section ->
            sectionAdapter.submitList(section.map { it.first })
        }

        viewModel.statistics.observe { stats ->
            statsAdapter.setStatistics(stats)

            setTotalStats(stats)

            val isEmpty = stats.isEmpty()

            binding.statisticsContainerProjectTotal.isVisible = !isEmpty
            binding.columnProjectTotal.isVisible = !isEmpty

            binding.emptyViewProjectTotal.isVisible = isEmpty
        }
    }

    private fun setTotalStats(stats: List<ProjectTotalStats>) {
        val required = stats.sumOf { it.required }
        val achieved = stats.sumOf { it.achieved }

        binding.requiredTotalProjectTotal.text = required.toString()
        binding.achievedTotalProjectTotal.text = achieved.toString()

        val remaining = required - achieved
        binding.remainingTotalProjectTotal.text = remaining.toString()
        binding.remainingTotalProjectTotal.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (remaining > 0) R.color.incomplete else R.color.textDefault
            )
        )

        val completionPercent = ProjectTotalStats.getCompletionPercent(required, achieved)
        binding.completeTotalProjectTotal.text = getString(R.string.percent, completionPercent)
    }
}
