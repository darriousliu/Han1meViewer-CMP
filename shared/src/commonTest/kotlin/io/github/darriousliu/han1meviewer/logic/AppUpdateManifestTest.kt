package io.github.darriousliu.han1meviewer.logic

import io.github.darriousliu.han1meviewer.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppUpdateManifestTest {
    private val releaseJson = """
        {
          "schemaVersion": 1,
          "repository": "${BuildConfig.GITHUB_REPOSITORY}",
          "versionName": "26.3.3",
          "versionCode": 260806,
          "downloadUrl": "https://github.com/${BuildConfig.GITHUB_REPOSITORY}/releases/tag/v26.3.3",
          "updateDescription": "CMP release notes",
          "forceUpdate": false,
          "isShowAnnouncement": true,
          "announcement": "CMP announcement"
        }
    """.trimIndent()

    @Test
    fun publishedReleaseCreatesAnUpdateAndPreservesAnnouncement() {
        val manifest = decodeUpdateManifest(releaseJson)
        val update = assertNotNull(manifest.toAvailableUpdate(260805, -1))
        assertEquals("26.3.3", update.versionName)
        assertEquals(260806, update.versionCode)
        assertEquals("CMP release notes", update.updateDescription)
        assertFalse(update.forceUpdate)
        assertTrue(manifest.isShowAnnouncement)
        assertEquals("CMP announcement", manifest.announcement)
    }

    @Test
    fun ignoresSameOlderAndUserDismissedVersions() {
        val manifest = decodeUpdateManifest(releaseJson)
        assertNull(manifest.toAvailableUpdate(260806, -1))
        assertNull(manifest.toAvailableUpdate(260807, -1))
        assertNull(manifest.toAvailableUpdate(260805, 260806))
        assertNotNull(manifest.copy(forceUpdate = true).toAvailableUpdate(260805, 260806))
        assertNull(manifest.copy(forceUpdate = true).toAvailableUpdate(260807, -1))
    }

    @Test
    fun rejectsLegacyAndForeignCaches() {
        assertFails {
            decodeUpdateManifest("""{"versionName":"99.0.0","versionCode":999999,"downloadUrl":"https://example.com","isShowAnnouncement":true}""")
        }
        assertFailsWith<IllegalArgumentException> {
            decodeUpdateManifest(releaseJson.replace(BuildConfig.GITHUB_REPOSITORY, "another/project"))
        }
        assertFailsWith<IllegalArgumentException> {
            decodeUpdateManifest(releaseJson.replace("https://github.com/", "https://example.com/"))
        }
    }

    @Test
    fun rejectsUnsupportedSchemaInvalidVersionAndWrongReleasePage() {
        for (invalid in listOf(
            releaseJson.replace("\"schemaVersion\": 1", "\"schemaVersion\": 2"),
            releaseJson.replace("\"versionCode\": 260806", "\"versionCode\": 0"),
            releaseJson.replace("\"versionName\": \"26.3.3\"", "\"versionName\": \"invalid\""),
            releaseJson.replace("/tag/v26.3.3", "/tag/v26.3.2"),
        )) {
            assertFailsWith<IllegalArgumentException> { decodeUpdateManifest(invalid) }
        }
    }

    @Test
    fun fetchesTheCmpReleaseAssetWithoutTheLegacyReferer() = runTest {
        val client = mockClient(HttpStatusCode.OK, releaseJson)
        try {
            assertEquals(260806, assertNotNull(fetchUpdateManifest(client)).versionCode)
        } finally {
            client.close()
        }
    }

    @Test
    fun missingFirstReleaseMeansNoUpdate() = runTest {
        val client = mockClient(HttpStatusCode.NotFound, "Not Found")
        try {
            assertNull(fetchUpdateManifest(client))
        } finally {
            client.close()
        }
    }

    @Test
    fun serverErrorsAndMalformedPayloadsCannotReplaceAValidCache() = runTest {
        for ((status, body) in listOf(
            HttpStatusCode.ServiceUnavailable to "Unavailable",
            HttpStatusCode.OK to "<html>bad gateway</html>",
        )) {
            val client = mockClient(status, body)
            try {
                assertFails { fetchUpdateManifest(client) }
            } finally {
                client.close()
            }
        }
    }

    private fun mockClient(status: HttpStatusCode, body: String) = HttpClient(MockEngine { request ->
        assertEquals(BuildConfig.UPDATE_MANIFEST_URL, request.url.toString())
        assertNull(request.headers[HttpHeaders.Referrer])
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
    })
}
