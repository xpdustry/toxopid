# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/),
and this project adheres to [Semantic Versioning](http://semver.org/).

## v4.2.0 - 2026-01-21

### Bugfixes

- Use single string dependency notation in ToxopidExtension to fix depreciation warnings for Gradle 10.
- Made ModMetadata [h]json parser more lenient for integral types.

### Maintenance

- Updated R8 and Android platform version to be in line with Mindustry and support Kotlin 2.3.
- Raised default Mindustry version to v154.
- Documentation improvements.

## v4.1.2 - 2025-01-24

### Fixes

- Fixed depreciation warnings for Gradle 9 and 10.

### Chores

- Updated build dependencies.
- Updated default version of R8 to `v8.7.18` for `DexJar`.

## v4.1.1 - 2024-11-14

### Fixes

- Fix R8 overriding `DexJar` classpath, causing warnings.

## v4.1.0 - 2024-09-30

### Features

- Dexing with `DexJar` no longer requires Android studio installed, android platform and R8 are downloaded at runtime.
- Allow the use of an explicit R8 and android platform version for `DexJar`.
- Added support for [gradleup's shadow](https://github.com/GradleUp/shadow), the official successor of shadow.

### Chores

- Upgraded Gradle to `v8.10.2`.

## v4.0.2 - 2024-06-20

### Fixes

- Add support for [shadow maintained fork](https://plugins.gradle.org/plugin/io.github.goooler.shadow).

## v4.0.1 - 2024-06-11

### Fixes

- Made `ModMetadata` and `ModDependency` serializable for task input compatibility.
- Avoid re-downloading assets in `GithubAssetDownload`.
- Remove android check for `dexTask`.

## v4.0.0-rc.2 - 2024-06-03

Final release candidate, and also because initial RC failed to publish on Gradle.

## v4.0.0-rc.1 - 2024-06-01

Initial release candidate for testing, consult [MIGRATING.md](https://github.com/xpdustry/toxopid/blob/master/MIGRATING.md) for more info.

## v3.2.0 - 2023-05-08

### Changes

- Change default Mindustry version to v143.

### Added

- Added option to use the Mindustry jitpack mirror for compilation.

### Bugfixes

- Fix a crash occurring when the shadow plugin is applied without the java plugin.

### Chores

- Bump mammoth version to 1.3.1.
- Replaced Kotlin stdlib-jdk8 with bundled Gradle Kotlin dsl version.

## v3.1.0 - 2023-03-09

### Features

- Added extension methods for groovy builds.

### Changes

- Force Arc version to be the same as Mindustry.

## v3.0.0 - 2022-12-25

Major release for y'all :)

### Changes

- `MindustryExec` now extends `JavaExec`, it enables a better debugging experience and more control over the Mindustry instance runtime.
- Renamed `JarArtifactDownload` to `GithubArtifactDownload`.
- Added new data fields for `ModMetadata` (subtitle, keepOutlines, texturescale, pregenerated).
- Moved some classes into dedicated packages (see migration guide at the bottom of the `README`).
- Github artifacts are now cached in a shared directory in the gradle user home (see `GithubArtifactDownload.java`). Which means download once, use everywhere.

### Added

- Suport for non compressed mods in `MindustryExec`.

## v2.2.0 - 2022-12-01

### Changes

- The default mmindustry version is now `v140`.
- Deprecated `GithubArtifact` and `GithubDownload` in favor of a simpler API `ModArtifactDownload`.
- Random optimizations.

### Chores

- Changed the internals to comply with the "configuration driven plugins".

## v2.1.1

### Fixes

- Fixed bug where the wrong Mindustry artifacts were downloaded

## v2.1.0

### Changes

- `GitHubArtifact` is now an interface that can be implemented.
- Misc changes.

### Fixes

- Fixed an issue with mindustry version in download tasks.

## v2.0.0

Complete overhaul of the plugin, it now follows gradle plugin convention to allow maximum customization :

### Changes

- Multiple sub-plugins.
- Task pipelines. (`downloadMindustry` -> `runMindustry`)
- Simpler extension settings...
- Much more !!!

## v1.3.2

### Fixes

- Removed `annotations` artifact since it's no longer published in V7.

## v1.3.1

### Fixes

- Fix bug where only the `ModTarget.DESKTOP` dependencies are applied.

## v1.3.0

**Breaking changes**

### Changes

- Renamed `MindustryDependency` to `ModDependency`.
- Renamed `MindustryTarget` to `ModTarget`.
- Renamed `ToxopidExtension` attributes accordingly.
- Removed `modFile`.
- Bug fixes related to `MindustryExec` tasks
- Addition of `addArtifact` method to `MindustryExec` for gradle multi-projects.
