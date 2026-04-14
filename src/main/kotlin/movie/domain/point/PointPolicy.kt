package movie.domain.point

import movie.domain.Point
import movie.domain.Price

class PointPolicy {
    fun usePoint(
        totalPrice: Price,
        usePoint: Point,
    ): Price {
        val usePointPrice = pointToPrice(point = usePoint)

        return totalPrice.minusPrice(usePointPrice)
    }

    private fun pointToPrice(point: Point): Price = Price(point.value)
}
