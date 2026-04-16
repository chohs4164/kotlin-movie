package movie.controller

import movie.MovieFixtures
import movie.domain.Point
import movie.domain.movie.MovieTitle
import movie.domain.movie.ReservationCart
import movie.domain.movie.ScreeningMovie
import movie.domain.payment.Card
import movie.domain.payment.Cash
import movie.domain.payment.PaymentMethod
import movie.domain.seat.number.SeatNumber
import movie.infrastructure.db.JdbcReservationRepository
import movie.infrastructure.db.JdbcScreeningRepository
import movie.view.InputParser
import movie.view.InputValidator
import movie.view.InputView
import movie.view.OutputView
import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class MovieController(
    private val screeningRepository: JdbcScreeningRepository,
    private val reservationRepository: JdbcReservationRepository,
    private val movieFixtures: MovieFixtures = MovieFixtures(),
) {
    fun run() {
        val isStart = getReservationStart()

        require(isStart) { return }

        val reservationCart = ReservationCart()

        while (true) {
            val movieTitle = getMovieTitle()
            val movieTimes = getMovieTimes(title = movieTitle)
            OutputView.printMovieStartTimes(movieTimes)
            val screeningMovie = getScreeningMovie(movieTimes, reservationCart)
            OutputView.printSeats(screeningMovie = screeningMovie)
            val selectedSeatNumbers = getSeatNumbers(screeningMovie = screeningMovie)
            val reservation =
                reservationCart.addReservation(
                    screeningMovie = screeningMovie,
                    seatNumbers = selectedSeatNumbers,
                )
            OutputView.printReservationAddMessage(reservation = reservation)
            require(getContinueReservation()) { break }
        }

        OutputView.printCart(reservationCart = reservationCart)

        var totalPrice = reservationCart.calculateDiscountedTotalPrice(movieFixtures.discountPolicy)

        val point = getUsePoint()
        val paymentMethod = getPaymentMethod()

        val pointUsedPrice =
            movieFixtures.pointPolicy.usePoint(totalPrice = totalPrice, usePoint = point)
        val paymentPrice =
            movieFixtures.payment.paymentPrice(method = paymentMethod, totalPrice = pointUsedPrice)

        OutputView.printTotalPrice(totalPrice = paymentPrice)

        val isPayment = getUserPayment()

        if (!isPayment) {
            reservationCart.resetSeat()
            return
        }

        try {
            reservationRepository.saveAll(reservationCart.getReservations())
        } catch (e: IllegalArgumentException) {
            OutputView.printErrorMessage(e.message)
            reservationCart.resetSeat()
            return
        }

        OutputView.printReceipt(
            reservationCart = reservationCart,
            paymentPrice = paymentPrice,
            usePoint = point,
        )

        OutputView.printThankYou()
    }

    fun getMovieTimes(title: MovieTitle): List<ScreeningMovie> =
        whileGetInput {
            val date = getDate()

            val screeningMovies = screeningRepository.findByTitleAndDate(title, date)
            require(screeningMovies.isNotEmpty()) { "날짜가 올바르지 않습니다." }

            screeningMovies
        }

    fun getReservationStart(): Boolean =
        whileGetInput {
            val input = InputView.readReservationStart()
            InputValidator.validateYesNo(input)

            InputParser.parseYesNo(input)
        }

    fun getMovieTitle(): MovieTitle =
        whileGetInput {
            val input = InputView.readMovieTitle()

            val movieTitle = InputParser.parseMovieTitle(input)

            require(screeningRepository.existsByTitle(movieTitle)) { "상영 중인 영화가 아닙니다." }

            movieTitle
        }

    fun getDate(): LocalDate =
        whileGetInput {
            val input = InputView.readMovieDate()
            InputValidator.validateDate(input)

            InputParser.parseDate(input)
        }

    fun getScreeningMovie(
        screeningMovies: List<ScreeningMovie>,
        reservationCart: ReservationCart,
    ): ScreeningMovie =
        whileGetInput {
            val input = InputView.readSelectedMovieTimeNumber()
            InputValidator.validateNumber(input)

            val index = InputParser.parseIndex(input = input, size = screeningMovies.size)
            val selectedMovie = screeningMovies[index]

            require(!reservationCart.isDupTime(selectedMovie.movieTime)) {
                throw IllegalArgumentException(
                    "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요.",
                )
            }

            selectedMovie
        }

    fun getSeatNumbers(screeningMovie: ScreeningMovie): List<SeatNumber> =
        whileGetInput {
            val input = InputView.readReservationSeat()
            InputValidator.validateSeatNumbers(input)

            val seatNumbers = InputParser.parseSeatNumbers(input)

            require(!screeningMovie.isAbleReservation(seatNumbers)) {
                throw IllegalArgumentException(
                    "이미 예약된 좌석입니다.",
                )
            }

            seatNumbers
        }

    fun getContinueReservation(): Boolean =
        whileGetInput {
            val input = InputView.readContinueReservation()
            InputValidator.validateYesNo(input)

            InputParser.parseYesNo(input)
        }

    fun getUsePoint(): Point =
        whileGetInput {
            val input = InputView.readUsePoint()
            InputValidator.validateNumber(input)

            InputParser.parsePoint(input)
        }

    fun getPaymentMethod(): PaymentMethod =
        whileGetInput {
            val paymentMethods = listOf(Cash, Card)
            val input = InputView.readPaymentType(paymentMethods)
            InputValidator.validateNumber(input)

            val index = InputParser.parseIndex(input = input, size = paymentMethods.size)

            paymentMethods[index]
        }

    fun getUserPayment(): Boolean =
        whileGetInput {
            val input = InputView.readUserPayment()
            InputValidator.validateYesNo(input)

            InputParser.parseYesNo(input)
        }

    private fun <T> whileGetInput(action: () -> T): T {
        while (true) {
            try {
                val input = action()

                return input
            } catch (e: IllegalArgumentException) {
                OutputView.printErrorMessage(e.message)
            }
        }
    }
}
