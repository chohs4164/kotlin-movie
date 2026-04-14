package movie.domain.payment

import movie.domain.Price

class Payment {
    fun paymentPrice(
        method: PaymentMethod,
        totalPrice: Price,
    ): Price {
        val discountPrice = Price((totalPrice.value * method.discountRate).toInt())

        return totalPrice.minusPrice(discountPrice)
    }
}
