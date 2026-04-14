package movie.domain.movie

import movie.domain.Price
import movie.domain.seat.Seats
import movie.domain.seat.number.SeatNumber
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class Theater(
    private val id: Uuid = Uuid.random(),
    val seats: Seats = Seats(),
    val openTime: LocalTime,
    val closeTime: LocalTime,
) {
    fun sameTheater(target: Theater): Boolean = id == target.id

    fun validateSeat(seatNumber: SeatNumber): Boolean = seats.contains(seatNumber)

    fun getPrice(seatNumber: SeatNumber): Price = seats.getPrice(seatNumber)

    fun validateTime(
        startTime: LocalTime,
        endTime: LocalTime,
    ): Boolean = (startTime in openTime..closeTime) && (endTime in openTime..closeTime)
}
