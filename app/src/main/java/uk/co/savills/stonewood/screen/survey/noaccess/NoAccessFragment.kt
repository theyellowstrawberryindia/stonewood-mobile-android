package uk.co.savills.stonewood.screen.survey.noaccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexboxLayoutManager
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentNoAccessBinding
import uk.co.savills.stonewood.screen.base.CameraFragmentBase
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.customview.ComboBox
import uk.co.savills.stonewood.util.photo.PhotoClickListener
import uk.co.savills.stonewood.util.photo.adapter.PhotoAdapter

class NoAccessFragment : CameraFragmentBase<NoAccessViewModel>() {
    private lateinit var adapter: PhotoAdapter
    override val viewModel: NoAccessViewModel by lazy {
        NoAccessViewModel(requireActivity().application, requireActivity() as LocationTracker)
    }

    private lateinit var binding: FragmentNoAccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navArgs: NoAccessFragmentArgs by navArgs()
        viewModel.setProperty(navArgs.propertyId, navArgs.propertyUPRN)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_no_access, container, false)

        binding.noAccessViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPhotoGridAdapter()
        setBindings()
    }

    private fun setPhotoGridAdapter() {
        adapter = PhotoAdapter(photoClickListener)
        binding.photoRecyclerViewNoAccess.layoutManager = FlexboxLayoutManager(requireContext())
        binding.photoRecyclerViewNoAccess.adapter = adapter
    }

    private fun setBindings() {
        viewModel.reasons.observe {
            binding.reasonComboBoxNoAccess.setItems(
                it,
                reasonSelectionListener,
                placeHolderResId = R.string.select_reason
            )
        }
        viewModel.photoFilePaths.observe(adapter::setList)
    }

    private val reasonSelectionListener = object : ComboBox.OnItemSelectedListener {
        override fun onPlaceHolderSelected() {
        }

        override fun onItemSelected(position: Int, isUserSelected: Boolean) {
            viewModel.setReason(position)
        }
    }

    private val photoClickListener = object : PhotoClickListener {
        override fun onAddPhotoClick() {
            camera.takePhoto(
                viewModel.photoFileName,
                viewModel.photoFolderName,
                viewModel::onImageAdded
            )
        }

        override fun onRemovePhotoClick(filePath: String) {
            viewModel.onImageRemoved(filePath)
        }
    }
}
