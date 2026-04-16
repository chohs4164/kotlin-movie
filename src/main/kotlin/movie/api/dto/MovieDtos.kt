package movie.api.dto

import java.time.LocalDateTime

data class MoviesResponse(
    val movies: List<MovieResponse>,
)

data class MovieResponse(
    val movieId: String,
    val title: String,
    val runningTimeMinutes: Int,
    val screenings: List<MovieScreeningResponse>,
)

data class MovieScreeningResponse(
    val screeningId: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)
