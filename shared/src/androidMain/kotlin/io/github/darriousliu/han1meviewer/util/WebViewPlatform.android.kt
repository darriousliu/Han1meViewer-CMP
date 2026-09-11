package io.github.darriousliu.han1meviewer.util

import android.webkit.CookieManager
import io.github.darriousliu.han1meviewer.logic.network.HCookieJar
import dev.nucleusframework.webview.web.NativeWebView

actual fun NativeWebView.enableDomStorage() {
    settings.domStorageEnabled = true
}

// 库自带的 CookieManager 在这一端是好用的，不用另外取
actual suspend fun readWebViewCookies(webView: NativeWebView?, url: String): String? = null

internal actual suspend fun clearPlatformCookies() {
    HCookieJar.cookieMap.clear()
    CookieManager.getInstance().removeAllCookies(null)
}
