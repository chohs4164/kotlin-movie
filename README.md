# kotlin-movie

## 1단계 기능 구현 사항

## 도메인

### Point

* [x] Point 도메인을 구현한다.
    * [x] 포인트 정보를 가진다.

### User

* [x] User 도메인을 구현한다.
    * [x] 식별자 userId를 가진다.
    * [x] 사용자 포인트 정보를 가진다.

### Movie

* [x] Movie 도메인을 구현한다.
    * [x] 영화 이름을 가진다.
    * [x] 영화 ID를 가진다.

### ScreeningMovie

* [x] ScreeningMovie 도메인을 구현한다.
    * [x] Theater를 가진다.
    * [x] Movie 정보를 가진다.
    * [x] 상영 시작 / 종료 시간을 가진다.

### Theater

* [x] Theater 도메인을 구현한다.
    * [x] theaterId를 가진다.
    * [x] 상영관 운영 시작 / 종료 시간을 가진다.
    * [x] 좌석 목록을 가진다.

### TheaterScheduler

* [x] TheaterScheduler 도메인을 구현한다.
    * [x] 스크린별 상영 영화 정보를 가지고 있는다.
    * [x] 특정 스크린 영화 상영 가능 여부를 검사한다.
    * [x] 특정 스크린에 영화를 추가한다.

### Seat

* [x] Seat 도메인을 구현한다.
    * [x] 행(알파벳) 정보를 가진다.
    * [x] 열(숫자) 정보를 가진다.
    * [x] 좌석 등급(S, A, B)을 가진다.
    * [x] 좌석 예약 여부(예약전, 예약중, 예약 완료)를 가진다.

### Seats

* [x] Seats 도메인을 구현한다.
    * [x] 예매된 좌석 번호를 가진다.

### Reservation

* [x] Reservation 도메인을 구현한다.
    * [x] 예매한 영화 정보(ScreeningMovie)를 가진다.
    * [x] 예매한 좌석 정보들을 가진다.

### Ticket

* [x] Ticket 도메인을 구현한다.
    * [x] 예매 목록(Reservation)을 가진다.
    * [x] 총 결제 금액을 가진다.

### DiscountCondition

* [x] DiscountCondition 도메인을 구현한다.
    * [x] 무비데이 할인 정책 여부를 판단한다.
    * [x] 시간 할인 정책 여부를 판단한다.

### DiscountPolicy

* [x] DiscountPolicy을 구현한다.
    * [x] 할인 정책에 따라 계산된 금액을 반환한다.

### PointPolicy

* [x] PointPolicy 도메인을 구현한다.
    * [x] 포인트 정책에 따라 계산된 금액을 반환한다.

### Payment

* [x] Payment 도메인을 구현한다.
    * [x] 결제 수단에 따른 할인을 적용한다.

## 2단계 기능 구현 사항

## View

### OutputView

* [x] OutputView를 구현한다.
    * [x] 상영 목록을 출력한다.
    * [x] 좌석배치도를 출력한다.
    * [x] 예매 내역을 출력한다.

### InputView

* [x] InputView를 구현한다.
    * [x] 예매 시작 여부를 입력받는다.
    * [x] 영화 제목을 입력받는다.
    * [x] 날짜를 입력받는다.
    * [x] 상영 회차를 입력받는다.
    * [x] 예매할 좌석을 입력받는다.
    * [x] 다른 영화를 추가할지 여부를 입력받는다.
    * [x] 포인트 사용 여부를 입력받는다.
    * [x] 결제 수단을 입력받는다.
    * [x] 최종 결제 여부를 입력받는다.

### InputParser

* [x] InputParser를 구현한다.
    * [x] Y or N를 파싱한다.
    * [x] 날짜를 파싱한다.
    * [x] Int 타입의 숫자를 파싱한다.
    * [x] 예매할 좌석을 파싱한다.
    * [x] 포인트를 파싱한다.

## Validator

### InputValidator

* [x] InputValidator를 구현한다.
    * [x] Y or N를 검증한다.
    * [x] 날짜를 검증한다.
    * [x] Int 타입의 숫자를 검증한다.
    * [x] 예매할 좌석을 검증한다.

## Controller

### MovieController

* [x] MovieController를 구현한다.
    * [x] 영화 예매를 진행할 수 있다.
