package movie.domain.movie

import movie.domain.Price
import movie.domain.seat.number.SeatNumber
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ScreeningMovie(
    val theater: Theater,
    val movie: Movie,
    val movieTime: MovieTime,
    reservedSeats: List<SeatNumber> = emptyList(),
    val screeningId: String? = null,
) {
    private val reservedSeatNumbers = reservedSeats.toMutableList()

    fun addReservedSeats(seatNumbers: List<SeatNumber>) {
        reservedSeatNumbers.addAll(seatNumbers)
    }

    fun deleteReservedSeats(seatNumbers: List<SeatNumber>) {
        reservedSeatNumbers.removeAll(seatNumbers)
    }

    fun reserve(targetSeatNumbers: List<SeatNumber>) {
        targetSeatNumbers.forEach {
            reserveCheck(it)
        }

        reservedSeatNumbers.addAll(targetSeatNumbers)
    }

    fun reserveCheck(seatNumber: SeatNumber) {
        require(theater.validateSeat(seatNumber)) { "존재하지 않는 좌석입니다." }

        require(!reservedSeatNumbers.contains(seatNumber)) { throw IllegalArgumentException("이미 예약된 좌석입니다.") }
    }

    fun calculatePrice(targetSeatNumbers: List<SeatNumber>): Price =
        targetSeatNumbers
            .map { theater.getPrice(it) }
            .fold(Price(0)) { price, targetPrice ->
                price.sumPrice(targetPrice = targetPrice)
            }

    fun isReserved(seatNumber: SeatNumber) = reservedSeatNumbers.contains(seatNumber)

    fun isAbleReservation(seatNumbers: List<SeatNumber>): Boolean = seatNumbers.any { reservedSeatNumbers.contains(it) }
}
