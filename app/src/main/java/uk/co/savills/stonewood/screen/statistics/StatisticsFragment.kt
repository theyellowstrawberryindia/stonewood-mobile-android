package uk.co.savills.stonewood.screen.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentStatisticsBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.screen.statistics.ondevice.OnDeviceFragment
import uk.co.savills.stonewood.screen.statistics.projecttotal.ProjectTotalFragment

class StatisticsFragment : BaseFragment<StatisticsViewModel>() {
    override val viewModel: StatisticsViewModel by viewModels()

    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentStatisticsBinding
            .inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventHandlers()
    }

    private fun setEventHandlers() {
        binding.backButtonStatistics.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onNavAnimationEnd() {
        super.onNavAnimationEnd()

        setViews()
    }

    private fun setViews() {
        with(binding) {
            pagerStatistics.adapter = typeAdapter
            pagerStatistics.offscreenPageLimit = viewModel.types.size
            pagerStatistics.isUserInputEnabled = false

            TabLayoutMediator(tabLayoutStatistics, pagerStatistics) { tab, position ->
                tab.setText(
                    when (viewModel.types[position]) {
                        Type.ON_DEVICE -> R.string.on_device
                        Type.PROJECT_TOTAL -> R.string.project_total
                    }
                )
            }.attach()
        }
    }

    private val typeAdapter by lazy {
        object : FragmentStateAdapter(this@StatisticsFragment) {
            override fun getItemCount() = viewModel.types.size

            override fun createFragment(position: Int): Fragment {
                return when (viewModel.types[position]) {
                    Type.ON_DEVICE -> OnDeviceFragment()
                    Type.PROJECT_TOTAL -> ProjectTotalFragment()
                }
            }
        }
    }
}
