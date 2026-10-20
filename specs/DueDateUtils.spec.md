# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: 완료 항목은 `CLEAR`, 마감일 없는 미완료 항목은 빈 문자열, 그 밖의 미완료 항목은 전체 달력 일수에 따른 D-day 문자열을 반환한다. 월·연도·윤일 경계에서도 동일한 의미를 유지한다.

**Pre-conditions**
- `today`는 호출자가 제공한 유효한 `LocalDate`다. 테스트는 실제 시스템 시각 대신 아래의 고정 날짜를 직접 전달한다.
- `dueDate`는 유효한 `LocalDate` 또는 `null`이며, `null` 자체는 오류가 아니다.
- `isCompleted`의 두 값 모두 유효하다. 완료 여부를 먼저 판정한다.

**Post-conditions**
- Success cases
  - U01: 완료 항목은 마감일이 없어도 CLEAR다. `[Unit]`
    - Tip: Given `dueDate=null`, `today=2024-03-01`, `isCompleted=true` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"CLEAR"`를 반환한다.
  - U02: 완료 항목은 이미 지난 마감일보다 완료 상태를 우선한다. `[Unit]`
    - Tip: Given `dueDate=2024-02-01`, `today=2024-03-01`, `isCompleted=true` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"CLEAR"`를 반환한다.
  - U03: 완료 항목은 마감일이 오늘이어도 D-Day가 아닌 CLEAR다. `[Unit]`
    - Tip: Given `dueDate=2024-03-01`, `today=2024-03-01`, `isCompleted=true` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"CLEAR"`를 반환한다.
  - U04: 완료 항목은 미래 마감일이어도 CLEAR다. `[Unit]`
    - Tip: Given `dueDate=2025-03-01`, `today=2024-03-01`, `isCompleted=true` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"CLEAR"`를 반환한다.
  - U05: 미완료 항목에 마감일이 없으면 빈 문자열이다. `[Unit]`
    - Tip: Given `dueDate=null`, `today=2024-03-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `""`를 반환한다.
  - U06: 미래 마감일은 D- 뒤에 남은 전체 일수를 표시한다. `[Unit]`
    - Tip: Given `dueDate=2024-03-06`, `today=2024-03-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-5"`를 반환한다.
  - U07: 지난 마감일은 D+ 뒤에 초과한 전체 일수를 표시한다. `[Unit]`
    - Tip: Given `dueDate=2024-03-01`, `today=2024-03-06`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+5"`를 반환한다.
- Error cases
  - None. 명세에서 거부해야 한다고 지정한 입력은 없다. 날짜 파싱 실패는 함수 호출 전에 발생하는 별도 문제다.
- Edge cases
  - U08: 0일 경계 직전: 어제 마감. `[Unit]`
    - Tip: Given `dueDate=2024-02-29`, `today=2024-03-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+1"`를 반환한다.
  - U09: 0일 경계: 오늘 마감은 D-0이나 D+0이 아니다. `[Unit]`
    - Tip: Given `dueDate=2024-03-01`, `today=2024-03-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-Day"`를 반환한다.
  - U10: 0일 경계 직후: 내일 마감. `[Unit]`
    - Tip: Given `dueDate=2024-03-02`, `today=2024-03-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-1"`를 반환한다.
  - U11: 한 달 경계 직전: 총 30일. `[Unit]`
    - Tip: Given `dueDate=2024-01-31`, `today=2024-01-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-30"`를 반환한다.
  - U12: 한 달 경계: 월 성분이 생겨도 총 31일을 표시한다. `[Unit]`
    - Tip: Given `dueDate=2024-02-01`, `today=2024-01-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-31"`를 반환한다.
  - U13: 한 달 경계 직후: 1일이 아니라 총 32일. `[Unit]`
    - Tip: Given `dueDate=2024-02-02`, `today=2024-01-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-32"`를 반환한다.
  - U14: 역방향 한 달 경계 직전: 30일 초과. `[Unit]`
    - Tip: Given `dueDate=2024-01-02`, `today=2024-02-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+30"`를 반환한다.
  - U15: 역방향 한 달 경계: 31일 초과. `[Unit]`
    - Tip: Given `dueDate=2024-01-01`, `today=2024-02-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+31"`를 반환한다.
  - U16: 역방향 한 달 경계 직후: 32일 초과. `[Unit]`
    - Tip: Given `dueDate=2023-12-31`, `today=2024-02-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+32"`를 반환한다.
  - U17: 연말의 전날 마감. `[Unit]`
    - Tip: Given `dueDate=2023-12-30`, `today=2023-12-31`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+1"`를 반환한다.
  - U18: 연말 당일 마감. `[Unit]`
    - Tip: Given `dueDate=2023-12-31`, `today=2023-12-31`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-Day"`를 반환한다.
  - U19: 연도가 바뀌는 다음 날 마감. `[Unit]`
    - Tip: Given `dueDate=2024-01-01`, `today=2023-12-31`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-1"`를 반환한다.
  - U20: 윤일 직전 날짜의 당일 마감. `[Unit]`
    - Tip: Given `dueDate=2024-02-28`, `today=2024-02-28`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-Day"`를 반환한다.
  - U21: 윤일은 실제 달력의 하루다. `[Unit]`
    - Tip: Given `dueDate=2024-02-29`, `today=2024-02-28`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-1"`를 반환한다.
  - U22: 윤일을 지나면 총 2일이다. `[Unit]`
    - Tip: Given `dueDate=2024-03-01`, `today=2024-02-28`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-2"`를 반환한다.
  - U23: 평년의 2월 말은 윤년과 구별한다. `[Unit]`
    - Tip: Given `dueDate=2023-03-01`, `today=2023-02-28`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-1"`를 반환한다.
  - U24: 연 성분이 있어도 윤년을 포함한 총 366일을 표시한다. `[Unit]`
    - Tip: Given `dueDate=2025-01-01`, `today=2024-01-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D-366"`를 반환한다.
  - U25: 역방향에서도 연 성분을 버리지 않고 366일 초과로 표시한다. `[Unit]`
    - Tip: Given `dueDate=2024-01-01`, `today=2025-01-01`, `isCompleted=false` / When `dueDateLabel`을 한 번 호출 / Then 정확히 `"D+366"`를 반환한다.

**Undefined behavior** (optional)
- 잘못된 날짜 문자열이나 존재하지 않는 날짜를 `LocalDate`로 변환하는 동작은 이 함수 밖이다. 이 함수가 문자열을 거부한다고 가정하는 테스트는 작성하지 않는다.
- 시각·시간대·일광절약시간 변환, 문자열 지역화, 화면 표시 상태는 이 함수의 계약이 아니다.
- UI에 표시할 자릿수 제한이나 날짜 범위 제한은 원본 요구사항에 없으므로 새로 부과하지 않는다.
