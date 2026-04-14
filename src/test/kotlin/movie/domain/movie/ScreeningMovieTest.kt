package movie.domain.movie

import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ScreeningMovieTest {
    @Test
    fun `예매할 자리들이 모두 예약되지 않은 상태라면 예약이 진행된다`() {
        val screeningMovie =
            ScreeningMovie(
                theater = Theater(openTime = LocalTime.of(0, 0, 0), closeTime = LocalTime.of(0, 0, 0)),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 8),
                        startTime = LocalTime.of(0, 0, 0),
                        endTime = LocalTime.of(1, 0, 0),
                    ),
            )

        val targetSeatNumbers =
            listOf(
                SeatNumber(
                    row = Row('A'),
                    col = Column(1),
                ),
                SeatNumber(
                    row = Row('A'),
                    col = Column(2),
                ),
            )

        Assertions.assertDoesNotThrow {
            screeningMovie.reserve(targetSeatNumbers = targetSeatNumbers)
        }
    }

    @Test
    fun `예매할 자리 중 예약된 좌석이 있다면 예외가 발생한다`() {
        val screeningMovie =
            ScreeningMovie(
                theater = Theater(openTime = LocalTime.of(0, 0, 0), closeTime = LocalTime.of(0, 0, 0)),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 8),
                        startTime = LocalTime.of(0, 0, 0),
                        endTime = LocalTime.of(1, 0, 0),
                    ),
                reservedSeats =
                    listOf(
                        SeatNumber(
                            row = Row('A'),
                            col = Column(1),
                        ),
                    ),
            )

        val targetSeatNumbers =
            listOf(
                SeatNumber(
                    row = Row('A'),
                    col = Column(1),
                ),
                SeatNumber(
                    row = Row('A'),
                    col = Column(2),
                ),
            )

        assertThrows<IllegalArgumentException> {
            screeningMovie.reserve(targetSeatNumbers = targetSeatNumbers)
        }
    }
}
