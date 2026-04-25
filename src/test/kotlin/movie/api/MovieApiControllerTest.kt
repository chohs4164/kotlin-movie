package movie.api

import movie.api.dto.MovieResponse
import movie.api.dto.MovieScreeningResponse
import movie.api.dto.MoviesResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.client.RestTestClient
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MovieApiControllerTest {
    @LocalServerPort
    private var port: Int = 0

    private lateinit var client: RestTestClient

    @BeforeEach
    fun setUp() {
        client =
            RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
    }

    @Test
    fun `영화 목록을 조회한다`() {
        val actual =
            client
                .get()
                .uri("/api/movies")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(MoviesResponse::class.java)
                .returnResult()
                .responseBody!!

        val expected =
            MoviesResponse(
                movies =
                    listOf(
                        MovieResponse(
                            id = 1,
                            title = "인터스텔라",
                            runningTimeMinutes = 169,
                            screenings =
                                listOf(
                                    MovieScreeningResponse(
                                        id = 101,
                                        startAt = LocalDateTime.of(2026, 4, 20, 13, 30),
                                        endAt = LocalDateTime.of(2026, 4, 20, 16, 19),
                                    ),
                                    MovieScreeningResponse(
                                        id = 102,
                                        startAt = LocalDateTime.of(2026, 4, 20, 18, 0),
                                        endAt = LocalDateTime.of(2026, 4, 20, 20, 49),
                                    ),
                                ),
                        ),
                        MovieResponse(
                            id = 2,
                            title = "오펜하이머",
                            runningTimeMinutes = 180,
                            screenings =
                                listOf(
                                    MovieScreeningResponse(
                                        id = 201,
                                        startAt = LocalDateTime.of(2026, 4, 20, 10, 0),
                                        endAt = LocalDateTime.of(2026, 4, 20, 13, 0),
                                    ),
                                ),
                        ),
                    ),
            )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `예매를 생성한다`() {
        val screeningId = findScreeningId(title = "오펜하이머")

        client
            .post()
            .uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                {
                  "reservations": [
                    {
                      "screeningId": $screeningId,
                      "seats": ["C2", "C3"]
                    }
                  ],
                  "usedPoints": 2000,
                  "paymentMethod": "CREDIT_CARD"
                }
                """.trimIndent(),
            ).exchange()
            .expectStatus()
            .isCreated()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.reservationId")
            .exists()
            .jsonPath("$.reservations[0].screeningId")
            .isEqualTo(screeningId)
            .jsonPath("$.reservations[0].seats[0]")
            .isEqualTo("C2")
            .jsonPath("$.usedPoints")
            .isEqualTo(2000)
            .jsonPath("$.paymentMethod")
            .isEqualTo("CREDIT_CARD")
            .jsonPath("$.totalPrice")
            .isEqualTo(26980)
    }

    @Test
    fun `이미 예약된 좌석을 예매하면 충돌 응답을 반환한다`() {
        val screeningId = findScreeningId(title = "오펜하이머")

        reserveSeat(screeningId = screeningId, seat = "A1")

        client
            .post()
            .uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                {
                  "reservations": [
                    {
                      "screeningId": $screeningId,
                      "seats": ["A1"]
                    }
                  ],
                  "usedPoints": 0,
                  "paymentMethod": "CASH"
                }
                """.trimIndent(),
            ).exchange()
            .expectStatus()
            .isEqualTo(409)
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("이미 예약된 좌석입니다.")
    }

    @Test
    fun `존재하지 않는 상영 예매는 찾을 수 없음 응답을 반환한다`() {
        client
            .post()
            .uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                {
                      "reservations": [
                        {
                          "screeningId": 999,
                          "seats": ["A1"]
                        }
                      ],
                  "usedPoints": 0,
                  "paymentMethod": "CASH"
                }
                """.trimIndent(),
            ).exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("존재하지 않는 상영 정보입니다. screeningId=999")
    }

    @Test
    fun `잘못된 요청 형식은 잘못된 요청 응답을 반환한다`() {
        client
            .post()
            .uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{ invalid-json }")
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("잘못된 요청 형식입니다.")
    }

    @Test
    fun `잘못된 상영 아이디 타입은 잘못된 요청 응답을 반환한다`() {
        client
            .post()
            .uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                {
                  "reservations": [
                    {
                      "screeningId": "invalid",
                      "seats": ["A1"]
                    }
                  ],
                  "usedPoints": 0,
                  "paymentMethod": "CASH"
                }
                """.trimIndent(),
            ).exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("잘못된 요청 형식입니다.")
    }

    private fun findScreeningId(title: String): Int =
        client
            .get()
            .uri("/api/movies")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(MoviesResponse::class.java)
            .returnResult()
            .responseBody!!
            .movies
            .first { it.title == title }
            .screenings
            .first()
            .id

    private fun reserveSeat(
        screeningId: Int,
        seat: String,
    ) {
        client
            .post()
            .uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                {
                  "reservations": [
                    {
                      "screeningId": $screeningId,
                      "seats": ["$seat"]
                    }
                  ],
                  "usedPoints": 0,
                  "paymentMethod": "CASH"
                }
                """.trimIndent(),
            ).exchange()
            .expectStatus()
            .isCreated()
    }
}
