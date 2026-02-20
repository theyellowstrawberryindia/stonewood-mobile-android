package uk.co.savills.stonewood.screen.survey.survey.hhsrs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.screen.survey.survey.hhsrs.HHSRSSurveyViewModel.Companion.OTHER_LOCATION_PREFIX
import uk.co.savills.stonewood.util.setOptionStyle

class LocationAdapter(
    private var locations: List<String>,
    private val locationClickListener: LocationClickListener
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    private var selectedLocations: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun getItemCount(): Int = locations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        val isOtherLocation = location.equals("other", ignoreCase = true)

        if (isOtherLocation) {
            val isSelected = selectedLocations.any { it.startsWith(OTHER_LOCATION_PREFIX) }
            holder.setOthersView(isSelected) { _isSelected ->
                if (_isSelected) {
                    selectedLocations.add(OTHER_LOCATION_PREFIX)
                } else {
                    selectedLocations.removeIf { it.startsWith(OTHER_LOCATION_PREFIX) }
                }

                locationClickListener.onOtherLocationClick(_isSelected)
            }
        } else {
            val isSelected = selectedLocations.contains(location)
            holder.bind(location, isSelected) { _isSelected: Boolean ->
                if (_isSelected) {
                    selectedLocations.add(location)
                } else {
                    selectedLocations.remove(location)
                }

                locationClickListener.onLocationClick(location, _isSelected)
            }
        }
    }

    fun setSelectedLocations(selectedLocations: List<String>) {
        this.selectedLocations = selectedLocations.toMutableList()

        notifyDataSetChanged()
    }

    class ViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val button: Button = view.findViewById(R.id.buttonHHSRSLocation)

        var isSelected = false

        fun bind(location: String, isSelected: Boolean, clickListener: (Boolean) -> Unit) {
            this.isSelected = isSelected

            button.text = location
            button.setOptionStyle(isSelected)

            button.setOnClickListener {
                this.isSelected = !this.isSelected

                button.setOptionStyle(this.isSelected)
                clickListener.invoke(this.isSelected)
            }
        }

        fun setOthersView(isSelected: Boolean, clickListener: (Boolean) -> Unit) {
            this.isSelected = isSelected

            button.setText(R.string.others_option)
            setOtherLocationButtonStyle()

            button.setOnClickListener {
                this.isSelected = !this.isSelected

                setOtherLocationButtonStyle()
                clickListener.invoke(this.isSelected)
            }
        }

        private fun setOtherLocationButtonStyle() {
            button.setBackgroundResource(if (isSelected) R.color.colorPrimary else R.color.backgroundGray)
            button.setTextColor(
                ContextCompat.getColor(
                    button.context,
                    if (isSelected) R.color.textWhite else R.color.textDefault
                )
            )
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(
                    R.layout.list_item_hhsrs_location,
                    parent,
                    false
                )

                return ViewHolder(view)
            }
        }
    }
}
