package movie.view

import movie.domain.payment.PaymentMethod

object InputView {
    fun readReservationStart(): String {
        println("\n영화 예매를 시작합니다. 새 예매를 생성하시겠습니까? (Y/N)")

        return readln()
    }

    fun readMovieTitle(): String {
        println("\n예매할 영화 제목을 입력하세요:")

        return readln()
    }

    fun readMovieDate(): String {
        println("\n날짜를 입력하세요 (YYYY-MM-DD):")

        return readln()
    }

    fun readSelectedMovieTimeNumber(): String {
        println("\n상영 번호를 선택하세요:")

        return readln()
    }

    fun readReservationSeat(): String {
        println("\n예약할 좌석을 입력하세요 (A1, B2):")

        return readln()
    }

    fun readContinueReservation(): String {
        println("\n다른 영화를 추가하시겠습니까? (Y/N)")

        return readln()
    }

    fun readUsePoint(): String {
        println("\n사용할 포인트를 입력하세요 (없으면 0):")

        return readln()
    }

    fun readPaymentType(paymentMethods: List<PaymentMethod>): String {
        println("\n결제 수단을 선택하세요:")

        paymentMethods.forEachIndexed { index, method ->
            val discountPercent = (method.discountRate * 100).toInt()

            println("${index + 1}) ${method.name}($discountPercent% 할인)")
        }

        return readln()
    }

    fun readUserPayment(): String {
        println("\n위 금액으로 결제하시겠습니까? (Y/N)")

        return readln()
    }
}
