package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentStockSurveyBinding
import uk.co.savills.stonewood.screen.survey.survey.ElementFinder
import uk.co.savills.stonewood.screen.survey.survey.SurveyTabFragment
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyFragmentBase
import uk.co.savills.stonewood.screen.survey.survey.stocksurvey.selectcommunaldata.SelectCommunalDataDialog
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.customview.WarningDialog
import uk.co.savills.stonewood.util.hideKeyboard

class StockSurveyFragment : SurveyFragmentBase<StockSurveyViewModel>(), ElementFinder {
    override val viewModel: StockSurveyViewModel by lazy {
        StockSurveyViewModel(requireActivity().application, requireActivity() as LocationTracker)
    }

    private val groupAdapter by lazy {
        GroupAdapter(StockSurveyElementGroupDiffCallBack()) { element ->
            viewModel.onGroupSelected(element)
            binding.elementRecyclerViewStockSurvey.scrollToPosition(0)
            requireView().hideKeyboard()
        }
    }

    private val elementAdapter by lazy { ElementAdapter(viewModel, ::scrollToPosition, viewModel.areRepairsAvailable) }
    private val elementLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private lateinit var binding: FragmentStockSurveyBinding

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_stock_survey, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment as SurveyTabFragment).setStockUpdateListener(this)
    }

    override fun onVisibleFirstTime() {
        super.onVisibleFirstTime()

        setViews()
        setBindings()
    }

    private fun setViews() {
        with(binding) {
            categoryRecyclerViewStockSurvey.adapter = groupAdapter

            elementRecyclerViewStockSurvey.layoutManager = elementLayoutManager
            elementRecyclerViewStockSurvey.adapter = elementAdapter
            elementRecyclerViewStockSurvey.isNestedScrollingEnabled = false
        }
    }

    private fun setBindings() {
        viewModel.groups.observe {
            groupAdapter.submitList(it)
        }

        viewModel.selectedGroup.observe { group ->
            val elements = group.elements
                .filterNot { element -> element.isSkipped }
                .map { element ->
                    element
                        .copy(subElements = element.subElements.filterNot { it.isSkipped })
                        .apply {
                            unitTobeUsed = element.unitTobeUsed
                        }
                }

            elementAdapter.setData(
                group.title,
                elements,
                viewModel.ageBands,
                viewModel.renewalBands,
                viewModel.noAccessReasons,
                viewModel.ageOfProperty
            )
        }

        viewModel.addPhoto.observe { (fileName, successListener) ->
            camera.takePhoto(fileName, viewModel.photoFolderName, successListener)
        }

        viewModel.elementUpdated.observe {
            elementAdapter.notifyItemChanged(it)
        }

        viewModel.groupUpdated.observe {
            groupAdapter.notifyDataSetChanged()
        }

        viewModel.excessiveValueDetected.observe {
            excessiveValueWarningDialog
                .setDescription(getString(R.string.excessive_value_warning_dialog_message, it.first, it.second))
                .show()
        }

        viewModel.excessivelyLowValueDetected.observe {
            excessivelyLowValueWarningDialog
                .setDescription(getString(R.string.excessively_low_value_warning_dialog_message, it.first, it.second))
                .show()
        }

        viewModel.communalDataRequested.observe {
            SelectCommunalDataDialog(
                it.first,
                selectionListener = it.second
            ).show(childFragmentManager, "Select commnual data")
        }
    }

    private fun scrollToPosition(position: Int) {
        try {
            elementLayoutManager.startSmoothScroll(getSmoothScroller(position))
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

    override fun onFindElement(elementId: Int) {
        val group = viewModel.groups.value?.find { group ->
            group.elements.filter { !it.isSkipped }.any { it.id == elementId }
        } ?: return

        if (!group.isSelected) viewModel.onGroupSelected(group)

        group.elements.filterNot { element ->
            element.isSkipped
        }.indexOfFirst {
            it.id == elementId
        }.let(::scrollToPosition)
    }
}
