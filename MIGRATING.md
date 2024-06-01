# Migration guide for major versions of Toxopid

## From Toxopid 3.x.x to 4.x.x

- **Groovy support has been dropped due to its hacky nature.** This affects the extensions methods for `repositories` and `dependencies` blocks.

- The group and id of the plugin have been changed to `com.xpdustry.toxpid`, change your imports accordingly.

- `GithubDownloadArtifact` has been renamed to `GithubAssetDownload`. And also the properties `user` has been renamed to `owner` and `name` has been renamed to `asset`. These changes aim to better reflect the internal structure of the GitHub API.

- Mindustry dependencies are no longer automatically added with the `mindustryDependencies()` and misc extension functions. It has been replaced by a typesafe accessor via `toxopid.dependencies`. This change is to give more control over the dependencies and easier usage for mindustry libraries.

  ```gradle.kts
  // Toxopid 3
  dependencies {
    // It automatically added compileOnly and testImplementation for each target
    mindustryDependencies()
  }
  
  // Toxopid 4
  dependencies {
    compileOnly(toxopid.dependencies.mindustryCore)
    compileOnly(toxopid.dependencies.arcCore)
    // If you have unit tests
    testImplementation(toxopid.dependencies.mindustryCore)
    testImplementation(toxopid.dependencies.arcCore)
  }
  ```
  
- `ModMetadata#toJson` is now a static function. Replace `metadata.toJson()` with `ModMetadata.toJson(metadata)`.

- Given the possibility that mod dependencies may contain extra information in the future, `ModMetadata#softDependencies` has been removed and `ModMetadata#dependencies` has been changed to a list of `ModDependency`.

  ```gradle.kts
  // Toxopid 3
  val metadata = ModMetadata()
  metadata.dependencies += listOf("mod1", "mod2")
  metadata.softDependencies += listOf("mod3", "mod4")
  
  // Toxopid 4
  val metadata = ModMetadata()
  metadata.dependencies += listOf(
    ModDependency("mod1"),
    ModDependency("mod2"),
    ModDependency("mod3", soft = true),
    ModDependency("mod4", soft = true)
  )
  ```
  
- `MindustryExec#modsPath` has been changed to `MindustryExec#modsDir` which is now a directory property instead of a string.

## From Toxopid 2.x.x to 3.x.x

- If you are using the `ModArtifactDownload`, rename it to `GithubDownloadArtifact` and add an explicit `name` as the
  name of the downloaded artifact.

- The `GithubArtifact` and `GithubDownload` classes have been removed. You won't have a problem if you followed the
  depreciation warnings.

- `MindustryExec` now extends `JavaExec` instead of extending `DefaultTask`. The API is mostly the same but now with
  more control over the execution.

- The internal tasks of Toxopid have been changed to use the new task classes. Change your build scripts accordingly if
  you happen to configure them.

- `ModPlatform` and `ModMetadata` have been moved to the `com.xpdustry.toxopid.spec` package.

- The extension methods have been moved to the `fr.xpdustry.toxopid.dsl` package.