package io.github.darriousliu.han1meviewer.util

import io.github.darriousliu.han1meviewer.HanimeApplication
import io.github.darriousliu.utils.applicationContext

actual fun switchLauncherIcon(alias: String) {
    (applicationContext as? HanimeApplication)?.switchLauncher(alias)
}

actual val isLauncherIconSwitchSupported: Boolean = true
