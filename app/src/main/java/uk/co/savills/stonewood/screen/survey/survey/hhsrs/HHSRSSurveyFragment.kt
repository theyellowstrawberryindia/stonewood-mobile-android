package uk.co.savills.stonewood.screen.survey.survey.hhsrs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.flexbox.FlexboxLayoutManager
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentHhsrsSurveyBinding
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyFragmentBase
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.allowNestedScrolling
import uk.co.savills.stonewood.util.customview.StandardDialog
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.hideKeyboard
import uk.co.savills.stonewood.util.photo.PhotoClickListener
import uk.co.savills.stonewood.util.photo.adapter.PhotoAdapter
import uk.co.savills.stonewood.util.setDoneButton

@SuppressLint("InvalidClassName")
class HHSRSSurveyFragment : SurveyFragmentBase<HHSRSSurveyViewModel>() {
    override val viewModel: HHSRSSurveyViewModel by lazy {
        HHSRSSurveyViewModel(requireActivity().application, requireActivity() as LocationTracker)
    }

    private val elementAdapter by lazy {
        ElementAdapter(HHSRSSurveyElementDiffCallback(), ::onElementSelected)
    }

    private val ratingAdapter by lazy {
        RatingAdapter(viewModel.ratings, viewModel::onRatingSelected)
    }

    private val photoAdapter: PhotoAdapter by lazy { PhotoAdapter(photoClickListener) }

    private lateinit var internalLocationAdapter: LocationAdapter
    private lateinit var externalLocationAdapter: LocationAdapter

    private lateinit var binding: FragmentHhsrsSurveyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hhsrs_survey, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onVisibleFirstTime() {
        super.onVisibleFirstTime()

        setViews()
        setBindings()
    }

    private fun setViews() {
        with(binding) {
            elementRecyclerViewHHSRSSurvey.adapter = elementAdapter

            remarksEditTextHHSRSSurvey.allowNestedScrolling()
            remarksEditTextHHSRSSurvey.setDoneButton()

            otherInternalLocationEditTextHHSRSSurvey.allowNestedScrolling()
            otherInternalLocationEditTextHHSRSSurvey.setDoneButton()

            otherExternalLocationEditTextHHSRSSurvey.allowNestedScrolling()
            otherExternalLocationEditTextHHSRSSurvey.setDoneButton()

            ratingRecyclerView.layoutManager = FlexboxLayoutManager(requireContext())
            ratingRecyclerView.adapter = ratingAdapter

            binding.internalLocationRecyclerViewHHSRSSurvey.layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

            binding.externalLocationRecyclerViewHHSRSSurvey.layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

            photosRecyclerViewHHSRSSurvey.layoutManager = FlexboxLayoutManager(requireContext())
            photosRecyclerViewHHSRSSurvey.adapter = photoAdapter
        }
    }

    private fun setBindings() {
        viewModel.internalLocations.observe {
            internalLocationAdapter = LocationAdapter(it, internalLocationClickListener)
            binding.internalLocationRecyclerViewHHSRSSurvey.adapter = internalLocationAdapter
        }

        viewModel.externalLocations.observe {
            externalLocationAdapter = LocationAdapter(it, externalLocationClickListener)
            binding.externalLocationRecyclerViewHHSRSSurvey.adapter = externalLocationAdapter
        }

        viewModel.elements.observe(elementAdapter::submitList)

        viewModel.selectedElement.observe { element ->
            ratingAdapter.setSelectedRating(element.entry.rating)
            photoAdapter.setList(element.entry.imagePaths)
            internalLocationAdapter.setSelectedLocations(element.entry.internalLocations)
            externalLocationAdapter.setSelectedLocations(element.entry.externalLocations)
        }

        viewModel.elementsUpdated.observe {
            elementAdapter.notifyDataSetChanged()
        }

        viewModel.elementAutoSelected.observe(::onElementSelected)

        viewModel.ratingChangedToTypical.observe {
            typicalRatingWarning.show()
        }
    }

    private fun onElementSelected(position: Int) {
        viewModel.onElementSelected(position)
        scrollToPosition(position)
        binding.scrollViewHHSRSSurvey.scrollTo(0, 0)
        requireView().hideKeyboard()
    }

    private fun scrollToPosition(position: Int) {
        val layoutManager =
            binding.elementRecyclerViewHHSRSSurvey.layoutManager as LinearLayoutManager
        if (position >= 0 && position < elementAdapter.itemCount) {
            val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                override fun calculateDtToFit(
                    viewStart: Int,
                    viewEnd: Int,
                    boxStart: Int,
                    boxEnd: Int,
                    snapPreference: Int
                ): Int {
                    return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
                }
            }
            smoothScroller.targetPosition = position
            layoutManager.startSmoothScroll(smoothScroller)
        }
    }

    private val photoClickListener = object : PhotoClickListener {
        val imagePaths
            get() = viewModel.selectedElement.getNonNullValue().entry.imagePaths

        override fun onAddPhotoClick() {
            camera.takePhoto(
                viewModel.photoFileName,
                viewModel.photoFolderName,
            ) {
                viewModel.onImageAdded(it)
                photoAdapter.setList(imagePaths)
            }
        }

        override fun onRemovePhotoClick(filePath: String) {
            viewModel.onImageRemoved(filePath)
            photoAdapter.setList(imagePaths)
        }
    }

    private val internalLocationClickListener = object : LocationClickListener {
        override fun onLocationClick(location: String, isSelected: Boolean) {
            if (isSelected) {
                viewModel.onInternalLocationSelected(location)
            } else {
                viewModel.onInternalLocationDeselected(location)
            }
        }

        override fun onOtherLocationClick(isSelected: Boolean) {
            if (isSelected) {
                viewModel.onOtherInternalLocationSelected()
            } else {
                viewModel.onOtherInternalLocationDeselected()
            }
        }
    }

    private val externalLocationClickListener = object : LocationClickListener {
        override fun onLocationClick(location: String, isSelected: Boolean) {
            if (isSelected) {
                viewModel.onExternalLocationSelected(location)
            } else {
                viewModel.onExternalLocationDeselected(location)
            }
        }

        override fun onOtherLocationClick(isSelected: Boolean) {
            if (isSelected) {
                viewModel.onOtherExternalLocationSelected()
            } else {
                viewModel.onOtherExternalLocationDeselected()
            }
        }
    }

    private val typicalRatingWarning by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.warning_dialog_header)
            .setDescription(R.string.typical_rating_warning_dialog_message)
            .setPositiveButton(R.string.yes) {
                viewModel.changeRatingToTypical()
            }
            .setNegativeButton(R.string.no)
            .build()
    }
}
