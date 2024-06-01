plugins {
    id("com.diffplug.spotless") version "6.25.0"
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.dokka") version "1.9.20"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    id("net.kyori.indra") version "3.1.3"
    id("net.kyori.indra.git") version "3.1.3"
    id("net.kyori.indra.publishing.gradle-plugin") version "3.1.3"
    `kotlin-dsl`
}

group = "com.xpdustry"
version = "4.0.0" + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
description = "Gradle plugin for building and testing mindustry mods/plugins."

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleApi())
    implementation("org.hjson:hjson:3.1.0")
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

spotless {
    kotlin {
        ktlint()
        licenseHeaderFile(rootProject.file("HEADER.txt"))
    }
    kotlinGradle {
        ktlint()
    }
}

indra {
    javaVersions {
        target(17)
        minimumToolchain(17)
    }

    publishSnapshotsTo("xpdustry", "https://maven.xpdustry.com/snapshots")
    publishReleasesTo("xpdustry", "https://maven.xpdustry.com/releases")

    mitLicense()

    github("xpdustry", "toxopid") {
        ci(true)
        issues(true)
        scm(true)
    }

    configurePublications {
        pom {
            organization {
                name.set("Xpdustry")
                url.set("https://www.xpdustry.com")
            }

            developers {
                developer {
                    id.set("Phinner")
                    timezone.set("Europe/Brussels")
                }
            }
        }
    }
}

kotlin {
    explicitApi()
}

indraPluginPublishing {
    website("https://github.com/xpdustry/toxopid")

    plugin(
        "toxopid",
        "com.xpdustry.toxopid.plugin.ToxopidPlugin",
        "Toxopid",
        project.description,
        listOf("mindustry", "testing"),
    )
}

tasks.javadocJar {
    from(tasks.dokkaHtml)
}
