package fr.xpdustry.toxopid

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import fr.xpdustry.toxopid.extension.ModTarget
import fr.xpdustry.toxopid.extension.ToxopidExtension
import fr.xpdustry.toxopid.task.MindustryExec
import fr.xpdustry.toxopid.util.findOrCreate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants

@Suppress("unused")
class ToxopidPlugin : Plugin<Project> {
    private companion object {
        const val TOXOPID_EXTENSION_NAME = "toxopid"
        const val TOXOPID_GROUP_NAME = "toxopid"
    }

    override fun apply(project: Project) {
        project.plugins.apply(ShadowPlugin::class.java)
        project.extensions.findOrCreate(TOXOPID_EXTENSION_NAME, ToxopidExtension::class.java, project)

        val shadow = project.tasks.named("shadowJar", ShadowJar::class.java) {
            val file = project.rootDir.listFiles()?.find { f -> f.name.matches(Regex("^(mod|plugin)\\.h?json$")) }
            if (file != null) it.from(file)
        }

        project.tasks.getByName("build").dependsOn(shadow.get())

        project.configure(listOf(
            project.tasks.create("runMindustryServer", MindustryExec::class.java, ModTarget.HEADLESS),
            project.tasks.create("runMindustryClient", MindustryExec::class.java, ModTarget.DESKTOP)
        )) {
            it.group = TOXOPID_GROUP_NAME
            it.description = "Run your artifacts in a ${it.target.name.lowercase()} environment."
        }

        project.repositories.mavenCentral()
        project.repositories.maven {
            it.name = "jitpack-anuken"
            it.url = project.uri("https://www.jitpack.io")
            it.mavenContent { c -> c.includeGroupByRegex("^com\\.github\\.Anuken.*") }
        }

        project.afterEvaluate { p ->
            val ext = p.extensions.findByType(ToxopidExtension::class.java)!!

            if (ext.mindustryBuildDependencies.get()) {
                val arcCompileVersion = ext.arcCompileVersion.getOrElse(ext.mindustryCompileVersion.get())

                p.mindustryDependency("com.github.Anuken.Arc:arc-core:$arcCompileVersion")
                p.mindustryDependency("com.github.Anuken.Mindustry:core:${ext.mindustryCompileVersion.get()}")

                if (ext.modTarget.get() == ModTarget.HEADLESS) {
                    p.mindustryDependency("com.github.Anuken.Arc:backend-headless:$arcCompileVersion")
                    p.mindustryDependency("com.github.Anuken.Mindustry:server:${ext.mindustryCompileVersion.get()}")
                }
            }

            if (!ext.publishShadowJar.get()) {
                // Ugly way to avoid publishing shadow artifacts to maven repos
                // -> https://github.com/johnrengelman/shadow/issues/651
                p.components.withType(AdhocComponentWithVariants::class.java).forEach { c ->
                    c.withVariantsFromConfiguration(p.configurations.getByName("shadowRuntimeElements")) { it.skip() }
                }
            }
        }
    }
}

private fun Project.dependency(configuration: String, dependency: String) {
    configurations.getByName(configuration).dependencies.add(dependencies.create(dependency))
}

private fun Project.mindustryDependency(dependency: String) {
    dependency("compileOnly", dependency)
    dependency("testImplementation", dependency)
}
