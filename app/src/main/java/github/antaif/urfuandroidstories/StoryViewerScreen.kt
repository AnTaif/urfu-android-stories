package github.antaif.urfuandroidstories

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import github.antaif.urfuandroidstories.model.Story
import github.antaif.urfuandroidstories.viewmodel.StoryViewerViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StoriesViewingScreen(
    startIndex: Int,
    viewModel: StoryViewerViewModel = koinViewModel(
        parameters = { parametersOf(startIndex) }
    )
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.stories.size }
    )
    val coroutineScope = rememberCoroutineScope()

    if (state.isLoading || state.stories.isEmpty()) {
        return
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    LaunchedEffect(state.currentPage) {
        if (state.currentPage != pagerState.currentPage && state.currentPage < state.stories.size) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(state.currentPage)
            }
        }
    }

    LaunchedEffect(state.shouldClose) {
        if (state.shouldClose) {
            viewModel.onClose()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            key(page) {
                StoryItem(
                    story = state.stories[page],
                    isActive = page == pagerState.currentPage,
                    onVideoEnd = {
                        viewModel.onVideoEnd(page)
                    },
                    onVideoProgress = { progress ->
                        viewModel.onVideoProgress(page, progress)
                    }
                )
            }
        }

        ProgressBars(
            currentProgress = state.currentProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun StoryItem(
    story: Story,
    isActive: Boolean,
    onVideoEnd: () -> Unit,
    onVideoProgress: (Float) -> Unit
) {
    when (story.type) {
        Story.Type.IMAGE -> {
            AsyncImage(
                model = story.url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Story.Type.VIDEO -> {
            VideoPlayer(
                videoUrl = story.url,
                isActive = isActive,
                onVideoEnd = onVideoEnd,
                onVideoProgress = onVideoProgress
            )
        }
    }
}

@Composable
fun ProgressBars(
    currentProgress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StoryProgressBar(
            progress = currentProgress,
            modifier = Modifier
                .weight(1f)
                .height(3.dp)
        )
    }
}

@Composable
fun StoryProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 100),
            label = "progress"
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )
    }
}
