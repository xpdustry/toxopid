# Toxopid

![Xpdustry Toxopid](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/toxopid?color=00FFFF&name=Toxopid&prefix=v)
[![Commit](https://github.com/Xpdustry/Toxopid/actions/workflows/commit.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Toxopid/actions/workflows/commit.yml)

## Description

Gradle plugin for deploying mindustry mods/plugins + some build utilities.

## Usage

/!\ Waiting gradle approval for publishing, you will have to add this your `settings.gradle`:

```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repo.xpdustry.fr/releases") }
    }
}
```

Apply the plugin in your `build.gradle` with:

```gradle
plugins {
    id("fr.xpdustry.toxopid") version "1.1.0"
}
```

Finally, enjoy the plugin.

## Tips

- For the base settings, you will need at least:

```gradle
toxopid {
    arcCompileVersion.set("your compile version")
    mindustryCompileVersion.set("your compile version")
}
```

- Toxopid automatically include mindustry dependencies, so you don't have to worry about that in your build script anymore, but if you wish to disable that, set `addMindustryDependencies` to `false`.

- If you want to run your plugin/mod on a BE server, set `repository` to `MindustryRepository.BE` and set `mindustryRuntimeVersion` to the BE build number.

- You can include external dependencies from GitHub by defining a list of `MindustryDependency` on `modDependencies`.

- The plugin will look for a `./[mod|plugin].[h]json` and include it in the jar, but you can specify its location with `modFile`.

- Mods/Plugins are bundled with shadow, no need to include them manually.

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy).
