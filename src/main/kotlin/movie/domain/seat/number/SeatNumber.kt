package movie.domain.seat.number

data class SeatNumber(
    val row: Row,
    val col: Column,
) {
    override fun toString(): String = row.toString() + col.toString()
}
