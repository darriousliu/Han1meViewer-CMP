package io.github.daisukikaffuchino.han1meviewer.util

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

/** 桌面注入原生窗口 owner，移动端沿用 FileKit 的默认平台设置。 */
val LocalFileDialogSettings = staticCompositionLocalOf { FileKitDialogSettings.createDefault() }
