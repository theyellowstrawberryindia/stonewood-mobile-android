package uk.co.savills.stonewood.util.navigation

import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import uk.co.savills.stonewood.R

class NavigationEventObserver(private val navController: NavController) :
    Observer<NavigationCommand> {

    private val navOptionsBuilder = NavOptions.Builder()
        .setEnterAnim(R.anim.nav_enter_anim)
        .setExitAnim(R.anim.nav_exit_anim)
        .setPopEnterAnim(R.anim.nav_pop_enter_anim)
        .setPopExitAnim(R.anim.nav_pop_exit_anim)

    override fun onChanged(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.Navigate -> {
                val direction = command.direction
                val navAction =
                    requireNotNull(navController.currentDestination).getAction(direction.actionId)
                val predefinedNavOptions = requireNotNull(navAction).navOptions

                predefinedNavOptions?.apply {
                    navOptionsBuilder.setPopUpTo(getPopUpTo(), isPopUpToInclusive())
                        .setLaunchSingleTop(shouldLaunchSingleTop())
                }

                navController.navigate(direction, navOptionsBuilder.build())
            }

            is NavigationCommand.PopBackStack -> {
                navController.popBackStack()
            }
        }
    }
}