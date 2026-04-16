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
    val interstellar = Movie(title = MovieTitle("인터스텔라"))

    @OptIn(ExperimentalUuidApi::class)
    val oppenheimer = Movie(title = MovieTitle("오펜하이머"))

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

    val screeningMovieList =
        listOf(
            ScreeningMovie(
                movie = interstellar,
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 20),
                        startTime = LocalTime.of(13, 30, 0),
                        endTime = LocalTime.of(16, 19, 0),
                    ),
                theater = theaterList[0],
            ),
            ScreeningMovie(
                movie = interstellar,
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 20),
                        startTime = LocalTime.of(18, 0, 0),
                        endTime = LocalTime.of(20, 49, 0),
                    ),
                theater = theaterList[1],
            ),
            ScreeningMovie(
                movie = oppenheimer,
                movieTime =
                    MovieTime(
                        date = LocalDate.of(2026, 4, 20),
                        startTime = LocalTime.of(10, 0, 0),
                        endTime = LocalTime.of(13, 0, 0),
                    ),
                theater = theaterList[0],
            ),
        )

    val screeningMovies = ScreeningMovies(screeningMovieList)

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
