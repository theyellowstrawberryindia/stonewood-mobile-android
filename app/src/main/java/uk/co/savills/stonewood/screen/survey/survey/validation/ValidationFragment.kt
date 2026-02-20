package uk.co.savills.stonewood.screen.survey.survey.validation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentValidationsBinding
import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyFragmentBase
import uk.co.savills.stonewood.util.LocationTracker

class ValidationFragment : SurveyFragmentBase<ValidationViewModel>() {

    override val viewModel: ValidationViewModel by lazy {
        ValidationViewModel(requireActivity().application, requireActivity() as LocationTracker)
    }

    private lateinit var binding: FragmentValidationsBinding

    private val categoryAdapter by lazy {
        CategoryAdapter(viewModel::onCategorySelected)
    }

    private val validationGroupAdapter by lazy {
        ValidationGroupAdapter(viewModel::onOperandClick)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentValidationsBinding.inflate(
            inflater,
            container,
            false
        ).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.getValidations()
    }

    override fun onVisibleFirstTime() {
        super.onVisibleFirstTime()

        setViews()
        setBindings()
    }

    private fun setViews() {
        binding.categoriesValidations.adapter = categoryAdapter
        binding.elementsValidations.adapter = validationGroupAdapter
    }

    private fun setBindings() {
        viewModel.categories.observe { categories ->
            categoryAdapter.submitList(categories)

            val isEmpty = categories.isEmpty()
            with(binding) {
                paneSeparatorValidations.isVisible = !isEmpty
                emptyViewValidations.isVisible = isEmpty
            }
        }

        viewModel.selectedCategory.observe { category ->
            setCategoryTitle(category)
            validationGroupAdapter.submitList(category.groups.map { it.value })
        }
    }

    private fun setCategoryTitle(category: CategoryPresentationModel) {
        binding.categoryTitleValidations.setText(
            when (category.category) {
                ValidationCategory.S_E -> R.string.se_validation_label
                ValidationCategory.E_LOG -> R.string.e_log_validation_label
                ValidationCategory.S_LOG -> R.string.s_log_validation_label
            }
        )
    }
}
