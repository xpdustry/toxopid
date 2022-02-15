package fr.xpdustry.toxopid.task

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import fr.xpdustry.toxopid.extension.ModDependency
import fr.xpdustry.toxopid.extension.ModTarget
import fr.xpdustry.toxopid.extension.ToxopidExtension
import fr.xpdustry.toxopid.util.downloadTo
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import java.net.URL
import javax.inject.Inject

@Suppress("HasPlatformType")
abstract class MindustryExec @Inject constructor(@get:Internal val target: ModTarget) : DefaultTask() {
    private val artifacts = project.objects.setProperty(Jar::class.java)

    init {
        addArtifact(project.tasks.named("shadowJar", ShadowJar::class.java).get())
    }

    fun addArtifact(jar: Jar) {
        artifacts.add(jar)
        dependsOn.add(jar)
    }

    fun addArtifact(task: TaskProvider<Jar>): Unit = addArtifact(task.get())

    @TaskAction
    fun exec() {
        val extension = project.extensions.getByType(ToxopidExtension::class.java)
        val modDirectory = temporaryDir.resolve(target.modDirectory)

        val mindustryVersion = extension.mindustryRuntimeVersion.getOrElse(extension.mindustryCompileVersion.get())
        val mindustryArtifact = extension.mindustryRepository.get().getArtifactName(target, mindustryVersion)
        val mindustryUrl = URL("https://github.com/${extension.mindustryRepository.get().repo}/releases/download/$mindustryVersion/$mindustryArtifact")
        val mindustryFile = temporaryDir.resolve(mindustryArtifact)

        val runtimeData = ModRuntimeData(mindustryUrl.toString(), extension.modDependencies.get())
        val runtimeDataFile = temporaryDir.resolve("runtime-data.txt")
        val oldRuntimeData = if (runtimeDataFile.exists()) runtimeDataFile.readText() else null

        if (oldRuntimeData == null || runtimeData.toString() != oldRuntimeData) {
            project.delete(temporaryDir.listFiles { f -> f.name != "runtime-data.txt" })
            logger.debug("Reset runtime directory")
            mindustryUrl.downloadTo(mindustryFile)
            logger.debug("Downloaded Mindustry: ${mindustryFile.name}")
            modDirectory.mkdirs()
            extension.modDependencies.get().forEach {
                it.url.downloadTo(modDirectory.resolve(it.artifact ?: "${it.repo.replace('/', '-')}.zip"))
                logger.debug("Downloaded mod dependency: (${it.repo}, ${it.version}, ${it.artifact})")
            }
        }

        project.delete {
            val dependencies = extension.modDependencies.get().map { m -> m.artifact ?: "${m.repo.replace('/', '-')}.zip" }.toSet()
            val files = modDirectory.listFiles { f -> f.name !in dependencies }
            it.delete(files)
            logger.debug("Removed mod dependencies: $files")
        }

        artifacts.get().forEach {
            it.archiveFile.get().asFile.copyTo(modDirectory.resolve(it.archiveFileName.get()))
            logger.debug("Copied artifact: ${it.name}")
        }

        runtimeDataFile.writeText(runtimeData.toString())

        project.javaexec {
            it.mainClass.set(target.mainClass)
            it.workingDir = temporaryDir
            it.classpath = project.objects.fileCollection().from(mindustryFile)
            it.standardInput = System.`in`
            it.environment["MINDUSTRY_DATA_DIR"] = temporaryDir
        }
    }

    /** Temporary data for refreshing the files. */
    private data class ModRuntimeData(
        val mindustryUrl: String,
        val dependencies: List<ModDependency>
    )
}