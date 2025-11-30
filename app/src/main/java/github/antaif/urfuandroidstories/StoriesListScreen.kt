package github.antaif.urfuandroidstories

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import github.antaif.urfuandroidstories.model.Story

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesListScreen(
    stories: List<Story>,
    onStoryClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(stories.size) { index ->
            val s = stories[index]
            StoryIcon(
                story = s,
                onClick = {
                    onStoryClick(index)
                }
            )
        }
    }
}

@Composable
fun StoryIcon(story: Story, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(0.5.dp, Color.Black, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            when (story.type) {
                Story.Type.IMAGE -> {
                    AsyncImage(
                        model = story.url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Story.Type.VIDEO -> {
                    VideoThumbnail(
                        url = story.url,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}