package movie.domain.movie

class AlreadyReservedSeatException(
    message: String = "이미 예약된 좌석입니다.",
) : IllegalStateException(message)
