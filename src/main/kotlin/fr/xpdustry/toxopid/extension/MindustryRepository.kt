package fr.xpdustry.toxopid.extension

/** Represent the remote Mindustry repository. */
abstract class MindustryRepository(val repo: String) {
    companion object {
        /** Main repository. */
        @JvmField
        val MAIN: MindustryRepository = MainMindustryRepository

        /** Bleeding edge repository. */
        @JvmField
        val BE: MindustryRepository = BeMindustryRepository
    }

    abstract fun getArtifactName(target: ModTarget, version: String): String

    private object MainMindustryRepository : MindustryRepository("Anuken/Mindustry") {
        override fun getArtifactName(target: ModTarget, version: String): String = when (target) {
            ModTarget.DESKTOP -> "Mindustry.jar"
            ModTarget.HEADLESS -> "server-release.jar"
        }
    }

    private object BeMindustryRepository : MindustryRepository("Anuken/MindustryBuilds") {
        override fun getArtifactName(target: ModTarget, version: String): String = when (target) {
            ModTarget.DESKTOP -> "Mindustry-BE-Desktop-$version.jar"
            ModTarget.HEADLESS -> "Mindustry-BE-Server-$version.jar"
        }
    }
}