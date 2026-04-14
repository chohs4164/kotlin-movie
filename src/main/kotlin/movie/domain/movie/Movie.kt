package movie.domain.movie

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Movie(
    private val id: Uuid = Uuid.random(),
    val title: MovieTitle,
) {
    fun isSameMovie(target: Movie): Boolean = id == target.id
}
