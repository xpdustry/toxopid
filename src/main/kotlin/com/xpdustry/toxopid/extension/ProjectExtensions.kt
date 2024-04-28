package com.xpdustry.toxopid.extension

import com.xpdustry.toxopid.Toxopid
import com.xpdustry.toxopid.ToxopidExtension
import com.xpdustry.toxopid.ToxopidExtensionImpl
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

internal inline val Project.toxopid: ToxopidExtension
    get() =
        extensions.findByName(Toxopid.EXTENSION_NAME) as ToxopidExtension?
            ?: extensions.create(ToxopidExtension::class, Toxopid.EXTENSION_NAME, ToxopidExtensionImpl::class)
