package movie.domain.movie

import movie.domain.Price
import movie.domain.seat.Seat
import movie.domain.seat.SeatLayout
import movie.domain.seat.Seats
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import movie.domain.seat.rank.ARank
import movie.domain.seat.rank.BRank
import movie.domain.seat.rank.SRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

class ReservationTest {
    private class TestSeatLayout : SeatLayout {
        override fun createSeats(): List<Seat> =
            listOf(
                Seat(
                    seatNumber = SeatNumber(Row('A'), Column(1)),
                    rank = SRank(),
                ),
                Seat(
                    seatNumber = SeatNumber(Row('A'), Column(2)),
                    rank = ARank(),
                ),
                Seat(
                    seatNumber = SeatNumber(Row('A'), Column(3)),
                    rank = BRank(),
                ),
            )
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `예매하고자 하는 영화의 선택된 좌석에 대한 총 가격을 반환한다`() {
        val seatNumbers =
            listOf(
                // S 랭크 좌석
                SeatNumber(
                    row = Row('A'),
                    col = Column(1),
                ),
                // A 랭크 좌석
                SeatNumber(
                    row = Row('A'),
                    col = Column(2),
                ),
                // B 랭크 좌석
                SeatNumber(
                    row = Row('A'),
                    col = Column(3),
                ),
            )

        val screeningMovie =
            ScreeningMovie(
                theater =
                    Theater(
                        seats = Seats(seatLayout = TestSeatLayout()),
                        openTime = LocalTime.of(0, 0, 0),
                        closeTime = LocalTime.of(0, 0, 0),
                    ),
                movie = Movie(title = MovieTitle("아이언맨")),
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 8),
                        startTime = LocalTime.of(0, 0, 0),
                        endTime = LocalTime.of(1, 0, 0),
                    ),
            )
        val reserved =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers,
            )
        assertThat(reserved.getTotalPrice()).isEqualTo(Price(45_000))
    }
}
