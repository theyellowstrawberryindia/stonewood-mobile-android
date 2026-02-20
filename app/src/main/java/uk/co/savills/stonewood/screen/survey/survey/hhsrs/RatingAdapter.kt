package uk.co.savills.stonewood.screen.survey.survey.hhsrs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.HHSRSElementRating
import uk.co.savills.stonewood.util.setOptionStyle

@SuppressLint("InvalidClassName")
class RatingAdapter(
    private val ratings: List<HHSRSElementRating> = listOf(),
    private val onRatingSelected: (HHSRSElementRating) -> Unit
) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {
    private var selectedRating: HHSRSElementRating? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun getItemCount(): Int = ratings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = ratings[position]
        val isSelected = rating == selectedRating
        holder.bind(rating, isSelected) {
            setSelectedRating(rating)
            onRatingSelected(rating)
        }
    }

    fun setSelectedRating(rating: HHSRSElementRating?) {
        selectedRating = rating
//        notifyDataSetChanged()
    }

    class ViewHolder private constructor(
        private val button: Button,
    ) : RecyclerView.ViewHolder(button) {

        init {
            (button.layoutParams as FlexboxLayoutManager.LayoutParams).flexGrow = 1f
        }

        fun bind(
            rating: HHSRSElementRating,
            isSelected: Boolean,
            onItemSelected: () -> Unit
        ) {
            button.text = rating.title
            button.setOptionStyle(isSelected)
            button.setOnClickListener { onItemSelected() }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(
                    R.layout.list_item_hhsrs_rating,
                    parent,
                    false
                )

                return ViewHolder(view as Button)
            }
        }
    }
}
