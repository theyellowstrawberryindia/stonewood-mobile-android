package uk.co.savills.stonewood.screen.survey.survey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.element.CloseEndedQuestionElementModel
import uk.co.savills.stonewood.util.setQuestionButtonStyle

class CloseEndedQuestionSurveyAdapter<T : CloseEndedQuestionElementModel>(
    private val optionClick: (T, CloseEndedQuestionAnswer) -> Unit
) :
    RecyclerView.Adapter<CloseEndedQuestionSurveyAdapter.ViewHolder<T>>() {
    private var listItems: List<T> = listOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_close_ended_question, parent, false)
        return ViewHolder(
            view,
            optionClick
        )
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(listItems[position])
    }

    fun setList(listItems: List<T>) {
        this.listItems = listItems
        notifyDataSetChanged()
    }

    class ViewHolder<T : CloseEndedQuestionElementModel>(
        view: View,
        private val optionClick: (T, CloseEndedQuestionAnswer) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {
        private val questionTextView: TextView = view.findViewById(R.id.textViewCloseEndedQuestion)
        private val yesButton: Button = view.findViewById(R.id.yesButtonCloseEndedQuestion)
        private val noButton: Button = view.findViewById(R.id.noButtonCloseEndedQuestion)

        fun bind(listItem: T) {
            questionTextView.text = listItem.question

            yesButton.setOnClickListener() {
                setButtonStyle(CloseEndedQuestionAnswer.YES)
                optionClick.invoke(listItem, CloseEndedQuestionAnswer.YES)
            }

            noButton.setOnClickListener() {
                setButtonStyle(CloseEndedQuestionAnswer.NO)
                optionClick.invoke(listItem, CloseEndedQuestionAnswer.NO)
            }

            setButtonStyle(listItem.answer)
        }

        private fun setButtonStyle(answer: CloseEndedQuestionAnswer) {
            when (answer) {
                CloseEndedQuestionAnswer.YES -> {
                    yesButton.setQuestionButtonStyle(true)
                    noButton.setQuestionButtonStyle(false)
                }
                CloseEndedQuestionAnswer.NO -> {
                    yesButton.setQuestionButtonStyle(false)
                    noButton.setQuestionButtonStyle(true)
                }
                CloseEndedQuestionAnswer.UNANSWERED -> {
                    yesButton.setQuestionButtonStyle(false)
                    noButton.setQuestionButtonStyle(false)
                }
            }
        }
    }
}
