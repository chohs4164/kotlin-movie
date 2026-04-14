package movie.view

object InputValidator {
    private const val DATE_PATTERN = """\d{4}-\d{2}-\d{2}"""
    private const val SEAT_PATTERN = """^[A-Za-z]\d+$"""

    fun validateYesNo(input: String) {
        require(input.uppercase() in setOf("Y", "N")) { "Y 또는 N만 입력할 수 있습니다." }
    }

    fun validateDate(input: String) {
        require(Regex(DATE_PATTERN).matches(input)) { "날짜는 YYYY-MM-DD 형식이어야 합니다." }
    }

    fun validateNumber(input: String) {
        requireNotNull(input.toIntOrNull()) { "숫자를 입력해야 합니다." }
    }

    fun validateSeatNumbers(input: String) {
        val seatInputs = input.split(",").map { it.trim() }

        require(seatInputs.isNotEmpty()) { "좌석을 하나 이상 입력해야 합니다." }

        seatInputs.forEach { seat ->
            require(Regex(SEAT_PATTERN).matches(seat)) {
                "좌석은 A1, B2 형식으로 입력해야 합니다."
            }
        }
    }
}
