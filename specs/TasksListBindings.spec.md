# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: 현재 바인딩한 할 일의 상태에 맞게 `TextView`의 표시를 갱신한다. 이전 호출 때의 텍스트가 새로운 빈 표시를 가로막아서는 안 되며, 마감일이 없는 항목의 완료 취소 후에는 `CLEAR`가 남지 않는다.

**Pre-conditions**
- 실제 Android `TextView`를 UI 스레드에서 조작한다. `[Integration]`은 Android 기기 또는 에뮬레이터에서 실행할 테스트를 뜻한다.
- 동일한 `TextView`가 여러 번 재사용될 수 있다. 이전 텍스트나 표시 상태가 빈 상태라는 가정을 하지 않는다.
- 날짜가 있는 미완료 항목은 기기의 현재 로컬 날짜를 기준으로 한다. 함수에 `today` 인자가 없으므로 테스트는 UI 스레드에서 기준 날짜 T와 입력을 함께 준비하고, 호출 전후 로컬 날짜가 같은지 확인한다. 날짜가 달라지면 환경 변화로 보고하며 기대값을 임의로 바꾸지 않는다.
- 아래 날짜 Tip의 `2026-10-20`은 구체적인 예시 기준일이다. 실제 테스트는 그날을 강제로 설정하지 않고 T에 대한 -1/0/+1일 관계를 유지하며, 실제 T와 dueDate를 결과에 기록한다. 기대 문자열은 D+1/D-Day/D-1로 고정하며 생산 함수의 반환값으로 만들지 않는다.
- `task=null`은 기능 요구사항에 직접 규정되지 않은 경우이므로 이번 검증 범위에서 제외한다.

**Post-conditions**
- Success cases
  - B01: 마감일 없는 완료 항목은 CLEAR를 보이게 한다. `[Integration]`
    - Tip: Given `text="OLD"`, `visibility=View.GONE`, `Task(id="task-1", isCompleted=true, dueDate=null)` / When `setDueDateLabel` 호출 / Then `text="CLEAR"`, `visibility=View.VISIBLE`.
  - B02: 마감일이 있는 완료 항목에도 동일한 완료 표시를 쓴다. `[Integration]`
    - Tip: Given `text="OLD"`, `visibility=View.VISIBLE`, `Task(id="task-1", isCompleted=true, dueDate=2024-02-01)` / When 호출 / Then `text="CLEAR"`, `visibility=View.VISIBLE`.
  - B03: 마감일 없는 미완료 항목은 기존 텍스트를 비운다. `[Integration]`
    - Tip: Given `text="OLD"`, `visibility=View.VISIBLE`, `Task(id="task-1", isCompleted=false, dueDate=null)` / When 호출 / Then `text=""`이며 이전 `OLD`가 남지 않는다.
  - B04: 내일 마감인 미완료 항목을 보여준다. `[Integration]`
    - Tip: Given 기기 기준일 `2026-10-20`, `text="OLD"`, `Task(id="task-1", isCompleted=false, dueDate=2026-10-21)` / When 호출 / Then `text="D-1"`, `visibility=View.VISIBLE`.
  - B05: 오늘 마감인 미완료 항목을 보여준다. `[Integration]`
    - Tip: Given 기기 기준일 `2026-10-20`, `text="OLD"`, `Task(id="task-1", isCompleted=false, dueDate=2026-10-20)` / When 호출 / Then `text="D-Day"`, `visibility=View.VISIBLE`.
  - B06: 어제 마감인 미완료 항목을 보여준다. `[Integration]`
    - Tip: Given 기기 기준일 `2026-10-20`, `text="OLD"`, `Task(id="task-1", isCompleted=false, dueDate=2026-10-19)` / When 호출 / Then `text="D+1"`, `visibility=View.VISIBLE`.
- Error cases
  - None. 위 유효 입력을 거부한다는 요구사항은 없다. `task=null`은 정의되지 않은 입력으로 이번 테스트 대상에서 제외한다.
- Edge cases
  - B08: 같은 항목의 미완료 → 완료 → 완료 취소에서 매 단계 최신 텍스트를 반영한다. `[Integration]`
    - Tip: Given 동일한 `TextView`와 `id="task-1"`, `dueDate=null` / When `isCompleted=false`, `true`, `false`를 차례로 바인딩 / Then 각 단계의 텍스트는 정확히 `""`, `"CLEAR"`, `""`이며 완료 단계는 `View.VISIBLE`이다.
  - B09: 뷰를 다른 항목에 재사용해도 이전 항목의 표시가 남지 않는다. `[Integration]`
    - Tip: Given 같은 뷰에 `Task(id="first", isCompleted=true, dueDate=null)`를 바인딩하여 `text="CLEAR"` / When `Task(id="second", isCompleted=false, dueDate=null)`를 바인딩 / Then `text=""`.
  - B10: 마감일을 제거하면 기존 D-day 표시도 비운다. `[Integration]`
    - Tip: Given 기기 기준일 `2026-10-20`, 같은 뷰와 `id="task-1"`의 미완료 항목에 `dueDate=2026-10-21`을 먼저 바인딩하여 `text="D-1"` / When `dueDate=null`인 같은 항목을 바인딩 / Then `text=""`.
  - B11: 이미 비어 있는 표시를 비우는 동작을 반복해도 정상이다. `[Integration]`
    - Tip: Given `text=""`인 같은 뷰와 `Task(id="task-1", isCompleted=false, dueDate=null)` / When 같은 입력으로 두 번 호출 / Then 두 번 모두 `text=""`이고 예외를 발생시키지 않는다.
  - B12: 실제 목록의 완료 체크박스로 왕복해도 CLEAR가 남지 않는다. `[Integration]`
    - Tip: Given Fake repository에 `Task(title="No due date", description="Binding regression", id="no-due", isCompleted=false, dueDate=null)` 한 개를 넣고 Tasks 화면 표시 / When 해당 행의 완료 체크박스를 클릭한 뒤 다시 클릭 / Then 중간에는 체크됨과 `"CLEAR"`, 마지막에는 체크 해제됨과 `text=""`를 같은 행에서 확인한다.

**Undefined behavior** (optional)
- `task=null` 바인딩 시 UI 동작은 PDF p.53에 지정되지 않았다. 이번 기능 명세와 테스트에서 기대 동작을 새로 만들지 않는다.
- 빈 텍스트에서 `visibility`를 `GONE`, `INVISIBLE`, `VISIBLE` 중 어느 값으로 둘지는 지정하지 않는다. PDF의 'no label'에 없는 레이아웃 정책을 강제하지 않는다.
- 글자색, 글꼴, 공백 영역의 크기, 다른 UI 요소 및 화면 이동은 이 함수의 계약이 아니다.
- `LocalDate.now()`의 시계 주입 구조를 바꾸거나 생산 코드를 테스트 편의상 수정하지 않는다.
