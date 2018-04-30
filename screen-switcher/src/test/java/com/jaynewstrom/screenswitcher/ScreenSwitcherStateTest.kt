package com.jaynewstrom.screenswitcher

import org.fest.assertions.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.Arrays

class ScreenSwitcherStateTest {
    @Test fun constructorMakesDefensiveCopyOfScreensPassedIn() {
        val passedList = listOf(mock(Screen::class.java))
        val state = ScreenTestUtils.defaultState(passedList)
        state.screens.add(mock(Screen::class.java))
        assertThat(passedList).hasSize(1)
        assertThat(state.screens).hasSize(2)
    }

    @Test fun constructorDoesNotAllowEmptyScreens() {
        try {
            ScreenTestUtils.defaultState(emptyList())
            fail()
        } catch (expected: IllegalArgumentException) {
            assertThat(expected).hasMessage("screens must contain at least one screen")
        }
    }

    @Test fun constructorRejectsDuplicateScreens() {
        val screen = mock(Screen::class.java)
        try {
            ScreenTestUtils.defaultState(Arrays.asList(screen, screen))
            fail()
        } catch (expected: IllegalArgumentException) {
            assertThat(expected).hasMessage("screen already exists")
        }
    }

    @Test fun handlesPopIsFalseWhenNoPopListeners() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        assertThat(state.handlesPop(screen)).isFalse
    }

    @Test fun handlesPopIsFalseWhenPopListenerReturnsFalse() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        val popListener = mock(ScreenPopListener::class.java)
        state.registerPopListener(screen, popListener)
        `when`(popListener.onScreenPop(screen)).thenReturn(false)
        assertThat(state.handlesPop(screen)).isFalse
    }

    @Test fun handlesPopIsTrueWhenPopListenerReturnsTrue() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        val popListener = mock(ScreenPopListener::class.java)
        state.registerPopListener(screen, popListener)
        `when`(popListener.onScreenPop(screen)).thenReturn(true)
        assertThat(state.handlesPop(screen)).isTrue
    }

    @Test fun handlesPopIsFalseForScreenNotMatchingPopListenerThatReturnsTrue() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        val popListener = mock(ScreenPopListener::class.java)
        state.registerPopListener(screen, popListener)
        `when`(popListener.onScreenPop(screen)).thenReturn(true)
        assertThat(state.handlesPop(mock(Screen::class.java))).isFalse
    }

    @Test fun handlesPopRemovesPopListenerWhenReturnsFalse() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        val popListener = mock(ScreenPopListener::class.java)
        state.registerPopListener(screen, popListener)
        `when`(popListener.onScreenPop(screen)).thenReturn(false)
        assertThat(state.handlesPop(screen)).isFalse // Will be removed here.
        assertThat(state.handlesPop(screen)).isFalse // Will return false because it's gone.
        // Will verify it was only used once, even though #handlesPop was called twice.
        verify(popListener).onScreenPop(screen)
    }

    @Test fun handlesPopKeepsPopListenerWhenReturnsTrue() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        val popListener = mock(ScreenPopListener::class.java)
        state.registerPopListener(screen, popListener)
        `when`(popListener.onScreenPop(screen)).thenReturn(true)
        assertThat(state.handlesPop(screen)).isTrue
        assertThat(state.handlesPop(screen)).isTrue
        verify(popListener, times(2)).onScreenPop(screen)
    }

    @Test fun addScreenRejectsDuplicateScreen() {
        val screen = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(screen)
        try {
            state.addScreen(screen)
            fail()
        } catch (expected: IllegalArgumentException) {
            assertThat(expected).hasMessage("screen already exists")
        }
    }

    @Test fun indexOfReturnsNegativeOneIfTheScreenDoesNotExist() {
        val state = ScreenTestUtils.defaultState(mock(Screen::class.java))
        assertThat(state.indexOf(mock(Screen::class.java))).isEqualTo(-1)
    }

    @Test fun indexOfReturnsTheCorrectIndexOfAnExistingScreen() {
        val screen0 = mock(Screen::class.java)
        val screen1 = mock(Screen::class.java)
        val screen2 = mock(Screen::class.java)
        val state = ScreenTestUtils.defaultState(Arrays.asList(screen0, screen1, screen2))
        assertThat(state.indexOf(screen1)).isEqualTo(1)
    }

    @Test fun screenCountIsUpdatedWhenScreensAreAdded() {
        val state = ScreenTestUtils.defaultState(Arrays.asList(mock(Screen::class.java), mock(Screen::class.java)))
        assertThat(state.screenCount()).isEqualTo(2)
        state.addScreen(mock(Screen::class.java))
        assertThat(state.screenCount()).isEqualTo(3)
    }
}
