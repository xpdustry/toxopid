# Toxopid

![Xpdustry Toxopid](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/toxopid?color=00FFFF&name=Toxopid&prefix=v)
[![Commit](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Xpdustry/Toxopid/actions/workflows/build.yml)

## Description

Gradle plugin for building and testing mindustry mods/plugins.

## Usage

Apply the plugin in your `build.gradle` with:

```gradle
plugins {
    id("fr.xpdustry.toxopid") version "2.0.0"
}
```

## Tips

- For the base settings, you will need at least:

```gradle
toxopid {
    mindustryCompileVersion.set("your compile version")
}
```

- Toxopid automatically include mindustry dependencies, so you don't have to worry about that in your build script
  anymore, but if you wish to disable that, set `mindustryBuildDependencies` to `false`.

- If you want to run your plugin/mod on a BE server, set `mindustryRepository` to `MindustryRepository.BE` and
  set `mindustryRuntimeVersion` to the BE build number.

- You can include external dependencies from GitHub by defining a list of `ModDependency` on `modDependencies`.

> Also, checkout [Mindeploy](https://github.com/NiChrosia/Mindeploy).
