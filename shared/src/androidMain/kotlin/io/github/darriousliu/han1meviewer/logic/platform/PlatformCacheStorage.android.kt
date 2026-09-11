package io.github.darriousliu.han1meviewer.logic.platform

import io.github.darriousliu.utils.applicationContext
import io.github.darriousliu.utils.folderSize

actual suspend fun cacheFolderSize(): Long = applicationContext.cacheDir.folderSize

actual fun cacheFolderSizeBlocking(): Long = applicationContext.cacheDir.folderSize

actual suspend fun clearCacheFolder(): Boolean =
    applicationContext.cacheDir?.deleteRecursively() == true
