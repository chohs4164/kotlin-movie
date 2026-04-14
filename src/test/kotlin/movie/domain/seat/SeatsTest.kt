package movie.domain.seat

import movie.domain.Price
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import movie.domain.seat.rank.SRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SeatsTest {
    @Test
    fun `좌석 금액을 정상적으로 가져온다`() {
        val seats = Seats(seatLayout = TestSeatLayout())
        val seatNumber =
            SeatNumber(
                row = Row('A'),
                col = Column(1),
            )

        assertThat(seats.getPrice(seatNumber = seatNumber)).isEqualTo(Price(18_000))
    }

    companion object {
        class TestSeatLayout : SeatLayout {
            override fun createSeats(): List<Seat> {
                val rows = 'A'..'B'
                val cols = 1..5

                return rows.flatMap { row ->
                    cols.map { col ->
                        Seat(
                            seatNumber =
                                SeatNumber(
                                    row = Row(row),
                                    col = Column(col),
                                ),
                            rank = SRank(),
                        )
                    }
                }
            }
        }
    }
}
