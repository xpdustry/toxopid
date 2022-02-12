plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    id("com.gradle.plugin-publish") version "0.20.0"
    id("net.kyori.indra.publishing.gradle-plugin") version "2.1.0"
}

group = "fr.xpdustry"
version = "1.1.0"

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.hjson:hjson:3.0.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")

    compileOnly(gradleApi())
    compileOnly(kotlin("stdlib"))
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

indra {
    mitLicense()
    github("Xpdustry", "Toxopid"){
        ci(true)
        issues(true)
    }

    configurePublications{
        pom{
            organization{
                name.set("Xpdustry")
                url.set("https://www.xpdustry.fr")
            }

            developers {
                developer {
                    id.set("Phinner")
                }
            }
        }
    }
}

indraPluginPublishing {
    plugin(
        "fr.xpdustry.toxopid",
        "fr.xpdustry.toxopid.ToxopidPlugin",
        "Xpdustry Mindustry Gradle plugin",
        "Gradle plugin for deploying mindustry mods/plugins + some build utilities.",
        listOf("xpdustry", "gradle-plugin", "mindustry")
    )
}
