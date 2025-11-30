package github.antaif.urfuandroidstories

import github.antaif.urfuandroidstories.navigation.Route
import github.antaif.urfuandroidstories.navigation.Routes
import github.antaif.urfuandroidstories.navigation.TopLevelBackStack
import org.koin.dsl.module

val mainModule = module {
    single { TopLevelBackStack<Route>(Routes.MainScreen) }
}