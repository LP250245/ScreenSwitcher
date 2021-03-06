package screenswitchersample.core.screen

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.jaynewstrom.concrete.Concrete
import com.jaynewstrom.concrete.ConcreteBlock
import com.jaynewstrom.concrete.ConcreteWall
import com.jaynewstrom.screenswitcher.Screen
import com.jaynewstrom.screenswitcher.ScreenSwitcherState
import screenswitchersample.core.components.PassthroughComponent
import screenswitchersample.core.leak.LeakWatcher
import screenswitchersample.core.view.inflate

private const val LEAK_WATCHER_DESCRIPTION = "Screen Has Been Removed"

abstract class BaseScreen<C> : Screen {
    private var state: BaseScreenState<C>? = null

    protected abstract fun createWallManager(screenSwitcherState: ScreenSwitcherState): ScreenWallManager<C>

    @LayoutRes protected abstract fun layoutId(): Int

    protected abstract fun bindView(view: View, component: C)

    final override fun createView(hostView: ViewGroup, screenSwitcherState: ScreenSwitcherState): View {
        val state = state
        val screenWall: ConcreteWall<C>
        if (state == null) {
            val parentWall = Concrete.findWall<ConcreteWall<PassthroughComponent>>(hostView.context)
            val wallManager = createWallManager(screenSwitcherState)
            screenWall = wallManager.createScreenWall(parentWall)
            val leakWatcher = parentWall.component.leakWatcher
            this.state = BaseScreenState(screenWall, leakWatcher, wallManager)
        } else {
            screenWall = state.screenWall
        }
        return hostView.inflate(layoutId(), context = screenWall.createContext(hostView.context))
    }

    final override fun bindView(view: View) {
        bindView(view, state!!.screenWall.component)
    }

    final override fun destroyScreen(associatedView: View) {
        val state = state!!
        val component = state.screenWall.component
        state.screenWall.destroy()
        state.leakWatcher.watch(this, LEAK_WATCHER_DESCRIPTION)
        state.leakWatcher.watch(associatedView, LEAK_WATCHER_DESCRIPTION)
        state.leakWatcher.watch(component, LEAK_WATCHER_DESCRIPTION)
    }

    override fun transition(): Screen.Transition = DefaultScreenTransition
}

private data class BaseScreenState<C>(
    val screenWall: ConcreteWall<C>,
    val leakWatcher: LeakWatcher,
    val wallManager: ScreenWallManager<C>
)

interface ScreenWallManager<C> {
    fun createScreenWall(parentWall: ConcreteWall<PassthroughComponent>): ConcreteWall<C>
}

class DefaultScreenWallManager<C>(
    private val blockFactory: (parentComponent: PassthroughComponent) -> ConcreteBlock<C>,
    private val initializationAction: ((wall: ConcreteWall<C>) -> Unit)? = null
) : ScreenWallManager<C> {
    override fun createScreenWall(parentWall: ConcreteWall<PassthroughComponent>): ConcreteWall<C> {
        return parentWall.stack(blockFactory(parentWall.component), initializationAction)
    }
}
