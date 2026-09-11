package io.github.darriousliu.han1meviewer.util

import dev.nucleusframework.webview.web.NativeWebView
import io.github.darriousliu.han1meviewer.logic.network.HCookieJar
import java.net.CookieHandler
import java.net.CookieManager

// TAO 的 WKWebView / WebView2 / WebKitGTK 默认启用 DOM storage。
actual fun NativeWebView.enableDomStorage() = Unit

/** 新版 WebView 自带的 CookieManager 通过 TAO 主线程异步读取平台 Cookie 存储。 */
actual suspend fun readWebViewCookies(webView: NativeWebView?, url: String): String? = null

internal actual suspend fun clearPlatformCookies() {
    HCookieJar.cookieMap.clear()
    (CookieHandler.getDefault() as? CookieManager)?.cookieStore?.removeAll()
}
