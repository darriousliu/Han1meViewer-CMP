package io.github.daisukikaffuchino.han1meviewer.ui.window

import androidx.compose.runtime.staticCompositionLocalOf
import dev.nucleusframework.application.NucleusWindow

/** 主窗口句柄用于播放页全屏；通过 Nucleus 公共接口控制 TAO 窗口。 */
val LocalDesktopWindow = staticCompositionLocalOf<NucleusWindow?> { null }
