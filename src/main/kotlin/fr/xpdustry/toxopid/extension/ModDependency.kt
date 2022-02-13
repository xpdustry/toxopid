package fr.xpdustry.toxopid.extension

import java.net.URL

/**
 * Create a plugin/mod dependency.
 * If [artifact] is null, it will download the repository zip instead.
 */
data class ModDependency constructor(
    val repo: String,
    val version: String?,
    val artifact: String?
) {
    constructor(repo: String) : this(repo, null, null)

    val url = URL("https://github.com/$repo/${if (artifact == null) "archive/master.zip" else "releases/download/${version!!}/$artifact"}")
}
