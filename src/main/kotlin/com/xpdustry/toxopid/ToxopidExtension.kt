package com.xpdustry.toxopid

import com.xpdustry.toxopid.spec.ModPlatform
import org.gradle.api.artifacts.Dependency
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty

public interface ToxopidExtension {
    /**
     * Mindustry compile version for dependency resolution.
     * If not set, the default version is [Toxopid.DEFAULT_MINDUSTRY_VERSION].
     */
    public val compileVersion: Property<String>

    /**
     * Mindustry runtime version for [com.xpdustry.toxopid.task.MindustryExec] tasks.
     * If not set, fallbacks to [ToxopidExtension.compileVersion].
     */
    public val runtimeVersion: Property<String>

    /**
     * Target platforms for the mod/plugin. It can add dependencies, tasks...
     * If not set, the default platform is [ModPlatform.DESKTOP].
     */
    public val platforms: SetProperty<ModPlatform>

    /**
     * Typesafe mindustry dependencies provider.
     */
    public val dependencies: Dependencies

    /**
     * Typesafe mindustry dependencies holder.
     */
    public interface Dependencies {
        public val mindustryCore: Provider<Dependency>
        public val arcCore: Provider<Dependency>
        public val mindustryHeadless: Provider<Dependency>
        public val arcHeadless: Provider<Dependency>
    }
}
