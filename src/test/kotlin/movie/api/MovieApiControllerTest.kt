package movie.api

import movie.api.dto.MoviesResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MovieApiControllerTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var client: RestTestClient

    @BeforeEach
    fun setUp() {
        client =
            RestTestClient
                .bindToApplicationContext(context)
                .build()
    }

    @Test
    fun `영화 목록을 조회한다`() {
        val response =
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

        assertThat(response.movies).hasSize(2)
        assertThat(response.movies.map { it.title }).containsExactlyInAnyOrder("인터스텔라", "오펜하이머")
        assertThat(response.movies.first { it.title == "인터스텔라" }.screenings).hasSize(2)
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
                      "screeningId": "$screeningId",
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
                      "screeningId": "$screeningId",
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
                          "screeningId": "00000000-0000-0000-0000-000000000000",
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
            .value<String> { message ->
                assertThat(message).contains("존재하지 않는 상영 정보입니다.")
            }
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

    private fun findScreeningId(title: String): String =
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
            .screeningId

    private fun reserveSeat(
        screeningId: String,
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
                      "screeningId": "$screeningId",
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
