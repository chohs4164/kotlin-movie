package movie

import movie.domain.discount.DefaultDiscountCondition
import movie.domain.discount.DiscountPolicy
import movie.domain.movie.Movie
import movie.domain.movie.MovieTime
import movie.domain.movie.MovieTitle
import movie.domain.movie.ScreeningMovie
import movie.domain.movie.ScreeningMovies
import movie.domain.movie.Theater
import movie.domain.movie.TheaterScheduler
import movie.domain.movie.Theaters
import movie.domain.payment.Payment
import movie.domain.point.PointPolicy
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

class MovieFixtures {
    @OptIn(ExperimentalUuidApi::class)
    val movie = Movie(title = MovieTitle("아이언맨"))
    val movieTime =
        MovieTime(
            date = LocalDate.of(2026, 4, 9),
            startTime = LocalTime.of(12, 0, 0),
            endTime = LocalTime.of(14, 30, 0),
        )

    @OptIn(ExperimentalUuidApi::class)
    val theaterList =
        listOf(
            Theater(
                openTime = LocalTime.of(12, 0, 0),
                closeTime = LocalTime.of(23, 59, 59),
            ),
            Theater(
                openTime = LocalTime.of(7, 0, 0),
                closeTime = LocalTime.of(23, 59, 59),
            ),
        )

    val screeningMovies =
        ScreeningMovies(
            listOf(
                ScreeningMovie(
                    movie = movie,
                    movieTime = movieTime,
                    theater = theaterList[0],
                ),
                ScreeningMovie(
                    movie = movie,
                    movieTime =
                        MovieTime(
                            date = LocalDate.of(2026, 4, 10),
                            startTime = LocalTime.of(20, 30, 0),
                            endTime = LocalTime.of(23, 30, 0),
                        ),
                    theater = theaterList[1],
                ),
            ),
        )

    val theaters: Theaters =
        Theaters(
            theaters = theaterList,
        )

    val scheduler: TheaterScheduler =
        TheaterScheduler(
            theaters = theaters,
            screeningMovies = screeningMovies,
        )

    val discountCondition = DefaultDiscountCondition()
    val discountPolicy = DiscountPolicy(discountCondition)
    val payment = Payment()
    val pointPolicy = PointPolicy()
}
