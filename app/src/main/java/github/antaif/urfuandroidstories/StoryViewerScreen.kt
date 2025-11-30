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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import github.antaif.urfuandroidstories.model.Story
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StoriesViewingScreen(
    startIndex: Int,
    stories: List<Story>,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { stories.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val progressStates = remember { mutableStateMapOf<Int, Float>() }

    LaunchedEffect(pagerState.currentPage) {
        val currentPage = pagerState.currentPage
        val currentStory = stories[currentPage]

        if (currentStory.type == Story.Type.IMAGE) {
            progressStates[currentPage] = 0f
            val duration = 15000L
            val steps = 150
            val stepDelay = duration / steps
            repeat(steps) {
                delay(stepDelay)
                if (pagerState.currentPage == currentPage) {
                    progressStates[currentPage] = (it + 1) / steps.toFloat()
                }
            }
            if (pagerState.currentPage == currentPage) {
                progressStates[currentPage] = 1f
                if (currentPage < stories.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(currentPage + 1)
                    }
                } else {
                    onClose()
                }
            }
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
                    story = stories[page],
                    isActive = page == pagerState.currentPage,
                    onVideoEnd = {
                        progressStates[page] = 1f
                        if (page < stories.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page + 1)
                            }
                        } else {
                            onClose()
                        }
                    },
                    onVideoProgress = { progress ->
                        progressStates[page] = progress
                    }
                )
            }
        }

        ProgressBars(
            stories = stories,
            currentPage = pagerState.currentPage,
            progressStates = progressStates,
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
    stories: List<Story>,
    currentPage: Int,
    progressStates: Map<Int, Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        stories.forEachIndexed { index, story ->
            val progress = when {
                index < currentPage -> 1f
                index == currentPage -> progressStates[index] ?: 0f
                else -> 0f
            }
            StoryProgressBar(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
            )
        }
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
