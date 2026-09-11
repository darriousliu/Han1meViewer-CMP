package io.github.darriousliu.han1meviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.aotTraining
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.window.NucleusDecoratedWindowTheme
import dev.nucleusframework.window.TitleBar
import dev.nucleusframework.window.WindowScaffold
import dev.nucleusframework.window.styling.LocalTitleBarStyle
import io.github.darriousliu.han1meviewer.di.initAppOnce
import io.github.darriousliu.han1meviewer.ui.crash.installUncaughtExceptionHandler
import io.github.darriousliu.han1meviewer.ui.navigation.main.normalizeDesktopLaunchArguments
import io.github.darriousliu.han1meviewer.ui.navigation.main.postSystemDeepLink
import io.github.darriousliu.han1meviewer.ui.player.prewarmMpvRuntime
import io.github.darriousliu.han1meviewer.ui.screen.crash.CrashScreenHost
import io.github.darriousliu.han1meviewer.ui.window.LocalDesktopWindow
import io.github.darriousliu.han1meviewer.ui.window.ProvideDesktopFileDialogs
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration.Companion.seconds

private val crashFlow = MutableStateFlow<Throwable?>(null)
private const val APP_NAME = "Han1meViewer"
private val MIN_WINDOW_SIZE = DpSize(900.dp, 640.dp)
private val INITIAL_WINDOW_SIZE = DpSize(1024.dp, 720.dp)

fun main(args: Array<String>) {
    installUncaughtExceptionHandler { crashFlow.value = it }
    FileKit.init(APP_NAME)
    initAppOnce()
    prewarmMpvRuntime()

    nucleusApplication(args = normalizeDesktopLaunchArguments(args), backend = NucleusBackend.Tao) {
        aotTraining(duration = 45.seconds)
        onDeepLink(::postSystemDeepLink)
        val crash by crashFlow.collectAsState()
        val state = rememberWindowState(size = INITIAL_WINDOW_SIZE)
        NucleusDecoratedWindowTheme(isDark = isSystemInDarkTheme()) {
            DecoratedWindow(
                onCloseRequest = ::exitApplication,
                state = state,
                minimumSize = MIN_WINDOW_SIZE,
                title = APP_NAME,
            ) {
                LaunchedEffect(nucleusWindow) {
                    nucleusWindow.toFront()
                    nucleusWindow.requestFocus()
                }
                CompositionLocalProvider(LocalDesktopWindow provides nucleusWindow) {
                    ProvideDesktopFileDialogs(nucleusWindow) {
                        WindowScaffold(
                            titleBar = if (state.placement == WindowPlacement.Fullscreen) null else {
                                {
                                    TitleBar {
                                        BasicText(
                                            APP_NAME,
                                            style = TextStyle(color = LocalTitleBarStyle.current.colors.content),
                                        )
                                    }
                                }
                            },
                        ) { padding ->
                            Box(Modifier.fillMaxSize().padding(padding)) {
                                val throwable = crash
                                if (throwable == null) App()
                                else CrashScreenHost(throwable, ::exitApplication)
                            }
                        }
                    }
                }
            }
        }
    }
}
