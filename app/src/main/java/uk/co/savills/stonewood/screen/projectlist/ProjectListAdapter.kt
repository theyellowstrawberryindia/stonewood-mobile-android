package uk.co.savills.stonewood.screen.projectlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.databinding.ListItemStandardBinding
import uk.co.savills.stonewood.model.survey.project.ProjectModel

class ProjectListAdapter(private val selectionListener: (ProjectModel) -> Unit) :
    ListAdapter<ProjectModel, ProjectListAdapter.ViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectionListener)
    }

    class ViewHolder private constructor(
        private val binding: ListItemStandardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: ProjectModel, selectionListener: (ProjectModel) -> Unit) = with(binding) {
            titleStandard.text = project.name
            root.setOnClickListener {
                selectionListener.invoke(project)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemStandardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ProjectModel>() {
        override fun areItemsTheSame(oldItem: ProjectModel, newItem: ProjectModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProjectModel, newItem: ProjectModel): Boolean {
            return oldItem == newItem
        }
    }
}
