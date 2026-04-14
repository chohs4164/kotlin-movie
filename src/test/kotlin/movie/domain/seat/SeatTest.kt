package movie.domain.seat

import movie.domain.Price
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import movie.domain.seat.rank.ARank
import movie.domain.seat.rank.BRank
import movie.domain.seat.rank.SRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SeatTest {
    @Test
    fun `시트 번호를 통해 같은 시트인지 확인할 수 있다`() {
        val seatNumber = SeatNumber(row = Row('A'), col = Column(1))
        val seat = Seat(seatNumber = seatNumber, rank = SRank())

        assertThat(seat.isSeat(seatNumber)).isTrue
    }

    @Test
    fun `특정 랭크일 때 시트 금액을 정상적으로 반환한다`() {
        val seatNumber = SeatNumber(row = Row('A'), col = Column(1))
        val sSeat = Seat(seatNumber = seatNumber, rank = SRank())
        val aSeat = Seat(seatNumber = seatNumber, rank = ARank())
        val bSeat = Seat(seatNumber = seatNumber, rank = BRank())

        assertThat(sSeat.getPrice()).isEqualTo(Price(18_000))
        assertThat(aSeat.getPrice()).isEqualTo(Price(15_000))
        assertThat(bSeat.getPrice()).isEqualTo(Price(12_000))
    }
}
