# Toxopid

![Xpdustry Toxopid](https://repo.xpdustry.fr/api/badge/latest/releases/fr/xpdustry/toxopid?color=00FFFF&name=Toxopid&prefix=v)

## Description

Gradle plugin for deploying mindustry mods/plugins + some build utilities.

## Usage

Put this in your `settings.gradle`:

```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repo.xpdustry.fr/releases") }
    }
}
```

Then apply the plugin in your `build.gradle` with:

```gradle
plugins {
    id("fr.xpdustry.toxopid") version "1.0.0"
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

- Don't forget to specify the `[mod|plugin].[h]json` location with `modFile`. It's set to `./plugin.json` by default for some reasons.
