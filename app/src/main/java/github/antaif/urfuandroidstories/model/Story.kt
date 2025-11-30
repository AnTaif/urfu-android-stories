package github.antaif.urfuandroidstories.model

data class Story(
    val type: Type,
    val url: String,
) {
    enum class Type { VIDEO, IMAGE }
}

val sampleStories = listOf(
    Story(
        Story.Type.VIDEO,
        "https://samplelib.com/lib/preview/mp4/sample-5s.mp4"
    ),
    Story(
        Story.Type.IMAGE,
        "https://cdn.ruwiki.ru/commonswiki/files/thumb/2/2b/Ufenau_-_Oryctolagus_cuniculus_2011-07-25_17-33-40.jpg/1280px-Ufenau_-_Oryctolagus_cuniculus_2011-07-25_17-33-40.jpg"
    ),
    Story(
        Story.Type.VIDEO,
        "https://samplelib.com/lib/preview/mp4/sample-20s.mp4"
    ),
    Story(
        Story.Type.IMAGE,
        "https://cdn.ruwiki.ru/commonswiki/files/a/ae/2007_Subaru_Forester_01.jpg"
    ),
    Story(
        Story.Type.VIDEO,
        "https://samplelib.com/lib/preview/mp4/sample-30s.mp4"
    ),
)