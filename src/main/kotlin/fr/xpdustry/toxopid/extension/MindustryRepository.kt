package fr.xpdustry.toxopid.extension

abstract class MindustryRepository(val repo: String) {
    companion object {
        @JvmField
        val MAIN: MindustryRepository = MainMindustryRepository
        @JvmField
        val BE: MindustryRepository = BeMindustryRepository
    }

    abstract fun getArtifactName(target: MindustryTarget, version: String): String

    object MainMindustryRepository : MindustryRepository("Anuken/Mindustry") {
        override fun getArtifactName(target: MindustryTarget, version: String): String = when (target) {
            MindustryTarget.DESKTOP -> "Mindustry.jar"
            MindustryTarget.HEADLESS -> "server-release.jar"
        }
    }

    object BeMindustryRepository : MindustryRepository("Anuken/MindustryBuilds") {
        override fun getArtifactName(target: MindustryTarget, version: String): String = when (target) {
            MindustryTarget.DESKTOP -> "Mindustry-BE-Desktop-$version.jar"
            MindustryTarget.HEADLESS -> "Mindustry-BE-Server-$version.jar"
        }
    }
}