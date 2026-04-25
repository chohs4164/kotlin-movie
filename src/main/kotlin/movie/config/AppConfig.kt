package movie.config

import movie.MovieFixtures
import movie.application.MovieCatalogOrder
import movie.domain.discount.DiscountPolicy
import movie.domain.payment.Payment
import movie.domain.point.PointPolicy
import movie.infrastructure.db.JdbcReservationRepository
import movie.infrastructure.db.JdbcScreeningRepository
import movie.infrastructure.db.SchemaInitializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.atomic.AtomicLong

@Configuration
class AppConfig {
    @Bean
    fun movieFixtures(): MovieFixtures = MovieFixtures()

    @Bean
    fun discountPolicy(): DiscountPolicy = DiscountPolicy()

    @Bean
    fun pointPolicy(): PointPolicy = PointPolicy()

    @Bean
    fun payment(): Payment = Payment()

    @Bean
    fun movieCatalogOrder(movieFixtures: MovieFixtures): MovieCatalogOrder =
        MovieCatalogOrder(
            titles =
                movieFixtures.screeningMovieList
                    .map { it.movie.title.value }
                    .distinct(),
        )

    @Bean
    fun schemaInitializer(): SchemaInitializer = SchemaInitializer()

    @Bean(destroyMethod = "close")
    fun connection(
        @Value("\${movie.database.url}") url: String,
    ): Connection = DriverManager.getConnection(url, "sa", "")

    @Bean
    fun screeningRepository(connection: Connection): JdbcScreeningRepository = JdbcScreeningRepository(connection)

    @Bean
    fun reservationRepository(connection: Connection): JdbcReservationRepository = JdbcReservationRepository(connection)

    @Bean
    fun reservationIdSequence(): AtomicLong = AtomicLong(0)

    @Bean
    fun databaseInitializer(
        connection: Connection,
        schemaInitializer: SchemaInitializer,
        screeningRepository: JdbcScreeningRepository,
        movieFixtures: MovieFixtures,
    ): ApplicationRunner =
        ApplicationRunner {
            schemaInitializer.initialize(connection)

            if (!screeningRepository.hasScreenings()) {
                screeningRepository.saveAll(movieFixtures.screeningMovieList)
            }
        }
}
