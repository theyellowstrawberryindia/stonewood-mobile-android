package uk.co.savills.stonewood.screen.survey.noaccess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import uk.co.savills.stonewood.R

class ReasonSpinnerAdapter(val context: Context, private var dataSource: List<String>) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_spinner_view, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        vh.title.setTextColor(ContextCompat.getColor(context, if (position == 0) R.color.textLight else R.color.textDefault))
        vh.title.text = dataSource[position]
        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(row: View?) {
        var title: TextView = row?.findViewById(R.id.titleTextSpinnerView) as TextView
    }
}
