package com.example.swpp.tododapp.tasks

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.Task
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * B12: User-facing regression: checking and unchecking one task that has
 * no due date must clear the label on the SAME list row.
 * Requires an Android emulator/physical device; no claim of execution is made
 * unless a genuine connectedDebugAndroidTest or CI result is available.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingFlowTest {
    private lateinit var repository: FakeAndroidTestRepository

    @Before
    fun setUpRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun resetRepository() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun setDueDateLabel_completeThenUncheck_noStaleClearInTaskList() = runTest { // B12
        // Given: one active task without a due date.
        repository.addTasks(
            Task(title = "No due date", description = "Binding regression",
                isCompleted = false, id = "no-due", dueDate = null)
        )
        launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.complete_checkbox)).check(matches(not(isChecked())))
        onView(withId(R.id.due_date_text)).check(matches(withText("")))

        // When: user completes that task, then cancels completion.
        onView(withId(R.id.complete_checkbox)).perform(click())
        onView(withId(R.id.complete_checkbox)).check(matches(isChecked()))
        onView(withId(R.id.due_date_text)).check(matches(withText("CLEAR")))
        onView(withId(R.id.complete_checkbox)).perform(click())

        // Then: the checkbox and label both reflect the newly ACTIVE task.
        onView(withId(R.id.complete_checkbox)).check(matches(not(isChecked())))
        onView(withId(R.id.due_date_text)).check(matches(withText("")))
    }
}
