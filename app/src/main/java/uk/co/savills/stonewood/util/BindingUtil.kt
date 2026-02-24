package uk.co.savills.stonewood.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import uk.co.savills.stonewood.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@BindingAdapter("visible")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

//@BindingAdapter("dateTime")
//fun setDateTime(view: TextView, instant: Instant) {
//    val formatter = DateTimeFormatter
//        .ofPattern("dd MMM yyyy")
//        .withZone(ZoneId.systemDefault())
//
//    view.text = formatter.format(instant)
//}
@BindingAdapter("dateTime")
fun setDateTime(view: TextView, instant: Instant?) {

    if (instant == null) {
        view.text = ""
        return
    }

    val formatter = DateTimeFormatter
        .ofPattern("dd MMM yyyy")
        .withZone(ZoneId.systemDefault())

    view.text = formatter.format(instant)
}
//
//@BindingAdapter("photoLabel")
//fun TextView.setPhotoLabel(remaining: Int) {
//    text = context.resources.getQuantityString(R.plurals.photo_view_label, remaining, remaining)
//
//    val textColor = if (remaining == 0) R.color.textDefault else R.color.incomplete
//    setTextColor(context.getColor(textColor))
//}

@BindingAdapter("photoLabel")
fun TextView.setPhotoLabel(remaining: Int?) {

    val value = remaining ?: 0

    text = context.resources.getQuantityString(
        R.plurals.photo_view_label,
        value,
        value
    )

    val textColor =
        if (value == 0) R.color.textDefault
        else R.color.incomplete

    setTextColor(context.getColor(textColor))
}

@BindingAdapter("properCase")
fun TextView.setProperCase(text: String?) {
    this.text = text?.toProperCase()
}
