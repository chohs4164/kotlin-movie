package movie.application

import movie.MovieFixtures
import movie.api.dto.CreateReservationRequest
import movie.api.dto.CreateReservationResponse
import movie.api.dto.MovieResponse
import movie.api.dto.MovieScreeningResponse
import movie.api.dto.MoviesResponse
import movie.api.dto.ReservationItemResponse
import movie.domain.Point
import movie.domain.movie.ReservationCart
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import movie.infrastructure.db.JdbcReservationRepository
import movie.infrastructure.db.JdbcScreeningRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

class ScreeningNotFoundException(
    screeningId: String,
) : RuntimeException("존재하지 않는 상영 정보입니다. screeningId=$screeningId")

class ReservationConflictException(
    message: String,
) : RuntimeException(message)

@Service
class MovieApiService(
    private val screeningRepository: JdbcScreeningRepository,
    private val reservationRepository: JdbcReservationRepository,
    private val movieFixtures: MovieFixtures,
    private val reservationIdSequence: AtomicLong,
) {
    fun getMovies(): MoviesResponse {
        val movies =
            screeningRepository
                .findAllMovieScreenings()
                .groupBy { it.movieId }
                .values
                .map { records ->
                    val firstRecord = records.first()
                    MovieResponse(
                        movieId = firstRecord.movieId,
                        title = firstRecord.title,
                        runningTimeMinutes =
                            Duration
                                .between(firstRecord.startAt, firstRecord.endAt)
                                .toMinutes()
                                .toInt(),
                        screenings =
                            records.map { record ->
                                MovieScreeningResponse(
                                    screeningId = record.screeningId,
                                    startAt = record.startAt,
                                    endAt = record.endAt,
                                )
                            },
                    )
                }.sortedBy { it.title }

        return MoviesResponse(movies = movies)
    }

    fun createReservation(request: CreateReservationRequest): CreateReservationResponse {
        require(request.reservations.isNotEmpty()) { "예매 목록은 비어 있을 수 없습니다." }

        val reservationCart = ReservationCart()

        request.reservations.forEach { reservationRequest ->
            require(reservationRequest.seats.isNotEmpty()) { "좌석 목록은 비어 있을 수 없습니다." }
            validateScreeningIdFormat(reservationRequest.screeningId)

            val seatNumbers = reservationRequest.seats.map(::toSeatNumber)
            require(seatNumbers.distinct().size == seatNumbers.size) { "중복된 좌석은 예매할 수 없습니다." }

            val screeningMovie =
                screeningRepository.findByScreeningId(reservationRequest.screeningId)
                    ?: throw ScreeningNotFoundException(reservationRequest.screeningId)

            require(!reservationCart.isDupTime(screeningMovie.movieTime)) {
                "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
            }

            try {
                seatNumbers.forEach(screeningMovie::reserveCheck)
            } catch (e: IllegalArgumentException) {
                if (e.message == "이미 예약된 좌석입니다.") {
                    throw ReservationConflictException(e.message ?: "이미 예약된 좌석입니다.")
                }
                throw e
            }

            reservationCart.addReservation(screeningMovie, seatNumbers)
        }

        val usedPoint = Point(request.usedPoints)
        val paymentMethod = request.paymentMethod.toDomain()
        val discountedPrice = reservationCart.calculateDiscountedTotalPrice(movieFixtures.discountPolicy)
        val pointAppliedPrice = movieFixtures.pointPolicy.usePoint(totalPrice = discountedPrice, usePoint = usedPoint)
        val totalPrice =
            movieFixtures.payment.paymentPrice(
                method = paymentMethod,
                totalPrice = pointAppliedPrice,
            )

        try {
            reservationRepository.saveAll(reservationCart.getReservations())
        } catch (e: IllegalArgumentException) {
            reservationCart.resetSeat()
            if (e.message == "이미 예약된 좌석입니다.") {
                throw ReservationConflictException(e.message ?: "이미 예약된 좌석입니다.")
            }
            throw e
        }

        return CreateReservationResponse(
            reservationId = reservationIdSequence.incrementAndGet(),
            reservations =
                request.reservations.map {
                    ReservationItemResponse(
                        screeningId = it.screeningId,
                        seats = it.seats,
                    )
                },
            usedPoints = request.usedPoints,
            paymentMethod = request.paymentMethod.name,
            totalPrice = totalPrice.value,
        )
    }

    private fun toSeatNumber(seat: String): SeatNumber {
        require(SEAT_PATTERN.matches(seat)) { "좌석은 A1 형식으로 입력해야 합니다." }

        return SeatNumber(
            row = Row(seat.first().uppercaseChar()),
            col = Column(seat.drop(1).toInt()),
        )
    }

    companion object {
        private val SEAT_PATTERN = Regex("^[A-Za-z]\\d+$")
    }

    private fun validateScreeningIdFormat(screeningId: String) {
        runCatching { UUID.fromString(screeningId) }
            .getOrElse { throw IllegalArgumentException("screeningId 형식이 올바르지 않습니다.") }
    }
}
