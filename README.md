# Toxopid

![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/fr.xpdustry.toxopid)
[![Commit](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml)

## Description

Gradle plugin for building and testing mindustry mods/plugins.

## Usage

1. Add the plugin in your build script :

  ```gradle
  plugins {
      id("fr.xpdustry.toxopid") version "2.0.0"
  }
  ```

2. Set up the Toxopid extension to fit your needs :

  ```gradle
  toxopid {
      compileVersion.set("insert-version-here") 
      runtimeVersion.set("insert-version-here") 
      platforms.add(ModPlatform.YOUR_PLATOFRM)
  }
  ```
  
  > If you do not set the extension, Toxopid will compile and run with Mindustry V6 (v126.2)
    and target the desktop platform by default.

3. Enjoy the plugin. Here's what you can do :

   - If you use the Gradle Kotlin DSL (aka using a `build.gradle.kts`), you can automatically
     add Mindustry dependencies with :

     ```gradle
     repositories {
         mavenCentral()
         anukenJitpack()
     }
  
     dependencies {
         mindustryDependencies()
     }
     ```

   - You can use your `[mod|plugin].[h]json` in your build script with `ModMetadata`. Example :

     ```gradle
     var metadata = ModMetadata.fromJson(project.file("mod.hjson"))
     project.version = metadata.version
     ```

   - You can run your mod/plugin in Mindustry desktop or server with the `runMindustryClient` and
     `runMindustryServer` tasks.

## Tips

- You can add other mods/plugins in a `runMindustry` task by doing :

    ```gradle
    tasks.runMindustryClient {
        mods.from(file("SomeMod.jar")) // Adding an external jar
        mods.from(tasks.compileSubMod) // Adding a task that can provide the jar
    }
    ```

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy).
