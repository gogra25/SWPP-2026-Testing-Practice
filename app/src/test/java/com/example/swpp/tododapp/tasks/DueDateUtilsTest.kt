package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Spec-driven tests for dueDateLabel() (reviewed DueDateUtils.spec.md U01-U25).
 * Expected strings are independently written from the feature intent, never
 * calculated using the implementation under test.
 * The production DueDateUtils.kt must NOT be edited to make these pass.
 */
class DueDateUtilsTest {
    private fun day(iso: String): LocalDate = LocalDate.parse(iso)

    @Test
    fun dueDateLabel_completedNoDueDate_returnsExpectedLabel() { // U01
        // Given: today=2024-03-01, dueDate=null, completed=true.
        // When: the actual production function is invoked.
        val result = dueDateLabel(null, day("2024-03-01"), true)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedPastDue_returnsExpectedLabel() { // U02
        // Given: today=2024-03-01, dueDate=2024-02-01, completed=true.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-02-01"), day("2024-03-01"), true)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedDueToday_returnsExpectedLabel() { // U03
        // Given: today=2024-03-01, dueDate=2024-03-01, completed=true.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-03-01"), day("2024-03-01"), true)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedFutureDue_returnsExpectedLabel() { // U04
        // Given: today=2024-03-01, dueDate=2025-03-01, completed=true.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2025-03-01"), day("2024-03-01"), true)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_activeNoDueDate_returnsExpectedLabel() { // U05
        // Given: today=2024-03-01, dueDate=null, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(null, day("2024-03-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_fiveDaysAhead_returnsExpectedLabel() { // U06
        // Given: today=2024-03-01, dueDate=2024-03-06, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-03-06"), day("2024-03-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-5", result)
    }

    @Test
    fun dueDateLabel_fiveDaysOverdue_returnsExpectedLabel() { // U07
        // Given: today=2024-03-06, dueDate=2024-03-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-03-01"), day("2024-03-06"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+5", result)
    }

    @Test
    fun dueDateLabel_yesterdayLeapDay_returnsExpectedLabel() { // U08
        // Given: today=2024-03-01, dueDate=2024-02-29, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-02-29"), day("2024-03-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_dueToday_returnsExpectedLabel() { // U09
        // Given: today=2024-03-01, dueDate=2024-03-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-03-01"), day("2024-03-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_dueTomorrow_returnsExpectedLabel() { // U10
        // Given: today=2024-03-01, dueDate=2024-03-02, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-03-02"), day("2024-03-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_thirtyDaysAhead_returnsExpectedLabel() { // U11
        // Given: today=2024-01-01, dueDate=2024-01-31, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-01-31"), day("2024-01-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_thirtyOneDaysAhead_returnsExpectedLabel() { // U12
        // Given: today=2024-01-01, dueDate=2024-02-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-02-01"), day("2024-01-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_thirtyTwoDaysAhead_returnsExpectedLabel() { // U13
        // Given: today=2024-01-01, dueDate=2024-02-02, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-02-02"), day("2024-01-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-32", result)
    }

    @Test
    fun dueDateLabel_thirtyDaysOverdue_returnsExpectedLabel() { // U14
        // Given: today=2024-02-01, dueDate=2024-01-02, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-01-02"), day("2024-02-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_thirtyOneDaysOverdue_returnsExpectedLabel() { // U15
        // Given: today=2024-02-01, dueDate=2024-01-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-01-01"), day("2024-02-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+31", result)
    }

    @Test
    fun dueDateLabel_thirtyTwoDaysOverdue_returnsExpectedLabel() { // U16
        // Given: today=2024-02-01, dueDate=2023-12-31, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2023-12-31"), day("2024-02-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+32", result)
    }

    @Test
    fun dueDateLabel_yesterdayBeforeYearEnd_returnsExpectedLabel() { // U17
        // Given: today=2023-12-31, dueDate=2023-12-30, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2023-12-30"), day("2023-12-31"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_dueAtYearEnd_returnsExpectedLabel() { // U18
        // Given: today=2023-12-31, dueDate=2023-12-31, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2023-12-31"), day("2023-12-31"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_tomorrowAcrossYearEnd_returnsExpectedLabel() { // U19
        // Given: today=2023-12-31, dueDate=2024-01-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-01-01"), day("2023-12-31"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_dueOnFebTwentyEight_returnsExpectedLabel() { // U20
        // Given: today=2024-02-28, dueDate=2024-02-28, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-02-28"), day("2024-02-28"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_dueOnLeapDay_returnsExpectedLabel() { // U21
        // Given: today=2024-02-28, dueDate=2024-02-29, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-02-29"), day("2024-02-28"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_twoDaysAcrossLeapDay_returnsExpectedLabel() { // U22
        // Given: today=2024-02-28, dueDate=2024-03-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-03-01"), day("2024-02-28"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-2", result)
    }

    @Test
    fun dueDateLabel_tomorrowInNonLeapYear_returnsExpectedLabel() { // U23
        // Given: today=2023-02-28, dueDate=2023-03-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2023-03-01"), day("2023-02-28"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_leapYearThreeHundredSixtySixDaysAhead_returnsExpectedLabel() { // U24
        // Given: today=2024-01-01, dueDate=2025-01-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2025-01-01"), day("2024-01-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D-366", result)
    }

    @Test
    fun dueDateLabel_leapYearThreeHundredSixtySixDaysOverdue_returnsExpectedLabel() { // U25
        // Given: today=2025-01-01, dueDate=2024-01-01, completed=false.
        // When: the actual production function is invoked.
        val result = dueDateLabel(day("2024-01-01"), day("2025-01-01"), false)
        // Then: the independently specified calendar-day label is observed.
        assertEquals("D+366", result)
    }
}
