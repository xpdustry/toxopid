package com.xpdustry.toxopid.extension

import com.xpdustry.toxopid.Toxopid
import com.xpdustry.toxopid.ToxopidExtension
import net.kyori.mammoth.Extensions
import org.gradle.api.plugins.ExtensionContainer

internal inline val ExtensionContainer.toxopid: ToxopidExtension
    get() = Extensions.findOrCreate(this, Toxopid.EXTENSION_NAME, ToxopidExtension::class.java)
