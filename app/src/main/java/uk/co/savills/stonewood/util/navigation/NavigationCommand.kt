package uk.co.savills.stonewood.util.navigation

import androidx.navigation.NavDirections

sealed class NavigationCommand {
    data class Navigate(val direction: NavDirections) : NavigationCommand()
    object PopBackStack : NavigationCommand()
}
