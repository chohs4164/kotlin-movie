package movie.domain.movie

import java.time.LocalDate

class ScreeningMovies(
    screeningMovies: List<ScreeningMovie> = emptyList(),
) {
    private val ScreeningMovies = screeningMovies.toMutableList()

    fun addMovie(movie: ScreeningMovie) {
        ScreeningMovies.add(movie)
    }

    fun checkDuplicateTime(
        theater: Theater,
        movieTime: MovieTime,
    ): Boolean =
        ScreeningMovies
            .filter {
                it.theater.sameTheater(theater)
            }.any {
                it.movieTime.checkDuplicate(movieTime)
            }

    fun getMovieTitles(): List<MovieTitle> =
        ScreeningMovies
            .map { it.movie.title }
            .distinct()

    fun getMovies(
        title: MovieTitle,
        date: LocalDate,
    ): List<ScreeningMovie> {
        val screeningMovies = ScreeningMovies.filter { it.movie.title == title && it.movieTime.date == date }

        require(!screeningMovies.isEmpty()) { throw IllegalArgumentException("날짜가 올바르지 않습니다.") }

        return screeningMovies
    }

    fun containsMovieTitle(movieTitle: MovieTitle): Boolean = ScreeningMovies.any { it.movie.title == movieTitle }
}
