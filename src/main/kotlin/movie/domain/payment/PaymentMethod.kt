package movie.domain.payment

interface PaymentMethod {
    val name: String
    val discountRate: Float
}
