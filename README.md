# toxopid

[![Mindustry 6.0 | 7.0](https://img.shields.io/badge/Mindustry-6.0%20%7C%207.0-00b0b3)](https://github.com/Anuken/Mindustry/releases)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/fr.xpdustry.toxopid?color=00b0b3&logoColor=00b0b3&label=Gradle)](https://plugins.gradle.org/plugin/fr.xpdustry.toxopid)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/Xpdustry/Toxopid/build.yml?color=00b0b3&label=Build)](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml)
[![Discord](https://img.shields.io/discord/519293558599974912?color=00b0b3&label=Discord)](https://discord.xpdustry.com)

## Description

A gradle plugin for building and testing mindustry mods/plugins.

## Links

- [Javadoc](https://maven.xpdustry.com/javadoc/releases/fr/xpdustry/toxopid/latest/)
- [Snapshots](https://maven.xpdustry.com/#/snapshots/fr/xpdustry/toxopid/)

## Usage

The following examples assume you are using a kotlin build script.

### Getting started

1. Add the plugin to your build script :

    ```gradle.kts
    plugins {
        id("fr.xpdustry.toxopid") version "VERSION"
    }
    ```

2. Set up Toxopid to fit your needs :

    ```gradle.kts
    import fr.xpdustry.toxopid.spec.ModPlatform

    toxopid {
        // The version with which your mod/plugin is compiled.
        // If not set, will compile with v143 by default.
        compileVersion.set("v126") 
        // The version with which your mod/plugin is tested.
        // If not set, defaults to the value of compileVersion.
        runtimeVersion.set("v143") 
        // The platforms you target, you can choose DESKTOP, HEADLESS or/and ANDROID.
        // If not set, will target DESKTOP by default.
        platforms.add(ModPlatform.HEADLESS)
    }
    ```

3. Automatically add Mindustry dependencies with :

    ```gradle.kts
    import fr.xpdustry.toxopid.dsl.anukenJitpack
    import fr.xpdustry.toxopid.dsl.mindustryDependencies

    repositories {
        mavenCentral()
        anukenJitpack()
        // If Jitpack does not work, replace it with
        // maven("https://maven.xpdustry.com/mindustry")
        // This repository contains mindustry artifacts built by xpdustry
        // More info at https://github.com/xpdustry/mindustry-publish
    }

    dependencies {
        mindustryDependencies()
    }
    ```

4. Load the info of your `[mod|plugin].[h]json` in your build script with `ModMetadata` and include it in the final
   Jar :

   ```gradle.kts
   import fr.xpdustry.toxopid.spec.ModMetadata

   val metadata = ModMetadata.fromJson(project.file("mod.json"))
   // Setting the project version from the one located in "mod.json"
   project.version = metadata.version
   
   tasks.jar {
       // Doing it in doFirst makes sure it's only executed when this task runs
       doFirst {
           from(file("mod.json"))
       }
   }
   ```

   or directly generate your `[mod|plugin].[h]json` from your build script and write it to the final Jar :

   ```gradle.kts
   import fr.xpdustry.toxopid.spec.ModMetadata

   project.version  = "1.0.0"
   
   val metadata = ModMetadata(
       name = "example",
       version = project.version.toString(),
       displayName = "Example",
       description = "A very nice mod :)",
       main = "org.example.mod.ModMain"
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

### Features

- You can run your mod/plugin in a Mindustry client or server locally with the `runMindustryClient` and
  `runMindustryServer` tasks.

- If your mod/plugin relies on another, you can download the dependency jar from GitHub with
  the `GithubArtifactDownload` task, or include it locally :

  ```gradle.kts
  import fr.xpdustry.toxopid.task.GithubArtifactDownload

  val downloadMod = tasks.register<GithubArtifactDownload>("downloadMod") {
      user.set("ExampleUser")
      repo.set("ExampleMod")
      name.set("ExampleMod.jar")
      version.set("v1.0.0")
  }
  
  val localMod = project.file("./libs/LocalMod.jar")
  
  tasks.runMindustryClient {
      // Don't forget to add your mod/plugin jar to the mods list
      mods.setFrom(setOf(tasks.jar, downloadMod, localMod))
  }
  ```

### About migrating from Toxopid 2.x.x to 3.x.x

- If you are using the `ModArtifactDownload`, rename it to `GithubDownloadArtifact` and add an explicit `name` as the
  name of the downloaded artifact.

- The `GithubArtifact` and `GithubDownload` classes have been removed. You won't have a problem if you followed the
  depreciation warnings.

- `MindustryExec` now extends `JavaExec` instead of extending `DefaultTask`. The API is mostly the same but now with
  more control over the execution.

- The internal tasks of Toxopid have been changed to use the new task classes. Change your build scripts accordingly if
  you happen to configure them.

- `ModPlatform` and `ModMetadata` have been moved to the `fr.xpdustry.toxopid.spec` package.

- The extension methods have been moved to the `fr.xpdustry.toxopid.dsl` package.

## Support

If you need help, you can talk to the maintainers on the
[Chaotic Neutral Discord](https://discord.xpdustry.com) in the `#support` channel.

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy), or [mgpp](https://github.com/PlumyGame/mgpp).
