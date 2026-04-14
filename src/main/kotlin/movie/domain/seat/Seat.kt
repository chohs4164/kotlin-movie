package movie.domain.seat

import movie.domain.Price
import movie.domain.seat.number.SeatNumber
import movie.domain.seat.rank.SeatRank

class Seat(
    val seatNumber: SeatNumber,
    val rank: SeatRank,
) {
    fun isSeat(seatNumber: SeatNumber): Boolean = this.seatNumber == seatNumber

    fun getPrice(): Price = rank.getPrice()
}
