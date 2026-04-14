package movie.domain.seat.rank

import movie.domain.Price

class ARank : SeatRank {
    override val name = "A"

    override fun getPrice(): Price = Price(15_000)
}
