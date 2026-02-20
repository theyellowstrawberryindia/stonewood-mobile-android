package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import uk.co.savills.stonewood.R

class ComboBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class Type { HIDE_WHEN_SINGLE_ITEM, DISABLE_WHEN_SINGLE_ITEM }

    interface OnItemSelectedListener {
        fun onPlaceHolderSelected()

        fun onItemSelected(position: Int, isUserSelected: Boolean)
    }

    private lateinit var items: List<String>
    private val spinner by lazy { findViewById<Spinner>(R.id.spinnerComboBox) }

    private var type = Type.HIDE_WHEN_SINGLE_ITEM

    var isDefaultSelection = true

    init {
        View.inflate(context, R.layout.layout_combo_box, this)
    }

    fun setType(type: Type) {
        this.type = type
    }

    fun setItems(
        list: List<String>,
        onItemSelectedListener: OnItemSelectedListener,
        selectedItemPosition: Int = -1,
        placeHolderResId: Int = R.string.combo_box_placeholder
    ) {
        items = if (list.size > 1) {
            when (type) {
                Type.HIDE_WHEN_SINGLE_ITEM -> spinner.isVisible = true
                Type.DISABLE_WHEN_SINGLE_ITEM -> spinner.isEnabled = true
            }

            list.toMutableList().apply { add(0, context.getString(placeHolderResId)) }
        } else {
            list
        }

        val listener = getItemSelectedListener(onItemSelectedListener)

        isDefaultSelection = true
        if (items.isNotEmpty()) {
            setInitialSelection(selectedItemPosition, list, listener)
        }
    }

    fun setSelection(position: Int) {
        if (!::items.isInitialized) return

        isDefaultSelection = true
        spinner.setSelection(
            if (position >= 0) {
                if (items.size > 1) position + 1 else position
            } else {
                0
            }
        )
    }

    private fun setInitialSelection(
        selectedItemPosition: Int,
        list: List<String>,
        listener: AdapterView.OnItemSelectedListener
    ) {
        val position = if (selectedItemPosition != -1) {
            if (list.size > 1) selectedItemPosition + 1 else selectedItemPosition
        } else {
            0
        }

        spinner.adapter = ItemAdapter(context, items)
        spinner.setSelection(position)

        spinner.post {
            listener.onItemSelected(spinner, spinner.getChildAt(position), position, 0)
            spinner.onItemSelectedListener = listener
        }
    }

    private fun getItemSelectedListener(listener: OnItemSelectedListener): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            var previouslySelectedPosition = -1

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (previouslySelectedPosition == position) return

                val titleView = view?.findViewById<TextView>(R.id.titleComboBoxPlaceholder)

                if (items.size > 1 && position == 0) {
                    listener.onPlaceHolderSelected()
                } else {
                    val isUserSelection = if (isDefaultSelection) false else items.size > 1

                    if (items.size > 1) {
                        listener.onItemSelected(position - 1, isUserSelection)
                    } else {
                        listener.onItemSelected(position, isUserSelection)

                        when (type) {
                            Type.HIDE_WHEN_SINGLE_ITEM -> spinner.isVisible = false
                            Type.DISABLE_WHEN_SINGLE_ITEM -> spinner.isEnabled = false
                        }
                    }

                    titleView?.setTextColor(parent.context.getColor(R.color.textDefault))
                }

                if (isDefaultSelection) isDefaultSelection = false

                previouslySelectedPosition = position
            }
        }
    }

    fun reset() {
        spinner.setSelection(0)
    }

    class ItemAdapter(
        context: Context,
        private val items: List<String>
    ) : ArrayAdapter<String>(
        context,
        R.layout.list_item_combo_box_placeholder,
        items
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val viewHolder = if (convertView == null || convertView.tag == null) {
                PlaceHolderViewHolder.from(parent)
            } else {
                convertView.tag as PlaceHolderViewHolder
            }

            val isPlaceHolder = items.size > 1 && position == 0
            viewHolder.setText(getItem(position), isPlaceHolder)
            viewHolder.setClickable(items.size > 1)
            return viewHolder.root
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val viewHolder = if (convertView == null || convertView.tag == null) {
                DropdownViewHolder.from(parent)
            } else {
                convertView.tag as DropdownViewHolder
            }

            viewHolder.setTitle(getItem(position))
            return if (position != 0) viewHolder.root else View(context)
        }

        override fun getCount() = items.size

        override fun getItem(position: Int) = items[position]

        class PlaceHolderViewHolder private constructor(val root: View) {
            private val titleTextView: TextView = root.findViewById(R.id.titleComboBoxPlaceholder)
            private val arrowImage: ImageView =
                root.findViewById(R.id.arrowImageComboBoxPlaceholder)

            fun setText(title: String, isPlaceHolder: Boolean) {
                titleTextView.text = title

                val color = if (isPlaceHolder) R.color.textLight else R.color.textDefault
                titleTextView.setTextColor(root.context.getColor(color))
            }

            fun setClickable(isClickable: Boolean) {
                arrowImage.isVisible = isClickable
            }

            companion object {
                fun from(parent: ViewGroup): PlaceHolderViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val view =
                        layoutInflater.inflate(
                            R.layout.list_item_combo_box_placeholder,
                            parent,
                            false
                        )

                    return PlaceHolderViewHolder(view)
                }
            }
        }

        class DropdownViewHolder private constructor(val root: View) {
            private val titleTextView: TextView =
                root.findViewById(R.id.titleComboBoxDropdown)

            fun setTitle(title: String) {
                titleTextView.text = title
            }

            companion object {
                fun from(parent: ViewGroup): DropdownViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val view = layoutInflater.inflate(
                        R.layout.list_item_combo_box_dropdown,
                        parent,
                        false
                    )

                    return DropdownViewHolder(view)
                }
            }
        }
    }
}
