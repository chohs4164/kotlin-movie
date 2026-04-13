package movie.domain.payment

object Cash : PaymentMethod {
    override val name = "현금"
    override val discountRate: Float = 0.02f
}
