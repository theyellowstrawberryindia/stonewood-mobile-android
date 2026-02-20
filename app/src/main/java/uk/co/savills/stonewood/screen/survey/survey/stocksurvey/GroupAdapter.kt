package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementGroupModel

class GroupAdapter(
    diffCallback: DiffUtil.ItemCallback<StockSurveyElementGroupModel>,
    private val selectionListener: (StockSurveyElementGroupModel) -> Unit
) : ListAdapter<StockSurveyElementGroupModel, GroupAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectionListener)
    }

    class ViewHolder private constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val groupImage: ImageView = view.findViewById(R.id.groupImageStockGroup)
        private val statusImage: ImageView = view.findViewById(R.id.statusImageStockGroup)

        fun bind(
            element: StockSurveyElementGroupModel,
            itemClick: (StockSurveyElementGroupModel) -> Unit
        ) {
            setGroupImage(element)

            statusImage.setImageResource(
                if (element.isComplete) R.drawable.ic_complete else R.drawable.ic_incomplete
            )

            view.setBackgroundResource(
                if (element.isSelected) R.color.colorSecondary else R.color.backgroundWhite
            )

            view.setOnClickListener { itemClick.invoke(element) }
        }

        private fun setGroupImage(group: StockSurveyElementGroupModel) {
            val communalPartNumber = group.elements.firstOrNull()?.entry?.communalPartNumber

            val resId = if (communalPartNumber != null && communalPartNumber != 0) {
                getCommunalAreaGroupDrawableResId(communalPartNumber)
            } else {
                getDrawableResIdFromTitle(group.title)
            }

            groupImage.setImageResource(resId)
        }

        private fun getDrawableResIdFromTitle(title: String): Int {
            for ((resId, keywords) in drawableMap) {
                if (keywords.any { title.equals(it, ignoreCase = true) }) {
                    return resId
                }
            }
            for ((resId, keywords) in drawableMap) {
                if (keywords.any { title.contains(it, ignoreCase = true) }) {
                    return resId
                }
            }

            return R.drawable.ic_default
        }

        private fun getCommunalAreaGroupDrawableResId(partNumber: Int): Int {
            if (partNumber in 1..19) {
                return view.resources.getIdentifier(
                    "ic_com_$partNumber", "drawable", view.context.packageName
                )
            }

            return R.drawable.ic_com_20
        }

        companion object {
            private val drawableMap = mapOf(
                R.drawable.ic_property_info to arrayOf("general", "property info", "property information", "pas - general"),
                R.drawable.ic_additional_wc to arrayOf("additional wc"),
                R.drawable.ic_bathroom to arrayOf("bathroom"),
                R.drawable.ic_detectors to arrayOf("detectors"),
                R.drawable.ic_doors to arrayOf("doors"),
                R.drawable.ic_electrics to arrayOf("electrics"),
                R.drawable.ic_environmental to arrayOf("environmental"),
                R.drawable.ic_external_store to arrayOf("ext store", "external store"),
                R.drawable.ic_external to arrayOf("ext", "external"),
                R.drawable.ic_heating to arrayOf("heating", "pas - heating"),
                R.drawable.ic_insulation to arrayOf("loft", "insulation"),
                R.drawable.ic_kitchen to arrayOf("kitchen"),
                R.drawable.ic_measurements to arrayOf("measurements", "dc - floor plan"),
                R.drawable.ic_windows to arrayOf("windows", "dc - windows"),
                R.drawable.ic_balcony to arrayOf("balcony"),
                R.drawable.ic_garage to arrayOf("garage"),
                R.drawable.ic_wall_finish to arrayOf("wall", "pas - walls"),
                R.drawable.ic_limited_access to arrayOf("limited access"),
                R.drawable.ic_miscellaneous to arrayOf("miscellaneous"),
                R.drawable.ic_disabled to arrayOf("disabled"),
                R.drawable.ic_pas_dc_misc to arrayOf("dc - miscellaneous"),
                R.drawable.ic_pas_floors to arrayOf("dc - floors"),
                R.drawable.ic_pas_meters to arrayOf("dc - meters"),
                R.drawable.ic_pas_obstructions to arrayOf("dc - obstructions"),
                R.drawable.ic_pas_pas_misc to arrayOf("pas - miscellaneous"),
                R.drawable.ic_pas_roofs to arrayOf("dc - roof"),
                R.drawable.ic_pas_ventilations to arrayOf("pas - ventilation"),
                R.drawable.ic_ext_misc to arrayOf("ext miscellaneous"),
                R.drawable.ic_int_misc to arrayOf("int miscellaneous"),
            )

            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_stock_group, parent, false)
                return ViewHolder(view)
            }
        }
    }
}
