package io.github.darriousliu.han1meviewer.ui.screen.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.darriousliu.han1meviewer.ui.player.MediampPlaybackEngine
import io.github.darriousliu.han1meviewer.ui.player.PlaybackEngine
import org.openani.mediamp.mpv.MpvMediampPlayer
import org.openani.mediamp.mpv.tao.TaoMpvMediampPlayerSurface

/**
 * 显式使用 TAO 渲染面，由 mediamp 的可选适配模块管理 GPU context 和帧生命周期。
 * 画面的信箱化仍由 mpv 按 AspectRatioMode.FIT 完成。
 */
@Composable
actual fun VideoRenderSurface(
    engine: PlaybackEngine,
    modifier: Modifier,
) {
    // 预览/占位引擎不是这一种时照旧铺黑底，别让播放页整块空掉
    val player = (engine as? MediampPlaybackEngine)?.player as? MpvMediampPlayer
    if (player == null) {
        Box(modifier = modifier.fillMaxSize().background(Color.Black))
        return
    }
    TaoMpvMediampPlayerSurface(player, modifier = modifier.fillMaxSize())
}
