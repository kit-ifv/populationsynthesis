plugins {
    kotlin("jvm") version "2.1.21"
    id("maven-publish")
}

group = "edu.kit.ifv.populationsynthesis"
version = "1.0-SNAPSHOT"

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


allprojects {
    apply(plugin = "maven-publish")

    project.group = "edu.kit.ifv.mobitopp"

    afterEvaluate {

        if (checkProperty("doPublish")) {
            /* mobiTopp publishing process (see .gitlab-ci.yml)
             * Parameters such as "doPublish" must be passed in gradle command:
             *  - ./gradlew <TASKS> publish -PdoPublish=true -Pparam=value...
             * Lookup of parameters doPublish and isRelease returns true if they are specified and their value reads "true".
             * Other required parameters must be specified, otherwise an error is thrown.
             *
             * The pipeline build version is used as the published artifacts version string.
             *  - uses parameter: "buildVersion"
             *
             * Every merge on main is published to local repo: see deploy-job
             *  - checks: doPublish=true, isRelease=false
             *  - requires parameters: "localUrl", "localRepoUser" and "localRepoPassword"
             *
             * Public releases must be published manually:
             *  - checks: doPublish=true, isRelease=true
             *  - requires parameters: "publicUrl", "publicRepoUser" and "publicRepoPassword"
             */

            project.version = requireProperty("buildVersion")
            println("Setup publishing configuration for ${group}:${project.name}:${version}.")

            publishing {

                publications {
                    register("mavenData", MavenPublication::class) {
                        from(components["kotlin"]) // For Kotlin projects
                        groupId = group.toString()
                        artifactId = project.name
                        version = project.version.toString()
                    }
                }

                repositories {
                    if (checkProperty("isRelease")) {
                        println("Activate: publish public release!")
                        println("WARNING: Public release still deactivated!")

                        //  Keep for first public release of reengineered mobitopp
                        //maven {
                        //    name = "PublicRepo"
                        //    url = uri(requireProperty("publicUrl"))
                        //    credentials {
                        //        username = requireProperty("publicRepoUser")
                        //        password = requireProperty("publicRepoPassword")
                        //    }
                        //}

                    } else {
                        println("Activate: publish local build!")
                        maven {
                            name = "LocalRepo"
                            url = uri(requireProperty("localUrl"))
                            credentials {
                                username = requireProperty("localRepoUser")
                                password = requireProperty("localRepoPassword")
                            }
                        }
                    }
                }

            }

        }

    }

}

fun requireProperty(property: String, orElse: String? = null): String =
    requireNotNull(project.findProperty(property) as? String ?: orElse) {
        "Could not find property '$property'. Please check the gradle command args. It should contain:\n" +
                "    ./gradlew ... -P$property=<VALUE> ..."
    }

fun checkProperty(property: String): Boolean = project.hasProperty(property) && project.property(property) == "true"

