package movie.domain.seat

import movie.domain.Price
import movie.domain.seat.number.SeatNumber

class Seats(
    seatLayout: SeatLayout = DefaultSeatLayout(),
) {
    private val seats = seatLayout.createSeats()

    fun getSeats() = seats

    fun contains(seatNumber: SeatNumber): Boolean = seats.any { it.isSeat(seatNumber) }

    fun getPrice(seatNumber: SeatNumber): Price =
        seats
            .firstOrNull { it.isSeat(seatNumber) }
            ?.getPrice()
            ?: throw IllegalArgumentException("존재하지 않는 좌석입니다.")
}
