# Toxopid

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/fr.xpdustry.toxopid)](https://plugins.gradle.org/plugin/fr.xpdustry.toxopid)
[![Commit](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml)
[![Javadoc](https://img.shields.io/badge/Javadoc-latest-ffff00)](https://maven.xpdustry.fr/javadoc/releases/fr/xpdustry/toxopid/latest/)
[![Mindustry 6.0 | 7.0 ](https://img.shields.io/badge/Mindustry-6.0%20%7C%207.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

A gradle plugin for building and testing mindustry mods/plugins.

It follows the gradle good practices as closely as possible for maximum efficiency, control and ease of use.

## Usage

The following examples assume you are using a kotlin build script ([which are much better than regular groovy scripts](https://docs.gradle.org/current/userguide/kotlin_dsl.html)).

### Getting started

1. Add the plugin in your build script :

    ```kotlin
    plugins {
        id("fr.xpdustry.toxopid") version "2.2.0"
    }
    ```

2. Set up the Toxopid extension to fit your needs :

    ```kotlin
    import fr.xpdustry.toxopid.ModPlatform
   
    toxopid {
        // The version with which your mod/plugin is compiled.
        // If not set, will compile with v140 by default.
        compileVersion.set("v126") 
        // The version with which your mod/plugin is tested.
        // If not set, defaults to compileVersion.
        runtimeVersion.set("v135") 
        // The platforms you target, you can choose DESKTOP, HEADLESS or/and ANDROID.
        // If not set, will target DESKTOP by default.
        platforms.add(ModPlatform.HEADLESS)
    }
    ```

### Features

- You can automatically add Mindustry dependencies with :

  ```kotlin
  import fr.xpdustry.toxopid.util.anukenJitpack
  import fr.xpdustry.toxopid.util.mindustryDependencies

  repositories {
      mavenCentral()
      anukenJitpack()
  }

  dependencies {
      mindustryDependencies()
  }
  ```

- You can load your `[mod|plugin].[h]json` in your build script with `ModMetadata` :

  ```kotlin
  import fr.xpdustry.toxopid.util.ModMetadata
  
  val metadata = ModMetadata.fromJson(project.file("mod.hjson"))
  // Setting the project version from the one located in "mod.json"
  project.version = metadata.version
  ```

  or generate one from a `ModMetadata` :

  ```kotlin
  import fr.xpdustry.toxopid.util.ModMetadata
  
  val metadata = ModMetadata(
      name = "example",
      displayName = "Example",
      description = "A very nice mod :)",
      main = "com.example.mod.ModMain"
  )

  tasks.jar {
      // Doing it in doFirst makes sure it's only executed when this task runs
      doFirst {
          val temp = temporaryDir.resolve("mod.json")
          temp.writeText(metadata.toJson(true))
          from(temp)
     }
  }
  ```

- You can run your mod/plugin in a Mindustry client or server locally with the `runMindustryClient` and
  `runMindustryServer` tasks.

- If your mod relies on another, you can download the dependency jar from GitHub with the `ModArtifactDownload` task, or include it locally :

  ```kotlin
  import fr.xpdustry.toxopid.task.ModArtifactDownload

  val downloadMod = tasks.register<ModArtifactDownload>("downloadMod") {
      it.user.set("Xpdustry")
      it.repo.set("ExampleMod")
      it.version.set("v1.0.0")
      // Set the name if the artifact name isn't "(repo-name).jar" such as "ExampleMod.jar"
      // it.name.set("Mod.jar")
  }
  
  val localMod = project.file("./libs/LocalMod.jar")
  
  tasks.runMindustryClient {
      mods.setFrom(setOf(tasks.jar, downloadMod, localMod))
  }
  ```
  
## Support

If you need help, you can talk to the maintainers on the [Xpdustry Discord](https://discord.xpdustry.fr) in the `#support` channel.
  

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy), or [mgpp](https://github.com/PlumyGame/mgpp).
