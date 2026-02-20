package uk.co.savills.stonewood.util.navigation

import uk.co.savills.stonewood.util.SingleLiveEvent

interface Navigator {
    val navigationEvent: SingleLiveEvent<NavigationCommand>
}
