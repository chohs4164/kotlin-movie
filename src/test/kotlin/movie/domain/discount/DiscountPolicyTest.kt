package movie.domain.discount

import movie.domain.movie.Movie
import movie.domain.movie.MovieTime
import movie.domain.movie.MovieTitle
import movie.domain.movie.ScreeningMovie
import movie.domain.movie.Theater
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
class DiscountPolicyTest {
    @Test
    fun `무비데이에 해당하면 할인된 금액을 반환한다`() {
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
                        date = LocalDate.of(2026, 4, 10),
                        startTime = LocalTime.of(0, 0, 0),
                        endTime = LocalTime.of(1, 0, 0),
                    ),
            )
        val discountPolicy = DiscountPolicy()

        val discountPrice =
            discountPolicy.calculateMovieDayDiscount(
                totalPrice =
                    screeningMovie.calculatePrice(
                        targetSeatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
                    ),
                date = LocalDate.of(2026, 4, 10),
            )

        assertThat(discountPrice.value).isEqualTo(16_200)
    }

    @Test
    fun `무비데이에 해당하지 않으면 기존 값을 반환한다`() {
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
                        date = LocalDate.of(2026, 4, 10),
                        startTime = LocalTime.of(0, 0, 0),
                        endTime = LocalTime.of(1, 0, 0),
                    ),
            )
        val discountPolicy = DiscountPolicy()

        val discountPrice =
            discountPolicy.calculateMovieDayDiscount(
                totalPrice =
                    screeningMovie.calculatePrice(
                        targetSeatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
                    ),
                date = LocalDate.of(2026, 4, 11),
            )

        assertThat(discountPrice.value).isEqualTo(18_000)
    }

    @Test
    fun `타임 할인에 해당하면 할인된 금액을 반환한다`() {
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
                        date = LocalDate.of(2026, 4, 10),
                        startTime = LocalTime.of(0, 0, 0),
                        endTime = LocalTime.of(1, 0, 0),
                    ),
            )
        val discountPolicy = DiscountPolicy()

        val discountPrice =
            discountPolicy.calculateTimeDiscount(
                totalPrice =
                    screeningMovie.calculatePrice(
                        targetSeatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
                    ),
                startTime = screeningMovie.movieTime.startTime,
            )

        assertThat(discountPrice.value).isEqualTo(16_000)
    }

    @Test
    fun `타임 할인에 해당하지 않으면 기존 금액을 반환한다`() {
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
                        date = LocalDate.of(2026, 4, 10),
                        startTime = LocalTime.of(12, 0, 0),
                        endTime = LocalTime.of(13, 0, 0),
                    ),
            )
        val discountPolicy = DiscountPolicy()

        val discountPrice =
            discountPolicy.calculateTimeDiscount(
                totalPrice =
                    screeningMovie.calculatePrice(
                        targetSeatNumbers = listOf(SeatNumber(Row('A'), Column(1))),
                    ),
                startTime = screeningMovie.movieTime.startTime,
            )

        assertThat(discountPrice.value).isEqualTo(18_000)
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
