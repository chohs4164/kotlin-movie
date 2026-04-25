package movie.application

import movie.api.dto.CreateReservationRequest
import movie.api.dto.CreateReservationResponse
import movie.api.dto.MovieResponse
import movie.api.dto.MovieScreeningResponse
import movie.api.dto.MoviesResponse
import movie.api.dto.ReservationItemRequest
import movie.api.dto.ReservationItemResponse
import movie.domain.Point
import movie.domain.Price
import movie.domain.discount.DiscountPolicy
import movie.domain.movie.ReservationCart
import movie.domain.movie.ReservationRepository
import movie.domain.movie.ScreeningMovie
import movie.domain.payment.Payment
import movie.domain.point.PointPolicy
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import movie.infrastructure.db.JdbcScreeningRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

class ScreeningNotFoundException(
    screeningId: Int,
) : NoSuchElementException("존재하지 않는 상영 정보입니다. screeningId=$screeningId")

@Service
class MovieApiService(
    private val screeningRepository: JdbcScreeningRepository,
    private val reservationRepository: ReservationRepository,
    private val discountPolicy: DiscountPolicy,
    private val pointPolicy: PointPolicy,
    private val payment: Payment,
    private val movieCatalogOrder: MovieCatalogOrder,
    private val reservationIdSequence: AtomicLong,
) {
    fun getMovies(): MoviesResponse {
        val movies =
            buildMovieCatalog().map { movie ->
                MovieResponse(
                    id = movie.id,
                    title = movie.title,
                    runningTimeMinutes = movie.runningTimeMinutes,
                    screenings =
                        movie.screenings.map { screening ->
                            MovieScreeningResponse(
                                id = screening.id,
                                startAt = screening.startAt,
                                endAt = screening.endAt,
                            )
                        },
                )
            }

        return MoviesResponse(movies = movies)
    }

    fun createReservation(request: CreateReservationRequest): CreateReservationResponse {
        validateReservationRequest(request)

        val movieCatalog = buildMovieCatalog()
        val reservationCart = buildReservationCart(request, movieCatalog)
        val totalPrice = calculateTotalPrice(request, reservationCart)

        saveReservations(reservationCart)

        return toCreateReservationResponse(request, totalPrice)
    }

    private fun validateReservationRequest(request: CreateReservationRequest) {
        require(request.reservations.isNotEmpty()) { "예매 목록은 비어 있을 수 없습니다." }
    }

    private fun buildReservationCart(
        request: CreateReservationRequest,
        movieCatalog: List<ApiMovie>,
    ): ReservationCart {
        val reservationCart = ReservationCart()

        request.reservations.forEach { reservationRequest ->
            val seatNumbers = validateSeatNumbers(reservationRequest)
            val screeningMovie = findScreeningMovie(reservationRequest, movieCatalog)

            reservationCart.addReservation(screeningMovie, seatNumbers)
        }

        return reservationCart
    }

    private fun validateSeatNumbers(reservationRequest: ReservationItemRequest): List<SeatNumber> {
        require(reservationRequest.seats.isNotEmpty()) { "좌석 목록은 비어 있을 수 없습니다." }

        val seatNumbers = reservationRequest.seats.map(::toSeatNumber)
        require(seatNumbers.distinct().size == seatNumbers.size) { "중복된 좌석은 예매할 수 없습니다." }

        return seatNumbers
    }

    private fun findScreeningMovie(
        reservationRequest: ReservationItemRequest,
        movieCatalog: List<ApiMovie>,
    ): ScreeningMovie {
        val apiScreening =
            movieCatalog
                .flatMap(ApiMovie::screenings)
                .find { it.id == reservationRequest.screeningId }
                ?: throw ScreeningNotFoundException(reservationRequest.screeningId)

        return screeningRepository.findByScreeningId(apiScreening.internalScreeningId)
            ?: throw ScreeningNotFoundException(reservationRequest.screeningId)
    }

    private fun calculateTotalPrice(
        request: CreateReservationRequest,
        reservationCart: ReservationCart,
    ): Price {
        val usedPoint = Point(request.usedPoints)
        val paymentMethod = request.paymentMethod.toDomain()
        val discountedPrice = reservationCart.calculateDiscountedTotalPrice(discountPolicy)
        val pointAppliedPrice = pointPolicy.usePoint(totalPrice = discountedPrice, usePoint = usedPoint)

        return payment.paymentPrice(
            method = paymentMethod,
            totalPrice = pointAppliedPrice,
        )
    }

    private fun saveReservations(reservationCart: ReservationCart) {
        reservationRepository.saveAll(reservationCart.getReservations())
    }

    private fun toCreateReservationResponse(
        request: CreateReservationRequest,
        totalPrice: Price,
    ): CreateReservationResponse =
        CreateReservationResponse(
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

    private fun buildMovieCatalog(): List<ApiMovie> {
        return screeningRepository
            .findAllMovieScreenings()
            .groupBy { it.title }
            .entries
            .sortedWith(compareBy({ movieCatalogOrder.indexOf(it.key) }, { it.key }))
            .mapIndexed { movieIndex, entry ->
                val sortedRecords = entry.value.sortedBy { it.startAt }
                val firstRecord = sortedRecords.first()

                ApiMovie(
                    id = movieIndex + 1,
                    title = entry.key,
                    runningTimeMinutes =
                        Duration
                            .between(firstRecord.startAt, firstRecord.endAt)
                            .toMinutes()
                            .toInt(),
                    screenings =
                        sortedRecords.mapIndexed { screeningIndex, record ->
                            ApiScreening(
                                id = (movieIndex + 1) * 100 + (screeningIndex + 1),
                                internalScreeningId = record.screeningId,
                                startAt = record.startAt,
                                endAt = record.endAt,
                            )
                        },
                )
            }
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

    private data class ApiMovie(
        val id: Int,
        val title: String,
        val runningTimeMinutes: Int,
        val screenings: List<ApiScreening>,
    )

    private data class ApiScreening(
        val id: Int,
        val internalScreeningId: String,
        val startAt: java.time.LocalDateTime,
        val endAt: java.time.LocalDateTime,
    )
}
