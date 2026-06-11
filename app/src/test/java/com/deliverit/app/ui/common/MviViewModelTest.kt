package com.deliverit.app.ui.common

import com.deliverit.app.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private data class TestState(val count: Int = 0)

private sealed interface TestIntent {
    data class Increment(val by: Int) : TestIntent
    data class Emit(val message: String) : TestIntent
}

private class TestViewModel : MviViewModel<TestState, TestIntent, String>(TestState()) {
    override fun onIntent(intent: TestIntent) {
        when (intent) {
            is TestIntent.Increment -> setState { copy(count = count + intent.by) }
            is TestIntent.Emit -> sendEvent(intent.message)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MviViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `currentState reflects the initial state`() {
        val viewModel = TestViewModel()

        assertEquals(TestState(0), viewModel.currentState)
    }

    @Test
    fun `setState updates currentState and the state flow`() = runTest {
        val viewModel = TestViewModel()

        viewModel.onIntent(TestIntent.Increment(5))

        assertEquals(TestState(5), viewModel.currentState)
        assertEquals(TestState(5), viewModel.state.value)
    }

    @Test
    fun `sendEvent delivers the event to a collector`() = runTest {
        val viewModel = TestViewModel()
        val events = mutableListOf<String>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TestIntent.Emit("hello"))
        runCurrent()

        job.cancel()
        assertEquals(listOf("hello"), events)
    }

    @Test
    fun `multiple events are delivered in order`() = runTest {
        val viewModel = TestViewModel()
        val events = mutableListOf<String>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TestIntent.Emit("first"))
        viewModel.onIntent(TestIntent.Emit("second"))
        runCurrent()

        job.cancel()
        assertEquals(listOf("first", "second"), events)
    }
}
