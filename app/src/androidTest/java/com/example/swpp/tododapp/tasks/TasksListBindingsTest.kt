package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android instrumented tests for the REAL setDueDateLabel() BindingAdapter.
 * Maps to reviewed TasksListBindings.spec.md B01-B06 and B08-B11.
 * Run with connectedDebugAndroidTest on an emulator or real device.
 * The app code is intentionally NOT changed; B03/B08/B09/B10 should reveal
 * the stale-label bug on the provided feature/due-date branch.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    private fun onUi(block: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { block() }
    }

    private fun label(initialText: String = "OLD", initialVisibility: Int = View.VISIBLE) =
        TextView(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            text = initialText
            visibility = initialVisibility
        }

    private fun task(completed: Boolean, dueDate: LocalDate?, id: String = "task-1") =
        Task(title = "Test task", description = "Test description",
            isCompleted = completed, id = id, dueDate = dueDate)

    @Test
    fun setDueDateLabel_completedWithoutDueDate_showsClear() = onUi { // B01
        val textView = label("OLD", View.GONE)
        setDueDateLabel(textView, task(true, null))
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedWithPastDueDate_showsClear() = onUi { // B02
        val textView = label()
        setDueDateLabel(textView, task(true, LocalDate.parse("2024-02-01")))
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeWithoutDueDate_clearsExistingText() = onUi { // B03
        val textView = label("OLD")
        setDueDateLabel(textView, task(false, null))
        assertEquals("", textView.text.toString())
    }

    @Test
    fun setDueDateLabel_tomorrow_showsOneDayRemaining() = onUi { // B04
        val today = LocalDate.now()
        val textView = label()
        setDueDateLabel(textView, task(false, today.plusDays(1)))
        assertEquals("D-1", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
        assertEquals("Device date changed while running the test", today, LocalDate.now())
    }

    @Test
    fun setDueDateLabel_today_showsDDay() = onUi { // B05
        val today = LocalDate.now()
        val textView = label()
        setDueDateLabel(textView, task(false, today))
        assertEquals("D-Day", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
        assertEquals("Device date changed while running the test", today, LocalDate.now())
    }

    @Test
    fun setDueDateLabel_yesterday_showsOneDayOverdue() = onUi { // B06
        val today = LocalDate.now()
        val textView = label()
        setDueDateLabel(textView, task(false, today.minusDays(1)))
        assertEquals("D+1", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
        assertEquals("Device date changed while running the test", today, LocalDate.now())
    }

    @Test
    fun setDueDateLabel_activeThenCompletedThenActive_clearsClear() = onUi { // B08
        val textView = label("", View.VISIBLE)
        setDueDateLabel(textView, task(false, null))
        assertEquals("", textView.text.toString())
        setDueDateLabel(textView, task(true, null))
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
        setDueDateLabel(textView, task(false, null))
        assertEquals("", textView.text.toString())
    }

    @Test
    fun setDueDateLabel_recycledViewForNewTask_clearsPreviousClear() = onUi { // B09
        val textView = label()
        setDueDateLabel(textView, task(true, null, "first"))
        assertEquals("CLEAR", textView.text.toString())
        setDueDateLabel(textView, task(false, null, "second"))
        assertEquals("", textView.text.toString())
    }

    @Test
    fun setDueDateLabel_dueDateRemoved_clearsPreviousDDay() = onUi { // B10
        val today = LocalDate.now()
        val textView = label()
        setDueDateLabel(textView, task(false, today.plusDays(1)))
        assertEquals("D-1", textView.text.toString())
        setDueDateLabel(textView, task(false, null))
        assertEquals("", textView.text.toString())
        assertEquals("Device date changed while running the test", today, LocalDate.now())
    }

    @Test
    fun setDueDateLabel_repeatedEmptyInput_remainsEmpty() = onUi { // B11
        val textView = label("")
        setDueDateLabel(textView, task(false, null))
        assertEquals("", textView.text.toString())
        setDueDateLabel(textView, task(false, null))
        assertEquals("", textView.text.toString())
    }
}
