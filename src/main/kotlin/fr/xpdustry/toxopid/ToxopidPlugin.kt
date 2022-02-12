@file:Suppress("unused")

package fr.xpdustry.toxopid

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import fr.xpdustry.toxopid.extension.MindustryTarget
import fr.xpdustry.toxopid.extension.ToxopidExtension
import fr.xpdustry.toxopid.task.MindustryExec
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

        val extension = project.extensions.create(TOXOPID_EXTENSION_NAME, ToxopidExtension::class.java, project)

        if (extension.addMindustryDependencies.get()) {
            project.repositories.maven { it.url = project.uri("https://www.jitpack.io") }
            project.mindustryDependency("com.github.Anuken.Arc:arc-core:${extension.compileVersion.get()}")
            project.mindustryDependency("com.github.Anuken.Mindustry:core:${extension.compileVersion.get()}")
            project.mindustryDependency("com.github.Anuken.Mindustry:annotations:${extension.compileVersion.get()}")
            if (extension.target.get() == MindustryTarget.HEADLESS) {
                project.mindustryDependency("com.github.Anuken.Arc:backend-headless:${extension.compileVersion.get()}")
                project.mindustryDependency("com.github.Anuken.Mindustry:server:${extension.compileVersion.get()}")
            }
        }

        val shadow = project.tasks.named("shadowJar", ShadowJar::class.java) { it.from(extension.modFile.get()) }

        project.tasks.getByName("build").dependsOn(shadow.get())

        project.configure(listOf(
            project.tasks.create("runMindustryServer", MindustryExec::class.java, MindustryTarget.HEADLESS),
            project.tasks.create("runMindustryClient", MindustryExec::class.java, MindustryTarget.DESKTOP)
        )) {
            it.group = TOXOPID_GROUP_NAME
        }

        if(!extension.publishShadowJar.get()){
            // Ugly way to avoid publishing shadow artifacts to maven repos
            // -> https://github.com/johnrengelman/shadow/issues/651
            project.components.withType(AdhocComponentWithVariants::class.java).forEach { c ->
                c.withVariantsFromConfiguration(project.configurations.getByName("shadowRuntimeElements")) { it.skip() }
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
