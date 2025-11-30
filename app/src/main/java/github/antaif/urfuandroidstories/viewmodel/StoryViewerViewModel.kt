package github.antaif.urfuandroidstories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.antaif.urfuandroidstories.data.StoriesRepository
import github.antaif.urfuandroidstories.model.Story
import github.antaif.urfuandroidstories.navigation.Route
import github.antaif.urfuandroidstories.navigation.TopLevelBackStack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoryViewerState(
    val currentPage: Int,
    val stories: List<Story> = emptyList(),
    val currentProgress: Float = 0f,
    val shouldClose: Boolean = false,
    val isLoading: Boolean = true
)

class StoryViewerViewModel(
    private val topLevelBackStack: TopLevelBackStack<Route>,
    private val repository: StoriesRepository,
    initialStartIndex: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        StoryViewerState(
            currentPage = initialStartIndex,
            isLoading = true
        )
    )
    val state: StateFlow<StoryViewerState> = _state.asStateFlow()

    private var progressJob: Job? = null

    init {
        loadStories()
    }

    private fun loadStories() {
        viewModelScope.launch {
            val stories = repository.getStories()
            _state.value = _state.value.copy(
                stories = stories,
                isLoading = false
            )
            startProgressForCurrentStory()
        }
    }

    fun onClose() {
        topLevelBackStack.removeLast()
    }

    fun onPageChanged(newPage: Int) {
        _state.value = _state.value.copy(currentPage = newPage, currentProgress = 0f)
        startProgressForCurrentStory()
    }

    fun onVideoProgress(page: Int, progress: Float) {
        if (page == _state.value.currentPage) {
            _state.value = _state.value.copy(currentProgress = progress)
        }
    }

    fun onVideoEnd(page: Int) {
        if (page == _state.value.currentPage) {
            _state.value = _state.value.copy(currentProgress = 1f)
        }

        if (page < _state.value.stories.size - 1) {
            moveToNextStory()
        } else {
            _state.value = _state.value.copy(shouldClose = true)
        }
    }

    fun onImageProgressComplete() {
        val currentPage = _state.value.currentPage
        if (currentPage < _state.value.stories.size - 1) {
            moveToNextStory()
        } else {
            _state.value = _state.value.copy(shouldClose = true)
        }
    }

    private fun moveToNextStory() {
        val nextPage = _state.value.currentPage + 1
        _state.value = _state.value.copy(currentPage = nextPage, currentProgress = 0f)
        startProgressForCurrentStory()
    }

    private fun startProgressForCurrentStory() {
        progressJob?.cancel()

        val currentPage = _state.value.currentPage
        val currentStory = _state.value.stories.getOrNull(currentPage) ?: return

        if (currentStory.type == Story.Type.IMAGE) {
            _state.value = _state.value.copy(currentProgress = 0f)

            progressJob = viewModelScope.launch {
                val duration = 15000L
                val steps = 150
                val stepDelay = duration / steps

                repeat(steps) {
                    delay(stepDelay)
                    if (_state.value.currentPage == currentPage) {
                        _state.value = _state.value.copy(
                            currentProgress = (it + 1) / steps.toFloat()
                        )
                    }
                }

                if (_state.value.currentPage == currentPage) {
                    _state.value = _state.value.copy(currentProgress = 1f)
                    onImageProgressComplete()
                }
            }
        } else {
            _state.value = _state.value.copy(currentProgress = 0f)
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
    }
}

