package movie.infrastructure.db

import movie.domain.movie.Movie
import movie.domain.movie.MovieTime
import movie.domain.movie.MovieTitle
import movie.domain.movie.Reservation
import movie.domain.movie.ScreeningMovie
import movie.domain.movie.Theater
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.sql.DriverManager
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JdbcReservationRepositoryTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `예약 좌석을 저장하면 다시 조회할 때 반영된다`() {
        val connection =
            DriverManager.getConnection(
                "jdbc:h2:mem:${Uuid.random()}",
                "sa",
                "",
            )
        SchemaInitializer().initialize(connection)

        val screeningRepository = JdbcScreeningRepository(connection)
        val reservationRepository = JdbcReservationRepository(connection)

        val screeningMovie =
            ScreeningMovie(
                theater =
                    Theater(
                        openTime = LocalTime.of(9, 0),
                        closeTime = LocalTime.of(23, 0),
                    ),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 15),
                        startTime = LocalTime.of(13, 30),
                        endTime = LocalTime.of(15, 30)
                    ),
            )
        screeningRepository.save(screeningMovie)

        val targetScreening =
            screeningRepository.findByTitleAndDate(
                title = MovieTitle("아이언맨"),
                date = LocalDate.of(2026, 4, 15),
            ).first()

        reservationRepository.save(
            Reservation(
                screeningMovie = targetScreening,
                seatNumbers =
                    listOf(
                        SeatNumber(Row('A'), Column(1)),
                        SeatNumber(Row('A'), Column(2)),
                    ),
            ),
        )
        val saved =
            screeningRepository.findByTitleAndDate(
                title = MovieTitle("아이언맨"),
                date = LocalDate.of(2026, 4, 15),
            ).first()

        assertThat(saved.isReserved(SeatNumber(Row('A'), Column(1)))).isTrue()
        assertThat(saved.isReserved(SeatNumber(Row('A'), Column(2)))).isTrue()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `이미 예약된 좌석을 다시 저장하면 예외가 발생한다`() {
        val connection =
            DriverManager.getConnection(
                "jdbc:h2:mem:${Uuid.random()}",
                "sa",
                "",
            )
        SchemaInitializer().initialize(connection)

        val screeningRepository = JdbcScreeningRepository(connection)
        val reservationRepository = JdbcReservationRepository(connection)

        val screeningMovie =
            ScreeningMovie(
                theater =
                    Theater(
                        openTime = LocalTime.of(9, 0),
                        closeTime = LocalTime.of(23, 0),
                    ),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 15),
                        startTime = LocalTime.of(13, 30),
                        endTime = LocalTime.of(15, 30),
                    ),
            )
        screeningRepository.save(screeningMovie)

        val targetScreening =
            screeningRepository.findByTitleAndDate(
                title = MovieTitle("아이언맨"),
                date = LocalDate.of(2026, 4, 15),
            ).first()

        reservationRepository.save(
            Reservation(
                screeningMovie = targetScreening,
                seatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
            ),
        )

        assertThatThrownBy {
            reservationRepository.save(
                Reservation(
                    screeningMovie = targetScreening,
                    seatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
                ),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("이미 예약된 좌석입니다.")
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `saveAll 저장 중 하나라도 실패하면 전체가 롤백된다`() {
        val connection =
            DriverManager.getConnection(
                "jdbc:h2:mem:${Uuid.random()}",
                "sa",
                "",
            )
        SchemaInitializer().initialize(connection)

        val screeningRepository = JdbcScreeningRepository(connection)
        val reservationRepository = JdbcReservationRepository(connection)

        val screeningMovie =
            ScreeningMovie(
                theater =
                    Theater(
                        openTime = LocalTime.of(9, 0),
                        closeTime = LocalTime.of(23, 0),
                    ),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 15),
                        startTime = LocalTime.of(13, 30),
                        endTime = LocalTime.of(15, 30),
                    ),
            )
        screeningRepository.save(screeningMovie)

        val targetScreening =
            screeningRepository.findByTitleAndDate(
                title = MovieTitle("아이언맨"),
                date = LocalDate.of(2026, 4, 15),
            ).first()

        reservationRepository.save(
            Reservation(
                screeningMovie = targetScreening,
                seatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
            ),
        )

        assertThatThrownBy {
            reservationRepository.saveAll(
                listOf(
                    Reservation(
                        screeningMovie = targetScreening,
                        seatNumbers = listOf(SeatNumber(Row('A'), Column(2)))
                    ),
                    Reservation(
                        screeningMovie = targetScreening,
                        seatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
                    ),
                ),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("이미 예약된 좌석입니다.")

        val saved =
            screeningRepository.findByTitleAndDate(
                title = MovieTitle("아이언맨"),
                date = LocalDate.of(2026, 4, 15),
            ).first()

        assertThat(saved.isReserved(SeatNumber(Row('A'), Column(1)))).isTrue()
        assertThat(saved.isReserved(SeatNumber(Row('A'), Column(2)))).isFalse()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `조회한 screeningId로 예매를 저장한다`() {
        val connection =
            DriverManager.getConnection(
                "jdbc:h2:mem:${Uuid.random()}",
                "sa",
                "",
            )
        SchemaInitializer().initialize(connection)

        val screeningRepository = JdbcScreeningRepository(connection)
        val reservationRepository = JdbcReservationRepository(connection)

        val firstScreeningMovie =
            ScreeningMovie(
                theater =
                    Theater(
                        openTime = LocalTime.of(9,0),
                        closeTime = LocalTime.of(23,0),
                    ),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026,4,15),
                        startTime = LocalTime.of(13,30),
                        endTime = LocalTime.of(15,30),
                    ),
            )
        val secondScreeningMovie =
            ScreeningMovie(
                theater =
                    Theater(
                        openTime = LocalTime.of(9,0),
                        closeTime = LocalTime.of(23,0)
                    ),
                movie = Movie(title = MovieTitle("아어언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026,4,15),
                        startTime = LocalTime.of(13,30),
                        endTime = LocalTime.of(15,30),
                    ),
            )

        screeningRepository.save(firstScreeningMovie)
        screeningRepository.save(secondScreeningMovie)

        val screenings =
            screeningRepository.findByTitleAndDate(
                title = MovieTitle("아이언맨"),
                date = LocalDate.of(2026,4,15),
            )

        val targetScreening = screenings.last()
        val otherScreening = screenings.first()

        reservationRepository.save(
            Reservation(
                screeningMovie = targetScreening,
                seatNumbers = listOf(SeatNumber(Row('A'),Column(3))),
            ),
        )

        connection.prepareStatement("select screnning_id seat_number from seats").use{statement ->
            statement.executeQuery().use { resultSet ->
                assertThat(resultSet.next()).isTrue()
                assertThat(resultSet.getString("screening_id")).isEqualTo(targetScreening.screeningId)
                assertThat(resultSet.getString("screening_id")).isEqualTo(otherScreening.screeningId)
                assertThat(resultSet.getString("seat_number")).isEqualTo("A3")

            }
        }
    }
}
