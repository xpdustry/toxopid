plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.20.0"
    id("net.kyori.indra") version "2.1.1"
    id("net.kyori.indra.publishing.gradle-plugin") version "2.1.1"
    id("com.github.ben-manes.versions") version "0.42.0"
}

group = "fr.xpdustry"
version = "1.3.2" + if (indraGit.headTag() == null) "-SNAPSHOT" else ""

tasks.javadocJar {
    from(tasks.dokkaHtml)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.hjson:hjson:3.0.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

indra {
    javaVersions {
        target(8)
        minimumToolchain(17)
    }

    publishSnapshotsTo("xpdustry", "https://repo.xpdustry.fr/snapshots")
    publishReleasesTo("xpdustry", "https://repo.xpdustry.fr/releases")

    mitLicense()

    github("Xpdustry", "Toxopid") {
        ci(true)
        issues(true)
        scm(true)
    }

    configurePublications {
        pom {
            organization {
                name.set("Xpdustry")
                url.set("https://www.xpdustry.fr")
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

indraPluginPublishing {
    website("https://github.com/Xpdustry/Toxopid")

    plugin(
        "toxopid",
        "fr.xpdustry.toxopid.ToxopidPlugin",
        "Xpdustry Mindustry Gradle plugin",
        "Gradle plugin for deploying mindustry mods/plugins + some build utilities.",
        listOf("xpdustry", "gradle-plugin", "mindustry")
    )
}
