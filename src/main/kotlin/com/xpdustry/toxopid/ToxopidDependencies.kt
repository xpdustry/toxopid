package com.xpdustry.toxopid

import org.gradle.api.artifacts.Dependency
import org.gradle.api.provider.Provider

public data class ToxopidDependencies(
    val mindustryCore: Provider<Dependency>,
    val arcCore: Provider<Dependency>,
    val mindustryHeadless: Provider<Dependency>,
    val arcHeadless: Provider<Dependency>,
)
