package io.github.darriousliu.han1meviewer.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.nucleusframework.application.NucleusWindow
import io.github.darriousliu.han1meviewer.ui.window.LocalDesktopWindow

private class DesktopPlayerHost(private val window: NucleusWindow?) : PlayerHostPlatform {

    /** 退出全屏要还原成进全屏之前的摆放方式，不能一律回 Floating。 */
    private var maximizedBeforeFullscreen: Boolean? = null

    override fun setFullscreen(enabled: Boolean, preferPortrait: Boolean) {
        val window = window ?: return
        if (enabled) {
            if (maximizedBeforeFullscreen == null) {
                maximizedBeforeFullscreen = window.isMaximized
            }
            window.setFullscreen(true)
        } else {
            window.setFullscreen(false)
            maximizedBeforeFullscreen?.let(window::setMaximized)
            maximizedBeforeFullscreen = null
        }
    }

    // 桌面没有「应用改屏幕亮度」这回事，手势整条隐藏，别再显示一个动不了的百分比
    override val supportsBrightness: Boolean = false
    override fun currentBrightness(): Float = 1f
    override fun overrideBrightness(value: Float?) = Unit
    override fun savedBrightness(): Float? = null

    // 桌面端明确不做画中画：要另开一个置顶小窗、把渲染面搬过去，收益远不及成本。
    // 想边看边干活直接把主窗口缩小就是了。设置里的开关也按平台隐藏了。
    override fun isInPipMode(): Boolean = false
}

@Composable
actual fun rememberPlayerHostPlatform(): PlayerHostPlatform {
    val window = LocalDesktopWindow.current
    return remember(window) { DesktopPlayerHost(window) }
}

@Composable
actual fun PlayerWindowEffect(restoreLightSystemBars: Boolean) {
}

// 桌面没有重力感应
@Composable
actual fun PlayerSensorOrientationEffect(
    enabled: Boolean,
    onLandscapeChange: (Boolean) -> Unit,
) {
}
