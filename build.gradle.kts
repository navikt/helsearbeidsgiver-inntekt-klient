import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.helsearbeidsgiver"
version = "0.1.9"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("org.jmailen.kotlinter")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
    mavenNav("*")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        mavenNav("helsearbeidsgiver-${rootProject.name}")
    }
}

dependencies {
    val ktorVersion: String by project
    val mockkVersion: String by project
    val tokenproviderVersion: String by project

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")
    implementation("no.nav.helsearbeidsgiver:tokenprovider:$tokenproviderVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

fun RepositoryHandler.mavenNav(repo: String): MavenArtifactRepository {
    val githubPassword: String by project

    return maven {
        setUrl("https://maven.pkg.github.com/navikt/$repo")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
}
