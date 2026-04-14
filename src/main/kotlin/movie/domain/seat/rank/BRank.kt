package movie.domain.seat.rank

import movie.domain.Price

class BRank : SeatRank {
    override val name = "B"

    override fun getPrice(): Price = Price(12_000)
}
