package uk.co.savills.stonewood.screen.statistics

import android.app.Application
import uk.co.savills.stonewood.screen.base.BaseViewModel

class StatisticsViewModel(application: Application) : BaseViewModel(application) {

    val types = Type.values().toList()
}
