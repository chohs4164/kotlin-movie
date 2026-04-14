package movie.domain.movie

import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class TheaterScheduler(
    private val theaters: Theaters,
    private val screeningMovies: ScreeningMovies = ScreeningMovies(),
) {
    fun addTheater(theater: Theater) {
        val newTheater =
            createTheater(
                openTime = theater.openTime,
                closeTime = theater.closeTime,
            )
        theaters.addTheater(newTheater)
    }

    fun createTheater(
        openTime: LocalTime,
        closeTime: LocalTime,
    ): Theater =
        Theater(
            openTime = openTime,
            closeTime = closeTime,
        )

    fun addMovie(
        theater: Theater,
        movie: Movie,
        movieTime: MovieTime,
    ) {
        require(
            !checkDuplicateTime(
                theater = theater,
                movieTime = movieTime,
            ),
        ) { "시간 중복으로 영화를 추가할 수 없습니다." }

        val newMovie =
            createScreeningMovie(
                theater = theater,
                movie = movie,
                movieTime = movieTime,
            )

        screeningMovies.addMovie(newMovie)
    }

    fun createScreeningMovie(
        theater: Theater,
        movie: Movie,
        movieTime: MovieTime,
    ): ScreeningMovie =
        ScreeningMovie(
            theater = theater,
            movie = movie,
            movieTime = movieTime,
        )

    fun checkDuplicateTime(
        theater: Theater,
        movieTime: MovieTime,
    ): Boolean =
        screeningMovies.checkDuplicateTime(
            theater = theater,
            movieTime = movieTime,
        )

    fun containsMovieTitle(movieTitle: MovieTitle): Boolean = screeningMovies.containsMovieTitle(movieTitle)

    fun getMovies(
        title: MovieTitle,
        date: LocalDate,
    ): List<ScreeningMovie> = screeningMovies.getMovies(title = title, date = date)
}
