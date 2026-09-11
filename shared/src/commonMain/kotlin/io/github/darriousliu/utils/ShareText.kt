package io.github.darriousliu.utils

import androidx.compose.runtime.Composable

/** 调起系统分享。 */
@Composable
expect fun rememberShareText(): (CharSequence, CharSequence?) -> Unit
