plugins {
    id("com.diffplug.spotless") version "6.11.0"
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.dokka") version "1.6.20"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.20.0"
    id("net.kyori.indra") version "3.0.1"
    id("net.kyori.indra.git") version "3.0.1"
    id("net.kyori.indra.publishing.gradle-plugin") version "3.0.1"
    id("net.kyori.indra.licenser.spotless") version "3.0.1"
    groovy
    `kotlin-dsl`
}

group = "fr.xpdustry"
version = "3.2.1" + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
description = "Gradle plugin for building and testing mindustry mods/plugins."

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleApi())
    implementation("org.hjson:hjson:3.0.0")
    implementation("net.kyori:mammoth:1.3.1")
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
    groovy {
        greclipse()
    }
}

indra {
    javaVersions {
        target(8)
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
        "fr.xpdustry.toxopid.ToxopidPlugin",
        "Toxopid",
        project.description,
        listOf("mindustry", "testing", "boilerplate")
    )
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("LICENSE_HEADER.md"))
}

tasks.javadocJar {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml)
}
