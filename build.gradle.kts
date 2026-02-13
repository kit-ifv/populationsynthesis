plugins {
    kotlin("jvm") version "2.1.21"
    id("maven-publish")
}

group = "edu.kit.ifv.mobitopp"
version = project.findProperty("buildVersion") as String? ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")

    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-releases/") }
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-central/") }
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-snapshots/") }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("org.apache.commons:commons-statistics-inference:1.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.2.1")
    implementation("it.unimi.dsi:fastutil:8.5.16")
    implementation("org.ejml:ejml-all:0.43")
    implementation("org.apache.spark:spark-mllib_2.13:3.5.4")
//    api("org.jetbrains.kotlin:kotlin-gradle-statistics:2.3.10")
    api("org.jetbrains.kotlinx:kandy-lets-plot:0.8.3")
    api("org.jetbrains.kotlinx:kandy-api:0.8.3")
    api("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.11.2")
    api("org.jetbrains.lets-plot:lets-plot-image-export:4.8.2")
    api("org.jetbrains.lets-plot:lets-plot-batik:4.8.2")
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.24.3"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    implementation("org.apache.logging.log4j:log4j-1.2-api")

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


publishing {
    version = project.findProperty("buildVersion") as String? ?: "1.0-SNAPSHOT"
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["kotlin"])
        }
    }

    repositories {
        maven {
            name = "nexus"
            val repoUrl =
                (findProperty("localUrl") as String?)
                    ?: System.getenv("NEXUS_URL")
                    ?: "https://nexus.ifv.kit.edu/repository/maven-snapshots/"

            url = uri(repoUrl)


            credentials {
                username =
                    (findProperty("localRepoUser") as String?)
                        ?: System.getenv("NEXUS_USER")

                password =
                    (findProperty("localRepoPassword") as String?)
                        ?: System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}

