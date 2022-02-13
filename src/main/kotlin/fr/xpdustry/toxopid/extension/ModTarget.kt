package fr.xpdustry.toxopid.extension

/** Represent the targeted platform by the mod/plugin. */
enum class ModTarget(val modDirectory: String, val mainClass: String) {
    DESKTOP("./mods", "mindustry.desktop.DesktopLauncher"),
    HEADLESS("./config/mods", "mindustry.server.ServerLauncher")
}