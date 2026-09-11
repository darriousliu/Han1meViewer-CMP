@file:Suppress("DEPRECATION")

package io.github.darriousliu.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@Composable
actual fun rememberReadClipboardText(): suspend () -> String? {
    val clipboard = LocalClipboardManager.current
    return remember(clipboard) { { clipboard.getText()?.text } }
}

@Composable
actual fun rememberCopyTextToClipboard(): (CharSequence) -> Unit {
    val clipboard = LocalClipboardManager.current
    return remember(clipboard) { { text -> clipboard.setText(AnnotatedString(text.toString())) } }
}
