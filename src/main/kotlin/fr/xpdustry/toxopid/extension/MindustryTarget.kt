package fr.xpdustry.toxopid.extension

enum class MindustryTarget(val modDirectory: String, val mainClass: String) {
    DESKTOP("./mods", "mindustry.desktop.DesktopLauncher"),
    HEADLESS("./config/mods", "mindustry.server.ServerLauncher")
}