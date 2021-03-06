package com.jaynewstrom.screenswitcher

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ScreenSwitcherFactoryTest {
    private lateinit var activity: Activity
    private lateinit var view: ViewGroup
    private lateinit var state: ScreenSwitcherState
    private lateinit var finishHandler: ScreenSwitcherFinishHandler

    @Before fun setup() {
        activity = mock(Activity::class.java)
        val hostView = mock(ViewGroup::class.java)
        `when`<View>(activity.findViewById<View>(android.R.id.content)).thenReturn(hostView)
        `when`(hostView.context).thenReturn(activity)
        view = mock(ViewGroup::class.java)
        `when`<Context>(view.context).thenReturn(activity)
        val screen = mock(Screen::class.java)
        ScreenTestUtils.mockCreateView(screen)
        state = ScreenTestUtils.defaultState(screen)
        finishHandler = mock(ScreenSwitcherFinishHandler::class.java)
    }

    @Test fun activityScreenSwitcherRejectsStateWithNoScreens() {
        try {
            state.removeScreen(state.screens[0])
            ScreenSwitcherFactory.activityScreenSwitcher(activity, state, finishHandler)
            fail()
        } catch (expected: IllegalArgumentException) {
            assertThat(expected).hasMessage("state needs screens in order to initialize a ScreenSwitcher")
        }
    }

    @Test fun activityScreenSwitcherIsCreated() {
        val screenSwitcher = ScreenSwitcherFactory.activityScreenSwitcher(activity, state, finishHandler)
        assertThat(screenSwitcher).isNotNull
    }

    @Test fun viewScreenSwitcherRejectsStateWithNoScreens() {
        try {
            state.removeScreen(state.screens[0])
            ScreenSwitcherFactory.viewScreenSwitcher(view, state, finishHandler)
            fail()
        } catch (expected: IllegalArgumentException) {
            assertThat(expected).hasMessage("state needs screens in order to initialize a ScreenSwitcher")
        }
    }

    @Test fun viewScreenSwitcherIsCreated() {
        val screenSwitcher = ScreenSwitcherFactory.viewScreenSwitcher(view, state, finishHandler)
        assertThat(screenSwitcher).isNotNull
    }
}
