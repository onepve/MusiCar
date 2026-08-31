# CarMedia 车机媒体 (蓝牙音乐)

> 吉利车机 (E02 / IHU516) 专属方向盘方控与媒体中心应用。  
> 软件原作者：**米小江**（QQ群：`917274937`）

---

## 核心特性

1. **方向盘按键深度拦截与映射**
   - 监听系统底层按键事件（`InputManager` / `CAR.INPUT`）；
   - 支持方向盘物理按键切歌（上一首 / 下一首 / 播放暂停）；
   - 支持自定义按键动作（打开主界面、打开播放源、打开自定义第三方 APP；支持单击/双击录制）。

2. **通用媒体控制与全平台歌词解析**
   - 基于 `MediaController.getTransportControls()` 深度控制目标播放器的 `MediaSession`；
   - 原生兼容 QQ音乐车机版、网易云音乐车机版、酷狗、酷我等所有标准车机音频应用；
   - **全新 1.3.4 特性**：新增汽水音乐（Luna / 抖音）歌词解析支持与网络优化。

3. **精致 UI 与多主题支持**
   - 主界面基于响应式 H5/CSS 视口自适应打造，完美适配车机 1920x720 宽屏；
   - 内置四大主题皮肤：**卡通动漫**、**复古唱片**、**赛博朋克**、**极光幻境**；
   - 支持外观模式：**日间模式**、**夜间模式**、**跟随车机系统（大灯/EAS）**。

4. **歌词与仪表盘 EAS 推送**
   - 支持在线歌词解析与大屏滚动；
   - 支持吉利车机 EAS 框架向仪表盘推送当前播放曲目与歌词。

---

## 架构与组件

- **包名**：`com.ecarx.carmedia`
- **当前版本**：`1.3.4`（versionCode `8`）
- **目标平台**：Android 9.0 (API 28) ~ Android 14+ (ARM64 / ARMv7)
- **主要组件**：
  - `MainActivity`：主控制台（WebView + JsBridge）
  - `CarMediaService`：后台媒体监控与按键桥接服务
  - `MediaNotificationListener`：通知与播放状态监听器
  - `BootReceiver`：开机自启广播接收器
  - `UiHotUpdate`：UI 界面热更新支持
  - `SodaMusicLyricFetcher`：汽水音乐歌词抓取模块

---

## 更新机制说明

- **全量 APK 整包更新接口**：`https://jqt.czrui.cn/CarMedia.txt`（分发 APK 直链：`https://jqt.czrui.cn/CarMedia-v1.3.4-signed.apk`）
- **前端 UI 热更新仓库**：`https://github.com/q361955274/carmedia`
