# Toxopid

## Description

Gradle plugin for deploying mindustry mods/plugins + some build utilities.

## Usage

/!\ Waiting gradle approval for publishing, the plugin is unusable for now...

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
