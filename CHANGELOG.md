# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/),
and this project adheres to [Semantic Versioning](http://semver.org/).

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
