package movie.domain.movie

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ScreeningMoviesTest {
    @Test
    fun `상영중인 영화가 정상적으로 추가된다`() {
        val screeningMovies = ScreeningMovies()
        val openTime = LocalTime.of(0, 0, 0)
        val closeTime = LocalTime.of(23, 59, 59)
        val theater = Theater(openTime = openTime, closeTime = closeTime)
        val movieTitle = MovieTitle("아이언맨")
        val movie = Movie(title = movieTitle)
        val screeningMovie =
            ScreeningMovie(
                theater = theater,
                movie = movie,
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 8),
                        startTime = LocalTime.of(1, 1, 1),
                        endTime = LocalTime.of(2, 1, 1),
                    ),
            )

        screeningMovies.addMovie(screeningMovie)

        assertThat(screeningMovies.getMovieTitles().size).isEqualTo(1)
    }

    @Test
    fun `시간이 겹친다면 true를 반환한다`() {
        val openTime = LocalTime.of(0, 0, 0)
        val closeTime = LocalTime.of(23, 59, 59)
        val theater = Theater(openTime = openTime, closeTime = closeTime)
        val movie = Movie(title = MovieTitle("아이언맨"))
        val movieTime =
            MovieTime(
                date = LocalDate.of(2026, 4, 8),
                startTime = LocalTime.of(1, 1, 1),
                endTime = LocalTime.of(2, 1, 1),
            )
        val screeningMovie =
            ScreeningMovie(
                theater = theater,
                movie = movie,
                movieTime = movieTime,
            )

        val screeningMovies = ScreeningMovies(listOf(screeningMovie))

        val isDupTime =
            screeningMovies.checkDuplicateTime(
                theater = theater,
                movieTime = movieTime,
            )

        assertThat(isDupTime).isTrue
    }

    @Test
    fun `시간이 겹치지 않는다면 false를 반환한다`() {
        val openTime = LocalTime.of(0, 0, 0)
        val closeTime = LocalTime.of(23, 59, 59)
        val theater = Theater(openTime = openTime, closeTime = closeTime)
        val movieTime =
            MovieTime(
                date = LocalDate.of(2026, 4, 8),
                startTime = LocalTime.of(1, 1, 1),
                endTime = LocalTime.of(2, 1, 1),
            )
        val screeningMovies = ScreeningMovies()

        val isDupTime =
            screeningMovies.checkDuplicateTime(
                theater = theater,
                movieTime = movieTime,
            )

        assertThat(isDupTime).isFalse
    }

    @Test
    fun `영화가 있다면 상영 시간 정보를 반환한다`() {
        val openTime = LocalTime.of(0, 0, 0)
        val closeTime = LocalTime.of(23, 59, 59)
        val theater = Theater(openTime = openTime, closeTime = closeTime)
        val movieTitle = MovieTitle("아이언맨")
        val movie = Movie(title = movieTitle)
        val movieTime =
            MovieTime(
                date = LocalDate.of(2026, 4, 8),
                startTime = LocalTime.of(1, 1, 1),
                endTime = LocalTime.of(2, 1, 1),
            )
        val screeningMovie =
            ScreeningMovie(
                theater = theater,
                movie = movie,
                movieTime = movieTime,
            )
        val screeningMovies = ScreeningMovies(listOf(screeningMovie))

        screeningMovies.getMovies(title = movie.title, date = movieTime.date)

        assertThat(screeningMovies.getMovies(title = movie.title, date = movieTime.date)).isEqualTo(listOf(screeningMovie))
    }
}
