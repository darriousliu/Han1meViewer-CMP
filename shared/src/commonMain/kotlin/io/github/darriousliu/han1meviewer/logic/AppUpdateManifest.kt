package io.github.darriousliu.han1meviewer.logic

import io.github.darriousliu.han1meviewer.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** 随 CMP Release 发布的更新通知，repository 同时用于隔离旧更新源的缓存。 */
@Serializable
internal data class AppUpdateManifest(
    val repository: String,
    val versionName: String,
    val versionCode: Int,
    val downloadUrl: String,
    val schemaVersion: Int = 1,
    val updateDescription: String = "",
    val forceUpdate: Boolean = false,
    val isShowAnnouncement: Boolean = false,
    val announcement: String = "",
)

private val updateManifestJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal fun decodeUpdateManifest(content: String): AppUpdateManifest =
    updateManifestJson.decodeFromString<AppUpdateManifest>(content).also { manifest ->
        require(manifest.schemaVersion == 1) { "Unsupported update manifest schema" }
        require(manifest.repository == BuildConfig.GITHUB_REPOSITORY) {
            "Update manifest belongs to another repository"
        }
        require(manifest.versionCode > 0 && manifest.versionName.matches(Regex("[0-9]+\\.[0-9]+\\.[0-9]+"))) {
            "Invalid update version"
        }
        require(manifest.downloadUrl == "https://github.com/${BuildConfig.GITHUB_REPOSITORY}/releases/tag/v${manifest.versionName}") {
            "Update download URL must point to the matching CMP release"
        }
    }

internal fun encodeUpdateManifest(manifest: AppUpdateManifest): String =
    updateManifestJson.encodeToString(manifest)

internal suspend fun fetchUpdateManifest(client: HttpClient): AppUpdateManifest? {
    val response = client.get(BuildConfig.UPDATE_MANIFEST_URL) {
        header(HttpHeaders.Accept, "application/json")
    }
    // 首次公开 Release 之前没有此附件；这是正常的“暂无更新”，同时应清除旧缓存。
    if (response.status == HttpStatusCode.NotFound) return null
    check(response.status.isSuccess()) {
        "Update check failed with HTTP ${response.status.value}"
    }
    return decodeUpdateManifest(response.bodyAsText())
}

internal fun AppUpdateManifest.toAvailableUpdate(
    currentVersionCode: Int,
    ignoredVersionCode: Int,
): AppUpdateInfo? = AppUpdateInfo(
    versionName = versionName,
    versionCode = versionCode,
    downloadUrl = downloadUrl,
    updateDescription = updateDescription,
    forceUpdate = forceUpdate,
).takeIf {
    it.versionCode > currentVersionCode &&
        (it.forceUpdate || it.versionCode != ignoredVersionCode)
}
