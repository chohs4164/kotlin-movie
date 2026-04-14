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

@OptIn(ExperimentalUuidApi::class)
class ReservationsTest {
    @Test
    fun `예약 1개를 가진다면 그 예약 금액과 동일하다`() {
        val seatNumbers =
            listOf(
                SeatNumber(
                    row = Row('A'),
                    col = Column(1),
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
        val reservation =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers,
            )

        val reservations = Reservations(listOf(reservation))

        assertThat(reservations.getTotalPrice()).isEqualTo(Price(18_000))
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `예약 여러개를 가진다면 예약 금액 합계를 반환한다`() {
        val seatNumbers1 =
            listOf(
                SeatNumber(
                    row = Row('A'),
                    col = Column(1),
                ),
            )
        val seatNumbers2 =
            listOf(
                SeatNumber(
                    row = Row('A'),
                    col = Column(2),
                ),
            )
        val seatNumbers3 =
            listOf(
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
        val reservation1 =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers1,
            )
        val reservation2 =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers2,
            )
        val reservation3 =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers3,
            )

        val reservations =
            Reservations(
                reservations = listOf(reservation1, reservation2, reservation3),
            )
        val totalPrice =
            reservation1
                .getTotalPrice()
                .sumPrice(reservation2.getTotalPrice())
                .sumPrice(reservation3.getTotalPrice())

        assertThat(reservations.getTotalPrice()).isEqualTo((totalPrice))
    }

    companion object {
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
    }
}
