package uk.co.savills.stonewood.screen.statistics.ondevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.databinding.FragmentOnDeviceBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.screen.statistics.SectionAdapter

class OnDeviceFragment() : BaseFragment<OnDeviceViewModel>() {

    override val viewModel: OnDeviceViewModel by viewModels()

    private lateinit var binding: FragmentOnDeviceBinding

    private val sectionAdapter by lazy {
        SectionAdapter(viewModel::onSectionSelected)
    }

    private val statisticsAdapter by lazy {
        StatisticsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentOnDeviceBinding
            .inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews()
        setBindings()
    }

    private fun setViews() {
        binding.sectionsOnDevice.adapter = sectionAdapter
        binding.statisticsOnDevice.adapter = statisticsAdapter
    }

    private fun setBindings() {
        viewModel.sections.observe { section ->
            sectionAdapter.submitList(section.map { it.first })
        }

        viewModel.statistics.observe { statistics ->
            statisticsAdapter.setStatistics(statistics)
            binding.totalCountOnDevice.text = statistics.sumBy { it.second }.toString()

            setupStatisticsView(statistics.isEmpty())
        }
    }

    private fun setupStatisticsView(isEmpty: Boolean) {
        with(binding) {
            legendOnDevice.isVisible = !isEmpty
            statisticsOnDevice.isVisible = !isEmpty
            totalLayoutOnDevice.isVisible = !isEmpty
            columnOnDevice.isVisible = !isEmpty

            emptyViewOnDevice.isVisible = isEmpty
        }
    }
}
