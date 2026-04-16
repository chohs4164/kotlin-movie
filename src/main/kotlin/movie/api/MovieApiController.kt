package movie.api

import movie.api.dto.CreateReservationRequest
import movie.api.dto.CreateReservationResponse
import movie.api.dto.MoviesResponse
import movie.application.MovieApiService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MovieApiController(
    private val movieApiService: MovieApiService,
) {
    @GetMapping("/movies")
    fun getMovies(): MoviesResponse = movieApiService.getMovies()

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(
        @RequestBody request: CreateReservationRequest,
    ): CreateReservationResponse = movieApiService.createReservation(request)
}
