package io.github.darriousliu.han1meviewer.logic.platform

actual val platformVideoCacheStore: VideoCacheStore
    get() = AndroidVideoCacheStore
