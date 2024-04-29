package com.xpdustry.toxopid.extension

import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec

public fun MindustryExec.configureServer() {
    classpath(project.tasks.named(GithubAssetDownload.MINDUSTRY_SERVER_DOWNLOAD_TASK_NAME))
    mainClass.convention("mindustry.server.ServerLauncher")
    modsPath.convention("./config/mods")
    standardInput = System.`in`
}

public fun MindustryExec.configureDesktop() {
    classpath(project.tasks.named(GithubAssetDownload.MINDUSTRY_DESKTOP_DOWNLOAD_TASK_NAME))
    mainClass.convention("mindustry.desktop.DesktopLauncher")
    modsPath.convention("./mods")
}
