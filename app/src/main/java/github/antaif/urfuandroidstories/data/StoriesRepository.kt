package github.antaif.urfuandroidstories.data

import github.antaif.urfuandroidstories.model.Story
import github.antaif.urfuandroidstories.model.sampleStories

class StoriesRepository {
    fun getStories(): List<Story> = sampleStories
}

