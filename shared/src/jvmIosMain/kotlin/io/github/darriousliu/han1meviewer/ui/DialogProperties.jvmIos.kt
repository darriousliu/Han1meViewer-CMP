package io.github.darriousliu.han1meviewer.ui

import androidx.compose.ui.window.DialogProperties

actual fun fullScreenDialogProperties() = DialogProperties(
    usePlatformDefaultWidth = false,
    usePlatformInsets = false
)