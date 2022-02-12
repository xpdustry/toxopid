package fr.xpdustry.toxopid.extension

import fr.xpdustry.toxopid.util.fileProvider
import org.gradle.api.Project

@Suppress("HasPlatformType")
open class ToxopidExtension(project: Project) {
    /** The build target. */
    val target = project.objects.property(MindustryTarget::class.java)
    /** The remote Mindustry repository. */
    val repository = project.objects.property(MindustryRepository::class.java)
    /** Mindustry compile version. */
    val compileVersion = project.objects.property(String::class.java)
    /** Mindustry runtime version. If not specified, fallbacks to [compileVersion]. */
    val runtimeVersion = project.objects.property(String::class.java)
    /** `[ mod|plugin ].[ h ]json` file. */
    val modFile = project.objects.fileProperty()
    /** List of dependencies grabbed from GitHub. */
    val modDependencies = project.objects.listProperty(MindustryDependency::class.java)
    /** Whether toxopid should add mindustry dependencies. */
    val addMindustryDependencies = project.objects.property(Boolean::class.java)
    /** Whether maven-publish should also publish shadowed jars. */
    val publishShadowJar = project.objects.property(Boolean::class.java)

    init {
        target.convention(MindustryTarget.DESKTOP)
        repository.convention(MindustryRepository.MAIN)
        compileVersion.convention("v126.2")
        modFile.convention(project.fileProvider(project.file("./plugin.json")))
        modDependencies.convention(emptyList())
        addMindustryDependencies.convention(true)
        publishShadowJar.convention(false)
    }
}