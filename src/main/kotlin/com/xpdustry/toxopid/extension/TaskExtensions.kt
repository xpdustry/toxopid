package com.xpdustry.toxopid.extension

import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec
import java.nio.file.Paths

/**
 * Configures a [MindustryExec] task to run a Mindustry server.
 */
public fun MindustryExec.configureServer() {
    classpath(project.tasks.named(GithubAssetDownload.MINDUSTRY_SERVER_DOWNLOAD_TASK_NAME))
    mainClass.convention("mindustry.server.ServerLauncher")
    modsPath.convention(Paths.get("config", "mods"))
    standardInput = System.`in`
}

/**
 * Configures a [MindustryExec] task to run a Mindustry desktop instance.
 */
public fun MindustryExec.configureDesktop() {
    classpath(project.tasks.named(GithubAssetDownload.MINDUSTRY_DESKTOP_DOWNLOAD_TASK_NAME))
    mainClass.convention("mindustry.desktop.DesktopLauncher")
    modsPath.convention(Paths.get("mods"))
}
