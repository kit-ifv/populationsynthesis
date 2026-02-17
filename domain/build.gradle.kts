plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = "edu.kit.ifv.mobitopp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":"))
    api("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.2.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["kotlin"])
            artifactId = "synthesis-domain" // choose your artifact name
        }
    }
}