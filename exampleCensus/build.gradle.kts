plugins {
    kotlin("jvm")
}

group = "edu.kit.ifv.populationsynthesis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    implementation(project(":"))
    testImplementation(kotlin("test"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

