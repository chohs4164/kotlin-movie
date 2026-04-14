package movie.domain.seat.number

@JvmInline
value class Row(
    private val value: Char,
) {
    init {
        require(value in 'A'..'Z') { "Row는 A부터 Z사이의 알파벳이여야 합니다." }
    }

    override fun toString(): String = value.toString()
}
