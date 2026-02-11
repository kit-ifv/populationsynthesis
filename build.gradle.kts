plugins {
    kotlin("jvm") version "2.1.21"
}

group = "edu.kit.ifv.populationsynthesis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.apache.commons:commons-statistics-inference:1.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.2.1")
    implementation("it.unimi.dsi:fastutil:8.5.16")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}