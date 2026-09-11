package io.github.darriousliu.han1meviewer.ui.navigation.main

import java.io.File
import java.net.URI

/** 命令行支持应用深链、file URI 和裸文件路径；TAO Apple Events 共用同一解析。 */
fun postDeepLinkFromArguments(args: Array<String>) {
    args.firstNotNullOfOrNull(::parseLaunchArgument)?.let(DeepLinkBus::post)
}

/** 单实例转发只识别 URI，先把现有本地文件路径转换成 file URI。 */
fun normalizeDesktopLaunchArguments(args: Array<String>): Array<String> = args.map { arg ->
    val file = File(arg)
    if (file.isFile) file.toPath().toUri().toString() else arg
}.toTypedArray()

fun postSystemDeepLink(uri: URI) {
    parseLaunchArgument(uri.toString())?.let(DeepLinkBus::post)
}

private fun parseLaunchArgument(arg: String): DeepLinkTarget? {
    parseDeepLink(arg)?.let { return it }
    val file = runCatching {
        if (arg.startsWith("file:", ignoreCase = true)) File(URI(arg)) else File(arg)
    }.getOrNull() ?: return null
    if (!file.isFile) return null
    return DeepLinkTarget.Video(LOCAL_VIDEO_CODE, file.toPath().toUri().toString())
}
