package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel

class ElementAdapter(
    private val updateListener: ElementUpdateListener,
    private val scrollToPosition: (Int) -> Unit,
    private val areRepairsAvailable: Boolean
) : ListAdapter<StockSurveyElementModel, ElementViewHolder>(StockSurveyElementCallback()), ElementViewHolder.UserActionListener {

    private var group = ""
    private var ageBands: List<BandModel> = listOf()
    private var renewalBands: List<BandModel> = listOf()
    private var noAccessReasons: List<String> = listOf()
    private var ageOfProperty: Int = Int.MAX_VALUE

    private val viewHolders = mutableListOf<ElementViewHolder>()

    private val adapterScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementViewHolder {
        return ElementViewHolder.get(
            parent,
            ageBands.map { it.copy() },
            renewalBands.map { it.copy() },
            noAccessReasons,
            areRepairsAvailable,
        )
    }

    override fun onBindViewHolder(holder: ElementViewHolder, position: Int) {
        val element = getItem(position)

        val oldHolder = viewHolders.find { it.id == element.id }

        if (oldHolder != null) {
            viewHolders.remove(oldHolder)
            if (oldHolder.isExpanded) holder.expand()
        }

        viewHolders.add(holder)

        holder.bind(element, ageOfProperty, updateListener, this)
    }

    fun setData(
        group: String,
        elements: List<StockSurveyElementModel>,
        ageBands: List<BandModel>,
        renewalBands: List<BandModel>,
        noAccessReasons: List<String>,
        ageOfProperty: Int
    ) {
        this.ageBands = ageBands
        this.renewalBands = renewalBands
        this.noAccessReasons = noAccessReasons
        this.ageOfProperty = ageOfProperty

        if (this.group != group) {
            this.group = group
            viewHolders.forEach { it.collapse() }
            viewHolders.clear()
        }

        submitList(elements)
    }

    override fun onSubElementSelected(position: Int) {
        scrollToPosition.invoke(position)
    }

    override fun onViewExpanded(element: StockSurveyElementModel, position: Int) {
        viewHolders.forEach { viewHolder ->
            if (viewHolder.id != element.id && viewHolder.isExpanded) {
                viewHolder.collapse()
            }
        }

        adapterScope.launch {
            delay(50)
            scrollToPosition(position)
        }
    }
}

class StockSurveyElementCallback : DiffUtil.ItemCallback<StockSurveyElementModel>() {
    override fun areItemsTheSame(
        oldItem: StockSurveyElementModel,
        newItem: StockSurveyElementModel
    ): Boolean {
        return oldItem.id == newItem.id &&
            oldItem.entry.communalPartNumber == newItem.entry.communalPartNumber
    }

    override fun areContentsTheSame(
        oldItem: StockSurveyElementModel,
        newItem: StockSurveyElementModel
    ): Boolean {
        return oldItem == newItem
    }
}
