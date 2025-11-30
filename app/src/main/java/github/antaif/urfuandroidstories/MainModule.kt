package github.antaif.urfuandroidstories

import github.antaif.urfuandroidstories.data.StoriesRepository
import github.antaif.urfuandroidstories.navigation.Route
import github.antaif.urfuandroidstories.navigation.Routes
import github.antaif.urfuandroidstories.navigation.TopLevelBackStack
import github.antaif.urfuandroidstories.viewmodel.StoriesListViewModel
import github.antaif.urfuandroidstories.viewmodel.StoryViewerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single { TopLevelBackStack<Route>(Routes.MainScreen) }
    single { StoriesRepository() }
    viewModel { StoriesListViewModel(get(), get()) }
    viewModel { params ->
        val startIndex = params.getOrNull<Int>() ?: 0
        StoryViewerViewModel(get(), get(), startIndex)
    }
}