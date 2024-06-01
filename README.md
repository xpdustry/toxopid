# toxopid

[![Mindustry 6.0 | 7.0](https://img.shields.io/badge/Mindustry-6.0%20%7C%207.0-00b0b3)](https://github.com/Anuken/Mindustry/releases)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/com.xpdustry.toxopid?color=00b0b3&logoColor=00b0b3&label=Gradle)](https://plugins.gradle.org/plugin/com.xpdustry.toxopid)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/Xpdustry/Toxopid/build.yml?color=00b0b3&label=Build)](https://github.com/xpdustry/toxopid/actions/workflows/build.yml)
[![Discord](https://img.shields.io/discord/519293558599974912?color=00b0b3&label=Discord)](https://discord.xpdustry.com)

## Description

A gradle plugin for building and testing mindustry mods/plugins.

## Links

- [Javadoc](https://maven.xpdustry.com/javadoc/releases/com/xpdustry/toxopid/latest/)
- [Snapshots](https://maven.xpdustry.com/#/snapshots/com/xpdustry/toxopid/)
- [Migration guide](MIGRATING.md)

## Usage

The following examples assume you are using a kotlin build script.

### Getting started

1. Add the plugin to your build script:

   ```gradle.kts
   plugins {
     id("com.xpdustry.toxopid") version "VERSION"
   }
   ```

2. Set up Toxopid to fit your needs:

   ```gradle.kts
   import com.xpdustry.toxopid.spec.ModPlatform

   toxopid {
     // The version with which your mod/plugin is compiled.
     // If not set, will compile with v146 by default.
     compileVersion = "v126"
     // The version with which your mod/plugin is tested.
     // If not set, defaults to the value of compileVersion.
     runtimeVersion = "v146"
     // The platforms you target, you can choose DESKTOP, SERVER or/and ANDROID.
     // If not set, will target DESKTOP by default.
     platforms = setOf(ModPlatform.DESKTOP, ModPlatform.SERVER)
   }
   ```

3. Add Mindustry dependencies with:

   ```gradle.kts
   import com.xpdustry.toxopid.extension.anukeJitpack
   import com.xpdustry.toxopid.extension.anukeXpdustry
   import com.xpdustry.toxopid.extension.anukeZelaux

   repositories {
     mavenCentral()
     // You can choose between the following repositories to get mindustry artifacts:
     // - jitpack is the offical maven repository of mindustry, but it breaks a lot 😡
     anukeJitpack()
     // - xpdustry is a repository maintained by us. More info at https://github.com/xpdustry/mindustry-publish
     anukeXpdustry()
     // - zelaux is a repository maintained by Zelaux. More info at https://github.com/Zelaux/MindustryRepo
     anukeZelaux()
   }

   dependencies {
     compileOnly(toxopid.dependencies.mindustryCore)
     compileOnly(toxopid.dependencies.arcCore)
   }
   ```

4. Load the info of your `[mod|plugin].[h]json` in your build script with `ModMetadata`:

   ```gradle.kts
   import com.xpdustry.toxopid.spec.ModMetadata

   val metadata = ModMetadata.fromJson(project.file("mod.json"))
   // Setting the project version from the one located in "mod.json"
   project.version = metadata.version
   ```

   or directly generate your `[mod|plugin].[h]json` from your build script and write it to the final Jar:

   ```gradle.kts
   import com.xpdustry.toxopid.spec.ModMetadata

   project.version  = "1.0.0"
   
   val metadata = ModMetadata(
     name = "example",
     version = project.version.toString(),
     displayName = "Example",
     description = "A very nice mod :)",
     mainClass = "org.example.mod.ModMain"
   )
   
   tasks.jar {
     // Doing it in doFirst makes sure it's only executed when this task runs
     doFirst {
       val temp = temporaryDir.resolve("mod.json")
       temp.writeText(Metadata.toJson(metadata, true))
       from(temp)
     }
   }
   ```
   
And voilà, you have a minimal toxopid setup for your mod/plugin.

### Features

- You can run your mod/plugin in a Mindustry client or server locally with the `runMindustryDesktop` and
  `runMindustryServer` tasks.

- If your mod/plugin relies on another, you can download the dependency jar from GitHub with
  the `GithubAssetDownload` task, or include it locally:

  ```gradle.kts
  import com.xpdustry.toxopid.task.GithubArtifactDownload

  val downloadMod = tasks.register<GithubAssetDownload>("downloadMod") {
    owner = "ExampleUser"
    repo = "ExampleMod"
    asset = "ExampleMod.jar"
    version = "v1.0.0"
  }
  
  val localMod = project.file("./libs/LocalMod.jar")
  
  tasks.runMindustryDesktop {
    mods.from(downloadMod, localMod)
  }
  ```

## Support

If you need help, you can talk to the maintainers on the
[Chaotic Neutral Discord](https://discord.xpdustry.com) in the `#support` channel.

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy), or [mgpp](https://github.com/PlumyGame/mgpp).
