# Toxopid

![Xpdustry Toxopid](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/toxopid?color=00FFFF&name=Toxopid&prefix=v)
[![Commit](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml)

## Description

Gradle plugin for building and testing mindustry mods/plugins.

## Usage

1. Add the plugin by applying it in your `build.gradle` with :

  ```gradle
  plugins {
      id("fr.xpdustry.toxopid") version "2.0.0"
  }
  ```

2. Set up the Toxopid extension to fit your needs with :

  ```gradle
  toxopid {
      compileVersion.set("insert-version-here") 
      runtimeVersion.set("insert-version-here") 
      platforms.add(ModPlatform.YOUR_PLATOFRM)
  }
  ```

  If you do not set the extension, Toxopid will compile with mindustry V6 (v126.2)
  and target the desktop platform by default.

3. Enjoy the plugin. Here's what you can do :

  - You can automatically add Mindustry dependencies in your `build.gradle` with :
  
    ```gradle
    toxopid {
        jitpackAnuken()
        mindustryDependencies()
    }
    ```
    
  - You can easily use your `[mod|plugin].[h]json` with `ModMetadata`. Example :

    ```gradle
    val metadata = ModMetadata.fromJson(project.file("mod.hjson"))
    project.version = metadata.version
    ```
    
  - You can run mindustry in desktop or server with the `runMindustryClient` and
    `runMindustryServer` tasks.

Now that you se

## Tips

- If you want to create a `ModMetadata` and use it to generate your `[mod|plugin].[h]json` file,
  you can create a pretty printed json string of it with `JsonOutput.prettyPrint(JsonOutput.toJson(metadata))`.

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy).
