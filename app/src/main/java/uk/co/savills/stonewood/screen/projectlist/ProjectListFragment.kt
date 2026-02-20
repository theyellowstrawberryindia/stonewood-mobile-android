package uk.co.savills.stonewood.screen.projectlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.FragmentProjectListBinding
import uk.co.savills.stonewood.screen.base.BaseFragment
import uk.co.savills.stonewood.util.customview.StandardDialog

class ProjectListFragment : BaseFragment<ProjectListViewModel>() {
    private lateinit var adapter: ProjectListAdapter
    override val viewModel: ProjectListViewModel by viewModels()

    private val logoutConfirmationDialog by lazy {
        StandardDialog.Builder(requireContext())
            .setTitle(R.string.warning_dialog_header)
            .setDescription(R.string.logout_warning_dialog_message)
            .setNegativeButton(R.string.no)
            .setPositiveButton(R.string.yes, viewModel::logout)
            .build()
    }

    private lateinit var binding: FragmentProjectListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_project_list, container, false)

        binding.projectListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews()
        setBindings()
        setEventHandlers()
    }

    private fun setViews() {
        with(binding) {
            adapter = ProjectListAdapter(viewModel::onProjectSelected)
            recyclerViewProjectList.adapter = adapter

            refreshLayoutProjectList.setColorSchemeColors(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.progress
                )
            )
        }
    }

    private fun setEventHandlers() {
        with(binding) {
            toolbarProjectList.setOptionClickListener(logoutConfirmationDialog::show)
            refreshLayoutProjectList.setOnRefreshListener(viewModel::refreshProjects)
        }
    }

    private fun setBindings() {
        viewModel.projects.observe { projects ->
            adapter.submitList(projects)
            binding.refreshLayoutProjectList.isRefreshing = false
        }

        viewModel.isRefreshingProjects.observe { isRefreshing ->
            binding.refreshLayoutProjectList.isRefreshing = isRefreshing
        }
    }
}
