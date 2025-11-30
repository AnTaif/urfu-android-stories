package github.antaif.urfuandroidstories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.antaif.urfuandroidstories.data.StoriesRepository
import github.antaif.urfuandroidstories.model.Story
import github.antaif.urfuandroidstories.navigation.Route
import github.antaif.urfuandroidstories.navigation.Routes
import github.antaif.urfuandroidstories.navigation.TopLevelBackStack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoriesListViewModel(
    private val topLevelBackStack: TopLevelBackStack<Route>,
    private val repository: StoriesRepository
) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    init {
        loadStories()
    }

    private fun loadStories() {
        viewModelScope.launch {
            _stories.value = repository.getStories()
        }
    }

    fun onStoryClick(index: Int) {
        topLevelBackStack.addTopLevel(
            Routes.StoriesViewingScreen(index)
        )
    }
}

