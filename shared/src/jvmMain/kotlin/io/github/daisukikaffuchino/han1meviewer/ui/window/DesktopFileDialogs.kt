package io.github.daisukikaffuchino.han1meviewer.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import dev.nucleusframework.application.NucleusWindow
import dev.nucleusframework.window.tao.XdgPortalParent
import io.github.daisukikaffuchino.han1meviewer.util.LocalFileDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitDialogParent
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProvideDesktopFileDialogs(window: NucleusWindow, content: @Composable () -> Unit) {
    val tao = window.unsafe.taoWindow
    val portal by produceState<XdgPortalParent?>(initialValue = null, key1 = tao) {
        var acquired: XdgPortalParent? = null
        try {
            withContext(Dispatchers.IO) {
                acquired = tao?.xdgPortalParent()
            }
            value = acquired
            awaitDispose { (acquired as? XdgPortalParent.Wayland)?.close() }
        } catch (failure: Throwable) {
            (acquired as? XdgPortalParent.Wayland)?.close()
            throw failure
        }
    }
    val settings = remember(tao, portal) {
        val currentPortal = portal
        val parent = when {
            System.getProperty("os.name").startsWith("Windows", ignoreCase = true) ->
                tao?.nativeHandle?.takeIf { it != 0L }?.let(FileKitDialogParent::windows)
            currentPortal is XdgPortalParent.X11 -> FileKitDialogParent.x11(currentPortal.xid)
            currentPortal is XdgPortalParent.Wayland -> FileKitDialogParent.wayland(currentPortal.handle)
            // FileKit 0.15 的 macOS native panel 不接受 NSWindow parent，使用独立模态面板。
            else -> null
        }
        FileKitDialogSettings(parent = parent)
    }
    CompositionLocalProvider(LocalFileDialogSettings provides settings, content = content)
}
