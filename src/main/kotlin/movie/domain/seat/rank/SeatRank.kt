package movie.domain.seat.rank

import movie.domain.Price

interface SeatRank {
    val name: String

    fun getPrice(): Price
}
