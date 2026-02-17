plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = "edu.kit.ifv.mobitopp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":"))
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