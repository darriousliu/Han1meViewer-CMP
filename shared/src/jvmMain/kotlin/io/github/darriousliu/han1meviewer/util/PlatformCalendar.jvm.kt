package io.github.darriousliu.han1meviewer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.darriousliu.han1meviewer.generated.resources.Res
import io.github.darriousliu.han1meviewer.generated.resources.no_calendar_app
import io.github.darriousliu.utils.SonnerToast
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.io.File

@Composable
actual fun rememberAddCalendarEvent(): (LocalDate) -> Unit {
    val scope = rememberCoroutineScope()
    return { date ->
        scope.launch {
            val invite = runCatching { buildCheckInInvite(date) }.getOrNull()
            val opened = invite != null && runCatching {
                openDesktopFile(File(invite.path))
            }.isSuccess
            if (!opened) SonnerToast.warning(Res.string.no_calendar_app)
        }
    }
}
