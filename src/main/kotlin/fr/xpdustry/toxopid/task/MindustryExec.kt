package fr.xpdustry.toxopid.task

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import fr.xpdustry.toxopid.extension.MindustryDependency
import fr.xpdustry.toxopid.extension.MindustryTarget
import fr.xpdustry.toxopid.extension.ToxopidExtension
import fr.xpdustry.toxopid.util.downloadTo
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Jar
import java.net.URL
import javax.inject.Inject

@Suppress("HasPlatformType")
abstract class MindustryExec @Inject constructor(@get:Internal val target: MindustryTarget) : JavaExec() {
    @get:Input @get:Optional
    val artifacts = project.objects.listProperty(Jar::class.java)

    init {
        mainClass.set(target.mainClass)
        artifacts.set(listOf(project.tasks.named("shadowJar", ShadowJar::class.java).get()))
    }

    @TaskAction
    override fun exec() {
        dependsOn.addAll(artifacts.get())

        val extension = project.extensions.getByType(ToxopidExtension::class.java)
        val version = extension.mindustryRuntimeVersion.getOrElse(extension.mindustryCompileVersion.get())

        val mindustry = extension.mindustryRepository.get().getArtifactName(target, version)
        val mindustryUrl = URL("https://github.com/${extension.mindustryRepository.get().repo}/releases/download/$version/$mindustry")
        val mindustryFile = temporaryDir.resolve(mindustry)

        val metadata = Metadata(mindustryUrl.toString(), extension.modDependencies.get())
        val metadataFile = temporaryDir.resolve("metadata.txt")
        val oldMetadata = if (metadataFile.exists()) metadataFile.readText() else null

        val modDirectory = temporaryDir.resolve(target.modDirectory).apply { mkdirs() }

        if (oldMetadata == null || metadata.toString() != oldMetadata) {
            project.delete(temporaryDir.listFiles { f -> f.name != "metadata.txt" })
            mindustryUrl.downloadTo(mindustryFile)
            extension.modDependencies.get().forEach {
                it.url.downloadTo(modDirectory.resolve(it.artifact ?: "${it.repo.replace('/', '-')}.zip"))
            }
        }

        artifacts.get().forEach {
            project.delete(modDirectory.listFiles { f -> f.name.startsWith(it.archiveBaseName.get()) })
            it.archiveFile.get().asFile.copyTo(modDirectory.resolve(it.archiveFileName.get()))
        }

        metadataFile.writeText(metadata.toString())

        workingDir = temporaryDir
        classpath = project.objects.fileCollection().from(mindustryFile)
        standardInput = System.`in`
        environment["MINDUSTRY_DATA_DIR"] = temporaryDir

        super.exec()
    }

    /** Temporary data for refreshing the files. */
    private data class Metadata(
        val url: String,
        val dependencies: List<MindustryDependency> = emptyList()
    )
}