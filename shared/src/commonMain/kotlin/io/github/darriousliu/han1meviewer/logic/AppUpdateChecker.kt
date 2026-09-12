package io.github.darriousliu.han1meviewer.logic

import io.github.darriousliu.utils.LogUtil
import io.github.darriousliu.han1meviewer.BuildConfig
import io.github.darriousliu.han1meviewer.logic.model.Announcement
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import io.github.darriousliu.han1meviewer.generated.resources.Res
import io.github.darriousliu.han1meviewer.generated.resources.update_announcement_title
import io.github.darriousliu.han1meviewer.logic.network.HClientSpec
import io.github.darriousliu.han1meviewer.logic.network.buildHttpClient
import kotlinx.coroutines.IO
import org.jetbrains.compose.resources.getString

@Serializable
data class AppUpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val downloadUrl: String,
    val updateDescription: String,
    val forceUpdate: Boolean,
)

data class AppUpdateCheckResult(
    val updateInfo: AppUpdateInfo? = null,
    val announcement: Announcement? = null,
)

sealed interface AppUpdateState {
    data object Checking : AppUpdateState
    data object NoUpdate : AppUpdateState
    data class Available(val info: AppUpdateInfo) : AppUpdateState
}

object AppUpdateChecker {
    private const val TAG = "AppUpdateChecker"

    private val client by lazy { buildHttpClient(HClientSpec.UPDATE) }

    suspend fun checkForUpdate(): AppUpdateCheckResult = withContext(Dispatchers.IO) {
        val cachedJson = SettingsRepository.current.cachedUpdateJson

        val request = runCatching { fetchUpdateManifest(client) }
            .onFailure {
                if (it is CancellationException) throw it
                LogUtil.e(TAG, "Failed to check CMP updates", it)
            }
        val manifest = if (request.isSuccess) {
            request.getOrNull().also {
                // 先校验内容再缓存；404 清空缓存，避免继续显示已不存在的发布。
                SettingsRepository.setCachedUpdateJson(it?.let(::encodeUpdateManifest))
            }
        } else {
            cachedJson?.let { content ->
                runCatching { decodeUpdateManifest(content) }
                    .onFailure { LogUtil.w(TAG, "Discarding incompatible update cache", it) }
                    .getOrNull()
                    .also { if (it == null) SettingsRepository.setCachedUpdateJson(null) }
            }
        }
        AppUpdateCheckResult(
            updateInfo = manifest?.toAvailableUpdate(
                currentVersionCode = BuildConfig.VERSION_CODE,
                ignoredVersionCode = SettingsRepository.current.ignoredVersionCode,
            ),
            announcement = manifest?.toAnnouncementOrNull(),
        )
    }

    suspend fun ignoreUpdate(versionCode: Int) = SettingsRepository.setIgnoredVersionCode(versionCode)

    private suspend fun AppUpdateManifest.toAnnouncementOrNull(): Announcement? {
        val content = announcement.trim()
        if (!isShowAnnouncement || content.isBlank()) return null
        return Announcement(
            title = getString(Res.string.update_announcement_title),
            content = content,
            isActive = true,
        )
    }
}
