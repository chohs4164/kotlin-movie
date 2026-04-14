package movie.domain.movie

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class TheaterSchedulerTest {
    @Test
    fun `상영중인 영화를 생성 시 ScreeningMovie를 반환한다`() {
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
        val theaterScheduler = TheaterScheduler(theaters = Theaters(listOf(theater)))

        val resultScreeningMovie =
            theaterScheduler.createScreeningMovie(
                theater = theater,
                movieTime = movieTime,
                movie = movie,
            )

        assertThat(resultScreeningMovie.theater).isEqualTo(theater)
        assertThat(resultScreeningMovie.movie).isEqualTo(movie)
        assertThat(resultScreeningMovie.movieTime).isEqualTo(movieTime)
    }
}
