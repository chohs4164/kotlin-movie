package movie.domain.seat.rank

import movie.domain.Price

class SRank : SeatRank {
    override val name = "S"

    override fun getPrice(): Price = Price(18_000)
}
