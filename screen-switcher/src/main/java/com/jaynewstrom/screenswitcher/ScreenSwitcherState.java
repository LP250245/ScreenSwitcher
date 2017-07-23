package com.jaynewstrom.screenswitcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.jaynewstrom.screenswitcher.Preconditions.checkArgument;
import static com.jaynewstrom.screenswitcher.Preconditions.checkNotNull;
import static com.jaynewstrom.screenswitcher.Utils.checkScreen;

/**
 * This object is designed to persist in memory when an {@link android.app.Activity} configuration change occurs.
 */
public final class ScreenSwitcherState {

    private final ScreenLifecycleListener lifecycleListener;
    private final List<Screen> screens;
    private final Map<Screen, ScreenPopListener> popListenerMap;

    /**
     * @param lifecycleListener The {@link ScreenLifecycleListener} to be notified of what's happening on the internals.
     * @param screens           The initial screens that the {@link ScreenSwitcher} should show.
     * @throws IllegalArgumentException if screens is empty
     * @throws IllegalArgumentException if the same screen is passed
     */
    public ScreenSwitcherState(ScreenLifecycleListener lifecycleListener, List<Screen> screens) {
        this.lifecycleListener = checkNotNull(lifecycleListener, "lifecycleListener == null");
        checkNotNull(screens, "screens == null");
        checkArgument(!screens.isEmpty(), "screens must contain at least one screen");
        this.screens = new ArrayList<>(screens.size());
        for (int i = 0, size = screens.size(); i < size; i++) {
            addScreen(screens.get(i));
        }
        popListenerMap = new LinkedHashMap<>();
    }

    /**
     * The pop listener will be automatically unregistered when the {@link Screen} is popped.
     * See documentation for {@link ScreenPopListener} for what objects should be registered.
     *
     * @param screen      The {@link Screen} to be notified of before it gets popped.
     * @param popListener The {@link ScreenPopListener} to call when the {@link Screen} is trying to be popped.
     */
    public void registerPopListener(Screen screen, ScreenPopListener popListener) {
        checkNotNull(screen, "screen == null");
        checkNotNull(popListener, "popListener == null");
        popListenerMap.put(screen, popListener);
    }

    /**
     * @return the index of the screen if it exists, or -1 if it doesn't.
     */
    public int indexOf(Screen screen) {
        checkNotNull(screen, "screen == null");
        return screens.indexOf(screen);
    }

    /**
     * @return number of screens that will be used in any associated {@link ScreenSwitcher}.
     */
    public int screenCount() {
        return screens.size();
    }

    List<Screen> getScreens() {
        return screens;
    }

    void addScreen(Screen screen) {
        checkScreen(this, screen);
        screens.add(screen);
        lifecycleListener.onScreenAdded(screen);
    }

    void removeScreen(Screen screen) {
        screens.remove(screen);
        lifecycleListener.onScreenRemoved(screen);
    }

    boolean handlesPop(Screen screen) {
        ScreenPopListener popListener = popListenerMap.get(screen);
        boolean handlesPop = popListener != null && popListener.onScreenPop(screen);
        if (!handlesPop) {
            popListenerMap.remove(screen);
        }
        return handlesPop;
    }

    ScreenLifecycleListener lifecycleListener() {
        return lifecycleListener;
    }
}
