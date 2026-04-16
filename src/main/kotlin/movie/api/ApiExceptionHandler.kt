package movie.api

import movie.application.ReservationConflictException
import movie.application.ScreeningNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponse(
    val message: String,
)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(ScreeningNotFoundException::class)
    fun handleScreeningNotFound(exception: ScreeningNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(exception.message ?: "존재하지 않는 상영 정보입니다."))

    @ExceptionHandler(ReservationConflictException::class)
    fun handleReservationConflict(exception: ReservationConflictException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(exception.message ?: "이미 예약된 좌석입니다."))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .badRequest()
            .body(ErrorResponse(exception.message ?: "잘못된 요청입니다."))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidRequest(): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .badRequest()
            .body(ErrorResponse("잘못된 요청 형식입니다."))
}
