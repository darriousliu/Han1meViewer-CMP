package io.github.darriousliu.han1meviewer.logic.platform

import io.github.darriousliu.han1meviewer.logic.model.HanimeVideo
import kotlinx.coroutines.flow.Flow

interface VideoCacheStore {
    fun load(videoCode: String): Flow<HanimeVideo?>
    suspend fun save(videoCode: String, info: HanimeVideo)
}
