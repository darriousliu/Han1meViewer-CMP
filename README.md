# 🚫 请不要在任何公开平台宣传本软件

本软件不接受任何形式的公开宣传。若出现公开宣传、搬运或引流，仓库维护者可能随时归档或隐藏仓库，并删除已编译的发行版。

# 🌸 Han1meViewer+ · CMP

🔞 **R18 警告：未满 18 岁禁止下载和使用。**

Han1meViewer+ 是用于浏览、搜索、播放和管理 hanime 相关视频内容的客户端。CMP 版本基于 Kotlin Multiplatform 与 Compose Multiplatform，将主要界面和业务逻辑共享到 **Android、iOS、Windows 和 macOS**，延续 Material 3 界面、播放管理和大屏布局。

本应用没有任何官方网站。[本仓库 GitHub Releases](https://github.com/darriousliu/Han1meViewer1/releases) 是唯一的正式版下载及更新渠道。各平台实际提供的版本以 Release 附件和说明为准。

**上游[原仓库](https://github.com/misaka10032w/Han1meViewer)已回归，本项目的诞生离不开在此之前的所有贡献者！**

还请大家好好看片，不要去打击用爱发电的开发者们的积极性。

## 📦 下载与安装

在 Release 页展开 **Assets**，按设备选择安装包：

| 平台 | 当前发布范围 | 选择的附件 | 安装与更新 |
| --- | --- | --- | --- |
| Android | Android 10+，`arm64-v8a` | `*-android-arm64-v8a.apk` | 下载 APK 安装；覆盖升级需包名和签名一致 |
| iOS / iPadOS | iOS 15.0+，arm64 真机 | `*-ios.ipa` | 需自行重签后侧载；当前不通过 App Store / TestFlight 分发 |
| Windows | x64 | `*-win-x64.exe` 或 `*-win-x64.zip` | EXE 为安装版，支持应用内更新；ZIP 为免安装版，需手动更新 |
| macOS | Apple 芯片，arm64 | `*-mac-arm64.dmg` | 将应用放入「应用程序」；安装版支持应用内更新 |

- iOS 附件采用临时 ad-hoc 自签名，没有 Apple 开发者分发签名，需重签侧载。
- macOS 当前产物未公证。遇到系统拦截时，按该版本的 Release 安装说明处理。
- macOS 的 `*-mac-arm64.zip` 用于自动更新；`latest*.yml` 和 `*.blockmap` 是更新元数据，日常安装无需下载。
- 当前不提供 Linux、Intel Mac 或 Windows ARM 的正式安装包。Android APK 不支持 32 位 ARM 和 x86 设备。

### 从 Android 旧版升级

CMP 版的 Android 应用标识为 `io.github.darriousliu.han1meviewer`，与原版 Android 使用不同包名，不能覆盖安装，可以并存。迁移旧数据时，请先在原版设置中导出备份，再在 CMP 版中导入。后续 CMP 版覆盖升级需保持包名和发布签名一致。Debug 版带 `.debug` 包名后缀，与正式版分开安装。

备份包含设置和部分本地记录，不打包视频文件，也不包含登录 Cookie。换设备或跨平台恢复后，需要重新登录、选择下载目录，并按需迁移或扫描已有视频文件。

## ✨ 主要功能

- 首页浏览、关键词与高级搜索、搜索历史、预览及 Getchu 内容入口。
- 视频详情、系列与推荐、评论、收藏、稍后观看、播放列表、订阅和观看历史。
- 清晰度切换、倍速、播放进度恢复、本地视频、关键 H 帧与播放手势。
- 下载队列、暂停与恢复、下载分组和分类、目录选择、已有文件扫描与导入。
- 手机、横屏、平板与桌面布局；播放页提供经典和分栏两种大屏样式。
- WebView 登录、手动 Cookie 导入、Cloudflare 验证和网络设置。
- 主题、语言、备份恢复与签到日历；隐私和系统功能按平台提供。

### 平台差异

| 功能 | Android | iOS / iPadOS | Windows / macOS |
| --- | --- | --- | --- |
| 播放内核 | ExoPlayer / MediaPlayer / mpv | AVKit / AVPlayer | libmpv |
| Anime4K 超分 | mpv 内核可用 | 不提供 | 不提供 |
| 画中画 | 支持 | 支持，受系统和播放状态影响 | 不提供 |
| 投屏 | Google Cast | AirPlay | 不提供 |
| 下载限速 | 支持 | 不提供；后台下载由系统调度 | 支持 |
| 自定义 DNS / DoH | 支持 | 不提供 | 支持 |
| 生物识别应用锁 | 支持 | 支持 | 尚未实现 |
| 防截屏、动态取色、签到小组件 | 支持 | 不提供 | 不提供 |

移动端不提供后台纯音频播放，离开应用后继续播放主要通过画中画实现。各平台功能仍需结合系统权限、设备能力和对应版本说明使用。

## 📱 应用截图

以下为已有的 Android 手机、横屏与平板界面截图，其他平台的界面以对应版本实机为准。

### 手机端

| 首页 | 播放页 | 播放设置 |
| --- | --- | --- |
| <img src="image/screenshots/phone_home.jpg" alt="手机端首页" width="240"> | <img src="image/screenshots/phone_player_1.jpg" alt="手机端播放页" width="240"> | <img src="image/screenshots/phone_player_2.jpg" alt="手机端播放设置" width="240"> |
| 设置 | Getchu | |
| <img src="image/screenshots/phone_settings.jpg" alt="手机端设置" width="240"> | <img src="image/screenshots/phone_getchu.jpg" alt="手机端 Getchu" width="240"> | |

### 横屏与平板端

| 首页 | 列表 | 搜索 |
| --- | --- | --- |
| ![平板端首页](image/screenshots/tablet_home.png) | ![平板端列表](image/screenshots/tablet_list.png) | ![平板端搜索](image/screenshots/tablet_search.png) |
| 播放页 | 播放详情 | 下载管理 |
| ![平板端播放页](image/screenshots/tablet_palyer_1.png) | ![平板端播放详情](image/screenshots/tablet_player_2.png) | ![平板端下载管理](image/screenshots/tablet_download.png) |

## 🛠 开发与贡献

当前工程由 `:shared` 共享模块、`:app` Android 壳、`:desktopApp` 桌面壳和 `iosApp` Xcode 工程组成。

- [技术文档](README_TECH.md)：源集、架构、开发入口与验证命令。
- [正式版构建行动指南](正式版构建行动指南.md)：环境、版本、签名、四端打包与发布顺序。
- [跨平台功能支持矩阵](跨平台功能支持矩阵.md)：迁移期的平台实现记录；具体行为以当前代码为准。
- [共享关键 H 帧说明](shared/src/commonMain/composeResources/files/h_keyframes/README.md)：数据格式与贡献方法。

共享代码改动应检查 Android、JVM 和 iOS 受影响的编译目标；中间源集改动还需检查 metadata。常用命令见技术文档。修改列表与分页时检查重复 key；修改播放、下载、账号、Cookie、Cloudflare 或更新逻辑时，请说明验证平台、步骤和结果。

## 📄 许可证

- 本项目作为包含 GPLv3 派生代码的整体，按 GNU GPLv3 发布。
- 项目包含来自 [MomoQR](https://github.com/daisukiKaffuChino/MomoQR) 的代码，归属作者 daisukiKaffuChino，并遵循 GPLv3。
- 原项目 Yenaly 的遗留归属和 MomoQR 归属见 [NOTICE](NOTICE)，Apache-2.0 许可证文本见 [LICENSE-APACHE](LICENSE-APACHE)；完整许可证说明见 [LICENSE](LICENSE)。
