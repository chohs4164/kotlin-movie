package movie.api.dto

import movie.domain.payment.Card
import movie.domain.payment.Cash
import movie.domain.payment.PaymentMethod

data class CreateReservationRequest(
    val reservations: List<ReservationItemRequest>,
    val usedPoints: Int,
    val paymentMethod: ReservationPaymentMethod,
)

data class ReservationItemRequest(
    val screeningId: String,
    val seats: List<String>,
)

data class CreateReservationResponse(
    val reservationId: Long,
    val reservations: List<ReservationItemResponse>,
    val usedPoints: Int,
    val paymentMethod: String,
    val totalPrice: Int,
)

data class ReservationItemResponse(
    val screeningId: String,
    val seats: List<String>,
)

enum class ReservationPaymentMethod {
    CREDIT_CARD,
    CASH,
    ;

    fun toDomain(): PaymentMethod =
        when (this) {
            CREDIT_CARD -> Card
            CASH -> Cash
        }
}
