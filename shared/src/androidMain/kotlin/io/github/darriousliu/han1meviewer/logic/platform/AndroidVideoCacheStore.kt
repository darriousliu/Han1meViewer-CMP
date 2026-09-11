package io.github.darriousliu.han1meviewer.logic.platform

import io.github.darriousliu.han1meviewer.HCacheManager
import io.github.darriousliu.han1meviewer.logic.model.HanimeVideo
import io.github.darriousliu.utils.application
import kotlinx.coroutines.flow.Flow

object AndroidVideoCacheStore : VideoCacheStore {
    override fun load(videoCode: String): Flow<HanimeVideo?> =
        HCacheManager.loadHanimeVideoInfo(application, videoCode)

    override suspend fun save(videoCode: String, info: HanimeVideo) =
        HCacheManager.saveHanimeVideoInfo(application, videoCode, info)
}
