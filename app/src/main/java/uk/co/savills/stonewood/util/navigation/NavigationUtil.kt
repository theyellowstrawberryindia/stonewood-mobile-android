package uk.co.savills.stonewood.util.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import uk.co.savills.stonewood.APP_STORE_ADDRESS
import uk.co.savills.stonewood.util.AppAnalytics
import java.lang.Exception

fun Context.makePhoneCall(number: String) {
    val callIntent = Intent().apply {
        action = Intent.ACTION_DIAL
        data = Uri.parse("tel:$number")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    startActivity(callIntent)
}

fun Context.searchAddressOnMap(address: String) {
    val searchText = address.replace(' ', '+')

    val intentUri = Uri.parse("geo:0,0?q=$searchText")
    val mapIntent = Intent(Intent.ACTION_VIEW, intentUri).apply {
        setPackage("com.google.android.apps.maps")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(mapIntent)
}

fun Context.openExternalLink(link: String) {
    val linkIntent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(link)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    startActivity(linkIntent)
}

fun Context.goToAppStore() {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(APP_STORE_ADDRESS)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        startActivity(
            if (APP_STORE_ADDRESS.startsWith("https://play.google.com")) {
                Intent(intent).setPackage("com.android.vending")
            } else {
                intent
            }
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(intent)
    } catch (e: Exception) {
        AppAnalytics.trackError(e)
    }
}
