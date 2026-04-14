package movie.domain.seat.number

@JvmInline
value class Column(
    private val value: Int,
) {
    init {
        require(value in 1..10) { "Column은 1부터 10사이의 숫자여야 합니다." }
    }

    override fun toString(): String = value.toString()
}
