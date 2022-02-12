plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.6.10"
}

group = "fr.xpdustry"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
}

gradlePlugin {
    plugins {
        create("toxopid") {
            id = "fr.xpdustry.toxopid"
            implementationClass = "fr.xpdustry.toxopid.ToxopidPlugin"
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        pom {
            url.set("https://github.com/Xpdustry/Toxopid")

            organization {
                name.set("Xpdustry")
                url.set("https://www.xpdustry.fr")
            }

            developers {
                developer { name.set("Phinner") }
            }
        }
    }

    repositories.maven {
        name = "releases"
        url = uri("https://repo.xpdustry.fr/releases")

        credentials {
            username = System.getenv("XPDUSTRY_MAVEN_USERNAME") ?: ""
            password = System.getenv("XPDUSTRY_MAVEN_PASSWORD") ?: ""
        }
    }
}

