package movie.infrastructure.db

import movie.domain.movie.Movie
import movie.domain.movie.MovieTime
import movie.domain.movie.MovieTitle
import movie.domain.movie.ScreeningMovie
import movie.domain.movie.Theater
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.sql.DriverManager
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JdbcScreeningRepositoryTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `영화 제목과 날짜로 상영 정보를 조회할 수 있다`() {
        val connection = DriverManager.getConnection(
            "jdbc:h2:mem:${Uuid.random()}",
            "sa",
            ""
        )
        // 스키마 초기화
        SchemaInitializer().initialize(connection)

        val repository = JdbcScreeningRepository(connection)

        // 상영관과 영화관 예시 하나
        val screeningMovie = ScreeningMovie(
            theater = Theater(
                openTime = LocalTime.of(9, 0),
                closeTime = LocalTime.of(23, 0)
            ),
            movie = Movie(title = MovieTitle("아이언맨")),
            movieTime = MovieTime(
                date = LocalDate.of(2026, 4, 15),
                startTime = LocalTime.of(13, 30),
                endTime = LocalTime.of(15, 30),
            ),
        )

        repository.save(screeningMovie)

        val result = repository.findByTitleAndDate(
            title = MovieTitle("아이언맨"),
            date = LocalDate.of(2026, 4, 15),
        )

        assertThat(result).hasSize(1)
        assertThat(result.first().movie.title).isEqualTo(MovieTitle("아이언맨"))
        assertThat(result.first().movieTime.date).isEqualTo(LocalDate.of(2026, 4, 15))
        assertThat(result.first().movieTime.startTime).isEqualTo(LocalTime.of(13, 30))
        assertThat(result.first().movieTime.endTime).isEqualTo(LocalTime.of(15, 30))
    }

}