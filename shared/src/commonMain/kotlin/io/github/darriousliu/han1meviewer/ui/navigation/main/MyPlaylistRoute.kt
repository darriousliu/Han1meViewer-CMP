package io.github.darriousliu.han1meviewer.ui.navigation.main

import androidx.compose.runtime.Composable
import io.github.darriousliu.han1meviewer.getHanimeShareText
import io.github.darriousliu.han1meviewer.ui.screen.home.myplaylist.PlaylistScreen
import io.github.darriousliu.han1meviewer.ui.viewmodel.MyPlayListViewModel
import io.github.darriousliu.utils.rememberCopyTextToClipboard
import io.github.darriousliu.utils.SonnerToast
import io.github.darriousliu.han1meviewer.generated.resources.Res
import io.github.darriousliu.han1meviewer.generated.resources.copy_to_clipboard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyPlaylistRouteScreen(
    onBack: () -> Unit,
    onNavigateToVideo: (String) -> Unit,
) {
    val viewModel: MyPlayListViewModel = koinViewModel()
    val copyTextToClipboard = rememberCopyTextToClipboard()
    PlaylistScreen(
        viewModel = viewModel,
        navigateBack = onBack,
        onClickItem = onNavigateToVideo,
        onLongClickItem = { videoCode, title ->
            copyTextToClipboard(getHanimeShareText(title, videoCode))
            SonnerToast.success(Res.string.copy_to_clipboard)
        },
    )
}
