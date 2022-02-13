package fr.xpdustry.toxopid.extension

import org.gradle.api.Project

@Suppress("HasPlatformType")
open class ToxopidExtension(project: Project) {
    /** The remote Mindustry repository. */
    val mindustryRepository = project.objects.property(MindustryRepository::class.java)

    /** Mindustry compile version. */
    val mindustryCompileVersion = project.objects.property(String::class.java)

    /** Mindustry runtime version. If not specified, fallbacks to [mindustryCompileVersion]. */
    val mindustryRuntimeVersion = project.objects.property(String::class.java)

    /** Whether toxopid should add mindustry dependencies. */
    val mindustryBuildDependencies = project.objects.property(Boolean::class.java)

    /** Arc compile version. If not specified, fallbacks to [mindustryCompileVersion]. */
    val arcCompileVersion = project.objects.property(String::class.java)

    /** `[ mod|plugin ].[ h ]json` file. */
    val modFile = project.objects.fileProperty()

    /** The build target. */
    val modTarget = project.objects.property(ModTarget::class.java)

    /** List of dependencies grabbed from GitHub. */
    val modDependencies = project.objects.listProperty(ModDependency::class.java)

    /** Whether maven-publish should also publish shadowed jars. */
    val publishShadowJar = project.objects.property(Boolean::class.java)

    init {
        mindustryRepository.convention(MindustryRepository.MAIN)
        mindustryCompileVersion.convention("v126.2")
        mindustryBuildDependencies.convention(true)
        modTarget.convention(ModTarget.DESKTOP)
        modDependencies.convention(emptyList())
        publishShadowJar.convention(false)
    }
}